package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.event.ZenBlockPlaceEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static java.util.Objects.*;
import static org.bukkit.Material.*;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public final class Plough extends Zenchantment {
    public static final String KEY = "plough";

    private static final String                             NAME        = "Plough";
    private static final String                             DESCRIPTION = "Tills all soil within a radius";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.RIGHT;

    private final NamespacedKey key;

    private final HashSet<UUID> pendingOperations = new HashSet<>();

    public Plough(
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

        // PlayerInteractEvent means run the method, but a following BlockPlaceEvent means a block is being placed, so don't run the method after all.
        // This is why we need to schedule and possibly cancel the method. IMO this is because Bukkit-implementing servers behave inconsistently
        pendingOperations.add(event.getPlayer().getUniqueId());
        Bukkit.getScheduler().scheduleSyncDelayedTask(ZenchantmentsPlugin.getInstance(), () -> {
            delayedOperationhandler(event.getPlayer(), clickedBlock, location, level, usedHand);
        }, 0);
        return true;
    }

    @Override
    public boolean onBlockPlace(BlockPlaceEvent evt, final int level, final boolean usedHand) {
        if(!(evt instanceof ZenBlockPlaceEvent)) {
            pendingOperations.remove(evt.getPlayer().getUniqueId());
        }
        return false;
    }

    private void delayedOperationhandler(Player player, final Block clickedBlock, final Location location, final int level, final boolean usedHand) {
        if (!pendingOperations.contains(player.getUniqueId())) {
            return;
        }
        pendingOperations.remove(player.getUniqueId());
        performDelayed(player, clickedBlock, location, level, usedHand);
    }

    private void performDelayed(Player player, final Block clickedBlock, final Location location, final int level, final boolean usedHand) {
        final int radiusXZ = (int) Math.round(this.getPower() * level + 2);
        final int radiusY = 2;

        ItemStack toolUsed = Utilities.getUsedItemStack(player, usedHand);
        int numUsesAvailable = Utilities.getUsesRemainingOnTool(toolUsed);
        int unbreakingLevel = Utilities.getUnbreakingLevel(toolUsed);
        int damageApplied = 0;

        for (int x = -radiusXZ; x <= radiusXZ; x++) {
            for (int y = -radiusY; y <= 0; y++) {
                for (int z = -radiusXZ; z <= radiusXZ; z++) {
                    final Block relativeBlock = clickedBlock.getRelative(x, y, z);

                    if (damageApplied >= numUsesAvailable || !(relativeBlock.getLocation().distanceSquared(location) < radiusXZ * radiusXZ)) {
                        continue;
                    }

                    if (((relativeBlock.getType() != DIRT
                        && relativeBlock.getType() != GRASS_BLOCK
                        && relativeBlock.getType() != MYCELIUM))
                        || !MaterialList.AIR.contains(relativeBlock.getRelative(BlockFace.UP).getType())
                    ) {
                        continue;
                    }

                    CompatibilityAdapter.instance().placeBlock(relativeBlock, player, Material.FARMLAND, null);

                    if (Utilities.decideRandomlyIfDamageToolRespectUnbreaking(unbreakingLevel)) {
                        damageApplied++;
                    }
                }
            }
        }

        if (damageApplied != 0) {
            Utilities.damageItemStackIgnoreUnbreaking(player, damageApplied, usedHand);
        }
    }
}
