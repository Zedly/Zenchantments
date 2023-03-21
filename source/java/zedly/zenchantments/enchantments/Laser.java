package zedly.zenchantments.enchantments;

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
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.player.PlayerDataProvider;

import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

@AZenchantment(runInSlots = Slots.MAIN_HAND, conflicting = {})
public final class Laser extends Zenchantment {
    @Override
    public boolean onEntityInteract(@NotNull PlayerInteractEntityEvent event, int level, final EquipmentSlot slot) {
        if (slot == EquipmentSlot.HAND && !event.getPlayer().isSneaking()) {
            this.shoot(event.getPlayer(), level);
            return true;
        }
        return false;
    }

    @Override
    public boolean onBlockInteract(@NotNull PlayerInteractEvent event, int level, final EquipmentSlot slot) {
        if (slot == EquipmentSlot.HAND && !event.getPlayer().isSneaking()
            && (event.getAction() == RIGHT_CLICK_AIR || event.getAction() == RIGHT_CLICK_BLOCK)
        ) {
            this.shoot(event.getPlayer(), level);
            event.setCancelled(true);
            return true;
        }

        return false;
    }

    private void shoot(final @NotNull Player player, final int level) {
        // Avoid conflicting with Lumber zenchantment.
        PlayerDataProvider
            .getDataForPlayer(player)
            .setCooldown(new NamespacedKey(ZenchantmentsPlugin.getInstance(), Zenchantment.keyForClass(Laser.class)), 5);

        final Block block = player.getTargetBlock(null, 6 + (int) Math.round(level * this.getPower() * 3));
        final Location playerLocation = player.getLocation();
        final Location target = Utilities.getCenter(block.getLocation());

        target.setY(target.getY() + .5);
        playerLocation.setY(playerLocation.getY() + 1.1);
        final double d = target.distance(playerLocation);

        for (int i = 0; i < (int) d * 5; i++) {
            final Location particleLocation = target.clone();
            particleLocation.setX(playerLocation.getX() + (i * ((target.getX() - playerLocation.getX()) / (d * 5))));
            particleLocation.setY(playerLocation.getY() + (i * ((target.getY() - playerLocation.getY()) / (d * 5))));
            particleLocation.setZ(playerLocation.getZ() + (i * ((target.getZ() - playerLocation.getZ()) / (d * 5))));

            player.getWorld().spawnParticle(Particle.REDSTONE, particleLocation, 1, new Particle.DustOptions(Color.RED, 0.5f));

            for (final Entity entity : player.getWorld().getNearbyEntities(particleLocation, 0.3, 0.3, 0.3)) {
                if (entity instanceof LivingEntity && entity != player) {
                    if(WorldInteractionUtil
                        .attackEntity((LivingEntity) entity, player, 1 + (level + this.getPower() * 2))) {
                        Utilities.damageItemStackRespectUnbreaking(player, 1, EquipmentSlot.HAND);
                    }
                    return;
                }
            }
        }

        if (WorldInteractionUtil.isBlockSafeToBreak(block)
            && !MaterialList.LASER_BLACKLIST_BLOCKS.contains(block.getType())
        ) {
            WorldInteractionUtil.breakBlock(block, player);
        }
    }
}
