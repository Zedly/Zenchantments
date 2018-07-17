package zedly.zenchantments.enchantments;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static zedly.zenchantments.enums.Tool.PICKAXE;

public class Reveal extends CustomEnchantment {

    public Reveal() {
        super(68);
        maxLevel = 4;
        loreName = "Reveal";
        probability = 0;
        enchantable = new Tool[]{PICKAXE};
        conflicting = new Class[]{Switch.class, Pierce.class, Spectral.class};
        description = "Makes nearby ores glow white through the stone.";
        cooldown = 100;
        power = 1.0;
        handUse = Hand.NONE;
    }

    @Override
    public boolean onBlockInteract(final PlayerInteractEvent evt, int level, boolean usedHand) {
        Player player = evt.getPlayer();
        if(evt.getAction() == Action.RIGHT_CLICK_AIR || evt.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(evt.getPlayer().isSneaking()) {
                int radius = (int) Math.max(2, Math.round((2 + level) * power));
                int found = 0;
                for(int x = -radius; x <= radius; x++) {
                    for(int y = -radius; y <= radius; y++) {
                        for(int z = -radius; z <= radius; z++) {
                            Block blk = evt.getPlayer().getLocation().getBlock().getRelative(x, y, z);
                            if(ArrayUtils.contains(Storage.ORES, blk.getType())) {
                                boolean exposed = false;
                                for(BlockFace face : Storage.CARDINAL_BLOCK_FACES) {
                                    if(blk.getRelative(face).getType() == Material.AIR) {
                                        exposed = true;
                                    }
                                }
                                if(exposed) {
                                    continue;
                                }

                                found++;
                                int entityId = 2000000000 + (blk.hashCode()) % 10000000;
                                if(Storage.glowingBlocks.containsKey(blk)) {
                                    Storage.glowingBlocks.put(blk,
                                                              Storage.glowingBlocks.get(blk) + 1);
                                } else {
                                    Storage.glowingBlocks.put(blk, 1);
                                }

                                if(!ADAPTER.showShulker(blk, entityId, player)) {
                                    return false;
                                }
                                Bukkit.getServer().getScheduler()
                                      .scheduleSyncDelayedTask(Storage.zenchantments, () -> {
                                          ADAPTER.hideShulker(entityId, player);
                                          if(Storage.glowingBlocks.containsKey(blk)
                                             && Storage.glowingBlocks.get(blk) > 1) {
                                              Storage.glowingBlocks.put(blk,
                                                                        Storage.glowingBlocks.get(blk) - 1);
                                          } else {
                                              Storage.glowingBlocks.remove(blk);
                                          }
                                      }, 100);
                            }
                        }
                    }
                }
                Utilities.damageTool(evt.getPlayer(), Math.max(16, (int) Math.round(found * 1.3)), usedHand);
                return true;
            }
        }
        return false;
    }

}
