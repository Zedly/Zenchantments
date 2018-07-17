package zedly.zenchantments.enchantments;

import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.potion.PotionEffectType.SLOW;
import static zedly.zenchantments.enums.Tool.SWORD;

public class IceAspect extends CustomEnchantment {

    public IceAspect() {
        super(29);
        maxLevel = 2;
        loreName = "Ice Aspect";
        probability = 0;
        enchantable = new Tool[]{SWORD};
        conflicting = new Class[]{};
        description = "Temporarily freezes the target";
        cooldown = 0;
        power = 1.0;
        handUse = Hand.LEFT;
    }

    @Override
    public boolean onEntityHit(EntityDamageByEntityEvent evt, int level, boolean usedHand) {
        Utilities.addPotion((LivingEntity) evt.getEntity(), SLOW,
                            (int) Math.round(40 + level * power * 40), (int) Math.round(power * level * 2));
        Utilities.display(Utilities.getCenter(evt.getEntity().getLocation()), Particle.CLOUD, 10, .1f, 1f, 2f, 1f);
        return true;
    }
}
