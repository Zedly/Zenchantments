package zedly.zenchantments.enchantments;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.arrows.enchanted.SiphonArrow;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import static zedly.zenchantments.Tool.BOW;
import static zedly.zenchantments.Tool.SWORD;

public class Siphon extends Zenchantment {

    public static final int ID = 53;

    @Override
    public Builder<Siphon> defaults() {
        return new Builder<>(Siphon::new, ID)
                .maxLevel(4)
                .name("Siphon")
                .probability(0)
                .enchantable(new Tool[]{BOW, SWORD})
                .conflicting(new Class[]{})
                .description("Drains the health of the mob that you attack, giving it to you")
                .cooldown(0)
                .power(1.0)
                .handUse(Hand.BOTH);
    }

    @Override
    public boolean onEntityHit(EntityDamageByEntityEvent event, int level, boolean usedHand) {
        if (event.getEntity() instanceof LivingEntity
                && ADAPTER.attackEntity((LivingEntity) event.getEntity(), (Player) event.getDamager(), 0)) {
            Player player = (Player) event.getDamager();
            int difference = (int) Math.round(.17 * level * power * event.getDamage());
            while (difference > 0) {
                if (player.getHealth() < player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
                    player.setHealth(Math.min(player.getHealth() + 1, player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
                }
                difference--;
            }
        }
        return true;
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent event, int level, boolean usedHand) {
        SiphonArrow arrow = new SiphonArrow((Arrow) event.getProjectile(), level, power);
        EnchantedArrow.putArrow((Arrow) event.getProjectile(), arrow, (Player) event.getEntity());
        return true;
    }
}
