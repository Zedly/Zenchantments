package zedly.zenchantments;

import java.util.LinkedList;
import java.util.Set;
import org.bukkit.Location;
import static org.bukkit.Material.*;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

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
            if (Storage.rnd.nextInt(125) == 0 && block.getChunk().isLoaded()) {
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
    }
}
