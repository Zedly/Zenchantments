package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.compatibility.EnumStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Material.*;
import static org.bukkit.entity.EntityType.EXPERIENCE_ORB;

public class Fire extends Zenchantment {
    public static final String KEY = "fire";

    // Locations where Fire has been used on a block and the drop was changed. 
    // BlockBreakEvent is not cancelled but the original item drop is not desired.
    public static final Set<Block> CANCELLED_ITEM_DROPS = new HashSet<>();

    private static final String                             NAME        = "Fire";
    private static final String                             DESCRIPTION = "Drops the smelted version of the block broken";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of(Switch.class, Variety.class);
    private static final Hand                               HAND_USE    = Hand.LEFT;

    private static final int     MAX_BLOCKS          = 256;
    private static final int[][] SEARCH_FACES_CACTUS = new int[][] {new int[] {0, 1, 0}};
    private static final int[][] SEARCH_FACES_CHORUS = new int[][] {
        new int[] {-1, 0, 0},
        new int[] {1, 0, 0},
        new int[] {0, 1, 0},
        new int[] {0, 0, -1},
        new int[] {0, 0, 1}
    };

    private final NamespacedKey key;

    public Fire(
        @NotNull ZenchantmentsPlugin plugin,
        @NotNull Set<Tool> enchantable,
        int maxLevel,
        int cooldown,
        double power,
        float probability
    ) {
        super(plugin, enchantable, maxLevel, cooldown, power, probability);
        this.key = new NamespacedKey(plugin, Fire.KEY);
    }

    @Override
    @NotNull
    public NamespacedKey getKey() {
        return this.key;
    }

    @Override
    @NotNull
    public String getName() {
        return Fire.NAME;
    }

    @Override
    @NotNull
    public String getDescription() {
        return Fire.DESCRIPTION;
    }

    @Override
    @NotNull
    public Set<Class<? extends Zenchantment>> getConflicting() {
        return Fire.CONFLICTING;
    }

    @Override
    @NotNull
    public Hand getHandUse() {
        return Fire.HAND_USE;
    }

    @Override
    public boolean onBlockBreak(@NotNull BlockBreakEvent event, int level, boolean usedHand) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return false;
        }

        ItemStack hand = Utilities.usedStack(event.getPlayer(), usedHand);
        Block block = event.getBlock();
        Material original = block.getType();
        Material material = AIR;

        if (Tool.PICKAXE.contains(hand.getType())) {
            if (Storage.COMPATIBILITY_ADAPTER.FireRaw().contains(original)) {
                material = Storage.COMPATIBILITY_ADAPTER.FireCooked().get(
                    Storage.COMPATIBILITY_ADAPTER.FireRaw().indexOf(original)
                );
            }
            if (original == GOLD_ORE || original == IRON_ORE) {
                ExperienceOrb experienceOrb = (ExperienceOrb) block.getWorld().spawnEntity(
                    Utilities.getCenter(block),
                    EXPERIENCE_ORB
                );
                int experience = ThreadLocalRandom.current().nextInt(5);
                experienceOrb.setExperience(original == IRON_ORE ? experience + 1 : experience + 3);
            }
        }

        if (original == WET_SPONGE) {
            material = SPONGE;
        } else if (Storage.COMPATIBILITY_ADAPTER.Sands().contains(original)) {
            material = GLASS;
        } else if (Storage.COMPATIBILITY_ADAPTER.Logs().contains(original)
            || Storage.COMPATIBILITY_ADAPTER.StrippedLogs().contains(original)
            || Storage.COMPATIBILITY_ADAPTER.StrippedWoods().contains(original)
            || Storage.COMPATIBILITY_ADAPTER.Woods().contains(original)
        ) {
            material = CHARCOAL;
        } else if (original == CLAY) {
            return this.handleClay(block);
        } else if (original == CACTUS) {
            return this.handleCactus(block);
        } else if (original == CHORUS_PLANT) {
            return this.handleChorusPlant(block, event.getPlayer());
        }

        if (material != AIR) {
            return this.handleEverythingElse(block, material);
        }

        return false;
    }

    private boolean handleClay(@NotNull Block block) {
        Utilities.display(Utilities.getCenter(block), Particle.FLAME, 10, 0.1f, 0.5f, 0.5f, 0.5f);

        ItemStack brickStack = new ItemStack(BRICK);
        for (int x = 0; x < 4; x++) {
            block.getWorld().dropItemNaturally(block.getLocation(), brickStack);
        }

        CANCELLED_ITEM_DROPS.add(block);

        this.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(
            this.getPlugin(),
            () -> CANCELLED_ITEM_DROPS.remove(block),
            5
        );

        return true;
    }

    private boolean handleCactus(@NotNull Block block) {
        List<Block> blocks = Utilities.bfs(
            block,
            MAX_BLOCKS,
            false,
            256,
            SEARCH_FACES_CACTUS,
            new EnumStorage<>(new Material[] {CACTUS}),
            new EnumStorage<>(new Material[] {}),
            false,
            true
        );

        for (int i = blocks.size() - 1; i >= 0; i--) {
            Block bfsBlock = blocks.get(i);

            Utilities.display(Utilities.getCenter(bfsBlock), Particle.FLAME, 10, 0.1f, 0.5f, 0.5f, 0.5f);

            block.getWorld().dropItemNaturally(
                Utilities.getCenter(bfsBlock.getLocation()),
                new ItemStack(Storage.COMPATIBILITY_ADAPTER.Dyes().get(13), 1)
            );

            bfsBlock.setType(AIR);

            CANCELLED_ITEM_DROPS.add(bfsBlock);

            this.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(
                this.getPlugin(),
                () -> CANCELLED_ITEM_DROPS.remove(bfsBlock),
                5
            );

        }
        return true;
    }

    private boolean handleChorusPlant(@NotNull Block block, @NotNull Player player) {
        List<Block> blocks = Utilities.bfs(
            block,
            MAX_BLOCKS,
            false,
            256,
            SEARCH_FACES_CHORUS,
            new EnumStorage<>(new Material[] {CHORUS_PLANT, CHORUS_FLOWER}),
            new EnumStorage<>(new Material[] {}),
            false,
            true
        );

        for (int i = blocks.size() - 1; i >= 0; i--) {
            Block bfsBlock = blocks.get(i);

            Utilities.display(Utilities.getCenter(bfsBlock), Particle.FLAME, 10, 0.1f, 0.5f, 0.5f, 0.5f);

            if (bfsBlock.getType().equals(CHORUS_PLANT)) {
                block.getWorld().dropItemNaturally(
                    Utilities.getCenter(bfsBlock.getLocation()),
                    new ItemStack(CHORUS_FRUIT, 1)
                );

                bfsBlock.setType(AIR);
            } else {
                if (!Storage.COMPATIBILITY_ADAPTER.breakBlockNMS(bfsBlock, player)) {
                    return false;
                }
            }

            CANCELLED_ITEM_DROPS.add(bfsBlock);

            this.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(
                this.getPlugin(),
                () -> CANCELLED_ITEM_DROPS.remove(bfsBlock),
                5
            );
        }

        return true;
    }

    private boolean handleEverythingElse(@NotNull Block block, @NotNull Material material) {
        block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(material, 1));

        Utilities.display(Utilities.getCenter(block), Particle.FLAME, 10, 0.1f, 0.5f, 0.5f, 0.5f);

        CANCELLED_ITEM_DROPS.add(block);

        this.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(
            this.getPlugin(),
            () -> CANCELLED_ITEM_DROPS.remove(block),
            5
        );

        return true;
    }
}