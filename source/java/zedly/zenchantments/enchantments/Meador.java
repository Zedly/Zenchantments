package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Set;

import static org.bukkit.potion.PotionEffectType.JUMP;

public final class Meador extends Zenchantment {
    public static final String KEY = "meador";

    private static final String                             NAME        = "Meador";
    private static final String                             DESCRIPTION = "Gives the player both a speed and jump boost";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of(Weight.class, Speed.class, Jump.class);
    private static final Hand                               HAND_USE    = Hand.NONE;

    private final NamespacedKey key;

    public Meador(
        final @NotNull Set<Tool> enchantable,
        final int maxLevel,
        final int cooldown,
        final double probability,
        final float power
    ) {
        super(enchantable, maxLevel, cooldown, probability, power);
        this.key = new NamespacedKey(ZenchantmentsPlugin.getInstance(), KEY);
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
    public boolean onScan(final @NotNull Player player, final int level, final boolean usedHand) {
        final float speed = (float) Math.min(0.5f + level * this.getPower() * 0.05f, 1);

        player.setWalkSpeed(speed);
        player.setFlySpeed(speed);

        player.setMetadata("ze.speed", new FixedMetadataValue(ZenchantmentsPlugin.getInstance(), System.currentTimeMillis()));

        Utilities.addPotionEffect(player, JUMP, 610, (int) Math.round(this.getPower() * level + 2));

        return true;
    }
}
