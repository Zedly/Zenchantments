package zedly.zenchantments.event.listener.merge;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;

import static org.bukkit.event.EventPriority.MONITOR;

/**
 * @author Dennis
 */
public class GrindstoneMergeListener implements Listener {
    private final ZenchantmentsPlugin plugin;

    public GrindstoneMergeListener(@NotNull ZenchantmentsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = MONITOR)
    private void onClick(@NotNull InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.GRINDSTONE) {
            return;
        }

        GrindstoneInventory inventory = (GrindstoneInventory) event.getInventory();
        World world = event.getViewers().get(0).getWorld();

        if (event.getSlot() == 2) {
            this.removeOutputEnchants(inventory, world);
        } else {
            this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(
                this.plugin,
                () -> this.removeOutputEnchants(inventory, world),
                0
            );
        }
    }

    private void removeOutputEnchants(@NotNull GrindstoneInventory inventory, @NotNull World world) {
        ItemStack itemStack = inventory.getItem(2);

        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return;
        }

        for (Zenchantment enchantment : Zenchantment.getEnchants(itemStack, world).keySet()) {
            Zenchantment.setEnchantment(itemStack, enchantment, 0, world);
        }

        inventory.setItem(2, itemStack);
    }
}