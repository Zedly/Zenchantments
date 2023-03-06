package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public class Blanket extends Zenchantment {
    public static final String KEY = "blanket";

    private static final String                             NAME        = "Blanket";
    private static final String                             DESCRIPTION = "Extinguishes all fire in a radius";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();

    private final NamespacedKey key;

    private final HashSet<UUID> pendingOperations = new HashSet<>();

    public Blanket(
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
        return Slots.MAIN_HAND;
    }

    @Override
    public boolean onBlockInteract(final @NotNull PlayerInteractEvent event, final int level, final EquipmentSlot slot) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return false;
        }

        final Block clickedBlock = requireNonNull(event.getClickedBlock());
        final Location location = clickedBlock.getLocation();


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

        for (int x = -radiusXZ; x <= radiusXZ; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -radiusXZ; z <= radiusXZ; z++) {
                    final Block block = location.getBlock().getRelative(x, y, z);

                    if (!(block.getLocation().distanceSquared(location) < radiusXZ * radiusXZ)) {
                        continue;
                    }

                    if(block.getType() != Material.FIRE) {
                        continue;
                    }

                    final boolean blockAltered = CompatibilityAdapter.instance().breakBlock(block, player);
                    if (!blockAltered) {
                        continue;
                    }
                }
            }
        }
    }
}
