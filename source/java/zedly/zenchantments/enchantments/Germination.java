package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Objects.requireNonNull;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public final class Germination extends Zenchantment {
    public static final String KEY = "germination";

    private static final String NAME = "Germination";
    private static final String DESCRIPTION = "Uses bone meal from the player's inventory to grow nearby plants";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand HAND_USE = Hand.RIGHT;

    private final NamespacedKey key;

    private final HashSet<UUID> pendingOperations = new HashSet<>();

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

        // PlayerInteractEvent means run the method, but a following BlockPlaceEvent means a block is being placed, so don't run the method after all.
        // This is why we need to schedule and possibly cancel the method. IMO this is because Bukkit-implementing servers behave inconsistently
        pendingOperations.add(event.getPlayer().getUniqueId());
        Bukkit.getScheduler().scheduleSyncDelayedTask(ZenchantmentsPlugin.getInstance(), () -> {
            delayedOperationhandler(event.getPlayer(), clickedBlock, location, level, slot);
        }, 0);
        return true;
    }

    @Override
    public boolean onBlockPlace(final @NotNull BlockPlaceEvent event, final int level, final EquipmentSlot slot) {
        pendingOperations.remove(event.getPlayer().getUniqueId());
        return false;
    }

    private void delayedOperationhandler(Player player, final Block clickedBlock, final Location location, final int level, final EquipmentSlot slot) {
        if (!pendingOperations.contains(player.getUniqueId())) {
            return;
        }
        pendingOperations.remove(player.getUniqueId());
        performDelayed(player, clickedBlock, location, level, slot);
    }

    private void performDelayed(Player player, final Block clickedBlock, final Location location, final int level, final EquipmentSlot slot) {
        final int radiusXZ = (int) Math.round(this.getPower() * level + 2);
        final int radiusY = 2;

        ItemStack toolUsed = player.getInventory().getItem(slot);
        int numUsesAvailable = Utilities.getUsesRemainingOnTool(toolUsed);
        int unbreakingLevel = Utilities.getUnbreakingLevel(toolUsed);
        int damageApplied = 0;
        int numBoneMealUsed = 0;
        int numBoneMealAvailable = Utilities.countItems(player.getInventory(), (is) -> {
            return is != null && is.getType() == Material.BONE_MEAL;
        });

        for (int x = -(radiusXZ); x <= radiusXZ; x++) {
            for (int y = -(radiusY) - 1; y <= radiusY - 1; y++) {
                for (int z = -(radiusXZ); z <= radiusXZ; z++) {
                    if (numBoneMealUsed >= numBoneMealAvailable || damageApplied >= numUsesAvailable) {
                        break;
                    }

                    final Block relativeBlock = clickedBlock.getRelative(x, y, z);
                    if (!(relativeBlock.getLocation().distanceSquared(location) < radiusXZ * radiusXZ)) {
                        continue;
                    }

                    if (CompatibilityAdapter.instance().grow(relativeBlock, player)) {
                        numBoneMealUsed++;
                        if (Utilities.decideRandomlyIfDamageToolRespectUnbreaking(unbreakingLevel)) {
                            damageApplied++;
                        }
                    } else {
                        continue;
                    }

                    // Grow it even more so it looks natural
                    if (numBoneMealUsed >= numBoneMealAvailable || damageApplied >= numUsesAvailable) {
                        break;
                    }
                    if (ThreadLocalRandom.current().nextBoolean()) {
                        if (CompatibilityAdapter.instance().grow(relativeBlock, player)) {
                            numBoneMealUsed++;
                            if (Utilities.decideRandomlyIfDamageToolRespectUnbreaking(unbreakingLevel)) {
                                damageApplied++;
                            }
                        } else {
                            continue;
                        }
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
                }
            }
        }

        if (numBoneMealUsed != 0) {
            Utilities.removeMaterialsFromPlayer(player, Material.BONE_MEAL, numBoneMealUsed);
            Utilities.damageItemStackIgnoreUnbreaking(player, damageApplied, slot);
        }
    }

    @Override
    public boolean onBlockPlaceOtherHand(final @NotNull BlockPlaceEvent event, final int level, final EquipmentSlot slot) {
        event.setCancelled(true);
        return false;
    }
}
