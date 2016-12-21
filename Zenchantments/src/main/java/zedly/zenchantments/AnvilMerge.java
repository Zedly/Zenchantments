package zedly.zenchantments;

import java.util.*;
import java.util.Map.Entry;
import org.bukkit.ChatColor;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import static org.bukkit.event.EventPriority.MONITOR;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.Material;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

// This class manages the combination of enchantments in an anvil. It takes into account conflicting enchantments, 
//      the max number of enchantments per tool, and the enchantment's max level. It shuffles the results every time
//      so that the player can find the combination they desire when there are conficting or too many enchantment
public class AnvilMerge implements Listener {

    public ItemStack doMerge(ItemStack leftItem, ItemStack rightItem, ItemStack oldOutItem, Config config) {
        if (leftItem == null || rightItem == null || oldOutItem == null) {
            return null;
        }
        if (leftItem.getType() == Material.AIR || rightItem.getType() == Material.AIR || oldOutItem.getType() == Material.AIR) {
            return null;
        }
        if (!oldOutItem.hasItemMeta()) {
            return null;
        }

        Map<CustomEnchantment, Integer> leftEnchantments = config.getEnchants(leftItem, true);
        Map<CustomEnchantment, Integer> rightEnchantments = config.getEnchants(rightItem, true);

        for (CustomEnchantment e : leftEnchantments.keySet()) {
            if (e.getClass().equals(CustomEnchantment.Unrepairable.class)) {
                return new ItemStack(Material.AIR);
            }
        }
        for (CustomEnchantment e : rightEnchantments.keySet()) {
            if (e.getClass().equals(CustomEnchantment.Unrepairable.class)) {
                return new ItemStack(Material.AIR);
            }
        }

        EnchantmentPool pool = new EnchantmentPool(oldOutItem, config.getMaxEnchants());
        pool.addAll(leftEnchantments);
        List<Entry<CustomEnchantment, Integer>> rightEnchantmentList = new ArrayList<>();
        rightEnchantmentList.addAll(rightEnchantments.entrySet());
        Collections.shuffle(rightEnchantmentList);
        pool.addAll(rightEnchantmentList);
        HashMap<CustomEnchantment, Integer> outEnchantments = pool.getEnchantmentMap();

        ItemStack newOutItem = new ItemStack(oldOutItem);
        ItemMeta newOutMeta = newOutItem.getItemMeta();
        List<String> outLore = new ArrayList<>();

        for (Entry<CustomEnchantment, Integer> enchantEntry : outEnchantments.entrySet()) {
            outLore.add(ChatColor.GRAY + enchantEntry.getKey().loreName + " " + Utilities.getRomanString(enchantEntry.getValue()));
        }
        // TODO: Preserve passive lore

        newOutMeta.setLore(outLore);
        newOutItem.setItemMeta(newOutMeta);
        return config.descriptionLore() ? config.addDescriptions(newOutItem, null) : newOutItem;
    }

    @EventHandler(priority = MONITOR)
    public void onClicks(final PrepareAnvilEvent evt) {
        if (evt.getViewers().size() < 1) {
            return;
        }
        final Config config = Config.get(evt.getViewers().get(0).getWorld());
        final AnvilInventory anvilInv = (AnvilInventory) evt.getInventory();
        Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
            ItemStack leftItem = anvilInv.getItem(0);
            ItemStack rightItem = anvilInv.getItem(1);
            ItemStack outItem = anvilInv.getItem(2);
            ItemStack stack = doMerge(leftItem, rightItem, outItem, config);
            if (stack != null) {
                anvilInv.setItem(2, stack);
            }
        }, 0);
    }

    //@EventHandler(priority = MONITOR) // Disabled because unnecessary now?
    public void onClicks(final InventoryClickEvent evt) {
        final Config config = Config.get(evt.getWhoClicked().getWorld());
        if (evt.getInventory().getType() == InventoryType.ANVIL) {
            final AnvilInventory anvilInv = (AnvilInventory) evt.getInventory();
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
                if (!evt.isCancelled()) {
                    ItemStack leftItem = anvilInv.getItem(0);
                    ItemStack rightItem = anvilInv.getItem(1);
                    ItemStack outItem = anvilInv.getItem(2);
                    ItemStack stack = doMerge(leftItem, rightItem, outItem, config);
                    evt.getView().setItem(2, stack);
                    ((Player) evt.getWhoClicked()).updateInventory();
                }
            }, 0);

        }
    }

    private class EnchantmentPool {

        private final HashMap<CustomEnchantment, Integer> enchantPool = new HashMap<>();
        private final ItemStack is;
        private final int maxCapacity;

        public EnchantmentPool(ItemStack is, int maxCapacity) {
            this.is = is;
            this.maxCapacity = maxCapacity;
        }

        public void addAll(Map<CustomEnchantment, Integer> enchantsToAdd) {
            addAll(enchantsToAdd.entrySet());
        }

        public void addAll(Collection<Entry<CustomEnchantment, Integer>> enchantsToAdd) {
            for (Entry<CustomEnchantment, Integer> enchantEntry : enchantsToAdd) {
                addEnchant(enchantEntry);
            }
        }

        private void addEnchant(Entry<CustomEnchantment, Integer> enchantEntry) {
            CustomEnchantment ench = enchantEntry.getKey();
            if (is.getType() != Material.ENCHANTED_BOOK && !ench.validMaterial(is)) {
                return;
            }
            for (CustomEnchantment e : enchantPool.keySet()) {
                if (ArrayUtils.contains(ench.conflicting, e.getClass())) {
                    return;
                }
            }
            if (enchantPool.containsKey(ench)) {
                int leftLevel = enchantPool.get(ench);
                int rightLevel = enchantEntry.getValue();
                if (leftLevel == rightLevel && leftLevel < ench.maxLevel) {
                    enchantPool.put(ench, leftLevel + 1);
                } else if (rightLevel > leftLevel) {
                    enchantPool.put(ench, rightLevel);
                }
            } else if (enchantPool.size() < maxCapacity) {
                enchantPool.put(ench, enchantEntry.getValue());
            }
        }

        public HashMap<CustomEnchantment, Integer> getEnchantmentMap() {
            return enchantPool;
        }
    }
}
