package zedly.zenchantments.enchantments;

import java.util.HashSet;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.compatibility.EnumStorage;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import java.util.List;
import java.util.Set;

import static org.bukkit.Material.*;
import static org.bukkit.entity.EntityType.EXPERIENCE_ORB;
import org.bukkit.entity.Item;

import static zedly.zenchantments.Tool.*;

public class Fire extends Zenchantment {

    private static final int MAX_BLOCKS = 256;

    public static int[][] SEARCH_FACES_CACTUS = new int[][]{new int[]{0, 1, 0}};
    public static int[][] SEARCH_FACES_CHORUS = new int[][]{new int[]{-1, 0, 0}, new int[]{1, 0, 0}, new int[]{0, 1, 0}, new int[]{0, 0, -1}, new int[]{0, 0, 1}};

    public static final int ID = 13;
    
    // Locations where Fire has been used on a block and the drop was changed. 
    // BlockBreakEvent is not cancelled but the original item drop is not desired.
    public static final Set<Block> cancelledItemDrops = new HashSet<>();

    @Override
    public Builder<Fire> defaults() {
        return new Builder<>(Fire::new, ID)
                .maxLevel(1)
                .loreName("Fire")
                .probability(0)
                .enchantable(new Tool[]{PICKAXE, AXE, SHOVEL})
                .conflicting(new Class[]{Switch.class, Variety.class})
                .description("Drops the smelted version of the block broken")
                .cooldown(0)
                .power(-1.0)
                .handUse(Hand.LEFT);
    }

    @Override
    public boolean onBlockBreak(BlockBreakEvent evt, int level, boolean usedHand) {
        if (evt.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            return false;
        }

        ItemStack hand = Utilities.usedStack(evt.getPlayer(), usedHand);
        Material original = evt.getBlock().getType();
        Material mat = AIR;
        if (Tool.PICKAXE.contains(hand)) {
            if (Storage.COMPATIBILITY_ADAPTER.FireRaw().contains(original)) {
                mat = Storage.COMPATIBILITY_ADAPTER.FireCooked().get(
                        Storage.COMPATIBILITY_ADAPTER.FireRaw().indexOf(original));
            }
            if (original == GOLD_ORE || original == IRON_ORE) {
                ExperienceOrb o
                        = (ExperienceOrb) evt.getBlock().getWorld().spawnEntity(Utilities.getCenter(evt.getBlock()),
                                EXPERIENCE_ORB);
                o.setExperience(original == IRON_ORE ? Storage.rnd.nextInt(5) + 1 : Storage.rnd.nextInt(5) + 3);
            }
        }

        if (original == WET_SPONGE) {
            mat = SPONGE;
        } else if (Storage.COMPATIBILITY_ADAPTER.Sands().contains(original)) {
            mat = GLASS;
        } else if (Storage.COMPATIBILITY_ADAPTER.Logs().contains(original)
                || Storage.COMPATIBILITY_ADAPTER.StrippedLogs().contains(original)
                || Storage.COMPATIBILITY_ADAPTER.StrippedWoods().contains(original)
                || Storage.COMPATIBILITY_ADAPTER.Woods().contains(original)) {
            mat = CHARCOAL;
        } else if (original == CLAY) {
            Utilities.display(Utilities.getCenter(evt.getBlock()), Particle.FLAME, 10, .1f, .5f, .5f, .5f);
            for (int x = 0; x < 4; x++) {
                evt.getBlock().getWorld()
                        .dropItemNaturally(evt.getBlock().getLocation(), new ItemStack(BRICK));
            }

            Block affectedBlock = evt.getBlock();
            cancelledItemDrops.add(affectedBlock);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
                cancelledItemDrops.remove(affectedBlock);
            }, 5);

            return true;
        } else if (original == CACTUS) {
            List<Block> bks = Utilities.bfs(evt.getBlock(), MAX_BLOCKS, false, 256,
                    SEARCH_FACES_CACTUS, new EnumStorage<>(new Material[]{CACTUS}), new EnumStorage<>(new Material[]{}),
                    false, true);

            for (int i = bks.size() - 1; i >= 0; i--) {
                Block block = bks.get(i);

                Utilities.display(Utilities.getCenter(block), Particle.FLAME, 10, .1f, .5f, .5f, .5f);

                evt.getBlock().getWorld().dropItemNaturally(Utilities.getCenter(block.getLocation()),
                        new ItemStack(Storage.COMPATIBILITY_ADAPTER.Dyes().get(13), 1));
                block.setType(AIR);

                cancelledItemDrops.add(block);

                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
                    cancelledItemDrops.remove(block);
                }, 5);

            }
            return true;
        } else if (original == CHORUS_PLANT) {
            List<Block> bks = Utilities.bfs(evt.getBlock(), MAX_BLOCKS, false, 256,
                    SEARCH_FACES_CHORUS, new EnumStorage<>(new Material[]{CHORUS_PLANT, CHORUS_FLOWER}), new EnumStorage<>(new Material[]{}),
                    false, true);

            for (int i = bks.size() - 1; i >= 0; i--) {
                Block block = bks.get(i);

                Utilities.display(Utilities.getCenter(block), Particle.FLAME, 10, .1f, .5f, .5f, .5f);

                if (block.getType().equals(CHORUS_PLANT)) {
                    evt.getBlock().getWorld().dropItemNaturally(Utilities.getCenter(block.getLocation()),
                            new ItemStack(CHORUS_FRUIT, 1));
                    block.setType(AIR);
                } else {
                    if (!Storage.COMPATIBILITY_ADAPTER.breakBlockNMS(block, evt.getPlayer())) {
                        return false;
                    }
                }

                cancelledItemDrops.add(block);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
                    cancelledItemDrops.remove(block);
                }, 5);

            }
            return true;
        }
        if (mat != AIR) {
            Item item = evt.getBlock().getWorld().dropItemNaturally(evt.getBlock().getLocation(), new ItemStack((mat), 1));
            

            Utilities.display(Utilities.getCenter(evt.getBlock()), Particle.FLAME, 10, .1f, .5f, .5f, .5f);
            Block affectedBlock = evt.getBlock();
            cancelledItemDrops.add(affectedBlock);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
                cancelledItemDrops.remove(affectedBlock);
            }, 5);

            return true;
        }
        return false;
    }
}
