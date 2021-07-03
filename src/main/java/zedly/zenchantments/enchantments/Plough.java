package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Objects.*;
import static org.bukkit.Material.*;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public final class Plough extends Zenchantment {
    public static final String KEY = "plough";

    private static final String                             NAME        = "Plough";
    private static final String                             DESCRIPTION = "Tills all soil within a radius";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.RIGHT;

    private final NamespacedKey key;

    public Plough(
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
    public boolean onBlockInteract(final @NotNull PlayerInteractEvent event, final int level, final boolean usedHand) {
        if (event.getAction() != RIGHT_CLICK_BLOCK) {
            return false;
        }

        final Block block = requireNonNull(event.getClickedBlock());
        final Location location = block.getLocation();
        int radiusXZ = (int) Math.round(this.getPower() * level + 2);

        for (int x = -radiusXZ; x <= radiusXZ; x++) {
            for (int y = -2; y <= 0; y++) {
                for (int z = -radiusXZ; z <= radiusXZ; z++) {
                    final Block relativeBlock = block.getRelative(x, y, z);

                    if (!(relativeBlock.getLocation().distanceSquared(location) < radiusXZ * radiusXZ)) {
                        continue;
                    }

                    if (((relativeBlock.getType() != DIRT
                        && relativeBlock.getType() != GRASS_BLOCK
                        && relativeBlock.getType() != MYCELIUM))
                        || !MaterialList.AIR.contains(relativeBlock.getRelative(0, 1, 0).getType())
                    ) {
                        continue;
                    }

                    this.getPlugin()
                        .getCompatibilityAdapter()
                        .placeBlock(relativeBlock, event.getPlayer(), Material.FARMLAND, null);

                    if (ThreadLocalRandom.current().nextBoolean()) {
                        Utilities.damageItemStack(event.getPlayer(), 1, usedHand);
                    }
                }
            }
        }

        return true;
    }
}
