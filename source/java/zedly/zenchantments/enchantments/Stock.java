package zedly.zenchantments.enchantments;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import static org.bukkit.Material.AIR;

@AZenchantment(runInSlots = Slots.ARMOR, conflicting = {})
public final class Stock extends Zenchantment {
    @Override
    public boolean onBlockInteract(final @NotNull PlayerInteractEvent event, final int level, final EquipmentSlot slot) {
        final PlayerInventory inventory = event.getPlayer().getInventory();
        final Material handItem = inventory.getItemInMainHand().getType();

        if (handItem == AIR) {
            return false;
        }

        final Player player = event.getPlayer();

        ZenchantmentsPlugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(ZenchantmentsPlugin.getInstance(), () -> {
            final ItemStack newHandItem = inventory.getItemInMainHand();

            if (newHandItem.getType() != AIR) {
                return;
            }

            int current = -1;

            final ItemStack[] invContents = player.getInventory().getContents();
            for (int i = 0; i < event.getPlayer().getInventory().getContents().length; i++) {
                final ItemStack stack = invContents[i];
                if (stack != null && stack.getType() == handItem) {
                    current = i;
                    break;
                }
            }

            if (current != -1) {
                inventory.setItemInMainHand(invContents[current]);
                inventory.setItem(current, new ItemStack(AIR));
            }
        }, 1);

        return false;
    }
}
