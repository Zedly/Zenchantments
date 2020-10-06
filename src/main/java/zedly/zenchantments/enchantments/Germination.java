package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public final class Germination extends Zenchantment {
    public static final String KEY = "germination";

    private static final String                             NAME        = "Germination";
    private static final String                             DESCRIPTION = "Uses bone meal from the player's inventory to grow nearby plants";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.RIGHT;

    private final NamespacedKey key;

    public Germination(
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

        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        Location location = clickedBlock.getLocation();
        int radiusXZ = (int) Math.round(this.getPower() * level + 2);
        int radiusY = 2;
        boolean applied = false;

        for (int x = -(radiusXZ); x <= radiusXZ; x++) {
            for (int y = -(radiusY) - 1; y <= radiusY - 1; y++) {
                for (int z = -(radiusXZ); z <= radiusXZ; z++) {
                    Block relativeBlock = clickedBlock.getRelative(x, y, z);

                    if (!(relativeBlock.getLocation().distanceSquared(location) < radiusXZ * radiusXZ)
                        || !Utilities.hasItem(player, Material.BONE_MEAL, 1)
                        || !ADAPTER.grow(relativeBlock, player)
                    ) {
                        continue;
                    }

                    applied = true;

                    if (ThreadLocalRandom.current().nextBoolean()) {
                        ADAPTER.grow(relativeBlock, player);
                    }

                    Utilities.display(
                        Utilities.getCenter(relativeBlock),
                        Particle.VILLAGER_HAPPY,
                        30,
                        1f,
                        0.3f,
                        0.3f,
                        0.3f
                    );

                    if (ThreadLocalRandom.current().nextInt(10) <= 3) {
                        Utilities.damageTool(player, 1, usedHand);
                    }

                    Utilities.removeItem(player, Material.BONE_MEAL, 1);
                }
            }
        }

        return applied;
    }
}