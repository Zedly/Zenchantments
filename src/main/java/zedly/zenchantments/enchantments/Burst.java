package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.arrows.MultiArrow;
import zedly.zenchantments.arrows.ZenchantedArrow;

import java.util.Set;

import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public final class Burst extends Zenchantment {
    public static final String KEY = "burst";

    private static final String                             NAME        = "Burst";
    private static final String                             DESCRIPTION = "Rapidly fires arrows in series";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of(Spread.class);
    private static final Hand                               HAND_USE    = Hand.RIGHT;

    private final NamespacedKey key;

    public Burst(
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
    public boolean onBlockInteract(final @NotNull PlayerInteractEvent event, final int level, final boolean usedHand) {
        final Player player = event.getPlayer();
        final ItemStack itemInHand = Utilities.getUsedItemStack(player, usedHand);

        if (event.getAction() != RIGHT_CLICK_AIR && event.getAction() != RIGHT_CLICK_BLOCK) {
            return false;
        }

        boolean result = false;

        for (int i = 0; i <= (int) Math.round((this.getPower() * level) + 1); i++) {
            if ((!itemInHand.containsEnchantment(Enchantment.ARROW_INFINITE) || !Utilities.playerHasMaterial(player, Material.ARROW, 1))
                && !Utilities.removeMaterialsFromPlayer(player, Material.ARROW, 1)
            ) {
                continue;
            }

            result = true;
            Utilities.setItemStackInHand(player, itemInHand, usedHand);

            this.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(this.getPlugin(), () -> {
                final Arrow arrow = player.getWorld().spawnArrow(
                    player.getEyeLocation(),
                    player.getLocation().getDirection(),
                    1,
                    0
                );

                arrow.setShooter(player);

                if (itemInHand.containsEnchantment(Enchantment.ARROW_FIRE)) {
                    arrow.setFireTicks(Integer.MAX_VALUE);
                }

                arrow.setVelocity(player.getLocation().getDirection().normalize().multiply(1.7));

                // Some of the parameters below have been added since this class was last updated.
                // This zenchantment may need more testing to determine whether or not it still works properly.
                final EntityShootBowEvent shootEvent = new EntityShootBowEvent(
                    player,
                    itemInHand,
                    null,
                    arrow,
                    usedHand ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND,
                    1f,
                    false
                );

                final ProjectileLaunchEvent launchEvent = new ProjectileLaunchEvent(arrow);

                this.getPlugin().getServer().getPluginManager().callEvent(shootEvent);
                this.getPlugin().getServer().getPluginManager().callEvent(launchEvent);

                if (shootEvent.isCancelled() || launchEvent.isCancelled()) {
                    arrow.remove();
                } else {
                    arrow.setMetadata("ze.arrow", new FixedMetadataValue(this.getPlugin(), null));
                    arrow.setCritical(true);
                    ZenchantedArrow.putArrow(arrow, new MultiArrow(this.getPlugin(), arrow), player);
                    Utilities.damageItemStack(player, 1, usedHand);
                }
            }, i * 2L);
        }

        return result;
    }
}
