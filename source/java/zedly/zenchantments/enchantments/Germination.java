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

import static java.util.Objects.requireNonNull;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public final class Germination extends Zenchantment {
    public static final String KEY = "germination";

    private static final String                             NAME        = "Germination";
    private static final String                             DESCRIPTION = "Uses bone meal from the player's inventory to grow nearby plants";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.RIGHT;

    private final NamespacedKey key;

    public Germination(
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
    public boolean onBlockInteract(final @NotNull PlayerInteractEvent event, final int level, final boolean usedHand) {
        if (event.getAction() != RIGHT_CLICK_BLOCK) {
            return false;
        }

        final Player player = event.getPlayer();
        final Block clickedBlock = requireNonNull(event.getClickedBlock());
        final Location location = clickedBlock.getLocation();
        final int radiusXZ = (int) Math.round(this.getPower() * level + 2);
        final int radiusY = 2;

        boolean applied = false;

        for (int x = -(radiusXZ); x <= radiusXZ; x++) {
            for (int y = -(radiusY) - 1; y <= radiusY - 1; y++) {
                for (int z = -(radiusXZ); z <= radiusXZ; z++) {
                    final Block relativeBlock = clickedBlock.getRelative(x, y, z);

                    if (!(relativeBlock.getLocation().distanceSquared(location) < radiusXZ * radiusXZ)
                        || !Utilities.playerHasMaterial(player, Material.BONE_MEAL, 1)
                        || !ZenchantmentsPlugin.getInstance().getCompatibilityAdapter().grow(relativeBlock, player)
                    ) {
                        continue;
                    }

                    applied = true;

                    if (ThreadLocalRandom.current().nextBoolean()) {
                        ZenchantmentsPlugin.getInstance().getCompatibilityAdapter().grow(relativeBlock, player);
                    }

                    Utilities.displayParticle(
                        Utilities.getCenter(relativeBlock),
                        Particle.VILLAGER_HAPPY,
                        30,
                        1f,
                        0.3f,
                        0.3f,
                        0.3f
                    );

                    if (ThreadLocalRandom.current().nextInt(10) <= 3) {
                        Utilities.damageItemStack(player, 1, usedHand);
                    }

                    Utilities.removeMaterialsFromPlayer(player, Material.BONE_MEAL, 1);
                }
            }
        }

        return applied;
    }
}
