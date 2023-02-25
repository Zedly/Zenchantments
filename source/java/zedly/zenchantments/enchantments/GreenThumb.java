package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Material.*;

public final class GreenThumb extends Zenchantment {
    public static final String KEY = "green_thumb";

    private static final String                             NAME        = "Green Thumb";
    private static final String                             DESCRIPTION = "Grows the foliage around the player";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.NONE;

    private final NamespacedKey key;

    public GreenThumb(
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
    public Collection<EquipmentSlot> getApplyToSlots() {
        return Slots.ARMOR;
    }

    @Override
    public boolean onScan(final @NotNull Player player, final int level, final EquipmentSlot slot) {
        final Location location = player.getLocation().clone();
        final Block centerBlock = location.getBlock();
        int radiusSquare = (int) Math.round(this.getPower() * level + 2);
        radiusSquare *= radiusSquare;

        for (int x = -(radiusSquare); x <= radiusSquare; x++) {
            for (int y = -(radiusSquare) - 1; y <= radiusSquare - 1; y++) {
                for (int z = -(radiusSquare); z <= radiusSquare; z++) {
                    final Block relativeBlock = centerBlock.getRelative(x, y, z);
                    if (relativeBlock.getLocation().distanceSquared(location) >= radiusSquare) {
                        continue;
                    }

                    if (level != 10 && ThreadLocalRandom.current().nextInt((int) (300 / (this.getPower() * level / 2))) != 0) {
                        continue;
                    }

                    boolean applied = false;
                    if (relativeBlock.getType() != DIRT) {
                        applied = CompatibilityAdapter.instance().grow(relativeBlock, player);
                    } else {
                        if (MaterialList.AIR.contains(relativeBlock.getRelative(0, 1, 0).getType())) {
                            final Material material;
                            switch (relativeBlock.getBiome()) {
                                case MUSHROOM_FIELDS:
                                    material = MYCELIUM;
                                    break;
                                default:
                                    material = GRASS_BLOCK;
                            }

                            applied = CompatibilityAdapter.instance().placeBlock(relativeBlock, player, material, null);
                        }
                    }

                    // Display particles and damage armour.
                    if (applied) {
                        Utilities.displayParticle(
                            Utilities.getCenter(centerBlock.getRelative(x, y + 1, z)),
                            Particle.VILLAGER_HAPPY,
                            20,
                            1f,
                            0.3f,
                            0.3f,
                            0.3f
                        );

                        final int random = ThreadLocalRandom.current().nextInt(50);
                        if (random <= 42 || level == 10) {
                            continue;
                        }

                        Utilities.damageItemStackRespectUnbreaking(player, 1, slot);
                        //player.getInventory().setArmorContents(armour);
                    }
                }
            }
        }

        return true;
    }
}
