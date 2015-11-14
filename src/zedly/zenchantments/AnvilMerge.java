package zedly.zenchantments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AnvilMerge implements Listener {

    @EventHandler
    public void onClicks(final InventoryClickEvent evt) {
        final Config config = Config.get(evt.getWhoClicked().getWorld());
        if (!evt.getInventory().getType().equals(InventoryType.ANVIL) || evt.isCancelled()) {
            return;
        }
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
            @Override
            public void run() {
                InventoryView view = evt.getView();
                HashMap<CustomEnchantment, Integer> firstItem = config.getEnchants(view.getItem(0));
                HashMap<CustomEnchantment, Integer> secondItem = config.getEnchants(view.getItem(1));
                HashMap<CustomEnchantment, Integer> toAdd = new HashMap<>();
                toAdd.putAll(firstItem);
                toAdd.putAll(secondItem);
                for (CustomEnchantment e : toAdd.keySet()) {
                    if (e.getClass().equals(CustomEnchantment.Unrepairable.class)) {
                        view.setItem(2, null);
                        return;
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
                            if (!ArrayUtils.contains(e.enchantable, view.getItem(0).getType())) {
                                b = true;
                            }
                        } else {
                            for (CustomEnchantment e1 : firstItem.keySet()) {
                                if (ArrayUtils.contains(e1.conflicting, e.getClass())) {
                                    b = true;
                                }
                                if (!ArrayUtils.contains(e.enchantable, view.getItem(0).getType())) {
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
                    return;
                }
                if (stk.hasItemMeta() == false) {
                    return;
                }
                ItemMeta m = stk.getItemMeta();
                ArrayList<String> s = new ArrayList<>();
                ArrayList<CustomEnchantment> toGo = new ArrayList<>();
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
                view.setItem(2, config.descriptionLore() ? config.addDescriptions(stk, null) : stk);

            }
        }, 1);
    }
}
