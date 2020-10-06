package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

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
        @NotNull ZenchantmentsPlugin plugin,
        @NotNull Set<Tool> enchantable,
        int maxLevel,
        int cooldown,
        double power,
        float probability
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
    public boolean onBlockInteract(@NotNull PlayerInteractEvent event, int level, boolean usedHand) {
        if (event.getAction() != RIGHT_CLICK_BLOCK) {
            return false;
        }

        Block block = event.getClickedBlock();
        Location location = block.getLocation();
        int radiusXZ = (int) Math.round(this.getPower() * level + 2);

        for (int x = -radiusXZ; x <= radiusXZ; x++) {
            for (int y = -2; y <= 0; y++) {
                for (int z = -radiusXZ; z <= radiusXZ; z++) {
                    Block relative = block.getRelative(x, y, z);

                    if (!(relative.getLocation().distanceSquared(location) < radiusXZ * radiusXZ)) {
                        continue;
                    }

                    if (((relative.getType() != DIRT
                        && relative.getType() != GRASS_BLOCK
                        && relative.getType() != MYCELIUM))
                        || !Storage.COMPATIBILITY_ADAPTER.Airs().contains(relative.getRelative(0, 1, 0).getType())
                    ) {
                        continue;
                    }

                    ADAPTER.placeBlock(block.getRelative(x, y, z), event.getPlayer(), Material.FARMLAND, null);
                    if (ThreadLocalRandom.current().nextBoolean()) {
                        Utilities.damageTool(event.getPlayer(), 1, usedHand);
                    }
                }
            }
        }

        return true;
    }
}