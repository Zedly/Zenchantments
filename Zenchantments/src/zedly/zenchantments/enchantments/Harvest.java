package zedly.zenchantments.enchantments;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.CropState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.Material.*;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import static zedly.zenchantments.enums.Tool.HOE;

public class Harvest extends CustomEnchantment {

    public static final  int        ID          = 26;

    @Override
    public Builder<Harvest> defaults() {
        return new Builder<>(Harvest::new, ID)
            .maxLevel(3)
            .loreName("Harvest")
            .probability(0)
            .enchantable(new Tool[]{HOE})
            .conflicting(new Class[]{})
            .description("Harvests fully grown crops within a radius when clicked")
            .cooldown(0)
            .power(1.0)
            .handUse(Hand.RIGHT);
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        if(evt.getAction() == RIGHT_CLICK_BLOCK) {
            Location loc = evt.getClickedBlock().getLocation();
            int radiusXZ = (int) Math.round(power * level + 2);
            int radiusY = 1;
            if (Storage.COMPATIBILITY_ADAPTER.GROWN_CROPS.contains(evt.getClickedBlock().getType()) ||
	            Storage.COMPATIBILITY_ADAPTER.GROWN_MELON.contains(evt.getClickedBlock().getType())) {
                for (int x = -radiusXZ; x <= radiusXZ; x++) {
                    for (int y = -radiusY - 1; y <= radiusY - 1; y++) {
                        for (int z = -radiusXZ; z <= radiusXZ; z++) {
                            Block block = loc.getBlock();

                            if (block.getRelative(x, y, z).getLocation().distanceSquared(loc) < radiusXZ * radiusXZ) {
	                            BlockData cropState = block.getBlockData();
	                            boolean harvestReady = !(cropState instanceof Ageable);
	                            if (!harvestReady) {
		                            Ageable ag = (Ageable) cropState;
		                            harvestReady = ag.getAge() == ag.getMaximumAge();
	                            }

                                if (harvestReady) {
                                    final Block blk = block.getRelative(x, y + 1, z);
                                    if (ADAPTER.breakBlockNMS(block.getRelative(x, y + 1, z), evt.getPlayer())) {
                                        Utilities.damageTool(evt.getPlayer(), 1, usedHand);
                                        Grab.grabLocs.put(block.getRelative(x, y + 1, z), evt.getPlayer().getLocation());
                                        Bukkit.getServer().getScheduler()
                                              .scheduleSyncDelayedTask(Storage.zenchantments, () -> {
                                                  Grab.grabLocs.remove(blk);
                                              }, 3);
                                    }
                                }
                            }
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }
}
