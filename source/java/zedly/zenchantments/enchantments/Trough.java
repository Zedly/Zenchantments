package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.entity.animal.EntityAnimal;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftAnimals;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
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

    private static final String NAME = "Trough";
    private static final String DESCRIPTION = "Feeds all animals in a radius";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand HAND_USE = Hand.RIGHT;

    private final NamespacedKey key;

    public Trough(
        final @NotNull Set<Tool> enchantable,
        final int maxLevel,
        final int cooldown,
        final double probability,
        final float power
    ) {
        super(enchantable, maxLevel, cooldown, probability, power);
        this.key = new NamespacedKey(ZenchantmentsPlugin.getInstance(), KEY);
    }

    @Override
    @NotNull
    public NamespacedKey getKey() {
        return this.key;
    }

    @Override
    @NotNull
    public String getName() {
        return NAME;
    }

    @Override
    @NotNull
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    @NotNull
    public Set<Class<? extends Zenchantment>> getConflicting() {
        return CONFLICTING;
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
            if (entity.getType() == clickedType && entity instanceof CraftAnimals animal) {
                if (animal.isAdult()) {
                    EntityAnimal ea = animal.getHandle();
                    int i = ea.h();
                    if (!ea.s.y && i == 0 && ea.fP()) {
                        if (mainHandUsed < mainHandAmount && animal.isBreedItem(mainHandItem)) {
                            mainHandUsed++;
                        } else if (offHandUsed < offHandAmount && animal.isBreedItem(offHandItem)) {
                            offHandUsed++;
                        } else {
                            continue;
                        }
                        ea.f(((CraftPlayer) player).getHandle());
                    }
                }
            }
        }

        if(mainHandAmount == mainHandUsed) {
            mainHandItem.setAmount(mainHandAmount - mainHandUsed);
            player.getInventory().setItemInMainHand(new ItemStack(AIR));
        } else {
            mainHandItem.setAmount(mainHandAmount - mainHandUsed);
            player.getInventory().setItemInMainHand(mainHandItem);
        }
        if(offHandAmount == offHandUsed) {
            player.getInventory().setItemInOffHand(new ItemStack(AIR));
        } else {
            offHandItem.setAmount(offHandAmount - offHandUsed);
            player.getInventory().setItemInOffHand(offHandItem);
        }

        return (mainHandUsed > 0 || offHandUsed > 0);
    }
}
