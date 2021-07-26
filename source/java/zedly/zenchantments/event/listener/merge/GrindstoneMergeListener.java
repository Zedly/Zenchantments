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
import zedly.zenchantments.configuration.WorldConfiguration;

import java.util.Set;

import static org.bukkit.event.EventPriority.MONITOR;

public class GrindstoneMergeListener implements Listener {
    private final ZenchantmentsPlugin plugin;

    public GrindstoneMergeListener(final @NotNull ZenchantmentsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = MONITOR)
    private void onClick(final @NotNull InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.GRINDSTONE) {
            return;
        }

        final GrindstoneInventory inventory = (GrindstoneInventory) event.getInventory();
        final World world = event.getViewers().get(0).getWorld();

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

    private void removeOutputEnchants(final @NotNull GrindstoneInventory inventory, final @NotNull World world) {
        final ItemStack itemStack = inventory.getItem(2);

        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return;
        }

        final WorldConfiguration worldConfiguration = this.plugin.getWorldConfigurationProvider().getConfigurationForWorld(world);
        final Set<Zenchantment> zenchantments = Zenchantment.getZenchantmentsOnItemStack(
            itemStack,
            this.plugin.getGlobalConfiguration(),
            worldConfiguration
        ).keySet();

        for (final Zenchantment zenchantment : zenchantments) {
            Zenchantment.setZenchantmentForItemStack(itemStack, zenchantment, 0, worldConfiguration);
        }

        inventory.setItem(2, itemStack);
    }
}
