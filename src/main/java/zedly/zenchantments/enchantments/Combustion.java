package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.Set;

public final class Combustion extends Zenchantment {
    public static final String KEY = "combustion";

    private static final String                             NAME        = "Combustion";
    private static final String                             DESCRIPTION = "Lights attacking entities on fire when player is attacked";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of(Spread.class);
    private static final Hand                               HAND_USE    = Hand.NONE;

    private final NamespacedKey key;

    public Combustion(
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
    public boolean onBeingHit(@NotNull EntityDamageByEntityEvent event, int level, boolean usedHand) {
        Entity entity;

        if (event.getDamager().getType() == EntityType.ARROW) {
            Arrow arrow = (Arrow) event.getDamager();
            if (!(arrow.getShooter() instanceof LivingEntity)) {
                return false;
            }

            entity = (Entity) arrow.getShooter();
        } else {
            entity = event.getDamager();
        }

        return ADAPTER.igniteEntity(entity, (Player) event.getEntity(), (int) (50 * level * this.getPower()));
    }

    public boolean onCombust(@NotNull EntityCombustByEntityEvent event, int level, boolean usedHand) {
        if (ADAPTER.isZombie(event.getCombuster())) {
            event.setDuration(0);
        }

        return false;
    }
}