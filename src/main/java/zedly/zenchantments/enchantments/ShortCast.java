package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.Set;

public class ShortCast extends Zenchantment {
    public static final String KEY = "short_cast";

    private static final String                             NAME        = "Short Cast";
    private static final String                             DESCRIPTION = "Launches fishing hooks closer in when casting";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of(LongCast.class);
    private static final Hand                               HAND_USE    = Hand.RIGHT;

    private final NamespacedKey key;

    public ShortCast(
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
    public boolean onProjectileLaunch(@NotNull ProjectileLaunchEvent event, int level, boolean usedHand) {
        Entity entity = event.getEntity();
        if (entity.getType() == EntityType.FISHING_HOOK) {
            entity.setVelocity(entity.getVelocity().normalize().multiply((.8f / (level * this.getPower()))));
        }
        return true;
    }
}