package zedly.zenchantments;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import static org.bukkit.Material.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class HFEffects implements Runnable {

    int tick;
    private final LinkedList<Arrow> toRemove;
    private final LinkedList<Block> websToRemove;

    public HFEffects() {
        toRemove = new LinkedList<>();
        websToRemove = new LinkedList<>();
    }

    @Override
    public void run() {
        //Arrows
        toRemove.clear();
        for (Set<Arrow> pro : Storage.advancedProjectiles.values()) {
            for (Arrow a : pro) {
                a.onFlight();
                a.tick++;
                if (a.entity.isDead() || a.tick > 600) {
                    toRemove.add(a);
                }
            }
        }
        for (Arrow pro : toRemove) {
            Storage.advancedProjectiles.remove(pro.entity);
            pro.entity.remove();
        }
        for (Block block : Storage.webs) {
            if (Storage.rnd.nextInt(175) == 0 && block.getChunk().isLoaded()) {
                block.setType(AIR);
                websToRemove.add(block);
            }
        }
        for (Block block : websToRemove) {
            Storage.webs.remove(block);
        }
        websToRemove.clear();
        for (LivingEntity ent : Storage.derpingEntities) {
            Location loc = ent.getLocation();
            loc.setYaw(Storage.rnd.nextFloat() * 360F);
            loc.setPitch(Storage.rnd.nextFloat() * 180F - 90F);
            ent.teleport(loc);
        }
        tick++;
        //Other stuff
        for (FallingBlock b : Storage.anthMobs2) {
            if (!Storage.anthVortex.contains(Storage.anthMobs.get(b))) {
                for (Entity e : b.getNearbyEntities(7, 7, 7)) {
                    if (e instanceof LivingEntity) {
                        LivingEntity lE = (LivingEntity) e;
                        if (!(lE instanceof Player) && lE instanceof Monster) {
                            b.setVelocity(e.getLocation().subtract(b.getLocation()).toVector().multiply(.25));
                            if (lE.getLocation().getWorld().equals(b.getLocation().getWorld())) {
                                if (lE.getLocation().distance(b.getLocation()) < 1.2) {
                                    EntityDamageEvent evt = new EntityDamageEvent(lE, EntityDamageEvent.DamageCause.SUFFOCATION, 100);
                                    Bukkit.getPluginManager().callEvent(evt);
                                    lE.setLastDamageCause(evt);
                                    if (!evt.isCancelled()) {
                                        lE.damage(8f);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        boolean r = Storage.fallBool;
        Storage.fallBool = !Storage.fallBool;
        for (FallingBlock b : Storage.anthMobs.keySet()) {
            if (Storage.anthVortex.contains(Storage.anthMobs.get(b))) {
                Location loc = Storage.anthMobs.get(b).getLocation();
                Vector v;
                if (b.getLocation().getWorld().equals(Storage.anthMobs.get(b).getLocation().getWorld())) {
                    if (r && b.getLocation().distance(Storage.anthMobs.get(b).getLocation()) < 10) {
                        v = b.getLocation().subtract(loc).toVector();
                    } else {
                        int x = Storage.rnd.nextInt(12) - 6;
                        int z = Storage.rnd.nextInt(12) - 6;
                        Location tLoc = loc.clone();
                        tLoc.setX(tLoc.getX() + x);
                        tLoc.setZ(tLoc.getZ() + z);
                        v = tLoc.subtract(b.getLocation()).toVector();
                    }
                    v.multiply(.05);
                    boolean close = false;
                    for (int x = -3; x < 0; x++) {
                        if (b.getLocation().getBlock().getRelative(0, x, 0).getType() != AIR) {
                            close = true;
                        }
                    }
                    if (close) {
                        v.setY(5);
                    } else {
                        v.setY(-.1);
                    }
                    b.setVelocity(v);
                }
            }
        }

        for (org.bukkit.entity.Arrow e : Storage.tracer.keySet()) {
            Entity close = null;
            double distance = 100;
            int level = Storage.tracer.get(e);
            level = level + 2;
            for (Entity e1 : e.getNearbyEntities(level, level, level)) {
                if (e1.getLocation().getWorld().equals(e.getLocation().getWorld())) {
                    double d = e1.getLocation().distance(e.getLocation());
                    if (e.getLocation().getWorld().equals(((Entity) e.getShooter()).getLocation().getWorld())) {
                        if (d < distance && e1 instanceof LivingEntity && !e1.equals(e.getShooter()) && e.getLocation().distance(((Entity) e.getShooter()).getLocation()) > 15) {
                            distance = d;
                            close = e1;
                        }
                    }
                }
            }
            if (close != null) {
                Location location = close.getLocation();
                org.bukkit.util.Vector v = new org.bukkit.util.Vector(0D, 0D, 0D);
                Location pos = e.getLocation();
                double its = Math.sqrt((location.getBlockX() - pos.getBlockX()) * (location.getBlockX() - pos.getBlockX()) + (location.getBlockY() - pos.getBlockY()) * (location.getBlockY() - pos.getBlockY()) + (location.getBlockZ() - pos.getBlockZ()) * (location.getBlockZ() - pos.getBlockZ()));
                if (its == 0) {
                    its = (double) 1;
                }
                v.setX((location.getBlockX() - pos.getBlockX()) / its);
                v.setY((location.getBlockY() - pos.getBlockY()) / its);
                v.setZ((location.getBlockZ() - pos.getBlockZ()) / its);
                e.setVelocity(v.multiply(2));
            }
        }

        for (Guardian g : Storage.guardianMove.keySet()) {
            if (g.getLocation().distance(Storage.guardianMove.get(g).getLocation()) > 2 && g.getTicksLived() < 160) {
                g.setVelocity(Storage.guardianMove.get(g).getLocation().toVector().subtract(g.getLocation().toVector()));
            } else {
                Storage.guardianMove.remove(g);
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            for (ItemStack stk : player.getInventory().getArmorContents()) {
                HashMap<Enchantment, Integer> map = Utilities.getEnchants(stk);
                for (Enchantment ench : map.keySet()) {
                    ench.onFastScan(player, map.get(ench));
                }
            }
            HashMap<Enchantment, Integer> map = Utilities.getEnchants(player.getItemInHand());
            for (Enchantment ench : map.keySet()) {
                ench.onFastScanHand(player, map.get(ench));
            }
        }
    }
}
