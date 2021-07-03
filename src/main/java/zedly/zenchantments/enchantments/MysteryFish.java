package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.task.EffectTask;
import zedly.zenchantments.task.Frequency;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.entity.EntityType.SQUID;

public final class MysteryFish extends Zenchantment {
    public static final String KEY = "mystery_fish";

    private static final String                             NAME        = "Mystery Fish";
    private static final String                             DESCRIPTION = "Catches water mobs like Squid and Guardians";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.RIGHT;

    private static final Map<Guardian, Player> GUARDIANS_AND_PLAYERS = new HashMap<>();

    private final NamespacedKey key;

    public MysteryFish(
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
    public boolean onPlayerFish(final @NotNull PlayerFishEvent event, final int level, final boolean usedHand) {
        if (!(ThreadLocalRandom.current().nextInt(10) < level * this.getPower())) {
            return true;
        }

        if (event.getCaught() == null) {
            return true;
        }

        final Location location = event.getCaught().getLocation();

        if (ThreadLocalRandom.current().nextBoolean()) {
            event.getPlayer().getWorld().spawnEntity(location, SQUID);
        } else {
            final Guardian guardian = (Guardian) this.getPlugin()
                .getCompatibilityAdapter()
                .spawnGuardian(location, ThreadLocalRandom.current().nextBoolean());

            GUARDIANS_AND_PLAYERS.put(guardian, event.getPlayer());
        }

        return true;
    }

    @EffectTask(Frequency.HIGH)
    public static void moveGuardians(final @NotNull ZenchantmentsPlugin plugin) {
        final Iterator<Guardian> iterator = GUARDIANS_AND_PLAYERS.keySet().iterator();
        while (iterator.hasNext()) {
            final Guardian guardian = iterator.next();
            final Player player = GUARDIANS_AND_PLAYERS.get(guardian);
            if (guardian.getLocation().distance(player.getLocation()) > 2 && guardian.getTicksLived() < 160) {
                guardian.setVelocity(player.getLocation().toVector().subtract(guardian.getLocation().toVector()));
            } else {
                iterator.remove();
            }
        }
    }
}
