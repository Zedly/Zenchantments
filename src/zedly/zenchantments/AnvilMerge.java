package zedly.zenchantments;

import java.util.*;
import org.bukkit.ChatColor;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import static org.bukkit.Material.AIR;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import static org.bukkit.event.EventPriority.MONITOR;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

// This class manages the combination of enchantments in an anvil. It takes into account conflicting enchantments, 
//      the max number of enchantments per tool, and the enchantment's max level. It shuffles the results every time
//      so that the player can find the combination they desire when there are conficting or too many enchantment
public class AnvilMerge implements Listener {

    public ItemStack getMerge(World world, boolean cancelled, Config config, InventoryView view) {
        if (!cancelled) {
            Map<CustomEnchantment, Integer> firstItem = config.getEnchants(view.getItem(0));
            Map<CustomEnchantment, Integer> secondItem = config.getEnchants(view.getItem(1));
            Map<CustomEnchantment, Integer> toAdd = new HashMap<>();
            toAdd.putAll(firstItem);
            toAdd.putAll(secondItem);
            for (CustomEnchantment e : toAdd.keySet()) {
                if (e.getClass().equals(CustomEnchantment.Unrepairable.class)) {
                    view.setItem(2, null);
                    return null;
                }
            }
            toAdd.clear();
            for (CustomEnchantment e : secondItem.keySet()) {
                if (firstItem.keySet().contains(e)) {
                    int i = firstItem.get(e);
                    int i2 = secondItem.get(e);
                    int i3;
                    if (i == i2 && i < e.maxLevel) {
                        i3 = i + 1;
                    } else {
                        i3 = Math.max(i, i2);
                    }
                    toAdd.put(e, i3);
                } else {
                    boolean b = false;
                    if (firstItem.keySet().isEmpty()) {
                        if (!e.validMaterial(view.getItem(0))) {
                            b = true;
                        }
                    } else {
                        for (CustomEnchantment e1 : firstItem.keySet()) {
                            if (ArrayUtils.contains(e1.conflicting, e.getClass())) {
                                b = true;
                            }
                            if (!e.validMaterial(view.getItem(0))) {
                                b = true;
                            }
                        }
                    }
                    if (!b) {
                        toAdd.put(e, secondItem.get(e));
                    }
                }
            }
            ItemStack stk = view.getItem(2);
            if (stk == null) {
                return new ItemStack(AIR);
            }
            if (!stk.hasItemMeta()) {
                return new ItemStack(AIR);
            }
            ItemMeta m = stk.getItemMeta();
            List<String> s = new ArrayList<>();
            List<CustomEnchantment> toGo = new ArrayList<>();
            for (CustomEnchantment e : toAdd.keySet()) {
                toGo.add(e);
            }
            Collections.shuffle(toGo);
            for (CustomEnchantment e : toGo) {
                if (firstItem.size() < config.getMaxEnchants()) {
                    firstItem.put(e, toAdd.get(e));
                }
            }
            for (CustomEnchantment ench : firstItem.keySet()) {
                s.add(ChatColor.GRAY + ench.loreName + " " + Utilities.getRomanString(firstItem.get(ench)));
            }
            m.setLore(s);
            stk.setItemMeta(m);
            return config.descriptionLore() ? config.addDescriptions(stk, null) : stk;
            
        }
        return new ItemStack(AIR);
    }

    @EventHandler(priority = MONITOR)
    public void onClicks(final PrepareAnvilEvent evt) {
        if (evt.getViewers().size() < 1) {
            return;
        }
        final Config config = Config.get(evt.getViewers().get(0).getWorld());
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
            public void run() {
                ItemStack stack = getMerge(config.getWorld(), false, config, evt.getView());
                if (stack.getType() != AIR) {
                    evt.setResult(stack);
                }
            }
        }, 0);
    }

    @EventHandler(priority = MONITOR)
    public void onClicks(final InventoryClickEvent evt) {
        final Config config = Config.get(evt.getWhoClicked().getWorld());
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
            public void run() {

                boolean cancelled = !evt.getInventory().getType().equals(InventoryType.ANVIL) || evt.isCancelled();
                ItemStack stack = getMerge(config.getWorld(), cancelled, config, evt.getView());
                evt.getView().setItem(2, stack);
                ((Player) evt.getWhoClicked()).updateInventory();
            }
        }, 0);
    }
}
