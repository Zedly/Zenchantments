package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

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

    private final NamespacedKey key;

    public Extraction(
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

        Block block = event.getBlock();

        if (block.getType() != GOLD_ORE && block.getType() != IRON_ORE) {
            return false;
        }

        Utilities.damageItemStack(event.getPlayer(), 1, usedHand);

        for (int x = 0; x < ThreadLocalRandom.current().nextInt((int) Math.round(this.getPower() * level + 1)) + 1; x++) {
            block.getWorld().dropItemNaturally(
                event.getBlock().getLocation(),
                new ItemStack(event.getBlock().getType() == GOLD_ORE ? GOLD_INGOT : IRON_INGOT)
            );
        }

        ExperienceOrb experienceOrb = (ExperienceOrb) block.getWorld().spawnEntity(event.getBlock().getLocation(), EXPERIENCE_ORB);

        int experience = ThreadLocalRandom.current().nextInt(5);
        experienceOrb.setExperience(block.getType() == IRON_ORE ? experience + 1 : experience + 3);

        block.setType(AIR);

        Utilities.displayParticle(block.getLocation(), Particle.FLAME, 10, 0.1f, 0.5f, 0.5f, 0.5f);

        return true;
    }
}
