package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Material.*;
import static org.bukkit.entity.EntityType.EXPERIENCE_ORB;

public final class Extraction extends Zenchantment {
    public static final String KEY = "extraction";

    private static final String                             NAME        = "Extraction";
    private static final String                             DESCRIPTION = "Smelts and yields more product from ores";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of(Switch.class);
    private static final Hand                               HAND_USE    = Hand.LEFT;
    private static final Map<Material, Material> ORE_SMELT_MAP = Map.of(
        Material.IRON_ORE, IRON_INGOT,
        Material.GOLD_ORE, GOLD_INGOT,
        Material.COPPER_ORE, COPPER_INGOT,
        Material.DEEPSLATE_IRON_ORE, IRON_INGOT,
        Material.DEEPSLATE_GOLD_ORE, GOLD_INGOT,
        Material.DEEPSLATE_COPPER_ORE, COPPER_INGOT
    );
    private static final Map<Material, Integer> ORE_EXPERIENCE_MAP = Map.of(
        Material.IRON_ORE, 1,
        Material.GOLD_ORE, 3,
        Material.COPPER_ORE, 2,
        Material.DEEPSLATE_IRON_ORE, 1,
        Material.DEEPSLATE_GOLD_ORE, 3,
        Material.DEEPSLATE_COPPER_ORE, 2
    );


    private final NamespacedKey key;

    public Extraction(
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
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return false;
        }

        final Block block = event.getBlock();

        if (!ORE_SMELT_MAP.containsKey(block.getType())) {
            return false;
        }

        Utilities.damageItemStack(event.getPlayer(), 1, usedHand);

        for (int x = 0; x < ThreadLocalRandom.current().nextInt((int) Math.round(this.getPower() * level + 1)) + 1; x++) {
            block.getWorld().dropItemNaturally(
                event.getBlock().getLocation(),
                new ItemStack(ORE_SMELT_MAP.get(block.getType()))
            );
        }

        final ExperienceOrb experienceOrb = (ExperienceOrb) block.getWorld().spawnEntity(
            event.getBlock().getLocation(),
            EXPERIENCE_ORB
        );

        final int experience = ThreadLocalRandom.current().nextInt(5);
        experienceOrb.setExperience(experience + ORE_EXPERIENCE_MAP.get(block.getType()));

        block.setType(AIR);

        Utilities.displayParticle(block.getLocation(), Particle.FLAME, 10, 0.1f, 0.5f, 0.5f, 0.5f);

        return true;
    }
}
