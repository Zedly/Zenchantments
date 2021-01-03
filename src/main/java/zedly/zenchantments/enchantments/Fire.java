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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Material.*;
import static org.bukkit.entity.EntityType.EXPERIENCE_ORB;

public final class Fire extends Zenchantment {
    public static final String KEY = "fire";

    // Locations where Fire has been used on a block and the drop was changed. 
    // BlockBreakEvent is not cancelled but the original item drop is not desired.
    public static final Set<Block> CANCELLED_ITEM_DROPS = new HashSet<>();

    private static final String                             NAME        = "Fire";
    private static final String                             DESCRIPTION = "Drops the smelted version of the block broken";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of(Switch.class, Variety.class);
    private static final Hand                               HAND_USE    = Hand.LEFT;

    private static final int     MAX_BLOCKS          = 256;
    private static final int[][] SEARCH_FACES_CACTUS = new int[][] { { 0, 1, 0 } };
    private static final int[][] SEARCH_FACES_CHORUS = new int[][] {
        { -1, 0, 0 },
        { 1, 0, 0 },
        { 0, 1, 0 },
        { 0, 0, -1 },
        { 0, 0, 1 }
    };

    private final NamespacedKey key;

    public Fire(
        final @NotNull ZenchantmentsPlugin plugin,
        final @NotNull Set<Tool> enchantable,
        final int maxLevel,
        final int cooldown,
        final double power,
        final float probability
    ) {
        super(plugin, enchantable, maxLevel, cooldown, power, probability);
        this.key = new NamespacedKey(plugin, KEY);
    }

    @Override
    @NotNull
    public NamespacedKey getKey() {
        return this.key;
    }

    @Override
    @NotNull
    public String getName() {
        return NAME;
    }

    @Override
    @NotNull
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    @NotNull
    public Set<Class<? extends Zenchantment>> getConflicting() {
        return CONFLICTING;
    }

    @Override
    @NotNull
    public Hand getHandUse() {
        return HAND_USE;
    }

    @Override
    public boolean onBlockBreak(@NotNull BlockBreakEvent event, int level, boolean usedHand) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return false;
        }

        ItemStack hand = Utilities.getUsedItemStack(event.getPlayer(), usedHand);
        Block block = event.getBlock();
        Material original = block.getType();
        Material material = AIR;

        if (Tool.PICKAXE.contains(hand.getType())) {
            if (MaterialList.FIRE_RAW.contains(original)) {
                material = MaterialList.FIRE_COOKED.get(MaterialList.FIRE_RAW.indexOf(original));
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
        } else if (MaterialList.SAND.contains(original)) {
            material = GLASS;
        } else if (MaterialList.LOGS.contains(original)
            || MaterialList.STRIPPED_LOGS.contains(original)
            || MaterialList.STRIPPED_WOOD.contains(original)
            || MaterialList.WOOD.contains(original)
        ) {
            material = CHARCOAL;
        } else if (original == CLAY) {
            return this.handleClay(block);
        } else if (original == CACTUS) {
            return this.handleCactus(block);
        } else if (original == CHORUS_PLANT) {
            return this.handleChorusPlant(block, event.getPlayer());
        }

        if (material != Material.AIR) {
            return this.handleEverythingElse(block, material);
        }

        return false;
    }

    private boolean handleClay(@NotNull Block block) {
        Utilities.displayParticle(Utilities.getCenter(block), Particle.FLAME, 10, 0.1f, 0.5f, 0.5f, 0.5f);

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
            MaterialList.CACTUS,
            MaterialList.EMPTY,
            false,
            true
        );

        for (int i = blocks.size() - 1; i >= 0; i--) {
            Block bfsBlock = blocks.get(i);

            Utilities.displayParticle(Utilities.getCenter(bfsBlock), Particle.FLAME, 10, 0.1f, 0.5f, 0.5f, 0.5f);

            block.getWorld().dropItemNaturally(
                Utilities.getCenter(bfsBlock.getLocation()),
                new ItemStack(MaterialList.DYES.get(13), 1)
            );

            bfsBlock.setType(Material.AIR);

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
            MaterialList.CHORUS_PLANTS,
            MaterialList.EMPTY,
            false,
            true
        );

        for (int i = blocks.size() - 1; i >= 0; i--) {
            Block bfsBlock = blocks.get(i);

            Utilities.displayParticle(Utilities.getCenter(bfsBlock), Particle.FLAME, 10, 0.1f, 0.5f, 0.5f, 0.5f);

            if (bfsBlock.getType().equals(CHORUS_PLANT)) {
                block.getWorld().dropItemNaturally(
                    Utilities.getCenter(bfsBlock.getLocation()),
                    new ItemStack(CHORUS_FRUIT, 1)
                );

                bfsBlock.setType(AIR);
            } else {
                if (!this.getPlugin().getCompatibilityAdapter().breakBlock(bfsBlock, player)) {
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

        Utilities.displayParticle(Utilities.getCenter(block), Particle.FLAME, 10, 0.1f, 0.5f, 0.5f, 0.5f);

        CANCELLED_ITEM_DROPS.add(block);

        this.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(
            this.getPlugin(),
            () -> CANCELLED_ITEM_DROPS.remove(block),
            5
        );

        return true;
    }
}
