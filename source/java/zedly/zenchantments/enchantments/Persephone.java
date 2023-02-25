package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static org.bukkit.Material.*;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public final class Persephone extends Zenchantment {
    public static final String KEY = "persephone";

    private static final String                             NAME = "Persephone";
    private static final String                             DESCRIPTION = "Plants seeds from the player's inventory around them";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE = Hand.RIGHT;

    private final NamespacedKey key;

    private final HashSet<UUID> pendingOperations = new HashSet<>();

    public Persephone(
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
        return Slots.HANDS;
    }

    @Override
    public boolean onBlockInteract(final @NotNull PlayerInteractEvent event, final int level, final EquipmentSlot slot) {
        if (event.getAction() != RIGHT_CLICK_BLOCK) {
            return false;
        }

        final Player player = event.getPlayer();
        final Block clickedBlock = requireNonNull(event.getClickedBlock());
        final Location location = clickedBlock.getLocation();

        if (!MaterialList.PERSEPHONE_CROPS.contains(event.getClickedBlock().getType())) {
            return false;
        }

        // PlayerInteractEvent means run the method, but a following BlockPlaceEvent means a block is being placed, so don't run the method after all.
        // This is why we need to schedule and possibly cancel the method. IMO this is because Bukkit-implementing servers behave inconsistently
        pendingOperations.add(event.getPlayer().getUniqueId());
        Bukkit.getScheduler().scheduleSyncDelayedTask(ZenchantmentsPlugin.getInstance(), () -> {delayedOperationhandler(event.getPlayer(), clickedBlock, location, level, slot);}, 0);
        return true;
    }

    @Override
    public boolean onBlockPlaceOtherHand(final @NotNull BlockPlaceEvent event, final int level, final EquipmentSlot slot) {
        pendingOperations.remove(event.getPlayer().getUniqueId());
        return false;
    }

    private void delayedOperationhandler(Player player, final Block clickedBlock, final Location location, final int level, final EquipmentSlot slot) {
        if(!pendingOperations.contains(player.getUniqueId())) {
            return;
        }
        pendingOperations.remove(player.getUniqueId());
        performDelayed(player, clickedBlock, location, level, slot);
    }

    private void performDelayed(Player player, final Block clickedBlock, final Location location, final int level, final EquipmentSlot slot) {
        final int radiusXZ = (int) Math.round(this.getPower() * level + 2);

        final PlayerInventory inventory = player.getInventory();
        ItemStack toolUsed = inventory.getItem(slot);
        int numUsesAvailable = Utilities.getUsesRemainingOnTool(toolUsed);
        int unbreakingLevel = Utilities.getUnbreakingLevel(toolUsed);
        int damageApplied = 0;
        int numWheatSown = 0;
        int numCarrotsSown = 0;
        int numPotatoesSown = 0;
        int numBeetrootsSown = 0;
        int numNetherwartsSown = 0;
        int numWheatAvailable = Utilities.countItems(player.getInventory(), (is) -> is != null && is.getType() == WHEAT_SEEDS);
        int numCarrotsAvailable = Utilities.countItems(player.getInventory(), (is) -> is != null && is.getType() == CARROT);
        int numPotatoesAvailable = Utilities.countItems(player.getInventory(), (is) -> is != null && is.getType() == POTATO);
        int numBeetrootSeedsAvailable = Utilities.countItems(player.getInventory(), (is) -> is != null && is.getType() == BEETROOT_SEEDS);
        int numNetherwartsAvailable = Utilities.countItems(player.getInventory(), (is) -> is != null && is.getType() == NETHER_WART);

        for (int x = -radiusXZ; x <= radiusXZ; x++) {
            for (int y = -2; y <= 0; y++) {
                for (int z = -radiusXZ; z <= radiusXZ; z++) {
                    Block soilBlock = clickedBlock.getRelative(x, y, z);
                    if (damageApplied >= numUsesAvailable || !(soilBlock.getLocation().distanceSquared(location) < radiusXZ * radiusXZ)) {
                        continue;
                    }

                    final Block blockAboveSoil = soilBlock.getRelative(BlockFace.UP);
                    if(!MaterialList.AIR.contains(blockAboveSoil.getType())) {
                        continue;
                    }

                    if (soilBlock.getType() == FARMLAND) {
                        if (numWheatAvailable > numWheatSown) {
                            if (CompatibilityAdapter.instance().placeBlock(blockAboveSoil, player, WHEAT, null)) {
                                numWheatSown++;
                                if (Utilities.decideRandomlyIfDamageToolRespectUnbreaking(unbreakingLevel)) {
                                    damageApplied++;
                                }
                            }
                        } else if (numCarrotsAvailable > numCarrotsSown) {
                            if (CompatibilityAdapter.instance().placeBlock(blockAboveSoil, player, CARROTS, null)) {
                                numCarrotsSown++;
                                if (Utilities.decideRandomlyIfDamageToolRespectUnbreaking(unbreakingLevel)) {
                                    damageApplied++;
                                }
                            }
                        } else if (numPotatoesAvailable > numPotatoesSown) {
                            if (CompatibilityAdapter.instance().placeBlock(blockAboveSoil, player, POTATOES, null)) {
                                numPotatoesSown++;
                                if (Utilities.decideRandomlyIfDamageToolRespectUnbreaking(unbreakingLevel)) {
                                    damageApplied++;
                                }
                            }
                        } else if (numBeetrootSeedsAvailable > numBeetrootsSown) {
                            if (CompatibilityAdapter.instance().placeBlock(blockAboveSoil, player, BEETROOTS, null)) {
                                numBeetrootsSown++;
                                if (Utilities.decideRandomlyIfDamageToolRespectUnbreaking(unbreakingLevel)) {
                                    damageApplied++;
                                }
                            }
                        }
                    } else if (soilBlock.getType() == SOUL_SAND) {
                        if (numNetherwartsAvailable > numNetherwartsSown) {
                            if (CompatibilityAdapter.instance().placeBlock(blockAboveSoil, player, NETHER_WART, null)) {
                                numNetherwartsSown++;
                                if (Utilities.decideRandomlyIfDamageToolRespectUnbreaking(unbreakingLevel)) {
                                    damageApplied++;
                                }
                            }
                        }
                    } else {
                        continue;
                    }
                }
            }
        }

        Utilities.removeMaterialsFromPlayer(player, WHEAT_SEEDS, numWheatSown);
        Utilities.removeMaterialsFromPlayer(player, CARROT, numCarrotsSown);
        Utilities.removeMaterialsFromPlayer(player, POTATO, numPotatoesSown);
        Utilities.removeMaterialsFromPlayer(player, BEETROOT_SEEDS, numBeetrootsSown);
        Utilities.removeMaterialsFromPlayer(player, NETHER_WART, numNetherwartsSown);

        if(damageApplied > 0) {
            Utilities.damageItemStackIgnoreUnbreaking(player, damageApplied, slot);
            // damage tool or not
        }
    }
}
