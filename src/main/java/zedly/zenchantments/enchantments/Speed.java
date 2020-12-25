package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.Set;

public final class Speed extends Zenchantment {
    public static final String KEY = "speed";

    private static final String                             NAME        = "Speed";
    private static final String                             DESCRIPTION = "Gives the player a speed boost";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of(Meador.class, Weight.class);
    private static final Hand                               HAND_USE    = Hand.NONE;

    private final NamespacedKey key;

    public Speed(
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
    public boolean onScan(@NotNull Player player, int level, boolean usedHand) {
        float speed = (float) Math.min((0.05f * level * this.getPower()) + 0.2f, 1);

        player.setWalkSpeed(speed);
        player.setFlySpeed(speed);
        player.setMetadata("ze.speed", new FixedMetadataValue(this.getPlugin(), System.currentTimeMillis()));

        return true;
    }
}
