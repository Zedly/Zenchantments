package zedly.zenchantments.enchantments;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Material.*;

@AZenchantment(runInSlots = Slots.ARMOR, conflicting = {})
public final class GreenThumb extends Zenchantment {
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
