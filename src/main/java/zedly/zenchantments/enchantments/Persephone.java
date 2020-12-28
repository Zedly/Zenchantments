package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Material.*;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public final class Persephone extends Zenchantment {
    public static final String KEY = "persephone";

    private static final String                             NAME = "Persephone";
    private static final String                             DESCRIPTION = "Plants seeds from the player's inventory around them";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE = Hand.RIGHT;

    private final NamespacedKey key;

    public Persephone(
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
    public boolean onBlockInteract(@NotNull PlayerInteractEvent event, int level, boolean usedHand) {
        if (event.getAction() != RIGHT_CLICK_BLOCK) {
            return false;
        }

        Player player = event.getPlayer();
        Location location = event.getClickedBlock().getLocation();
        int radiusXZ = (int) Math.round(this.getPower() * level + 2);

        if (!MaterialList.PERSEPHONE_CROPS.contains(event.getClickedBlock().getType())) {
            return false;
        }

        Block block = location.getBlock();
        for (int x = -radiusXZ; x <= radiusXZ; x++) {
            for (int y = -2; y <= 0; y++) {
                for (int z = -radiusXZ; z <= radiusXZ; z++) {
                    if (!(block.getRelative(x, y, z).getLocation().distanceSquared(location) < radiusXZ * radiusXZ)) {
                        continue;
                    }

                    if (block.getRelative(x, y, z).getType() == FARMLAND
                        && MaterialList.AIR.contains(block.getRelative(x, y + 1, z).getType())
                    ) {
                        Inventory inventory = player.getInventory();
                        if (inventory.contains(CARROT)) {
                            if (ADAPTER.placeBlock(block.getRelative(x, y + 1, z), player, CARROTS, null)) {
                                Utilities.removeMaterialsFromPlayer(player, CARROT, 1);
                            }
                        } else if (inventory.contains(POTATO)) {
                            if (ADAPTER.placeBlock(block.getRelative(x, y + 1, z), player, POTATOES, null)) {
                                Utilities.removeMaterialsFromPlayer(player, POTATO, 1);
                            }
                        } else if (inventory.contains(WHEAT_SEEDS)) {
                            if (ADAPTER.placeBlock(block.getRelative(x, y + 1, z), player, WHEAT, null)) {
                                Utilities.removeMaterialsFromPlayer(player, WHEAT_SEEDS, 1);
                            }
                        } else if (inventory.contains(BEETROOT_SEEDS)) {
                            if (ADAPTER.placeBlock(block.getRelative(x, y + 1, z), player, BEETROOTS, null)) {
                                Utilities.removeMaterialsFromPlayer(player, BEETROOT_SEEDS, 1);
                            }
                        }
                    } else if (block.getRelative(x, y, z).getType() == SOUL_SAND
                        && MaterialList.AIR.contains(block.getRelative(x, y + 1, z).getType())
                    ) {
                        if (event.getPlayer().getInventory().contains(NETHER_WART)) {
                            if (ADAPTER.placeBlock(block.getRelative(x, y + 1, z), player, NETHER_WART,
                                null
                            )) {
                                Utilities.removeMaterialsFromPlayer(player, NETHER_WART, 1);
                            }
                        }
                    } else {
                        continue;
                    }

                    if (ThreadLocalRandom.current().nextBoolean()) {
                        Utilities.damageItemStack(player, 1, usedHand);
                    }
                }
            }
        }
        return true;
    }
}
