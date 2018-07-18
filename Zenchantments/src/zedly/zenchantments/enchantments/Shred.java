package zedly.zenchantments.enchantments;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.*;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import java.util.HashSet;
import java.util.Set;

import static org.bukkit.Material.*;
import static zedly.zenchantments.enums.Tool.PICKAXE;
import static zedly.zenchantments.enums.Tool.SHOVEL;

public class Shred extends CustomEnchantment {

    private static final Material[] ALLOWED_MATERIALS =
            new Material[]{STONE, COAL_ORE, REDSTONE_ORE, DIAMOND_ORE, GOLD_ORE, IRON_ORE,
                           NETHERRACK, LAPIS_ORE, GLOWSTONE, QUARTZ_ORE, EMERALD_ORE, GRASS, SOUL_SAND,
                           GLOWING_REDSTONE_ORE,
                           DIRT, MYCEL, SAND, GRAVEL, SOUL_SAND, CLAY, HARD_CLAY, STAINED_CLAY, SANDSTONE,
                           RED_SANDSTONE, ICE, PACKED_ICE};

    private static final Material SHOVELABLE_MATERIALS[] =
            new Material[]{GLOWSTONE, GRASS, DIRT, MYCEL, SOUL_SAND, SAND, GRAVEL, SOUL_SAND, CLAY};

    public Shred() {
        super(52);
        maxLevel = 5;
        loreName = "Shred";
        probability = 0;
        enchantable = new Tool[]{SHOVEL, PICKAXE};
        conflicting = new Class[]{Pierce.class, Switch.class};
        description = "Breaks the blocks within a radius of the original block mined";
        cooldown = 0;
        power = -1.0;
        handUse = Hand.LEFT;
    }

    @Override
    public boolean onBlockBreak(BlockBreakEvent evt, int level, boolean usedHand) {
        if(evt.getBlock().getType() != AIR && !ArrayUtils.contains(ALLOWED_MATERIALS, evt.getBlock().getType())) {
            return false;
        }
        ItemStack hand = Utilities.usedStack(evt.getPlayer(), usedHand);
        final Config config = Config.get(evt.getBlock().getWorld());
        Set<Block> broken = new HashSet<>();
        blocks(evt.getBlock(), evt.getBlock(), new int[]{level + 3, level + 3, level + 3},
               0, 4.6 + (level * .22), broken, evt.getPlayer(), config, hand.getType(), usedHand);
        return true;
    }

    public void blocks(Block centerBlock, final Block relativeBlock, int[] coords, int time, double size,
                       Set<Block> used,
                       final Player player, final Config config, final Material itemType, boolean usedHand) {
        if(relativeBlock.getType() != AIR && !used.contains(relativeBlock)) {
            final Material originalType = relativeBlock.getType();
            if(!ArrayUtils.contains(ALLOWED_MATERIALS, relativeBlock.getType())
               || (Tool.SHOVEL.contains(itemType)
                   && !ArrayUtils.contains(SHOVELABLE_MATERIALS, relativeBlock.getType()))) {
                return;
            }
            if(config.getShredDrops() == 0) {
                ADAPTER.breakBlockNMS(relativeBlock, player);
            } else {
                BlockShredEvent relativeEvent = new BlockShredEvent(relativeBlock, player);
                Bukkit.getServer().getPluginManager().callEvent(relativeEvent);
                if(relativeEvent.isCancelled()) {
                    return;
                }
                if(config.getShredDrops() == 1) {
                    if(relativeBlock.getType().equals(QUARTZ_ORE)) {
                        relativeBlock.setType(NETHERRACK);
                    } else if(ArrayUtils.contains(Storage.ORES, relativeBlock.getType())) {
                        relativeBlock.setType(STONE);
                    }
                    WatcherEnchant.instance().onBlockShred(relativeEvent);
                    if(relativeEvent.isCancelled()) {
                        return;
                    }
                    relativeBlock.breakNaturally();
                } else {
                    relativeBlock.setType(AIR);
                }
            }
            Sound sound = null;
            switch(originalType) {
                case GRASS:
                    sound = Sound.BLOCK_GRASS_BREAK;
                    break;
                case DIRT:
                case GRAVEL:
                case CLAY:
                    sound = Sound.BLOCK_GRAVEL_BREAK;
                    break;
                case SAND:
                    sound = Sound.BLOCK_SAND_BREAK;
                    break;
                case AIR:
                    break;
                default:
                    sound = Sound.BLOCK_STONE_BREAK;
                    break;
            }
            if(sound != null) {
                relativeBlock.getLocation().getWorld().playSound(relativeBlock.getLocation(), sound, 10, 1);
            }

            Utilities.damageTool(player, 1, usedHand);

            used.add(relativeBlock);
            for(int i = 0; i < 3; i++) {
                if(coords[i] > 0) {
                    coords[i] -= 1;
                    Block blk1 = relativeBlock.getRelative(i == 0 ? -1 : 0, i == 1 ? -1 : 0, i == 2 ? -1 : 0);
                    Block blk2 = relativeBlock.getRelative(i == 0 ? 1 : 0, i == 1 ? 1 : 0, i == 2 ? 1 : 0);
                    if(blk1.getLocation().distanceSquared(centerBlock.getLocation()) <
                       size + (-1 + 2 * Math.random())) {
                        blocks(centerBlock, blk1, coords, time + 2, size, used, player, config, itemType, usedHand);
                    }
                    if(blk2.getLocation().distanceSquared(centerBlock.getLocation()) <
                       size + (-1 + 2 * Math.random())) {
                        blocks(centerBlock, blk2, coords, time + 2, size, used, player, config, itemType, usedHand);
                    }
                    coords[i] += 1;
                }
            }
        }
    }
}
