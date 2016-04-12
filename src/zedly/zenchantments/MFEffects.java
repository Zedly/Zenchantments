package zedly.zenchantments;

import java.util.*;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.*;
import static org.bukkit.Material.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import static org.bukkit.potion.PotionEffectType.*;

public class MFEffects implements Runnable {
    
    public void run() {
        anthropomorphism();
        scanPlayers();
        speedPlayers();
        updateBlocks();
        
    }

    // Removes Anthropomorphism blocks when they are dead
    private void anthropomorphism() {
        Iterator it = Storage.idleBlocks.keySet().iterator();
        while (it.hasNext()) {
            FallingBlock b = (FallingBlock) it.next();
            if (b.isDead()) {
                it.remove();
            }
        }
        it = Storage.attackBlocks.keySet().iterator();
        while (it.hasNext()) {
            FallingBlock b = (FallingBlock) it.next();
            if (b.isDead()) {
                it.remove();
            }
        }
    }

    // Scan of Player's Armor and their hand to register enchantments & make enchantment descriptions
    private void scanPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasMetadata("ze.haste")) {
                boolean has = false;
                for (CustomEnchantment e : Config.get(player.getWorld()).getEnchants(player.getItemInHand()).keySet()) {
                    if (e.getClass().equals(CustomEnchantment.Haste.class)) {
                        has = true;
                    }
                }
                if (!has) {
                    player.removePotionEffect(FAST_DIGGING);
                    player.removeMetadata("ze.haste", Storage.zenchantments);
                }
            }
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
                    Map<CustomEnchantment, Integer> map = config.getEnchants(stk);
                    for (CustomEnchantment ench : map.keySet()) {
                        ench.onScan(player, map.get(ench));
                    }
                }
            }
            if (player.getItemInHand() != null) {
                Map<CustomEnchantment, Integer> map = config.getEnchants(player.getItemInHand());
                for (CustomEnchantment ench : map.keySet()) {
                    ench.onScanHand(player, map.get(ench));
                }
            }
        }
    }

    // Sets player fly and walk speed to default after certain enchantments are removed
    private void speedPlayers() {
        Iterator it = Storage.speed.iterator();
        while (it.hasNext()) {
            Player player = (Player) it.next();
            Config world = Config.get(player.getWorld());
            if (!player.isOnline()) {
                it.remove();
                continue;
            }
            boolean check = false;
            for (ItemStack stk : player.getInventory().getArmorContents()) {
                Map<CustomEnchantment, Integer> map = world.getEnchants(stk);
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
                it.remove();
                break;
            }
        }
    }

    // Removes the blocks from NetherStep and FrozenStep after a peroid of time
    private void updateBlocks() {
        Iterator it = Storage.waterLocs.keySet().iterator();
        while (it.hasNext()) {
            Location location = (Location) it.next();
            if (Math.abs(System.nanoTime() - Storage.waterLocs.get(location)) > 9E8) {
                location.getBlock().setType(STATIONARY_WATER);
                it.remove();
            }
        }
        it = Storage.fireLocs.keySet().iterator();
        while (it.hasNext()) {
            Location location = (Location) it.next();
            if (Math.abs(System.nanoTime() - Storage.fireLocs.get(location)) > 9E8) {
                location.getBlock().setType(STATIONARY_LAVA);
                it.remove();
            }
        }
    }
    
}
