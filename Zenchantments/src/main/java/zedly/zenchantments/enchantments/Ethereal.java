package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.Config;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Tool;
import zedly.zenchantments.Utilities;

import java.util.*;

import static zedly.zenchantments.Tool.ALL;

public class Ethereal extends CustomEnchantment {

    public Ethereal() {
        maxLevel = 1;
        loreName = "Ethereal";
        probability = 0;
        enchantable = new Tool[]{ALL};
        conflicting = new Class[]{};
        description = "Prevents tools from breaking";
        cooldown = 0;
        power = -1.0;
        handUse = 0;
    }

    public int getEnchantmentId() {
        return 70;
    }

    @Override
    public boolean onScanHands(Player player, int level, boolean usedHand) {
        ItemStack stk = Utilities.usedStack(player, usedHand);
        int dura = stk.getDurability();
        stk.setDurability((short) 0);
        if(dura != 0) {
            if(usedHand) {
                player.getInventory().setItemInMainHand(stk);
            } else {
                player.getInventory().setItemInOffHand(stk);
            }
            player.updateInventory();
        }
        return dura != 0;
    }

    @Override
    public boolean onScan(Player player, int level, boolean usedHand) {
        for(ItemStack s : player.getInventory().getArmorContents()) {
            if(s != null) {
                Map<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(s);
                if(map.containsKey(zedly.zenchantments.enchantments.Ethereal.this)) {
                    s.setDurability((short) 0);
                }
            }
        }
        return true;
    }
}
