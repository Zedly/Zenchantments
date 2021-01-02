package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public final class Force extends Zenchantment {
    public static final String KEY = "force";

    private static final String                             NAME        = "Force";
    private static final String                             DESCRIPTION = "Pushes and pulls nearby mobs, configurable through shift clicking";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of(RainbowSlam.class, Gust.class);
    private static final Hand                               HAND_USE    = Hand.RIGHT;

    private final NamespacedKey key;

    public Force(
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
        Player player = event.getPlayer();

        if (!event.getPlayer().hasMetadata("ze.direction")) {
            player.setMetadata("ze.direction", new FixedMetadataValue(this.getPlugin(), true));
        }

        if (player.isSneaking() && (event.getAction() == RIGHT_CLICK_AIR || event.getAction() == RIGHT_CLICK_BLOCK)) {
            boolean mode = !player.getMetadata("ze.direction").get(0).asBoolean();
            player.setMetadata("ze.direction", new FixedMetadataValue(this.getPlugin(), mode));
            player.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + (mode ? "Push Mode" : "Pull Mode"));
            return false;
        }

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }

        List<Entity> nearbyEntities = player.getNearbyEntities(5, 5, 5);
        if (nearbyEntities.isEmpty()) {
            return false;
        }

        if (player.getFoodLevel() < 2) {
            return false;
        }

        if (ThreadLocalRandom.current().nextInt(10) == 5) {
            FoodLevelChangeEvent foodLevelChangeEvent = new FoodLevelChangeEvent(player, 2);
            this.getPlugin().getServer().getPluginManager().callEvent(foodLevelChangeEvent);
            if (!foodLevelChangeEvent.isCancelled()) {
                player.setFoodLevel(player.getFoodLevel() - 2);
            }
        }

        for (Entity entity : nearbyEntities) {
            Location playerLocation = player.getLocation();
            Location entityLocation = entity.getLocation();
            Location total = player.getMetadata("ze.direction").get(0).asBoolean()
                ? entityLocation.subtract(playerLocation)
                : playerLocation.subtract(entityLocation);
            Vector vector = new Vector(
                total.getX(),
                total.getY(),
                total.getZ()
            );

            vector.multiply((0.1f + (this.getPower() * level * 0.2f)));
            vector.setY(vector.getY() > 1 ? 1 : -1);

            if (entity instanceof LivingEntity
                && this.getPlugin().getCompatibilityAdapter().attackEntity((LivingEntity) entity, player, 0)
            ) {
                entity.setVelocity(vector);
            }
        }

        return true;
    }
}
