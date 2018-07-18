package zedly.zenchantments.enchantments;

import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static zedly.zenchantments.enums.Tool.CHESTPLATE;

public class Combustion extends CustomEnchantment {

    public Combustion() {
        super(9);
        maxLevel = 4;
        loreName = "Combustion";
        probability = 0;
        enchantable = new Tool[]{CHESTPLATE};
        conflicting = new Class[]{};
        description = "Lights attacking entities on fire when player is attacked";
        cooldown = 0;
        power = 1.0;
        handUse = Hand.NONE;
    }

    @Override
    public boolean onBeingHit(EntityDamageByEntityEvent evt, int level, boolean usedHand) {
        Entity ent;
        if(evt.getDamager().getType() == EntityType.ARROW) {
            Arrow arrow = (Arrow) evt.getDamager();
            if(arrow.getShooter() instanceof LivingEntity) {
                ent = (Entity) arrow.getShooter();
            } else {
                return false;
            }
        } else {
            ent = evt.getDamager();
        }
        return ADAPTER.igniteEntity(ent, (Player) evt.getEntity(), (int) (50 * level * power));
    }

    public boolean onCombust(EntityCombustByEntityEvent evt, int level, boolean usedHand) {
        if(ADAPTER.isZombie(evt.getCombuster())) {
            evt.setDuration(0);
        }
        return false;
    }
}
