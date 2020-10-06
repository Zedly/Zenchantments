package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.Set;

public final class Spikes extends Zenchantment {
    public static final String KEY = "spikes";

    private static final String                             NAME        = "Spikes";
    private static final String                             DESCRIPTION = "Damages entities the player jumps onto";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.NONE;

    private final NamespacedKey key;

    public Spikes(
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
    public boolean onFastScan(@NotNull Player player, int level, boolean usedHand) {
        if (!(player.getVelocity().getY() < -0.45)) {
            return true;
        }

        for (Entity entity : player.getNearbyEntities(1, 2, 1)) {
            double fall = Math.min(player.getFallDistance(), 20.0);
            if (entity instanceof LivingEntity) {
                ADAPTER.attackEntity((LivingEntity) entity, player, this.getPower() * level * fall * 0.25);
            }
        }

        return true;
    }
}