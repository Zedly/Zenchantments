package zedly.zenchantments;

import java.util.*;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import static org.bukkit.Material.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class WatcherEnchant implements Listener {

    @EventHandler(ignoreCancelled = false)
    public boolean onBlockBreak(BlockBreakEvent evt) {
        Player player = evt.getPlayer();
        if (!evt.isCancelled() && evt.getBlock().getType() != AIR) {
            LinkedHashMap<CustomEnchantment, Integer> map = Config.get(evt.getBlock().getWorld()).getEnchants(player.getItemInHand());
            if (!player.getItemInHand().getType().equals(Material.ENCHANTED_BOOK)) {
                for (CustomEnchantment ench : map.keySet()) {
                    if (Utilities.canUse(player, ench.loreName)) {
                        if (ench.onBlockBreak(evt, map.get(ench))) {
                            EnchantPlayer.matchPlayer(player).setCooldown(ench.loreName, ench.cooldown);
                        }
                    }
                }
            }
        }
        return true;
    }

    @EventHandler(ignoreCancelled = false)
    public boolean onBlockInteract(PlayerInteractEvent evt) {
        Player player = evt.getPlayer();
        for (ItemStack stk : Utilities.getRelevant(player)) {
            LinkedHashMap<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(stk);
            if (!stk.getType().equals(Material.ENCHANTED_BOOK)) {
                for (CustomEnchantment ench : map.keySet()) {
                    if (Utilities.canUse(player, ench.loreName)) {
                        if (ench.onBlockInteract(evt, map.get(ench))) {
                            EnchantPlayer.matchPlayer(player).setCooldown(ench.loreName, ench.cooldown);
                        }
                    }
                }
            }
        }
        return true;
    }

    @EventHandler
    public boolean onEntityInteract(PlayerInteractEntityEvent evt) {
        Player player = evt.getPlayer();
        LinkedHashMap<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(player.getItemInHand());
        if (!player.getItemInHand().getType().equals(Material.ENCHANTED_BOOK)) {
            for (CustomEnchantment ench : map.keySet()) {
                if (Utilities.canUse(player, ench.loreName)) {
                    if (ench.onEntityInteract(evt, map.get(ench))) {
                        EnchantPlayer.matchPlayer(player).setCooldown(ench.loreName, ench.cooldown);
                    }
                }
            }
        }
        return true;
    }

    @EventHandler
    public boolean onEntityKill(EntityDeathEvent evt) {
        if (evt.getEntity().getKiller() != null) {
            if (evt.getEntity().getKiller() instanceof Player) {
                Player player = evt.getEntity().getKiller();
                LinkedHashMap<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(player.getItemInHand());
                if (!player.getItemInHand().getType().equals(Material.ENCHANTED_BOOK)) {
                    for (CustomEnchantment ench : map.keySet()) {
                        if (Utilities.canUse(player, ench.loreName)) {
                            if (ench.onEntityKill(evt, map.get(ench))) {
                                EnchantPlayer.matchPlayer(player).setCooldown(ench.loreName, ench.cooldown);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    @EventHandler
    public boolean onEntityHit(EntityDamageByEntityEvent evt) {
        if (evt.getDamager() instanceof Player) {
            Player player = (Player) evt.getDamager();
            if (evt.getEntity() instanceof LivingEntity) {
                for (ItemStack stk : Utilities.getRelevant(player)) {
                    LinkedHashMap<CustomEnchantment, Integer> map = Config.get(evt.getEntity().getWorld()).getEnchants(stk);
                    if (!stk.getType().equals(Material.ENCHANTED_BOOK)) {
                        for (CustomEnchantment ench : map.keySet()) {
                            if (Utilities.canUse(player, ench.loreName)) {
                                if (ench.onHitting(evt, map.get(ench))) {
                                    EnchantPlayer.matchPlayer(player).setCooldown(ench.loreName, ench.cooldown);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            for (ItemStack stk : Utilities.getRelevant(player)) {
                LinkedHashMap<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(stk);
                if (!stk.getType().equals(Material.ENCHANTED_BOOK)) {
                    for (CustomEnchantment ench : map.keySet()) {
                        if (Utilities.canUse(player, ench.loreName)) {
                            if (ench.onBeingHit(evt, map.get(ench))) {
                                EnchantPlayer.matchPlayer(player).setCooldown(ench.loreName, ench.cooldown);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    @EventHandler
    public boolean onEntityDamage(EntityDamageEvent evt) {
        if (evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            for (ItemStack stk : Utilities.getRelevant(player)) {
                LinkedHashMap<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(stk);
                if (!stk.getType().equals(Material.ENCHANTED_BOOK)) {
                    for (CustomEnchantment ench : map.keySet()) {
                        if (Utilities.canUse(player, ench.loreName)) {
                            if (ench.onEntityDamage(evt, map.get(ench))) {
                                EnchantPlayer.matchPlayer(player).setCooldown(ench.loreName, ench.cooldown);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    @EventHandler
    public boolean onPlayerFish(PlayerFishEvent evt) {
        Player player = evt.getPlayer();
        LinkedHashMap<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(player.getItemInHand());
        if (!player.getItemInHand().getType().equals(Material.ENCHANTED_BOOK)) {
            for (CustomEnchantment ench : map.keySet()) {
                if (Utilities.canUse(player, ench.loreName)) {
                    if (ench.onPlayerFish(evt, map.get(ench))) {
                        EnchantPlayer.matchPlayer(player).setCooldown(ench.loreName, ench.cooldown);
                    }
                }
            }
        }
        return true;
    }

    @EventHandler
    public boolean onHungerChange(FoodLevelChangeEvent evt) {
        if (evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            for (ItemStack stk : Utilities.getRelevant(player)) {
                LinkedHashMap<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(stk);
                if (!stk.getType().equals(Material.ENCHANTED_BOOK)) {
                    for (CustomEnchantment ench : map.keySet()) {
                        if (Utilities.canUse(player, ench.loreName)) {
                            if (ench.onHungerChange(evt, map.get(ench))) {
                                EnchantPlayer.matchPlayer(player).setCooldown(ench.loreName, ench.cooldown);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    @EventHandler
    public boolean onShear(PlayerShearEntityEvent evt) {
        Player player = evt.getPlayer();
        if (!evt.isCancelled()) {
            LinkedHashMap<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(player.getItemInHand());
            if (!player.getItemInHand().getType().equals(Material.ENCHANTED_BOOK)) {
                for (CustomEnchantment ench : map.keySet()) {
                    if (Utilities.canUse(player, ench.loreName)) {
                        if (ench.onShear(evt, map.get(ench))) {
                            EnchantPlayer.matchPlayer(player).setCooldown(ench.loreName, ench.cooldown);
                        }
                    }
                }
            }
        }
        return true;
    }

    @EventHandler
    public boolean onProjectileHit(ProjectileHitEvent evt) {
        if (evt.getEntity().getShooter() != null) {
            if (evt.getEntity().getShooter() instanceof Player) {
                Player player = (Player) evt.getEntity().getShooter();
                LinkedHashMap<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(player.getItemInHand());
                if (!player.getItemInHand().getType().equals(Material.ENCHANTED_BOOK)) {
                    for (CustomEnchantment ench : map.keySet()) {
                        if (Utilities.canUse(player, ench.loreName)) {
                            if (ench.onProjectileHit(evt, map.get(ench))) {
                                EnchantPlayer.matchPlayer(player).setCooldown(ench.loreName, ench.cooldown);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    @EventHandler
    public boolean onEntityShootBow(EntityShootBowEvent evt) {
        if (evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            LinkedHashMap<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(player.getItemInHand());
            if (!player.getItemInHand().getType().equals(Material.ENCHANTED_BOOK)) {
                for (CustomEnchantment ench : map.keySet()) {
                    if (Utilities.canUse(player, ench.loreName)) {
                        if (ench.onEntityShootBow(evt, map.get(ench))) {
                            EnchantPlayer.matchPlayer(player).setCooldown(ench.loreName, ench.cooldown);
                        }
                    }
                }
            }
        }
        return true;
    }

    @EventHandler
    public boolean onPotionSplash(PotionSplashEvent evt) {
        Collection<LivingEntity> affected = evt.getAffectedEntities();
        for (LivingEntity entity : affected) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                int test = 0;
                for (ItemStack stk : Utilities.getRelevant(player)) {
                    if (test == 0) {
                        LinkedHashMap<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(stk);
                        if (!stk.getType().equals(Material.ENCHANTED_BOOK)) {
                            for (CustomEnchantment ench : map.keySet()) {
                                if (Utilities.canUse(player, ench.loreName)) {
                                    if (ench.onPotionSplash(evt, map.get(ench))) {
                                        test++;
                                    }
                                    EnchantPlayer.matchPlayer(player).setCooldown(ench.loreName, ench.cooldown);
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    @EventHandler
    public boolean onProjectileLaunch(ProjectileLaunchEvent evt) {
        if (evt.getEntity().getShooter() != null && evt.getEntity().getShooter() instanceof Player) {
            Player player = (Player) evt.getEntity().getShooter();
            LinkedHashMap<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(player.getItemInHand());
            if (!player.getItemInHand().getType().equals(Material.ENCHANTED_BOOK)) {
                for (CustomEnchantment ench : map.keySet()) {
                    if (Utilities.canUse(player, ench.loreName)) {
                        if (ench.onProjectileLaunch(evt, map.get(ench))) {
                            EnchantPlayer.matchPlayer(player).setCooldown(ench.loreName, ench.cooldown);
                        }
                    }
                }
            }
        }
        return true;
    }

    @EventHandler
    public boolean onDeath(PlayerDeathEvent evt) {
        Player player = (Player) evt.getEntity();
        for (ItemStack stk : ArrayUtils.addAll(player.getInventory().getArmorContents(), player.getInventory().getContents())) {
            LinkedHashMap<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(stk);
            if (stk != null) {
                if (!stk.getType().equals(Material.ENCHANTED_BOOK)) {
                    for (CustomEnchantment ench : map.keySet()) {
                        if (Utilities.canUse(player, ench.loreName)) {
                            if (ench.onPlayerDeath(evt, map.get(ench))) {
                                EnchantPlayer.matchPlayer(player).setCooldown(ench.loreName, ench.cooldown);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    @EventHandler
    public boolean onMove(PlayerMoveEvent evt) {
        Player player = evt.getPlayer();
        for (ItemStack stk : ArrayUtils.addAll(player.getInventory().getArmorContents())) {
            LinkedHashMap<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(stk);
            if (stk != null) {
                if (!stk.getType().equals(Material.ENCHANTED_BOOK)) {
                    for (CustomEnchantment ench : map.keySet()) {
                        if (Utilities.canUse(player, ench.loreName)) {
                            if (ench.onMove(evt, map.get(ench))) {
                                EnchantPlayer.matchPlayer(player).setCooldown(ench.loreName, ench.cooldown);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
