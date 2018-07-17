package zedly.zenchantments.enchantments;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffectType;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.EnchantArrow;
import zedly.zenchantments.enums.*;
import zedly.zenchantments.Utilities;

import static org.bukkit.potion.PotionEffectType.BLINDNESS;
import static zedly.zenchantments.enums.Tool.BOW_;
import static zedly.zenchantments.enums.Tool.SWORD;

public class Reaper extends CustomEnchantment {

    public Reaper() {
        maxLevel = 4;
        loreName = "Reaper";
        probability = 0;
        enchantable = new Tool[]{BOW_, SWORD};
        conflicting = new Class[]{};
        description = "Gives the target temporary wither effect and blindness";
        cooldown = 0;
        power = 1.0;
        handUse = Hand.BOTH;
    }

    public int getEnchantmentId() {
        return 49;
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        EnchantArrow.ArrowEnchantReaper arrow =
                new EnchantArrow.ArrowEnchantReaper((Projectile) evt.getProjectile(), level, power);
        Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }

    @Override
    public boolean onEntityHit(EntityDamageByEntityEvent evt, int level, boolean usedHand) {
        if(evt.getEntity() instanceof LivingEntity &&
           ADAPTER.attackEntity((LivingEntity) evt.getEntity(), (Player) evt.getDamager(), 0)) {
            int pow = (int) Math.round(level * power);
            int dur = (int) Math.round(10 + level * 20 * power);
            Utilities.addPotion((LivingEntity) evt.getEntity(), PotionEffectType.WITHER, dur, pow);
            Utilities.addPotion((LivingEntity) evt.getEntity(), BLINDNESS, dur, pow);
        }
        return true;
    }
}
