package zedly.zenchantments.event.listener.merge;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import zedly.zenchantments.Config;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.enchantments.Unrepairable;

import java.util.*;
import java.util.Map.Entry;

import static org.bukkit.Material.ENCHANTED_BOOK;
import static org.bukkit.event.EventPriority.MONITOR;

// This class manages the combination of enchantments in an anvil. It takes into account conflicting enchantments, 
//      the max number of enchantments per tool, and the enchantment's max level. It shuffles the results every time
//      so that the player can find the combination they desire when there are conflicting or too many enchantment
public class AnvilMergeListener implements Listener {
    private final ZenchantmentsPlugin plugin;

    public AnvilMergeListener(ZenchantmentsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = MONITOR)
    private void onClick(final InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.ANVIL || !event.getClick().isLeftClick()) {
            return;
        }

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() != ENCHANTED_BOOK) {
            return;
        }

        EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) event.getCurrentItem().getItemMeta();

        if (bookMeta.getStoredEnchants().containsKey(Enchantment.DURABILITY)
            && bookMeta.getStoredEnchants().get(Enchantment.DURABILITY) == 0
        ) {
            bookMeta.removeStoredEnchant(Enchantment.DURABILITY);
            event.getCurrentItem().setItemMeta(bookMeta);
        }
    }

    @EventHandler(priority = MONITOR)
    private void onClick(final PrepareAnvilEvent evt) {
        if (evt.getViewers().size() < 1) {
            return;
        }

        Config config = Config.get(evt.getViewers().get(0).getWorld());
        AnvilInventory anvilInv = evt.getInventory();

        ItemStack item0 = anvilInv.getItem(0);
        if (item0 != null && item0.getType() == ENCHANTED_BOOK) {
            EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) item0.getItemMeta();

            if (!bookMeta.getStoredEnchants().containsKey(Enchantment.DURABILITY)) {
                bookMeta.addStoredEnchant(Enchantment.DURABILITY, 0, true);
                item0.setItemMeta(bookMeta);
            }
        }

        ItemStack item1 = anvilInv.getItem(1);
        if (item1 != null && item1.getType() == ENCHANTED_BOOK) {
            EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) item1.getItemMeta();

            if (!bookMeta.getStoredEnchants().containsKey(Enchantment.DURABILITY)) {
                bookMeta.addStoredEnchant(Enchantment.DURABILITY, 0, true);
                item1.setItemMeta(bookMeta);
            }
        }

        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            ItemStack stack = this.doMerge(item0, item1, anvilInv.getItem(2), config);

            if (stack != null) {
                anvilInv.setItem(2, stack);
            }
        }, 0);
    }

    private ItemStack doMerge(ItemStack leftItem, ItemStack rightItem, ItemStack oldOutItem, Config config) {
        if (leftItem == null || rightItem == null || oldOutItem == null) {
            return null;
        }

        if (leftItem.getType() == Material.AIR
            || rightItem.getType() == Material.AIR
            || oldOutItem.getType() == Material.AIR
        ) {
            return null;
        }

        if (!oldOutItem.hasItemMeta()) {
            return null;
        }

        List<String> normalLeftLore = new ArrayList<>();
        Map<Zenchantment, Integer> leftEnchantments = Zenchantment.getEnchants(
            leftItem,
            true,
            config.getWorld(),
            normalLeftLore
        );
        Map<Zenchantment, Integer> rightEnchantments = Zenchantment.getEnchants(
            rightItem,
            true,
            config.getWorld()
        );

        boolean isBookLeft = leftItem.getType() == Material.ENCHANTED_BOOK;
        boolean isBookRight = rightItem.getType() == Material.ENCHANTED_BOOK;

        Map<Enchantment, Integer> leftEnch = isBookLeft
            ? ((EnchantmentStorageMeta) leftItem.getItemMeta()).getStoredEnchants()
            : leftItem.getEnchantments();
        Map<Enchantment, Integer> rightEnch = isBookRight
            ? ((EnchantmentStorageMeta) rightItem.getItemMeta()).getStoredEnchants()
            : rightItem.getEnchantments();

        int leftUnbreakingLevel = leftEnch.getOrDefault(Enchantment.DURABILITY, -1);
        int rightUnbreakingLevel = rightEnch.getOrDefault(Enchantment.DURABILITY, -1);

        for (Zenchantment enchantment : leftEnchantments.keySet()) {
            if (enchantment.getId() == Unrepairable.ID) {
                return new ItemStack(Material.AIR);
            }
        }

        for (Zenchantment enchantment : rightEnchantments.keySet()) {
            if (enchantment.getId() == Unrepairable.ID) {
                return new ItemStack(Material.AIR);
            }
        }

        if (leftEnchantments.isEmpty() && rightEnchantments.isEmpty()) {
            return oldOutItem;
        }

        EnchantmentPool pool = new EnchantmentPool(oldOutItem, config.getMaxEnchants());
        pool.addAll(leftEnchantments);

        List<Entry<Zenchantment, Integer>> rightEnchantmentList = new ArrayList<>(rightEnchantments.entrySet());
        Collections.shuffle(rightEnchantmentList);
        pool.addAll(rightEnchantmentList);

        Map<Zenchantment, Integer> outEnchantments = pool.getEnchantmentMap();

        ItemStack newOutItem = new ItemStack(oldOutItem);

        // Remove meta from item.
        ItemMeta meta = oldOutItem.getItemMeta();
        meta.setLore(null);
        newOutItem.setItemMeta(meta);

        for (Entry<Zenchantment, Integer> enchantEntry : outEnchantments.entrySet()) {
            enchantEntry.getKey().setEnchantment(newOutItem, enchantEntry.getValue(), config.getWorld());
        }

        ItemMeta newOutMeta = newOutItem.getItemMeta();
        List<String> outLore = newOutMeta.hasLore() ? newOutMeta.getLore() : new ArrayList<>();
        outLore.addAll(normalLeftLore);

        if (leftUnbreakingLevel * rightUnbreakingLevel == 0
            && leftUnbreakingLevel < 1
            && rightUnbreakingLevel < 1
        ) {
            if (oldOutItem.getType() == ENCHANTED_BOOK) {
                ((EnchantmentStorageMeta) newOutMeta).removeStoredEnchant(Enchantment.DURABILITY);
            } else {
                newOutMeta.removeEnchant(Enchantment.DURABILITY);
            }

            newOutMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        newOutMeta.setLore(outLore);
        newOutItem.setItemMeta(newOutMeta);

        Zenchantment.setGlow(newOutItem, !outEnchantments.isEmpty(), config.getWorld());

        return newOutItem;
    }

    private static class EnchantmentPool {
        private final Map<Zenchantment, Integer> enchantPool = new HashMap<>();
        private final ItemStack                  itemStack;
        private final int                        maxCapacity;

        public EnchantmentPool(ItemStack itemStack, int maxCapacity) {
            this.itemStack = itemStack;
            this.maxCapacity = maxCapacity;
        }

        public void addAll(Map<Zenchantment, Integer> enchantsToAdd) {
            this.addAll(enchantsToAdd.entrySet());
        }

        public void addAll(Collection<Entry<Zenchantment, Integer>> enchantsToAdd) {
            for (Entry<Zenchantment, Integer> enchantEntry : enchantsToAdd) {
                this.addEnchant(enchantEntry);
            }
        }

        private void addEnchant(Entry<Zenchantment, Integer> enchantEntry) {
            Zenchantment ench = enchantEntry.getKey();

            if (this.itemStack.getType() != Material.ENCHANTED_BOOK && !ench.validMaterial(this.itemStack)) {
                return;
            }

            for (Zenchantment enchantment : this.enchantPool.keySet()) {
                if (ArrayUtils.contains(ench.getConflicting(), enchantment.getClass())) {
                    return;
                }
            }

            if (this.enchantPool.containsKey(ench)) {
                int leftLevel = this.enchantPool.get(ench);
                int rightLevel = enchantEntry.getValue();
                if (leftLevel == rightLevel && leftLevel < ench.getMaxLevel()) {
                    this.enchantPool.put(ench, leftLevel + 1);
                } else if (rightLevel > leftLevel) {
                    this.enchantPool.put(ench, rightLevel);
                }
            } else if (this.enchantPool.size() < this.maxCapacity) {
                this.enchantPool.put(ench, enchantEntry.getValue());
            }
        }

        public Map<Zenchantment, Integer> getEnchantmentMap() {
            return this.enchantPool;
        }
    }
}