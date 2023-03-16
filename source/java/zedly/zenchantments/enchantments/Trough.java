package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Collection;
import java.util.Set;

import static org.bukkit.Material.AIR;

public class Trough extends Zenchantment {
    public static final String KEY = "trough";

    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();

    public Trough(
        final @NotNull Set<Tool> enchantable,
        final int maxLevel,
        final int cooldown,
        final double probability,
        final float power
    ) {
        super(enchantable, maxLevel, cooldown, probability, power, CONFLICTING, KEY);
    }

    @Override
    public Collection<EquipmentSlot> getApplyToSlots() {
        return Slots.ARMOR;
    }

    @Override
    public boolean onEntityInteract(final @NotNull PlayerInteractEntityEvent event, final int level, final EquipmentSlot slot) {
        return this.feed(event, level, slot);
    }

    private boolean feed(final @NotNull PlayerInteractEntityEvent event, final int level, final EquipmentSlot slot) {
        if(!(event.getRightClicked() instanceof Breedable)) {
            return false;
        }

        final EntityType clickedType = event.getRightClicked().getType();
        final int radius = (int) Math.round(level * this.getPower() + 2);
        final Player player = event.getPlayer();
        ItemStack mainHandItem = event.getPlayer().getInventory().getItemInMainHand();
        ItemStack offHandItem = event.getPlayer().getInventory().getItemInOffHand();
        int mainHandAmount = mainHandItem.getAmount();
        int offHandAmount = offHandItem.getAmount();
        int mainHandUsed = 0;
        int offHandUsed = 0;

        for (final Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity.getType() == clickedType && entity instanceof Animals animal) {
                if (CompatibilityAdapter.instance().canAnimalEnterLoveMode(animal)) {
                    if (mainHandUsed < mainHandAmount && animal.isBreedItem(mainHandItem)) {
                        mainHandUsed++;
                    } else if (offHandUsed < offHandAmount && animal.isBreedItem(offHandItem)) {
                        offHandUsed++;
                    } else {
                        continue;
                    }
                    CompatibilityAdapter.instance().animalEnterLoveMode(animal, player);
                }
            }
        }

        if (mainHandAmount == mainHandUsed) {
            mainHandItem.setAmount(mainHandAmount - mainHandUsed);
            player.getInventory().setItemInMainHand(new ItemStack(AIR));
        } else {
            mainHandItem.setAmount(mainHandAmount - mainHandUsed);
            player.getInventory().setItemInMainHand(mainHandItem);
        }
        if (offHandAmount == offHandUsed) {
            player.getInventory().setItemInOffHand(new ItemStack(AIR));
        } else {
            offHandItem.setAmount(offHandAmount - offHandUsed);
            player.getInventory().setItemInOffHand(offHandItem);
        }

        return (mainHandUsed > 0 || offHandUsed > 0);
    }
}
