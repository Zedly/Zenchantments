package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Map;
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
    public boolean onScan(final @NotNull Player player, final int level, final boolean usedHand) {
        final Location location = player.getLocation().clone();
        final Block centerBlock = location.getBlock();
        final int radius = (int) Math.round(this.getPower() * level + 2);

        for (int x = -(radius); x <= radius; x++) {
            for (int y = -(radius) - 1; y <= radius - 1; y++) {
                for (int z = -(radius); z <= radius; z++) {
                    final Block relativeBlock = centerBlock.getRelative(x, y, z);
                    if (!(relativeBlock.getLocation().distance(location) < radius)) {
                        continue;
                    }

                    if (level != 10 && ThreadLocalRandom.current().nextInt((int) (300 / (this.getPower() * level / 2))) != 0) {
                        continue;
                    }

                    boolean applied = false;
                    if (relativeBlock.getType() != DIRT) {
                        applied = this.getPlugin().getCompatibilityAdapter().grow(centerBlock.getRelative(x, y, z), player);
                    } else {
                        if (MaterialList.AIR.contains(relativeBlock.getRelative(0, 1, 0).getType())) {
                            final Material material;
                            switch (centerBlock.getBiome()) {
                                case MUSHROOM_FIELD_SHORE:
                                case MUSHROOM_FIELDS:
                                    material = MYCELIUM;
                                    break;
                                case GIANT_SPRUCE_TAIGA:
                                case GIANT_TREE_TAIGA:
                                case GIANT_SPRUCE_TAIGA_HILLS:
                                case GIANT_TREE_TAIGA_HILLS:
                                    material = PODZOL;
                                    break;
                                default:
                                    material = GRASS_BLOCK;
                            }

                            applied = this.getPlugin().getCompatibilityAdapter().placeBlock(relativeBlock, player, material, null);
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

                        final ItemStack[] armour = player.getInventory().getArmorContents();
                        for (int i = 0; i < 4; i++) {
                            if (armour[i] != null) {
                                final Map<Zenchantment, Integer> map = Zenchantment.getZenchantmentsOnItemStack(
                                    armour[i],
                                    this.getPlugin().getGlobalConfiguration(),
                                    this.getPlugin().getWorldConfigurationProvider().getConfigurationForWorld(player.getWorld())
                                );

                                if (map.containsKey(this)) {
                                    Utilities.addUnbreaking(player, armour[i], 1);
                                }

                                if (Utilities.getItemStackDamage(armour[i]) > armour[i].getType().getMaxDurability()) {
                                    armour[i] = null;
                                }
                            }
                        }

                        player.getInventory().setArmorContents(armour);
                    }
                }
            }
        }

        return true;
    }
}
