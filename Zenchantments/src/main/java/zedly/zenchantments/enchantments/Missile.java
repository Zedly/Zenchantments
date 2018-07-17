package zedly.zenchantments.enchantments;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.EnchantArrow;
import zedly.zenchantments.enums.*;
import zedly.zenchantments.Utilities;

import static zedly.zenchantments.enums.Tool.BOW;

public class Missile extends CustomEnchantment {

    public Missile() {
        maxLevel = 1;
        loreName = "Missile";
        probability = 0;
        enchantable = new Tool[]{BOW};
        conflicting = new Class[]{};
        description = "Shoots a missile from the bow";
        cooldown = 0;
        power = -1.0;
        handUse = Hand.RIGHT;
        id = 71;
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        EnchantArrow.ArrowAdminMissile arrow = new EnchantArrow.ArrowAdminMissile((Projectile) evt.getProjectile());
        Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
        evt.setCancelled(true);
        Utilities.damageTool((Player) evt.getEntity(), 1, usedHand);
        Utilities.removeItem(((Player) evt.getEntity()), Material.ARROW, 1);
        return true;
    }
}
