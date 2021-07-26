package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Material.AIR;

public final class Glide extends Zenchantment {
    public static final String KEY = "glide";

    private static final String                             NAME        = "Glide";
    private static final String                             DESCRIPTION = "Gently brings the player back to the ground when sneaking";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.NONE;

    public static final Map<UUID, Double> GLIDE_USERS = new HashMap<>();

    private final NamespacedKey key;

    public Glide(
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
    public boolean onFastScan(final @NotNull Player player, final int level, final boolean usedHand) {
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
        if (ThreadLocalRandom.current().nextInt(5 * level) == 5) {
            final ItemStack[] armour = player.getInventory().getArmorContents();
            for (int i = 0; i < 4; i++) {
                if (armour[i] == null) {
                    continue;
                }

                final Map<Zenchantment, Integer> map = Zenchantment.getZenchantmentsOnItemStack(
                    armour[i],
                    this.getPlugin().getGlobalConfiguration(),
                    this.getPlugin().getWorldConfigurationProvider().getConfigurationForWorld(player.getWorld())
                );

                if (map.containsKey(this)) {
                    Utilities.addUnbreaking(player, armour[i], 1);
                }

                if (Utilities.getItemStackDamage(armour[i]) > armour[i].getType().getMaxDurability()) {
                    armour[i] = null;
                }
            }

            player.getInventory().setArmorContents(armour);
        }

        GLIDE_USERS.put(uniqueId, player.getLocation().getY());

        return true;
    }
}
