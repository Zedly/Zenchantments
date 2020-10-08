package zedly.zenchantments.arrows.enchanted;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.arrows.EnchantedArrow;

public class ReaperArrow extends EnchantedArrow {

    public ReaperArrow(Arrow entity, int level, double power) {
        super(entity, level, power);
    }

    public boolean onImpact(@NotNull EntityDamageByEntityEvent event) {
        if (Storage.COMPATIBILITY_ADAPTER.attackEntity((LivingEntity) event.getEntity(), (Player) this.getArrow().getShooter(), 0)) {
            int power = (int) Math.round(this.getLevel() * this.getPower());
            int duration = (int) Math.round(20 + this.getLevel() * 10 * this.getPower());
            Utilities.addPotion((LivingEntity) event.getEntity(), PotionEffectType.WITHER, duration, power);
            Utilities.addPotion((LivingEntity) event.getEntity(), PotionEffectType.BLINDNESS, duration, power);
        }

        this.die();
        return true;
    }
}