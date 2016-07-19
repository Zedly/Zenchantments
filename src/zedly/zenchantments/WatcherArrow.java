package zedly.zenchantments;

import java.util.*;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import static org.bukkit.Material.*;
import org.bukkit.inventory.ItemStack;

// This is the watcher used by both the EnchantArrow and Elemental Arrow classes. Each method checks for certain events
// and conditions and will call the relevant methods defined in the AdvancedArrow interface
//      
public class WatcherArrow implements Listener {

    // Called when a player shoots a bow. This creates AdvancedArrows if the bow or arrows used call for this.
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
                    ElementalArrow arrow = (ElementalArrow) Utilities.construct(config.getArrows().get(type).getClass(), (Projectile) evt.getProjectile());
                    Set<AdvancedArrow> a = new HashSet<>();
                    a.add(arrow);
                    Storage.advancedProjectiles.put(evt.getProjectile(), a);
                    arrow.onLaunch(player, arrowShot.getItemMeta().getLore());
                }
            }
        }
        return true;
    }

    // Called when a player tries to break a block created by web arrows
    @EventHandler
    public boolean mine(BlockBreakEvent evt) {
        if (evt.getBlock().getType() == WEB && Storage.webs.contains(evt.getBlock())) {
            evt.setCancelled(true);
            evt.getBlock().setType(AIR);
            Storage.webs.remove(evt.getBlock());
        }
        return true;
    }

    // Called when an arrow hits a block
    @EventHandler
    public boolean impact(ProjectileHitEvent evt) {
        if (Storage.advancedProjectiles.containsKey(evt.getEntity())) {
            Set<AdvancedArrow> ar = Storage.advancedProjectiles.get(evt.getEntity());
            for (AdvancedArrow a : ar) {
                a.onImpact();
            }
        }
        return true;
    }

    // Called when an arrow hits an entity
    @EventHandler
    public boolean entityHit(EntityDamageByEntityEvent evt) {
        if (evt.getDamager() instanceof Arrow) {
            if (Storage.advancedProjectiles.containsKey(evt.getDamager())) {
                Set<AdvancedArrow> arrows = Storage.advancedProjectiles.get(evt.getDamager());
                for (AdvancedArrow arrow : arrows) {
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

    // Called when an arrow kills an entity; the advanced arrow is removed after this event
    @EventHandler
    public boolean entityDeath(EntityDeathEvent evt) {
        if (Storage.killedEntities.containsKey(evt.getEntity())) {
            AdvancedArrow arrow = Storage.killedEntities.get(evt.getEntity());
            arrow.onKill(evt);
            Storage.killedEntities.remove(evt.getEntity());
        }
        return true;
    }

    // Prevents lightning events from arrows from supercharging creepers
    @EventHandler
    public boolean charge(CreeperPowerEvent evt) {
        if (Storage.lightnings.contains(evt.getLightning())) {
            evt.setCancelled(true);
        }
        return true;
    }

    // Prevents lightning events from arrows from changing pigs to pigmen
    @EventHandler
    public boolean pigzap(PigZapEvent evt) {
        if (Storage.lightnings.contains(evt.getLightning())) {
            evt.setCancelled(true);
        }
        return true;
    }

    // Returns the arrow that it shot from the given player's inventory
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
