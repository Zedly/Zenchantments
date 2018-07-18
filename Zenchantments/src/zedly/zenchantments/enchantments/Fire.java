package zedly.zenchantments.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.Material.*;
import static org.bukkit.entity.EntityType.EXPERIENCE_ORB;
import static zedly.zenchantments.enums.Tool.*;

public class Fire extends CustomEnchantment {

    public Fire() {
        super(13);
        maxLevel = 1;
        loreName = "Fire";
        probability = 0;
        enchantable = new Tool[]{PICKAXE, AXE, SHOVEL};
        conflicting = new Class[]{Switch.class, Variety.class};
        description = "Drops the smelted version of the block broken";
        cooldown = 0;
        power = -1.0;
        handUse = Hand.LEFT;
    }

    @Override
    public boolean onBlockBreak(BlockBreakEvent evt, int level, boolean usedHand) {
        ItemStack hand = Utilities.usedStack(evt.getPlayer(), usedHand);
        Material mat = AIR;
        short itemInfo = 0;
        short s = evt.getBlock().getData();
        if(Tool.PICKAXE.contains(hand)) {
            if(evt.getBlock().getType() == STONE) {
                if(s == 1 || s == 3 || s == 5) {
                    s++;
                    mat = STONE;
                    itemInfo = s;
                } else if(s == 2 || s == 4 || s == 6) {
                    return false;
                } else {
                    mat = SMOOTH_BRICK;
                }
            } else if(evt.getBlock().getType() == IRON_ORE) {
                mat = IRON_INGOT;
            } else if(evt.getBlock().getType() == GOLD_ORE) {
                mat = GOLD_INGOT;
            } else if(evt.getBlock().getType() == COBBLESTONE) {
                mat = STONE;
            } else if(evt.getBlock().getType() == SPONGE && s == 1) {
                mat = SPONGE;
            } else if(evt.getBlock().getType() == MOSSY_COBBLESTONE) {
                mat = SMOOTH_BRICK;
                itemInfo = 1;
            } else if(evt.getBlock().getType() == NETHERRACK) {
                mat = NETHER_BRICK_ITEM;
            } else if(evt.getBlock().getType() == SMOOTH_BRICK && evt.getBlock().getData() == 0) {
                mat = SMOOTH_BRICK;
                itemInfo = 2;
            }
        }
        if(evt.getBlock().getType() == GOLD_ORE || evt.getBlock().getType() == IRON_ORE) {
            ExperienceOrb o = (ExperienceOrb) evt.getBlock().getWorld()
                                                 .spawnEntity(Utilities.getCenter(evt.getBlock()), EXPERIENCE_ORB);
            o.setExperience(
                    evt.getBlock().getType() == IRON_ORE ? Storage.rnd.nextInt(5) + 1 : Storage.rnd.nextInt(5) + 3);
        }
        if(evt.getBlock().getType() == SAND) {
            mat = GLASS;
        } else if(evt.getBlock().getType() == LOG || evt.getBlock().getType() == LOG_2) {
            mat = COAL;
            itemInfo = 1;
        } else if(evt.getBlock().getType() == CLAY) {
            Utilities.display(Utilities.getCenter(evt.getBlock()), Particle.FLAME, 10, .1f, .5f, .5f, .5f);
            for(int x = 0; x < 4; x++) {
                evt.getBlock().getWorld()
                   .dropItemNaturally(Utilities.getCenter(evt.getBlock()), new ItemStack(CLAY_BRICK));
            }

            Block affectedBlock = evt.getBlock();
            Storage.fireDropLocs.add(affectedBlock);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
                Storage.fireDropLocs.remove(affectedBlock);
            }, 5);

            return true;
        } else if(evt.getBlock().getType() == CACTUS) {
            Location location = evt.getBlock().getLocation().clone();
            double height = location.getY();
            for(double i = location.getY(); i <= 256; i++) {
                location.setY(i);
                if(location.getBlock().getType() == CACTUS) {
                    height++;
                } else {
                    break;
                }
            }
            for(double i = height - 1; i >= evt.getBlock().getLocation().getY(); i--) {
                location.setY(i);
                Utilities.display(Utilities.getCenter(evt.getBlock()), Particle.FLAME, 10, .1f, .5f, .5f, .5f);

                evt.getBlock().getWorld()
                   .dropItemNaturally(Utilities.getCenter(location), new ItemStack(INK_SACK, 1, (short) 2));

                Block affectedBlock = evt.getBlock();
                Storage.fireDropLocs.add(affectedBlock);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
                    Storage.fireDropLocs.remove(affectedBlock);
                }, 5);

                return true;
            }
        }
        if(mat != AIR) {

            evt.getBlock().getWorld()
               .dropItemNaturally(Utilities.getCenter(evt.getBlock()), new ItemStack((mat), 1, itemInfo));
            Utilities.display(Utilities.getCenter(evt.getBlock()), Particle.FLAME, 10, .1f, .5f, .5f, .5f);

            Block affectedBlock = evt.getBlock();
            Storage.fireDropLocs.add(affectedBlock);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
                Storage.fireDropLocs.remove(affectedBlock);
            }, 5);

            return true;
        }
        return false;
    }
}
