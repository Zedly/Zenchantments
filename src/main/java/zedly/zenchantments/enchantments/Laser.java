package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Set;

import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public final class Laser extends Zenchantment {
    public static final String KEY = "laser";

    private static final String                             NAME        = "Laser";
    private static final String                             DESCRIPTION = "Breaks blocks and damages mobs using a powerful beam of light";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.RIGHT;

    private final NamespacedKey key;

    public Laser(
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
    public boolean onEntityInteract(@NotNull PlayerInteractEntityEvent event, int level, boolean usedHand) {
        if (usedHand && !event.getPlayer().isSneaking()) {
            this.shoot(event.getPlayer(), level);
            return true;
        }

        return false;
    }

    @Override
    public boolean onBlockInteract(@NotNull PlayerInteractEvent event, int level, boolean usedHand) {
        if (usedHand && !event.getPlayer().isSneaking()
            && (event.getAction() == RIGHT_CLICK_AIR || event.getAction() == RIGHT_CLICK_BLOCK)
        ) {
            this.shoot(event.getPlayer(), level);
            return true;
        }

        return false;
    }

    private void shoot(@NotNull Player player, int level) {
        // Avoid conflicting with Lumber zenchantment.
        this.getPlugin()
            .getPlayerDataProvider()
            .getDataForPlayer(player)
            .setCooldown(new NamespacedKey(this.getPlugin(), Lumber.KEY), 5);

        Block block = player.getTargetBlock(null, 6 + (int) Math.round(level * this.getPower() * 3));
        Location playerLocation = player.getLocation();
        Location target = Utilities.getCenter(block.getLocation());

        target.setY(target.getY() + .5);
        playerLocation.setY(playerLocation.getY() + 1.1);
        double d = target.distance(playerLocation);

        for (int i = 0; i < (int) d * 5; i++) {
            Location particleLocation = target.clone();
            particleLocation.setX(playerLocation.getX() + (i * ((target.getX() - playerLocation.getX()) / (d * 5))));
            particleLocation.setY(playerLocation.getY() + (i * ((target.getY() - playerLocation.getY()) / (d * 5))));
            particleLocation.setZ(playerLocation.getZ() + (i * ((target.getZ() - playerLocation.getZ()) / (d * 5))));

            player.getWorld().spawnParticle(Particle.REDSTONE, particleLocation, 1, new Particle.DustOptions(Color.RED, 0.5f));

            for (Entity entity : playerLocation.getWorld().getNearbyEntities(particleLocation, 0.3, 0.3, 0.3)) {
                if (entity instanceof LivingEntity && entity != player) {
                    ADAPTER.attackEntity((LivingEntity) entity, player, 1 + (level + this.getPower() * 2));
                    return;
                }
            }
        }

        if (ADAPTER.isBlockSafeToBreak(block) && !ADAPTER.LaserBlackListBlocks().contains(block.getType())) {
            ADAPTER.breakBlockNMS(block, player);
        }
    }
}
