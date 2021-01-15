package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.task.EffectTask;
import zedly.zenchantments.task.Frequency;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.bukkit.Material.*;

public final class NetherStep extends Zenchantment {
    public static final String KEY = "nether_step";

    public static final Map<Location, Long> NETHERSTEP_LOCATIONS = new HashMap<>();

    private static final String                             NAME        = "Nether Step";
    private static final String                             DESCRIPTION = "Allows the player to slowly but safely walk on lava";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of(FrozenStep.class);
    private static final Hand                               HAND_USE    = Hand.RIGHT;

    private final NamespacedKey key;

    public NetherStep(
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
    public boolean onScan(@NotNull Player player, int level, boolean usedHand) {
        if (player.isSneaking() && player.getLocation().getBlock().getType() == LAVA && !player.isFlying()) {
            player.setVelocity(player.getVelocity().setY(.4));
        }

        Block block = player.getLocation().add(0, 0.2, 0).getBlock();
        int radius = (int) Math.round(this.getPower() * level + 2);

        Utilities.selfRemovingArea(SOUL_SAND, LAVA, radius, block, player, NETHERSTEP_LOCATIONS);

        return true;
    }

    @EffectTask(Frequency.MEDIUM_HIGH)
    public static void updateBlocks(final @NotNull ZenchantmentsPlugin plugin) {
        Iterator<Location> iterator = FrozenStep.FROZEN_LOCATIONS.keySet().iterator();
        while (iterator.hasNext()) {
            Location location = iterator.next();
            if (Math.abs(System.nanoTime() - FrozenStep.FROZEN_LOCATIONS.get(location)) > 9E8) {
                location.getBlock().setType(WATER);
                iterator.remove();
            }
        }

        iterator = NETHERSTEP_LOCATIONS.keySet().iterator();
        while (iterator.hasNext()) {
            Location location = iterator.next();
            if (Math.abs(System.nanoTime() - NETHERSTEP_LOCATIONS.get(location)) > 9E8) {
                location.getBlock().setType(LAVA);
                iterator.remove();
            }
        }
    }
}
