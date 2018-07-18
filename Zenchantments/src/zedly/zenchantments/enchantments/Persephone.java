package zedly.zenchantments.enchantments;

import org.apache.commons.lang3.ArrayUtils;
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

public class Persephone extends CustomEnchantment {

    private static final Material[] CROP_BLOCKS =
            {CROPS, POTATO, CARROT, BEETROOT_BLOCK, NETHER_WARTS, SOUL_SAND, SOIL};

    public Persephone() {
        super(41);
        maxLevel = 3;
        loreName = "Persephone";
        probability = 0;
        enchantable = new Tool[]{HOE};
        conflicting = new Class[]{};
        description = "Plants seeds from the player's inventory around them";
        cooldown = 0;
        power = 1.0;
        handUse = Hand.RIGHT;
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        if(evt.getAction() == RIGHT_CLICK_BLOCK) {
            Player player = evt.getPlayer();
            Location loc = evt.getClickedBlock().getLocation();
            int radiusXZ = (int) Math.round(power * level + 2);

            if(ArrayUtils.contains(CROP_BLOCKS, evt.getClickedBlock().getType())) {
                Block block = (Block) loc.getBlock();
                for(int x = -(radiusXZ); x <= radiusXZ; x++) {
                    for(int y = -2; y <= 0; y++) {
                        for(int z = -(radiusXZ); z <= radiusXZ; z++) {

                            if(block.getRelative(x, y, z).getLocation().distanceSquared(loc) <
                               radiusXZ * radiusXZ) {
                                if(block.getRelative(x, y, z).getType() == SOIL
                                   && block.getRelative(x, y + 1, z).getType() == AIR) {
                                    if(evt.getPlayer().getInventory().contains(CARROT_ITEM)) {
                                        if(ADAPTER.placeBlock(block.getRelative(x, y + 1, z), player, CARROT, 0)) {
                                            Utilities.removeItem(player, CARROT_ITEM, (short) 0, 1);
                                        }
                                    }
                                    if(evt.getPlayer().getInventory().contains(POTATO_ITEM)) {
                                        if(ADAPTER.placeBlock(block.getRelative(x, y + 1, z), player, POTATO, 0)) {
                                            Utilities.removeItem(player, POTATO_ITEM, (short) 0, 1);
                                        }
                                    }
                                    if(evt.getPlayer().getInventory().contains(SEEDS)) {
                                        if(ADAPTER.placeBlock(block.getRelative(x, y + 1, z), player, CROPS, 0)) {
                                            Utilities.removeItem(player, SEEDS, (short) 0, 1);
                                        }
                                    }
                                    if(evt.getPlayer().getInventory().contains(BEETROOT_SEEDS)) {
                                        if(ADAPTER.placeBlock(block.getRelative(x, y + 1, z), player,
                                                              Material.BEETROOT_BLOCK, 0)) {
                                            Utilities.removeItem(player, BEETROOT_SEEDS, (short) 0, 1);
                                        }
                                    }
                                } else if(block.getRelative(x, y, z).getType() == SOUL_SAND
                                          && block.getRelative(x, y + 1, z).getType() == AIR) {
                                    if(evt.getPlayer().getInventory().contains(NETHER_STALK)) {
                                        if(ADAPTER.placeBlock(block.getRelative(x, y + 1, z), player, NETHER_WARTS,
                                                              0)) {
                                            Utilities.removeItem(player, NETHER_STALK, (short) 0, 1);
                                        }
                                    }
                                } else {
                                    continue;
                                }
                                if(Storage.rnd.nextBoolean()) {
                                    Utilities.damageTool(evt.getPlayer(), 1, usedHand);
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
