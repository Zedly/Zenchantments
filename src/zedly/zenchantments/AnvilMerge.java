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
        if (!evt.getInventory().getType().equals(InventoryType.ANVIL)) {
            return;
        }
        if (evt.isCancelled()) {
            return;
        }
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
            @Override
            public void run() {
                InventoryView view = evt.getView();
                HashMap<Enchantment, Integer> result = Utilities.getEnchants(Utilities.removeDescriptions(view.getItem(0), null));
                HashMap<Enchantment, Integer> item2 = Utilities.getEnchants(Utilities.removeDescriptions(view.getItem(1), null));
                HashMap<Enchantment, Integer> temp = new HashMap<>();
                for (Enchantment e : item2.keySet()) {
                    if (result.keySet().contains(e)) {
                        int i = result.get(e);
                        int i2 = item2.get(e);
                        int i3;
                        if (i == i2 && i < e.maxLevel) {
                            i3 = i + 1;
                        } else {
                            i3 = Math.max(i, i2);
                        }
                        temp.put(e, i3);
                    } else {
                        boolean b = false;
                        if (result.keySet().isEmpty()) {
                            if (!ArrayUtils.contains(e.enchantable, view.getItem(0).getType())) {
                                b = true;
                            }
                        } else {
                            for (Enchantment e1 : result.keySet()) {
                                if (ArrayUtils.contains(e1.conflicting, Storage.originalEnchantClassesReverse.get(e))) {
                                    b = true;
                                }
                                if (!ArrayUtils.contains(e.enchantable, view.getItem(0).getType())) {
                                    b = true;
                                }
                            }
                        }
                        if (!b) {
                            temp.put(e, item2.get(e));
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
                ArrayList<Enchantment> toGo = new ArrayList<>();
                for (Enchantment e : temp.keySet()) {
                    toGo.add(e);
                }
                Collections.shuffle(toGo);
                for (Enchantment e : toGo) {
                    if (result.size() < Storage.maxEnchants) {
                        result.put(e, temp.get(e));
                    }
                }
                for (Enchantment ench : result.keySet()) {
                    s.add(ChatColor.GRAY + ench.loreName + " " + Utilities.getRomanString(result.get(ench)));
                }
                m.setLore(s);
                stk.setItemMeta(m);
                view.setItem(2, Storage.descriptions ? Utilities.addDescriptions(stk, null) : stk);
            }
        }, 1);
    }
}
