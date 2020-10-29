package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Set;

import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public final class Harvest extends Zenchantment {
    public static final String KEY = "harvest";

    private static final String                             NAME        = "Harvest";
    private static final String                             DESCRIPTION = "Harvests fully grown crops within a radius when clicked";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.RIGHT;

    private final NamespacedKey key;

    public Harvest(
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

        Location location = event.getClickedBlock().getLocation();
        int radiusXZ = (int) Math.round(this.getPower() * level + 2);
        boolean success = false;

        for (int x = -radiusXZ; x <= radiusXZ; x++) {
            for (int y = -2; y <= 0; y++) {
                for (int z = -radiusXZ; z <= radiusXZ; z++) {

                    final Block block = location.getBlock().getRelative(x, y, z);

                    if (!(block.getLocation().distanceSquared(location) < radiusXZ * radiusXZ)) {
                        continue;
                    }

                    if (!Storage.COMPATIBILITY_ADAPTER.GrownCrops().contains(block.getType())
                        && !Storage.COMPATIBILITY_ADAPTER.GrownMelon().contains(block.getType())
                    ) {
                        continue;
                    }

                    BlockData cropState = block.getBlockData();

                    // Is this block the crop's mature form?
                    boolean harvestReady = !(cropState instanceof Ageable);

                    // Is the mature form not a separate Material but just a particular data value?
                    if (!harvestReady) {
                        Ageable ageable = (Ageable) cropState;
                        harvestReady = ageable.getAge() == ageable.getMaximumAge();
                    }

                    if (!harvestReady) {
                        harvestReady = block.getType() == Material.SWEET_BERRY_BUSH;
                    }

                    if (!harvestReady) {
                        continue;
                    }

                    boolean blockAltered;
                    if (block.getType() == Material.SWEET_BERRY_BUSH) {
                        blockAltered = Storage.COMPATIBILITY_ADAPTER.pickBerries(block, event.getPlayer());
                    } else {
                        blockAltered = ADAPTER.breakBlockNMS(block, event.getPlayer());
                    }

                    if (!blockAltered) {
                        continue;
                    }

                    Utilities.damageItemStack(event.getPlayer(), 1, usedHand);

                    Grab.GRAB_LOCATIONS.put(block, event.getPlayer());

                    this.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(
                        this.getPlugin(),
                        () -> Grab.GRAB_LOCATIONS.remove(block),
                        3
                    );

                    success = true;
                }
            }
        }
        return success;
    }
}