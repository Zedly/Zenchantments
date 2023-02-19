package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.task.EffectTask;
import zedly.zenchantments.task.Frequency;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static org.bukkit.Material.*;

public final class NetherStep extends Zenchantment {
    public static final String KEY = "nether_step";

    public static final Map<Location, Long> NETHERSTEP_LOCATIONS = new HashMap<>();

    private static final String                             NAME        = "Nether Step";
    private static final String                             DESCRIPTION = "Allows the player to slowly but safely walk on lava";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.RIGHT;

    private final NamespacedKey key;

    public NetherStep(
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
    public boolean onScan(final @NotNull Player player, final int level, final EquipmentSlot slot) {
        if (player.isSneaking() && player.getLocation().getBlock().getType() == LAVA && !player.isFlying()) {
            player.setVelocity(player.getVelocity().setY(.4));
        }

        final Block block = player.getLocation().add(0, 0.2, 0).getBlock();
        final int radius = (int) Math.round(this.getPower() * level + 2);

        selfRemovingArea(SOUL_SAND, LAVA, radius, block, player, NETHERSTEP_LOCATIONS);

        return true;
    }

    private void selfRemovingArea(
        final @NotNull Material fill,
        final @NotNull Material check,
        final int radius,
        final @NotNull Block center,
        final @NotNull Player player,
        final @NotNull Map<Location, Long> placed
    ) {
        requireNonNull(fill);
        requireNonNull(check);
        requireNonNull(center);
        requireNonNull(player);
        requireNonNull(placed);

        for (var x = -radius; x <= radius; x++) {
            for (var z = -radius; z <= radius; z++) {
                final var possiblePlatformBlock = center.getRelative(x, -1, z);
                final var possiblePlatformLocation = possiblePlatformBlock.getLocation();

                if (!(possiblePlatformLocation.distanceSquared(center.getLocation()) < radius * radius - 2)) {
                    continue;
                }

                if (placed.containsKey(possiblePlatformLocation)) {
                    placed.put(possiblePlatformLocation, System.nanoTime());
                } else if (
                    possiblePlatformBlock.getType() == check
                        && MaterialList.AIR.contains(possiblePlatformBlock.getRelative(0, 1, 0).getType())
                ) {
                    if (possiblePlatformBlock.getBlockData() instanceof Levelled levelled && levelled.getLevel() != 0) {
                        continue;
                    }

                    if (
                        CompatibilityAdapter.instance()
                            .formBlock(possiblePlatformBlock, fill, player)
                    ) {
                        placed.put(possiblePlatformLocation, System.currentTimeMillis());
                    }
                }
            }
        }
    }

    @EffectTask(Frequency.MEDIUM_HIGH)
    public static void updateBlocks() {
        Iterator<Location> iterator = NETHERSTEP_LOCATIONS.keySet().iterator();
        while (iterator.hasNext()) {
            final Location location = iterator.next();
            if (Math.abs(System.currentTimeMillis() - NETHERSTEP_LOCATIONS.get(location)) > 900) {
                location.getBlock().setType(LAVA);
                iterator.remove();
            }
        }
    }
}
