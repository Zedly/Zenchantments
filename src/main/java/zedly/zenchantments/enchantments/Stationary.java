package zedly.zenchantments.enchantments;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.arrows.enchanted.StationaryArrow;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import static zedly.zenchantments.Tool.BOW;
import static zedly.zenchantments.Tool.SWORD;

public class Stationary extends Zenchantment {

    public static final int ID = 58;

    @Override
    public Builder<Stationary> defaults() {
        return new Builder<>(Stationary::new, ID)
                .maxLevel(1)
                .loreName("Stationary")
                .probability(0)
                .enchantable(new Tool[]{BOW, SWORD})
                .conflicting(new Class[]{})
                .description("Negates any knockback when attacking mobs, leaving them clueless as to who is attacking")
                .cooldown(0)
                .power(-1.0)
                .handUse(Hand.BOTH);
    }

    @Override
    public boolean onEntityHit(EntityDamageByEntityEvent event, int level, boolean usedHand) {
        if (!(event.getEntity() instanceof LivingEntity)
                || ADAPTER.attackEntity((LivingEntity) event.getEntity(), (Player) event.getDamager(), 0)) {
            LivingEntity ent = (LivingEntity) event.getEntity();
            if (event.getDamage() < ent.getHealth()) {
                event.setCancelled(true);
                Utilities.damageTool(((Player) event.getDamager()), 1, usedHand);
                ent.damage(event.getDamage());
            }
        }
        return true;
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent event, int level, boolean usedHand) {
        StationaryArrow arrow = new StationaryArrow((Arrow) event.getProjectile());
        EnchantedArrow.putArrow((Arrow) event.getProjectile(), arrow, (Player) event.getEntity());
        return true;
    }

}
