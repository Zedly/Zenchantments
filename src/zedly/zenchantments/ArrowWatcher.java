package zedly.zenchantments;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import static org.bukkit.Material.*;
import org.bukkit.entity.Arrow;
import org.bukkit.inventory.ItemStack;

public class ArrowWatcher implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public boolean shoot(EntityShootBowEvent evt) {
        Config config = Config.get(evt.getEntity().getWorld());
        if (evt.getEntity() instanceof Player) {
            if (evt.isCancelled()) {
                return true;
            }
            Player player = (Player) evt.getEntity();
            ItemStack arrowShot = findArrowShot(player);
            if (arrowShot != null && arrowShot.getItemMeta().hasLore()) {
                String type = ChatColor.stripColor(arrowShot.getItemMeta().getLore().get(0));
                if (config.getArrows().containsKey(type) && player.hasPermission("zenchantments.arrow.use") && !player.getItemInHand().getEnchantments().containsKey(Enchantment.ARROW_INFINITE)) {
                    CustomArrow arrow = config.getArrows().get(type);
                    arrow.entity = (Projectile) evt.getProjectile();
                    HashSet<CustomArrow> a = new HashSet<>();
                    a.add(arrow);
                    Storage.advancedProjectiles.put(evt.getProjectile(), a);
                    arrow.onLaunch(player, arrowShot.getItemMeta().getLore());
                }
            }
        }
        return true;
    }

    @EventHandler
    public boolean mine(BlockBreakEvent evt) {
        if (evt.getBlock().getType() == WEB && Storage.webs.contains(evt.getBlock())) {
            evt.setCancelled(true);
            evt.getBlock().setType(AIR);
            Storage.webs.remove(evt.getBlock());
        }
        return true;
    }

    @EventHandler
    public boolean impact(ProjectileHitEvent evt) {
        if (Storage.advancedProjectiles.containsKey(evt.getEntity())) {
            Set<CustomArrow> ar = Storage.advancedProjectiles.get(evt.getEntity());
            for (CustomArrow a : ar) {
                a.onImpact();
            }
        }
        return true;
    }

    @EventHandler
    public boolean entityHit(EntityDamageByEntityEvent evt) {
        if (evt.getDamager() instanceof Arrow) {
            if (Storage.advancedProjectiles.containsKey(evt.getDamager())) {
                Set<CustomArrow> arrows = Storage.advancedProjectiles.get(evt.getDamager());
                for (CustomArrow arrow : arrows) {
                    if (evt.getEntity() instanceof LivingEntity) {
                        if (!arrow.onImpact(evt)) {
                            evt.setDamage(0);
                        }
                    }
                    Storage.advancedProjectiles.remove(evt.getDamager());
                    if (evt.getEntity() instanceof LivingEntity && evt.getDamage() >= ((LivingEntity) evt.getEntity()).getHealth()) {
                        Storage.killedEntities.put(evt.getEntity(), arrow);
                    }
                }
            }
        }
        return true;
    }

    @EventHandler
    public boolean entityDeath(EntityDeathEvent evt) {
        if (Storage.killedEntities.containsKey(evt.getEntity())) {
            CustomArrow arrow = Storage.killedEntities.get(evt.getEntity());
            arrow.onKill(evt);
            Storage.killedEntities.remove(evt.getEntity());
        }
        return true;
    }

    @EventHandler
    public boolean charge(CreeperPowerEvent evt) {
        if (Storage.lightnings.contains(evt.getLightning())) {
            evt.setCancelled(true);
        }
        return true;
    }

    @EventHandler
    public boolean pigzap(PigZapEvent evt) {
        if (Storage.lightnings.contains(evt.getLightning())) {
            evt.setCancelled(true);
        }
        return true;
    }

    private ItemStack findArrowShot(Player player) {
        ItemStack[] inv = player.getInventory().getContents();
        for (ItemStack is : inv) {
            if (is != null && is.getType() == ARROW) {
                return is;
            }
        }
        return null;
    }
}
