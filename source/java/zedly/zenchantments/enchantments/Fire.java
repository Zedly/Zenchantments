package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Material.*;
import static org.bukkit.entity.EntityType.EXPERIENCE_ORB;

public final class Fire extends Zenchantment {
    public static final String KEY = "fire";

    // Locations where Fire has been used on a block and the drop was changed.
    // BlockBreakEvent is not cancelled but the original item drop is not desired.
    public static final Map<Block, ItemStack> ITEM_DROP_REPLACEMENTS = new HashMap<>();

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
    private static final Map<Material, Integer> ORE_EXPERIENCE_MAP = Map.of(
        Material.IRON_ORE, 1,
        Material.GOLD_ORE, 3,
        Material.COPPER_ORE, 2,
        Material.DEEPSLATE_IRON_ORE, 1,
        Material.DEEPSLATE_GOLD_ORE, 3,
        Material.DEEPSLATE_COPPER_ORE, 2
    );

    private final NamespacedKey key;

    public Fire(
        final @NotNull Set<Tool> enchantable,
        final int maxLevel,
        final int cooldown,
        final double probability,
        final float power
    ) {
        super(enchantable, maxLevel, cooldown, probability, power);
        this.key = new NamespacedKey(ZenchantmentsPlugin.getInstance(), KEY);
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
    public boolean onBlockBreak(final @NotNull BlockBreakEvent event, final int level, final boolean usedHand) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE || !event.isDropItems()) {
            return false;
        }

        final ItemStack hand = Utilities.getUsedItemStack(event.getPlayer(), usedHand);
        final Block block = event.getBlock();
        final Material original = block.getType();

        Material material = AIR;

        if (Tool.PICKAXE.contains(hand.getType())) {
            if(original == STONE) {
                return breakBlockAsPlayerAndChangeDrops(block,
                    event.getPlayer(),
                    hand.containsEnchantment(Enchantment.SILK_TOUCH) ?
                        new ItemStack(SMOOTH_STONE, 1) :
                        new ItemStack(STONE, 1));
            }

            if (MaterialList.FIRE_RAW.contains(original)) {
                material = MaterialList.FIRE_COOKED.get(MaterialList.FIRE_RAW.indexOf(original));

                final ExperienceOrb experienceOrb = (ExperienceOrb) block.getWorld().spawnEntity(
                    event.getBlock().getLocation(),
                    EXPERIENCE_ORB
                );
                final int experience = ThreadLocalRandom.current().nextInt(5);
                experienceOrb.setExperience(experience + ORE_EXPERIENCE_MAP.getOrDefault(block.getType(), 1));
            }
        }

         if (MaterialList.SANDS.contains(original)) {
            material = GLASS;
        } else if (MaterialList.LOGS.contains(original)
            || MaterialList.STRIPPED_LOGS.contains(original)
            || MaterialList.STRIPPED_WOOD.contains(original)
            || MaterialList.WOOD.contains(original)
        ) {
            material = CHARCOAL;
        } else {
             switch (original) {
                 case CLAY -> {
                     return breakBlockAsPlayerAndChangeDrops(block,
                         event.getPlayer(),
                         hand.containsEnchantment(Enchantment.SILK_TOUCH) ?
                             new ItemStack(TERRACOTTA, 1) :
                             new ItemStack(BRICK, 4));
                 }
                 case WET_SPONGE -> material = SPONGE;
                 case CACTUS -> material = GREEN_DYE;
                 case CHORUS_PLANT -> material = POPPED_CHORUS_FRUIT;
             }
         }

        if (material != Material.AIR) {
            return breakBlockAsPlayerAndChangeDrops(block, event.getPlayer(), new ItemStack(material, 1));
        }
        return false;
    }

    private boolean breakBlockAsPlayerAndChangeDrops(Block block, Player player, ItemStack drop) {
        ITEM_DROP_REPLACEMENTS.put(block, drop);
        if(CompatibilityAdapter.instance().breakBlock(block, player)) {
            Utilities.displayParticle(Utilities.getCenter(block), Particle.FLAME, 10, 0.1f, 0.5f, 0.5f, 0.5f);
            ITEM_DROP_REPLACEMENTS.remove(block);
            return true;
        }
        return false;
    }
}
