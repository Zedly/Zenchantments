package zedly.zenchantments.enchantments;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

@AZenchantment(runInSlots = Slots.MAIN_HAND, conflicting = {RainbowSlam.class})
public final class Force extends Zenchantment {
    @Override
    public boolean onBlockInteract(final @NotNull PlayerInteractEvent event, final int level, final EquipmentSlot slot) {
        final Player player = event.getPlayer();

        if (!event.getPlayer().hasMetadata("ze.direction")) {
            player.setMetadata("ze.direction", new FixedMetadataValue(ZenchantmentsPlugin.getInstance(), true));
        }

        if (player.isSneaking() && (event.getAction() == RIGHT_CLICK_AIR || event.getAction() == RIGHT_CLICK_BLOCK)) {
            boolean mode = !player.getMetadata("ze.direction").get(0).asBoolean();
            player.setMetadata("ze.direction", new FixedMetadataValue(ZenchantmentsPlugin.getInstance(), mode));
            player.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + (mode ? "Push Mode" : "Pull Mode"));
            return false;
        }

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }

        final List<Entity> nearbyEntities = player.getNearbyEntities(5, 5, 5);
        if (nearbyEntities.isEmpty()) {
            return false;
        }

        if (player.getFoodLevel() < 2) {
            return false;
        }

        if (ThreadLocalRandom.current().nextInt(10) == 5) {
            final FoodLevelChangeEvent foodLevelChangeEvent = new FoodLevelChangeEvent(player, 2);
            ZenchantmentsPlugin.getInstance().getServer().getPluginManager().callEvent(foodLevelChangeEvent);
            if (!foodLevelChangeEvent.isCancelled()) {
                player.setFoodLevel(player.getFoodLevel() - 2);
            }
        }

        for (final Entity entity : nearbyEntities) {
            final Location playerLocation = player.getLocation();
            final Location entityLocation = entity.getLocation();
            final Location total = player.getMetadata("ze.direction").get(0).asBoolean()
                ? entityLocation.subtract(playerLocation)
                : playerLocation.subtract(entityLocation);
            final Vector vector = new Vector(
                total.getX(),
                total.getY(),
                total.getZ()
            );

            vector.multiply((0.1f + (this.getPower() * level * 0.2f)));
            vector.setY(vector.getY() > 1 ? 1 : -1);

            if (entity instanceof LivingEntity
                && WorldInteractionUtil.attackEntity((LivingEntity) entity, player, 0)
            ) {
                entity.setVelocity(vector);
            }
        }

        return true;
    }
}
