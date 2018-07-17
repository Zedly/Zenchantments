package zedly.zenchantments.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.util.Vector;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import java.util.concurrent.atomic.AtomicBoolean;

import static zedly.zenchantments.enums.Tool.SWORD;

public class RainbowSlam extends CustomEnchantment {

    public RainbowSlam() {
        super(48);
        maxLevel = 4;
        loreName = "Rainbow Slam";
        probability = 0;
        enchantable = new Tool[]{SWORD};
        conflicting = new Class[]{Force.class, Gust.class};
        description = "Attacks enemy mobs with a powerful swirling slam";
        cooldown = 0;
        power = 1.0;
        handUse = Hand.RIGHT;
    }

    @Override
    public boolean onEntityInteract(final PlayerInteractEntityEvent evt, final int level, boolean usedHand) {
        if(!(evt.getRightClicked() instanceof LivingEntity) ||
           !ADAPTER.attackEntity((LivingEntity) evt.getRightClicked(), evt.getPlayer(), 0)) {
            return false;
        }
        Utilities.damageTool(evt.getPlayer(), 9, usedHand);
        final LivingEntity ent = (LivingEntity) evt.getRightClicked();
        final Location l = ent.getLocation().clone();
        ent.teleport(l);
        for(int i = 0; i < 30; i++) {
            final int fI = i;
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
                for(int j = 0; j < 40; j++) {
                    if(ent.isDead()) {
                        return;
                    }
                    Location loc = l.clone();
                    float t = 30 * fI + j;
                    loc.setY(loc.getY() + (t / 100));
                    loc.setX(loc.getX() + Math.sin(Math.toRadians(t)) * t / 330);
                    loc.setZ(loc.getZ() + Math.cos(Math.toRadians(t)) * t / 330);
                    Utilities.display(loc, Particle.REDSTONE, 1, 10f, 0, 0, 0);
                    loc.setY(loc.getY() + 1.3);
                    ent.setVelocity(loc.toVector().subtract(ent.getLocation().toVector()));
                }
            }, i);
        }
        AtomicBoolean applied = new AtomicBoolean(false);
        Storage.rainbowSlamNoFallEntities.add(ent);
        for(int i = 0; i < 3; i++) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
                //ent.setNoDamageTicks(20); // Prevent fall damage
                ent.setVelocity(l.toVector().subtract(ent.getLocation().toVector()).multiply(.3));
                ent.setFallDistance(0);
                if(ent.isOnGround() && !applied.get()) {
                    applied.set(true);
                    Storage.rainbowSlamNoFallEntities.remove(ent);
                    ADAPTER.attackEntity(ent, evt.getPlayer(), level * power);
                    for(int c = 0; c < 1000; c++) {
                        Vector v = new Vector(Math.sin(Math.toRadians(c)), Storage.rnd.nextFloat(),
                                              Math.cos(Math.toRadians(c))).multiply(.75);
                        Utilities.display(Utilities.getCenter(l), Particle.BLOCK_DUST, 1, (float) v.length(), 0, 0,
                                          0);
                    }
                }
            }, 35 + (i * 5));
        }
        return true;
    }
}
