package zedly.zenchantments;

import java.util.*;
import org.bukkit.*;
import static org.bukkit.GameMode.CREATIVE;
import static org.bukkit.Material.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

// This is "High Frequency Effects", called every tick to perform certain actions within the plugin
public class HFEffects implements Runnable {

    public void run() {
        anthropomorphism();
        blackholes();
        elementalArrows();
        guardian();
        haulDelay();
        hunger();
        scanPlayers();
        tracer();
    }

    // Moves Anthropomorphism blocks around depending on their state
    private void anthropomorphism() {
        // Move agressive Anthropomorphism Blocks towards a target & attack
        Iterator<FallingBlock> anthroIterator = Storage.attackBlocks.keySet().iterator();
        while (anthroIterator.hasNext()) {
            FallingBlock blockEntity = anthroIterator.next();
            if (!Storage.anthVortex.contains(Storage.idleBlocks.get(blockEntity))) {
                for (Entity e : blockEntity.getNearbyEntities(7, 7, 7)) {
                    if (e instanceof Monster) {
                        LivingEntity targetEntity = (LivingEntity) e;
                        blockEntity.setVelocity(e.getLocation().subtract(blockEntity.getLocation()).toVector().multiply(.25));
                        if (targetEntity.getLocation().getWorld().equals(blockEntity.getLocation().getWorld())) {
                            if (targetEntity.getLocation().distance(blockEntity.getLocation()) < 1.2
                                    && blockEntity.hasMetadata("ze.anthrothrower")) {
                                Player attacker = (Player) blockEntity.getMetadata("ze.anthrothrower").get(0).value();
                                if (targetEntity.getNoDamageTicks() == 0
                                        && Storage.COMPATIBILITY_ADAPTER.attackEntity(targetEntity, attacker, .5 * Storage.attackBlocks.get(blockEntity))) {
                                    targetEntity.setNoDamageTicks(0);
                                    anthroIterator.remove();
                                    blockEntity.remove();
                                }
                            }
                        }
                    }
                }
            }
        }
        // Move passive Anthropomorphism Blocks around
        Storage.fallBool = !Storage.fallBool;
        for (FallingBlock b : Storage.idleBlocks.keySet()) {
            if (Storage.anthVortex.contains(Storage.idleBlocks.get(b))) {
                Location loc = Storage.idleBlocks.get(b).getLocation();
                Vector v;
                if (b.getLocation().getWorld().equals(Storage.idleBlocks.get(b).getLocation().getWorld())) {
                    if (Storage.fallBool && b.getLocation().distance(Storage.idleBlocks.get(b).getLocation()) < 10) {
                        v = b.getLocation().subtract(loc).toVector();
                    } else {
                        double x = 6f * Math.sin(b.getTicksLived() / 10f);
                        double z = 6f * Math.cos(b.getTicksLived() / 10f);
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
                        v.setY(Math.abs(Math.sin(b.getTicksLived() / 10f)));
                    } else {
                        v.setY(0);
                    }
                    b.setVelocity(v);
                }
            }
        }
    }

    // Moves entities towards the black hole from the Singularity enchantment in pull state
    // Throws entities in the black hole out in reverse state
    private void blackholes() {
        for (Location l : Storage.blackholes.keySet()) {
            for (Entity e : l.getWorld().getNearbyEntities(l, 10, 10, 10)) {
                if (e instanceof Player) {
                    if (((Player) e).getGameMode().equals(CREATIVE)) {
                        continue;
                    }
                }
                if (Storage.blackholes.get(l)) {
                    Vector v = l.clone().subtract(e.getLocation()).toVector();
                    v.setX(v.getX() + (-.5f + Storage.rnd.nextFloat()) * 10);
                    v.setY(v.getY() + (-.5f + Storage.rnd.nextFloat()) * 10);
                    v.setZ(v.getZ() + (-.5f + Storage.rnd.nextFloat()) * 10);
                    e.setVelocity(v.multiply(.35f));
                    e.setFallDistance(0);
                } else {
                    Vector v = e.getLocation().subtract(l.clone()).toVector();
                    v.setX(v.getX() + (-.5f + Storage.rnd.nextFloat()) * 2);
                    v.setY(v.getY() + Storage.rnd.nextFloat());
                    v.setZ(v.getZ() + (-.5f + Storage.rnd.nextFloat()) * 2);
                    e.setVelocity(v.multiply(.35f));
                }
            }
        }
    }

    // Repeated actions for certain elemental arrows
    private void elementalArrows() {
        // Remove arrows if they don't exist or if it's been longer than 30 seconds
        Iterator it = Storage.advancedProjectiles.values().iterator();
        while (it.hasNext()) {
            for (AdvancedArrow a : (Set<AdvancedArrow>) it.next()) {
                a.onFlight();
                a.tick();
                if (a.getArrow().isDead() || a.getTick() > 600) {
                    it.remove();
                    a.getArrow().remove();
                    break;
                }
            }
        }
    }

    // Move Guardians from MysteryFish towards the player
    private void guardian() {
        Iterator it = Storage.guardianMove.keySet().iterator();
        while (it.hasNext()) {
            Guardian g = (Guardian) it.next();
            if (g.getLocation().distance(Storage.guardianMove.get(g).getLocation()) > 2 && g.getTicksLived() < 160) {
                g.setVelocity(Storage.guardianMove.get(g).getLocation().toVector().subtract(g.getLocation().toVector()));
            } else {
                it.remove();
            }
        }
    }

    // Manages the delay players have when using Haul enchantment
    private void haulDelay() {
        Iterator it = Storage.haulBlockDelay.keySet().iterator();
        while (it.hasNext()) {
            Player player = (Player) it.next();
            Storage.haulBlockDelay.put(player, Storage.haulBlockDelay.get(player) + 1);
            if (Storage.haulBlockDelay.get(player) > 5) {
                Storage.haulBlocks.remove(player);
                it.remove();
            }
        }
    }

    // Manages time left for players affected by Toxic enchantment
    private void hunger() {
        Iterator it = Storage.hungerPlayers.keySet().iterator();
        while (it.hasNext()) {
            Player player = (Player) it.next();
            if (Storage.hungerPlayers.get(player) < 1) {
                it.remove();
            } else {
                Storage.hungerPlayers.put(player, Storage.hungerPlayers.get(player) - 1);
            }
        }
    }

    // Fast Scan of Player's Armor and their hand to register enchantments 
    private void scanPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            EnchantPlayer.matchPlayer(player).tick();
            for (ItemStack stk : player.getInventory().getArmorContents()) {
                CustomEnchantment.applyForTool(player, stk, (ench, level) -> {
                    return ench.onFastScan(player, level, true);
                });
            }
            ItemStack stk = player.getInventory().getItemInMainHand();
            CustomEnchantment.applyForTool(player, stk, (ench, level) -> {
                return ench.onFastScanHands(player, level, true);
            });
            stk = player.getInventory().getItemInOffHand();
            CustomEnchantment.applyForTool(player, stk, (ench, level) -> {
                return ench.onFastScanHands(player, level, false);
            });
        }
    }

    // Moves Tracer arrows towards a target
    private void tracer() {
        for (Arrow e : Storage.tracer.keySet()) {
            Entity close = null;
            double distance = 100;
            int level = Storage.tracer.get(e);
            level += 2;
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
                double its = location.distance(pos);
                if (its == 0) {
                    its = 1;
                }
                v.setX((location.getX() - pos.getX()) / its);
                v.setY((location.getY() - pos.getY()) / its);
                v.setZ((location.getZ() - pos.getZ()) / its);
                v.add(e.getLocation().getDirection().multiply(.1));
                e.setVelocity(v.multiply(2));
            }
        }
    }
}
