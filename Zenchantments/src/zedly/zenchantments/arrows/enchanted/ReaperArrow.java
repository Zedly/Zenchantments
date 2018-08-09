package zedly.zenchantments.arrows.enchanted;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.arrows.EnchantedArrow;

import static org.bukkit.potion.PotionEffectType.BLINDNESS;

public class ReaperArrow extends EnchantedArrow {

    public ReaperArrow(Arrow entity, int level, double power) {
        super(entity, level, power);
    }

    public boolean onImpact(EntityDamageByEntityEvent evt) {
        if (Storage.COMPATIBILITY_ADAPTER.attackEntity((LivingEntity) evt.getEntity(), (Player) arrow.getShooter(), 0)) {
            int pow = (int) Math.round(getLevel() * getPower());
            int dur = (int) Math.round(20 + getLevel() * 10 * getPower());
            Utilities.addPotion((LivingEntity) evt.getEntity(), PotionEffectType.WITHER, dur, pow);
            Utilities.addPotion((LivingEntity) evt.getEntity(), BLINDNESS, dur, pow);
        }
        die();
        return true;
    }

}
