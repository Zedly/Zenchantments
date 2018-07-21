package zedly.zenchantments.enchantments;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.arrows.enchanted.StationaryArrow;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static zedly.zenchantments.enums.Tool.BOW;
import static zedly.zenchantments.enums.Tool.SWORD;

public class Stationary extends CustomEnchantment {

    @Override
    public Builder<Stationary> defaults() {
        return new Builder<>(Stationary::new, 58)
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
    public boolean onEntityHit(EntityDamageByEntityEvent evt, int level, boolean usedHand) {
        if(!(evt.getEntity() instanceof LivingEntity) ||
           !ADAPTER.attackEntity((LivingEntity) evt.getEntity(), (Player) evt.getDamager(), 0)) {
            LivingEntity ent = (LivingEntity) evt.getEntity();
            if(evt.getDamage() < ent.getHealth()) {
                evt.setCancelled(true);
                Utilities.damageTool(((Player) evt.getDamager()), 1, usedHand);
                ent.damage(evt.getDamage());
            }
        }
        return true;
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        StationaryArrow arrow = new StationaryArrow((Arrow) evt.getProjectile());
        EnchantedArrow.putArrow((Arrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }

}
