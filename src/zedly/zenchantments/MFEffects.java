package zedly.zenchantments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import static org.bukkit.Material.*;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MFEffects implements Runnable {

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Config config = Config.get(player.getWorld());
            for (ItemStack stk : (ItemStack[]) ArrayUtils.addAll(player.getInventory().getArmorContents(), player.getInventory().getContents())) {
                if (config.descriptionLore()) {
                    config.addDescriptions(stk, null);
                } else {
                    config.removeDescriptions(stk, null);
                }
            }
            for (ItemStack stk : player.getInventory().getArmorContents()) {
                if (stk != null) {
                    HashMap<CustomEnchantment, Integer> map = config.getEnchants(stk);
                    for (CustomEnchantment ench : map.keySet()) {
                        ench.onScan(player, map.get(ench));
                    }
                }
            }
            if (player.getItemInHand() != null) {
                HashMap<CustomEnchantment, Integer> map = config.getEnchants(player.getItemInHand());
                for (CustomEnchantment ench : map.keySet()) {
                    ench.onScanHand(player, map.get(ench));
                }
            }
        }
        Iterator iceIT = Storage.waterLocs.keySet().iterator();
        while (iceIT.hasNext()) {
            Location location = (Location) iceIT.next();
            if (Math.abs(System.nanoTime() - Storage.waterLocs.get(location)) > 9E8) {
                location.getBlock().setType(STATIONARY_WATER);
                iceIT.remove();
            }
        }
        Iterator fireIT = Storage.fireLocs.keySet().iterator();
        while (fireIT.hasNext()) {
            Location location = (Location) fireIT.next();
            if (Math.abs(System.nanoTime() - Storage.fireLocs.get(location)) > 9E8) {
                location.getBlock().setType(STATIONARY_LAVA);
                fireIT.remove();
            }
        }
        Iterator speedIt = Storage.speed.iterator();
        while (speedIt.hasNext()) {
            Player player = (Player) speedIt.next();
            Config world = Config.get(player.getWorld());
            if (!player.isOnline()) {
                speedIt.remove();
                continue;
            }
            boolean check = false;
            for (ItemStack stk : player.getInventory().getArmorContents()) {
                HashMap<CustomEnchantment, Integer> map = world.getEnchants(stk);
                Class[] enchs = new Class[]{CustomEnchantment.Weight.class, CustomEnchantment.Speed.class, CustomEnchantment.Meador.class};
                for (CustomEnchantment ench : map.keySet()) {
                    if (ArrayUtils.contains(enchs, ench.getClass())) {
                        check = true;
                    }
                }
            }
            if (!check) {
                player.setFlySpeed(.1f);
                player.setWalkSpeed(.2f);
                speedIt.remove();
                break;
            }
        }
        ArrayList<FallingBlock> toKill = new ArrayList<>();
        for (FallingBlock b : Storage.anthMobs.keySet()) {
            if (b.isDead()) {
                toKill.add(b);
            }
        }
        ArrayList<FallingBlock> toKill2 = new ArrayList<>();
        for (FallingBlock b : Storage.anthMobs2) {
            if (b.isDead()) {
                toKill2.add(b);
            }
        }
        Storage.anthMobs2.removeAll(toKill2);
        for (FallingBlock b : toKill) {
            Storage.anthMobs.remove(b);
        }
    }
}
