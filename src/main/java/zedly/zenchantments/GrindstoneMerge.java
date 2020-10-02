package zedly.zenchantments;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.event.EventPriority.MONITOR;

/**
 * @author Dennis
 */
public class GrindstoneMerge implements Listener {
    private static final GrindstoneMerge INSTANCE = new GrindstoneMerge();

    public static GrindstoneMerge instance() {
        return INSTANCE;
    }

    @EventHandler(priority = MONITOR)
    public void onClicks(final InventoryClickEvent evt) {
        if (evt.getInventory().getType() != InventoryType.GRINDSTONE) {
            return;
        }

        GrindstoneInventory inventory = (GrindstoneInventory) evt.getInventory();
        World world = evt.getViewers().get(0).getWorld();

        if (evt.getSlot() == 2) {
            this.removeOutputEnchants(inventory, world);
        } else {
            Bukkit.getScheduler().scheduleSyncDelayedTask(
                Storage.zenchantments,
                () -> this.removeOutputEnchants(inventory, world),
                0
            );
        }
    }

    private void removeOutputEnchants(GrindstoneInventory inventory, World world) {
        final ItemStack itemStack = inventory.getItem(2);

        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return;
        }

        for (CustomEnchantment enchantment : CustomEnchantment.getEnchants(itemStack, world).keySet()) {
            CustomEnchantment.setEnchantment(itemStack, enchantment, 0, world);
        }

        inventory.setItem(2, itemStack);
    }
}