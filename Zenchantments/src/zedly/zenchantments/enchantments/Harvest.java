package zedly.zenchantments.enchantments;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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

    private static final Material[] CROP_BLOCKS =
            {CROPS, POTATO, CARROT, MELON_BLOCK, PUMPKIN, COCOA, BEETROOT_BLOCK, NETHER_WARTS};

    public Harvest() {
        super(26);
        maxLevel = 3;
        loreName = "Harvest";
        probability = 0;
        enchantable = new Tool[]{HOE};
        conflicting = new Class[]{};
        description = "Harvests fully grown crops within a radius when clicked";
        cooldown = 0;
        power = 1.0;
        handUse = Hand.RIGHT;
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        if(evt.getAction() == RIGHT_CLICK_BLOCK) {
            Location loc = evt.getClickedBlock().getLocation();
            int radiusXZ = (int) Math.round(power * level + 2);
            int radiusY = 1;
            Player player = evt.getPlayer();
            if(ArrayUtils.contains(CROP_BLOCKS, evt.getClickedBlock().getType())) {
                for(int x = -(radiusXZ); x <= radiusXZ; x++) {
                    for(int y = -(radiusY) - 1; y <= radiusY - 1; y++) {
                        for(int z = -(radiusXZ); z <= radiusXZ; z++) {
                            Block block = (Block) loc.getBlock();
                            if(block.getRelative(x, y, z).getLocation().distanceSquared(loc) <
                               radiusXZ * radiusXZ) {
                                if((block.getRelative(x, y + 1, z).getType() == MELON_BLOCK ||
                                    block.getRelative(x, y + 1, z).getType() == PUMPKIN)
                                   || ((block.getRelative(x, y + 1, z).getType() == NETHER_WARTS ||
                                        block.getRelative(x, y + 1, z).getType() == BEETROOT_BLOCK) &&
                                       block.getRelative(x, y + 1, z).getData() == 3)
                                   || ((block.getRelative(x, y + 1, z).getType() == CROPS ||
                                        block.getRelative(x, y + 1, z).getType() == POTATO
                                        || (block.getRelative(x, y + 1, z).getType() == CARROT)) &&
                                       block.getRelative(x, y + 1, z).getData() == 7)) {
                                    final Block blk = block.getRelative(x, y + 1, z);
                                    if(ADAPTER.breakBlockNMS(block.getRelative(x, y + 1, z), evt.getPlayer())) {
                                        Utilities.damageTool(player, 1, usedHand);
                                        Storage.grabLocs
                                                .put(block.getRelative(x, y + 1, z), evt.getPlayer().getLocation());
                                        Bukkit.getServer().getScheduler()
                                              .scheduleSyncDelayedTask(Storage.zenchantments, () -> {
                                                  Storage.grabLocs.remove(blk);
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
