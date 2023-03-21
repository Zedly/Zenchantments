package zedly.zenchantments.enchantments;

import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import static org.bukkit.Material.AIR;

@AZenchantment(runInSlots = Slots.ARMOR, conflicting = {})
public class Trough extends Zenchantment {
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
                if (WorldInteractionUtil.canAnimalEnterLoveMode(animal)) {
                    if (mainHandUsed < mainHandAmount && animal.isBreedItem(mainHandItem)) {
                        mainHandUsed++;
                    } else if (offHandUsed < offHandAmount && animal.isBreedItem(offHandItem)) {
                        offHandUsed++;
                    } else {
                        continue;
                    }
                    WorldInteractionUtil.animalEnterLoveMode(animal, player);
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
