package zedly.zenchantments.event.listener.merge;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zedly.zenchantments.CompatibilityAdapter;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.configuration.WorldConfiguration;
import zedly.zenchantments.enchantments.Unrepairable;

import java.util.*;
import java.util.Map.Entry;

import static java.util.Objects.*;
import static org.bukkit.Material.AIR;
import static org.bukkit.Material.ENCHANTED_BOOK;
import static org.bukkit.enchantments.Enchantment.DURABILITY;

public class AnvilMergeListener implements Listener {
    private final ZenchantmentsPlugin plugin;
    private final HashSet<AnvilInventory> UNSTABLE_ANVIL_INVS = new HashSet<>();

    public AnvilMergeListener(final @NotNull ZenchantmentsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onClick(final @NotNull InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.ANVIL || !event.getClick().isLeftClick()) {
            return;
        }

        // Prevent user from taking an intermediate output item with Unbreaking I that results from adding Unbreaking 0
        if(event.getSlotType() == InventoryType.SlotType.RESULT && UNSTABLE_ANVIL_INVS.contains(event.getInventory())) {
            event.setCancelled(true);
            // Address another bug that results from fixing the Unbreaking I bug which
            // falsely displays a lower XP level for the player who attempted to extract the temporary item
            if(event.getWhoClicked() instanceof Player player) {
                player.setLevel(player.getLevel());
            }
            return;
        }

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() != ENCHANTED_BOOK) {
            return;
        }

        final EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) event.getCurrentItem().getItemMeta();

        if (requireNonNull(bookMeta).getStoredEnchants().containsKey(DURABILITY)
            && bookMeta.getStoredEnchants().get(DURABILITY) == 0
        ) {
            bookMeta.removeStoredEnchant(DURABILITY);
            event.getCurrentItem().setItemMeta(bookMeta);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onClick(final @NotNull PrepareAnvilEvent event) {
        if (event.getViewers().size() < 1) {
            return;
        }

        final AnvilInventory anvilInv = event.getInventory();
        UNSTABLE_ANVIL_INVS.add(anvilInv);

        // Apply unbreaking 0 if necessary
        ItemStack rightItem = anvilInv.getItem(1);
        if (handlePossibleEnchantedBook(rightItem)) {
            anvilInv.setItem(1, rightItem);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            final ItemStack stack = doMerge(
                anvilInv,
                this.plugin.getWorldConfigurationProvider().getConfigurationForWorld(
                    event.getViewers().get(0).getWorld()
                )
            );
            if (stack != null) {
                anvilInv.setItem(2, stack);
            }
            UNSTABLE_ANVIL_INVS.remove(anvilInv);
        }, 0);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onClose(final @NotNull InventoryCloseEvent event) {
        if (event.getInventory().getType() != InventoryType.ANVIL) {
            return;
        }
        AnvilInventory inv = (AnvilInventory) event.getInventory();
        ItemStack rightItem = inv.getItem(1);
        if (rightItem == null || rightItem.getType() != ENCHANTED_BOOK) {
            return;
        }
        final EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) rightItem.getItemMeta();
        if (requireNonNull(bookMeta).getStoredEnchants().containsKey(DURABILITY)
            && bookMeta.getStoredEnchants().get(DURABILITY) == 0
        ) {
            bookMeta.removeStoredEnchant(DURABILITY);
            rightItem.setItemMeta(bookMeta);
        }
    }


    private static boolean handlePossibleEnchantedBook(final @Nullable ItemStack item) {
        if (item != null && item.getType() == ENCHANTED_BOOK) {
            final EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) item.getItemMeta();

            if (!requireNonNull(bookMeta).getStoredEnchants().containsKey(DURABILITY)) {
                bookMeta.addStoredEnchant(DURABILITY, 0, true);
                item.setItemMeta(bookMeta);
                return true;
            }
        }
        return false;
    }

    @Nullable
    private static ItemStack doMerge(
        AnvilInventory anvilInv,
        final @NotNull WorldConfiguration worldConfiguration
    ) {
        final ItemStack leftItem = anvilInv.getItem(0);
        final ItemStack rightItem = anvilInv.getItem(1);
        ItemStack oldOutItem = anvilInv.getItem(2);
        if (leftItem == null || rightItem == null || oldOutItem == null) {
            return null;
        }
        if (leftItem.getType() == AIR || rightItem.getType() == AIR || oldOutItem.getType() == AIR) {
            return null;
        }
        if (!oldOutItem.hasItemMeta()) {
            return null;
        }


        final List<String> leftNonEnchantLore = new ArrayList<>();
        final Map<Zenchantment, Integer> leftZenchantments = Zenchantment.getZenchantmentsOnItemStack(
            leftItem,
            true,
            worldConfiguration,
            leftNonEnchantLore
        );
        final Map<Zenchantment, Integer> rightZenchantments = Zenchantment.getZenchantmentsOnItemStack(
            rightItem,
            true,
            worldConfiguration
        );

        for (final Zenchantment enchantment : leftZenchantments.keySet()) {
            if (enchantment.getKey().getKey().equals(Unrepairable.KEY)) {
                return new ItemStack(AIR);
            }
        }
        for (final Zenchantment enchantment : rightZenchantments.keySet()) {
            if (enchantment.getKey().getKey().equals(Unrepairable.KEY)) {
                return new ItemStack(AIR);
            }
        }

        final boolean isBookLeft = leftItem.getType() == ENCHANTED_BOOK;
        final boolean isBookRight = rightItem.getType() == ENCHANTED_BOOK;

        final Map<Enchantment, Integer> leftVanillaEnchantments = isBookLeft
            ? ((EnchantmentStorageMeta) requireNonNull(leftItem.getItemMeta())).getStoredEnchants()
            : leftItem.getEnchantments();
        final Map<Enchantment, Integer> rightVanillaEnchantments = isBookRight
            ? ((EnchantmentStorageMeta) requireNonNull(rightItem.getItemMeta())).getStoredEnchants()
            : rightItem.getEnchantments();

        final int leftUnbreakingLevel = leftVanillaEnchantments.getOrDefault(DURABILITY, -1);
        final int rightUnbreakingLevel = rightVanillaEnchantments.getOrDefault(DURABILITY, -1);

        final EnchantmentPool pool = new EnchantmentPool(oldOutItem, worldConfiguration.getMaxZenchantments());
        pool.addAll(leftZenchantments);

        final List<Entry<Zenchantment, Integer>> rightEnchantmentList = new ArrayList<>(rightZenchantments.entrySet());
        Collections.shuffle(rightEnchantmentList);
        boolean anyZenchantmentsAdded = pool.addAll(rightEnchantmentList);

        final Map<Zenchantment, Integer> outEnchantments = pool.getEnchantmentMap();
        final ItemStack newOutItem = new ItemStack(oldOutItem);

        final ItemMeta meta = requireNonNull(oldOutItem.getItemMeta());
        meta.setLore(null);
        newOutItem.setItemMeta(meta);

        for (final Entry<Zenchantment, Integer> enchantEntry : outEnchantments.entrySet()) {
            enchantEntry.getKey().setForItemStack(newOutItem, enchantEntry.getValue(), worldConfiguration);
        }

        final ItemMeta newOutMeta = requireNonNull(newOutItem.getItemMeta());
        final List<String> outLore = newOutMeta.hasLore() ? requireNonNull(newOutMeta.getLore()) : new ArrayList<>();
        outLore.addAll(leftNonEnchantLore);

        if (leftUnbreakingLevel < 1
            && rightUnbreakingLevel < 1
        ) {
            if (oldOutItem.getType() == ENCHANTED_BOOK) {
                ((EnchantmentStorageMeta) newOutMeta).removeStoredEnchant(DURABILITY);
            } else {
                newOutMeta.removeEnchant(DURABILITY);
            }
            newOutMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        if (rightItem.getType() == ENCHANTED_BOOK && !anyZenchantmentsAdded) {
            Map<Enchantment, Integer> prematureEnchants = CompatibilityAdapter.instance().getPrematureEnchantments(newOutMeta);
            if (prematureEnchants == null || prematureEnchants.equals(leftVanillaEnchantments)) {
                return new ItemStack(AIR);
            }
        }

        newOutMeta.setLore(outLore);
        newOutItem.setItemMeta(newOutMeta);
        Zenchantment.updateEnchantmentGlowForItemStack(newOutItem, !outEnchantments.isEmpty(), worldConfiguration);
        return newOutItem;
    }

    private static class EnchantmentPool {
        private final Map<Zenchantment, Integer> enchantmentPool = new HashMap<>();
        private final ItemStack itemStack;
        private final int maxCapacity;

        public EnchantmentPool(final @NotNull ItemStack itemStack, final int maxCapacity) {
            this.itemStack = itemStack;
            this.maxCapacity = maxCapacity;
        }

        @Contract(mutates = "this")
        public boolean addAll(final @NotNull Map<Zenchantment, Integer> enchantsToAdd) {
            return this.addAll(enchantsToAdd.entrySet());
        }

        @Contract(mutates = "this")
        public boolean addAll(final @NotNull Collection<Entry<Zenchantment, Integer>> enchantsToAdd) {
            boolean anyAdded = false;
            for (final Entry<Zenchantment, Integer> enchantEntry : enchantsToAdd) {
                anyAdded |= this.addEnchant(enchantEntry);
            }
            return anyAdded;
        }

        @Contract(mutates = "this")
        private boolean addEnchant(final @NotNull Entry<Zenchantment, Integer> enchantEntry) {
            final Zenchantment zenchantment = enchantEntry.getKey();

            if (this.itemStack.getType() != ENCHANTED_BOOK && !zenchantment.isValidMaterial(this.itemStack)) {
                return false;
            }

            for (final Zenchantment enchantment : this.enchantmentPool.keySet()) {
                if (zenchantment.getConflicting().contains(enchantment.getClass())) {
                    return false;
                }
            }

            if (this.enchantmentPool.containsKey(zenchantment)) {
                final int leftLevel = this.enchantmentPool.get(zenchantment);
                final int rightLevel = enchantEntry.getValue();

                if (leftLevel == rightLevel && leftLevel < zenchantment.getMaxLevel()) {
                    this.enchantmentPool.put(zenchantment, leftLevel + 1);
                    return true;
                } else if (rightLevel > leftLevel) {
                    this.enchantmentPool.put(zenchantment, rightLevel);
                    return true;
                }
            } else if (this.enchantmentPool.size() < this.maxCapacity) {
                this.enchantmentPool.put(zenchantment, enchantEntry.getValue());
                return true;
            }
            return false;
        }

        @NotNull
        public Map<Zenchantment, Integer> getEnchantmentMap() {
            return this.enchantmentPool;
        }
    }
}
