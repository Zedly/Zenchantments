package zedly.zenchantments.event.listener.merge;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.configuration.WorldConfiguration;

import java.util.Set;

import static java.util.Objects.requireNonNull;
import static org.bukkit.Material.ENCHANTED_BOOK;
import static org.bukkit.enchantments.Enchantment.DURABILITY;
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
            if (event.getCurrentItem() != null) {
                final ItemMeta itemMeta = (ItemMeta) event.getCurrentItem().getItemMeta();
                if (itemMeta != null && itemMeta.hasEnchants() && itemMeta.getEnchants().containsKey(DURABILITY)
                    && itemMeta.getEnchants().get(DURABILITY) == 0
                ) {
                    itemMeta.removeEnchant(DURABILITY);
                    event.getCurrentItem().setItemMeta(itemMeta);
                }
            }
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(ZenchantmentsPlugin.getInstance(), () -> {
                ensureToolIsGrindable((GrindstoneInventory) event.getInventory(), 0);
                ensureToolIsGrindable((GrindstoneInventory) event.getInventory(), 1);
            }, 0);
            this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(
                this.plugin,
                () -> this.removeOutputEnchants(inventory, world),
                0
            );
        }
    }

    private void ensureToolIsGrindable(final @NotNull GrindstoneInventory inventory, int slot) {
        ItemStack item = inventory.getItem(slot);
        if (item == null) {
            return;
        }
        final ItemMeta itemMeta = (ItemMeta) item.getItemMeta();

        if (itemMeta != null && (!itemMeta.hasEnchants() || !itemMeta.getEnchants().containsKey(DURABILITY))) {
            itemMeta.addEnchant(DURABILITY, 0, true);
            item.setItemMeta(itemMeta);
            inventory.setItem(slot, item);
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
