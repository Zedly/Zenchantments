package zedly.zenchantments.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.EnchantArrow;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static zedly.zenchantments.enums.Tool.BOW;

public class Spread extends CustomEnchantment {

    public Spread() {
        super(57);
        maxLevel = 5;
        loreName = "Spread";
        probability = 0;
        enchantable = new Tool[]{BOW};
        conflicting = new Class[]{Burst.class};
        description = "Fires an array of arrows simultaneously";
        cooldown = 0;
        power = 1.0;
        handUse = Hand.RIGHT;
    }

    @Override
    public boolean onProjectileLaunch(ProjectileLaunchEvent evt, int level, boolean usedHand) {
        Arrow originalArrow = (Arrow) evt.getEntity();
        Player player = (Player) originalArrow.getShooter();
        ItemStack hand = Utilities.usedStack(player, usedHand);
        EnchantArrow.ArrowGenericMulitple ar = new EnchantArrow.ArrowGenericMulitple(originalArrow);
        Utilities.putArrow(originalArrow, ar, player);
        Bukkit.getPluginManager().callEvent(
                new EntityShootBowEvent(player, hand, originalArrow, (float) originalArrow.getVelocity().length()));
        Utilities.damageTool(player, (int) Math.round(level / 2.0 + 1), usedHand);
        for(int i = 0; i < (int) Math.round(power * level * 4); i++) {
            Vector v = originalArrow.getVelocity();
            v.setX(v.getX() + Math.max(Math.min(Storage.rnd.nextGaussian() / 8, 0.75), -0.75));
            v.setZ(v.getZ() + Math.max(Math.min(Storage.rnd.nextGaussian() / 8, 0.75), -0.75));
            Arrow arrow = player.getWorld().spawnArrow(
                    player.getEyeLocation().add(player.getLocation().getDirection().multiply(1.0)), v, 1, 0);
            arrow.setShooter(player);
            arrow.setVelocity(v.normalize().multiply(originalArrow.getVelocity().length()));
            arrow.setFireTicks(originalArrow.getFireTicks());
            arrow.setKnockbackStrength(originalArrow.getKnockbackStrength());
            EntityShootBowEvent event =
                    new EntityShootBowEvent(player, hand, arrow, (float) originalArrow.getVelocity().length());
            Bukkit.getPluginManager().callEvent(event);
            if(evt.isCancelled()) {
                arrow.remove();
                return false;
            }
            arrow.setMetadata("ze.arrow", new FixedMetadataValue(Storage.zenchantments, null));
            arrow.setCritical(originalArrow.isCritical());
            Utilities.putArrow(originalArrow, new EnchantArrow.ArrowGenericMulitple(originalArrow), player);
        }
        return true;
    }
}
