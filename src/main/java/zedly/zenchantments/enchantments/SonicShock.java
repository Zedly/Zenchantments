package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.Set;

public final class SonicShock extends Zenchantment {
    public static final String KEY = "sonic_shock";

    private static final String                             NAME        = "Sonic Shock";
    private static final String                             DESCRIPTION = "Damages mobs when flying past at high speed";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.NONE;

    private final NamespacedKey key;

    public SonicShock(
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
    public boolean onFastScan(@NotNull Player player, int level, boolean usedHand) {
        if (!player.isGliding() || !(player.getVelocity().length() >= 1)) {
            return true;
        }

        for (Entity entity : player.getNearbyEntities(2 + 2 * level, 4, 2 + 2 * level)) {
            if (entity instanceof Monster) {
                double damage = player.getVelocity().length() * 1.5 * level * this.getPower();
                this.getPlugin().getCompatibilityAdapter().attackEntity((LivingEntity) entity, player, damage);
            }
        }

        return true;
    }
}
