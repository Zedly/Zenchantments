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

import java.util.*;

import static java.util.Objects.requireNonNull;
import static org.bukkit.Material.*;

public final class NetherStep extends Zenchantment {
    public static final String KEY = "nether_step";

    public static final Map<Location, Long> NETHERSTEP_LOCATIONS = new HashMap<>();

    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();

    public NetherStep(
        final @NotNull Set<Tool> enchantable,
        final int maxLevel,
        final int cooldown,
        final double probability,
        final float power
    ) {
        super(enchantable, maxLevel, cooldown, probability, power, CONFLICTING, KEY);
    }

    @Override
    public Collection<EquipmentSlot> getApplyToSlots() {
        return Slots.ARMOR;
    }

    @Override
    public boolean onScan(final @NotNull Player player, final int level, final EquipmentSlot slot) {
        if(slot != EquipmentSlot.FEET) {
            return false;
        }
        if (player.isSneaking() && player.getLocation().getBlock().getType() == LAVA && !player.isFlying()) {
            player.setVelocity(player.getVelocity().setY(.4));
        }

        final Block block = player.getLocation().add(0, 0.2, 0).getBlock();
        final int radius = (int) Math.round(this.getPower() * level + 2);

        selfRemovingArea(SOUL_SAND, LAVA, radius, block, player);

        return true;
    }

    private void selfRemovingArea(
        final @NotNull Material fill,
        final @NotNull Material check,
        final int radius,
        final @NotNull Block center,
        final @NotNull Player player
    ) {
        requireNonNull(fill);
        requireNonNull(check);
        requireNonNull(center);
        requireNonNull(player);

        long millis = System.currentTimeMillis();
        for (var x = -radius; x <= radius; x++) {
            for (var z = -radius; z <= radius; z++) {
                final var possiblePlatformBlock = center.getRelative(x, -1, z);
                final var possiblePlatformLocation = possiblePlatformBlock.getLocation();

                if (!(possiblePlatformLocation.distanceSquared(center.getLocation()) < radius * radius - 2)) {
                    continue;
                }

                if (NETHERSTEP_LOCATIONS.containsKey(possiblePlatformLocation)) {
                    NETHERSTEP_LOCATIONS.put(possiblePlatformLocation, millis);
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
                        NETHERSTEP_LOCATIONS.put(possiblePlatformLocation, millis);
                    }
                }
            }
        }
    }

    @EffectTask(Frequency.MEDIUM_HIGH)
    public static void updateBlocks() {
        long millis = System.currentTimeMillis();
        Iterator<Map.Entry<Location, Long>> iterator = NETHERSTEP_LOCATIONS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Location, Long> entry = iterator.next();
            if (millis - entry.getValue() > 900) {
                entry.getKey().getBlock().setType(LAVA);
                iterator.remove();
            }
        }
    }

    public static void cleanUpSoulsandImmediately() {
        Iterator<Map.Entry<Location, Long>> iterator = NETHERSTEP_LOCATIONS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Location, Long> entry = iterator.next();
            entry.getKey().getBlock().setType(LAVA);
        }
        NETHERSTEP_LOCATIONS.clear();
    }
}
