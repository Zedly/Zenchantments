package zedly.zenchantments;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.bukkit.Material;
import static org.bukkit.Material.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;

public class EnchantmentWatcher implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public boolean onBlockBreak(BlockBreakEvent evt) {
        if (!evt.isCancelled()) {
            if (evt.getBlock().getType() != AIR) {
                LinkedHashMap<Enchantment, Integer> map = Utilities.getEnchant(evt.getPlayer().getItemInHand());
                if (!evt.getPlayer().getItemInHand().getType().equals(Material.ENCHANTED_BOOK)) {
                    for (Enchantment ench : map.keySet()) {
                        if (Utilities.enchTF(evt.getPlayer(), ench)) {
                            ench.onBlockBreak(evt, map.get(ench));
                        }
                    }
                }
            }
        }
        return true;
    }

    @EventHandler(ignoreCancelled = false)
    public boolean onBlockInteract(PlayerInteractEvent evt) {
        LinkedHashMap<Enchantment, Integer> map = Utilities.getEnchant(evt.getPlayer().getItemInHand());
        if (!evt.getPlayer().getItemInHand().getType().equals(Material.ENCHANTED_BOOK)) {
            for (Enchantment ench : map.keySet()) {
                if (Utilities.enchTF(evt.getPlayer(), ench)) {
                    ench.onBlockInteract(evt, map.get(ench));
                }
            }
        }
        return true;
    }

    @EventHandler
    public boolean onEntityInteract(PlayerInteractEntityEvent evt) {
        LinkedHashMap<Enchantment, Integer> map = Utilities.getEnchant(evt.getPlayer().getItemInHand());
        if (!evt.getPlayer().getItemInHand().getType().equals(Material.ENCHANTED_BOOK)) {
            for (Enchantment ench : map.keySet()) {
                if (Utilities.enchTF(evt.getPlayer(), ench)) {
                    ench.onEntityInteract(evt, map.get(ench));
                }
            }
        }
        return true;
    }

    @EventHandler
    public boolean onEntityKill(EntityDeathEvent evt) {
        if (evt.getEntity() != null && evt.getEntity().getKiller() != null) {
            if (evt.getEntity().getKiller() instanceof Player) {
                LinkedHashMap<Enchantment, Integer> map = Utilities.getEnchant(evt.getEntity().getKiller().getItemInHand());
                if (!evt.getEntity().getKiller().getItemInHand().getType().equals(Material.ENCHANTED_BOOK)) {
                    for (Enchantment ench : map.keySet()) {
                        if (Utilities.enchTF((Player) evt.getEntity().getKiller(), ench)) {
                            ench.onEntityKill(evt, map.get(ench));
                        }
                    }
                }
            }
        }
        return true;
    }

    @EventHandler
    public boolean onEntityHit(EntityDamageByEntityEvent evt) {
        if (evt.getDamager() != null) {
            if (evt.getDamager() instanceof Player) {
                if (evt.getEntity() instanceof LivingEntity) {
                    for (ItemStack stk : Utilities.getRelevant(((Player) evt.getDamager()))) {
                        LinkedHashMap<Enchantment, Integer> map = Utilities.getEnchant(stk);
                        if (!stk.getType().equals(Material.ENCHANTED_BOOK)) {
                            for (Enchantment ench : map.keySet()) {
                                if (Utilities.enchTF((Player) evt.getDamager(), ench)) {
                                    ench.onHitting(evt, map.get(ench));
                                }
                            }
                        }
                    }
                }
            }
        }
        if (evt.getEntity() != null) {
            if (evt.getEntity() instanceof Player) {
                for (ItemStack stk : Utilities.getRelevant(((Player) evt.getEntity()))) {
                    LinkedHashMap<Enchantment, Integer> map = Utilities.getEnchant(stk);
                    if (!stk.getType().equals(Material.ENCHANTED_BOOK)) {
                        for (Enchantment ench : map.keySet()) {
                            if (Utilities.enchTF((Player) evt.getEntity(), ench)) {
                                ench.onBeingHit(evt, map.get(ench));
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
        if (evt.getEntity() != null) {
            if (evt.getEntity() instanceof Player) {
                for (ItemStack stk : Utilities.getRelevant(((Player) evt.getEntity()))) {
                    LinkedHashMap<Enchantment, Integer> map = Utilities.getEnchant(stk);
                    if (!stk.getType().equals(Material.ENCHANTED_BOOK)) {
                        for (Enchantment ench : map.keySet()) {
                            if (Utilities.enchTF((Player) evt.getEntity(), ench)) {
                                ench.onEntityDamage(evt, map.get(ench));
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
        if (evt.getPlayer() != null) {
            LinkedHashMap<Enchantment, Integer> map = Utilities.getEnchant(evt.getPlayer().getItemInHand());
            if (!evt.getPlayer().getItemInHand().getType().equals(Material.ENCHANTED_BOOK)) {
                for (Enchantment ench : map.keySet()) {
                    if (Utilities.enchTF(evt.getPlayer(), ench)) {
                        ench.onPlayerFish(evt, map.get(ench));
                    }
                }
            }
        }
        return true;
    }

    @EventHandler
    public boolean onHungerChange(FoodLevelChangeEvent evt) {
        if (evt.getEntity() instanceof Player) {
            for (ItemStack stk : Utilities.getRelevant((Player) evt.getEntity())) {
                LinkedHashMap<Enchantment, Integer> map = Utilities.getEnchant(stk);
                if (!stk.getType().equals(Material.ENCHANTED_BOOK)) {
                    for (Enchantment ench : map.keySet()) {
                        if (Utilities.enchTF((Player) evt.getEntity(), ench)) {
                            ench.onHungerChange(evt, map.get(ench));
                        }
                    }
                }
            }
        }
        return true;
    }

    @EventHandler
    public boolean onShear(PlayerShearEntityEvent evt) {
        if (evt.getPlayer() != null && evt.getEntity() != null) {
            LinkedHashMap<Enchantment, Integer> map = Utilities.getEnchant(evt.getPlayer().getItemInHand());
            if (!evt.getPlayer().getItemInHand().getType().equals(Material.ENCHANTED_BOOK)) {
                for (Enchantment ench : map.keySet()) {
                    if (Utilities.enchTF(evt.getPlayer(), ench)) {
                        ench.onShear(evt, map.get(ench));
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
                LinkedHashMap<Enchantment, Integer> map = Utilities.getEnchant(((Player) evt.getEntity().getShooter()).getItemInHand());
                if (!((Player) evt.getEntity().getShooter()).getItemInHand().getType().equals(Material.ENCHANTED_BOOK)) {
                    for (Enchantment ench : map.keySet()) {
                        if (Utilities.enchTF((Player) evt.getEntity().getShooter(), ench)) {
                            ench.onProjectileHit(evt, map.get(ench));
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
            LinkedHashMap<Enchantment, Integer> map = Utilities.getEnchant(((Player) evt.getEntity()).getItemInHand());
            if (!((Player) evt.getEntity()).getItemInHand().getType().equals(Material.ENCHANTED_BOOK)) {
                for (Enchantment ench : map.keySet()) {
                    if (Utilities.enchTF((Player) evt.getEntity(), ench)) {
                        ench.onEntityShootBow(evt, map.get(ench));
                    }
                }
            }
        }
        return true;
    }

    @EventHandler
    public boolean onPotionSplash(PotionSplashEvent evt) {
        Collection<LivingEntity> affected = evt.getAffectedEntities();
        for (LivingEntity ent : affected) {
            if (ent instanceof Player) {
                int test = 0;
                for (ItemStack stk : Utilities.getRelevant(((Player) ent))) {
                    if (test == 0) {
                        LinkedHashMap<Enchantment, Integer> map = Utilities.getEnchant(stk);
                        if (!stk.getType().equals(Material.ENCHANTED_BOOK)) {
                            for (Enchantment ench : map.keySet()) {
                                ench.onPotionSplash(evt, map.get(ench));
                                test++;
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
        if (evt.getEntity() != null) {
            if (evt.getEntity().getShooter() != null) {
                if (evt.getEntity().getShooter() instanceof Player) {
                    LinkedHashMap<Enchantment, Integer> map = Utilities.getEnchant(((Player) evt.getEntity().getShooter()).getItemInHand());
                    if (!((Player) evt.getEntity().getShooter()).getItemInHand().getType().equals(Material.ENCHANTED_BOOK)) {
                        for (Enchantment ench : map.keySet()) {
                            if (Utilities.enchTF((Player) evt.getEntity().getShooter(), ench)) {
                                ench.onProjectileLaunch(evt, map.get(ench));
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
