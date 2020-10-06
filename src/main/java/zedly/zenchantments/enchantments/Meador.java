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
    public boolean onScan(@NotNull Player player, int level, boolean usedHand) {
        float speed = (float) Math.min(0.5f + level * this.getPower() * 0.05f, 1);

        player.setWalkSpeed(speed);
        player.setFlySpeed(speed);

        player.setMetadata("ze.speed", new FixedMetadataValue(this.getPlugin(), System.currentTimeMillis()));

        Utilities.addPotion(player, JUMP, 610, (int) Math.round(this.getPower() * level + 2));

        return true;
    }
}