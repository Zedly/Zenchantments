package zedly.zenchantments.enchantments;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.*;
import zedly.zenchantments.enums.*;

import static zedly.zenchantments.enums.Tool.BOW;
import static zedly.zenchantments.enums.Tool.SWORD;

public class Siphon extends CustomEnchantment {

    public Siphon() {
        maxLevel = 4;
        loreName = "Siphon";
        probability = 0;
        enchantable = new Tool[]{BOW, SWORD};
        conflicting = new Class[]{};
        description = "Drains the health of the mob that you attack, giving it to you";
        cooldown = 0;
        power = 1.0;
        handUse = Hand.BOTH;
        id = 53;
    }

    @Override
    public boolean onEntityHit(EntityDamageByEntityEvent evt, int level, boolean usedHand) {
        if(evt.getEntity() instanceof LivingEntity &&
           ADAPTER.attackEntity((LivingEntity) evt.getEntity(), (Player) evt.getDamager(), 0)) {
            Player p = (Player) evt.getDamager();
            LivingEntity ent = (LivingEntity) evt.getEntity();
            int difference = (int) Math.round(level * power);
            if(Storage.rnd.nextInt(4) == 2) {
                while(difference > 0) {
                    if(p.getHealth() <= 19) {
                        p.setHealth(p.getHealth() + 1);
                    }
                    if(ent.getHealth() > 2) {
                        ent.setHealth(ent.getHealth() - 1);
                    }
                    difference--;
                }
            }
        }
        return true;
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        EnchantArrow.ArrowEnchantSiphon arrow =
                new EnchantArrow.ArrowEnchantSiphon((Projectile) evt.getProjectile(), level, power);
        Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }
}
