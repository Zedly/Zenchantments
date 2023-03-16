package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.task.EffectTask;
import zedly.zenchantments.task.Frequency;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Material.AIR;

public final class Glide extends Zenchantment {
    public static final String KEY = "glide";

    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();

    public static final Map<UUID, Double> GLIDE_USERS = new HashMap<>();

    public Glide(
        final @NotNull Set<Tool> enchantable,
        final int maxLevel,
        final int cooldown,
        final double probability,
        final float power
    ) {
        super(enchantable, maxLevel, cooldown, probability, power, CONFLICTING, KEY);
    }

    @Override
    public Collection<EquipmentSlot> getApplyToSlots() {
        return Slots.ARMOR;
    }

    @Override
    public boolean onFastScan(final @NotNull Player player, final int level, final EquipmentSlot slot) {
        final UUID uniqueId = player.getUniqueId();
        final Location location = player.getLocation();

        if (!GLIDE_USERS.containsKey(uniqueId)) {
            GLIDE_USERS.put(uniqueId, location.getY());
        }

        if (!player.isSneaking() || GLIDE_USERS.get(uniqueId) == location.getY()) {
            return false;
        }

        boolean safeFall = false;

        for (int i = -5; i < 0; i++) {
            if (location.getBlock().getRelative(0, i, 0).getType() != AIR) {
                safeFall = true;
            }
        }

        if (player.getVelocity().getY() > -0.5) {
            safeFall = true;
        }

        if (!safeFall) {
            final double cosPitch = Math.cos(Math.toRadians(location.getPitch()));
            final double sinYaw = Math.sin(Math.toRadians(location.getYaw()));
            final double cosYaw = Math.cos(Math.toRadians(location.getYaw()));

            final Vector vector = new Vector(-cosPitch * sinYaw, 0, -1 * (-cosPitch * cosYaw));
            vector.multiply(level * this.getPower() / 2);
            vector.setY(-1);

            player.setVelocity(vector);
            player.setFallDistance((float) (6 - level * this.getPower()) - 4);

            final Location particleLocation = location.clone();
            particleLocation.setY(particleLocation.getY() - 3);
            Utilities.displayParticle(particleLocation, Particle.CLOUD, 1, 0.1f, 0, 0, 0);
        }

        // Gradually damage all armour.
        if (ThreadLocalRandom.current().nextInt(4) == 0) {
            Utilities.damageItemStackRespectUnbreaking(player, 1, slot);
        }

        GLIDE_USERS.put(uniqueId, player.getLocation().getY());

        return true;
    }

    @EffectTask(Frequency.SLOW)
    public static void purgeOfflineGlideUsers() {
        GLIDE_USERS.entrySet().removeIf(entry -> !Bukkit.getServer().getOfflinePlayer(entry.getKey()).isOnline());
    }
}
