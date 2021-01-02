package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.arrows.SiphonArrow;
import zedly.zenchantments.arrows.ZenchantedArrow;

import java.util.Objects;
import java.util.Set;

public final class Siphon extends Zenchantment {
    public static final String KEY = "siphon";

    private static final String                             NAME        = "Siphon";
    private static final String                             DESCRIPTION = "Drains the health of the mob that you attack, giving it to you";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.BOTH;

    private final NamespacedKey key;

    public Siphon(
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
    public boolean onEntityHit(@NotNull EntityDamageByEntityEvent event, int level, boolean usedHand) {
        if (event.getEntity() instanceof LivingEntity
            && this.getPlugin().getCompatibilityAdapter().attackEntity((LivingEntity) event.getEntity(), (Player) event.getDamager(), 0)
        ) {
            Player player = (Player) event.getDamager();
            int difference = (int) Math.round(0.17 * level * this.getPower() * event.getDamage());

            double genericMaxHealth = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();

            while (difference > 0) {
                if (player.getHealth() < genericMaxHealth) {
                    player.setHealth(Math.min(player.getHealth() + 1, genericMaxHealth));
                }

                difference--;
            }
        }
        return true;
    }

    @Override
    public boolean onEntityShootBow(@NotNull EntityShootBowEvent event, int level, boolean usedHand) {
        SiphonArrow arrow = new SiphonArrow(this.getPlugin(), (Arrow) event.getProjectile(), level, this.getPower());
        ZenchantedArrow.putArrow((Arrow) event.getProjectile(), arrow, (Player) event.getEntity());
        return true;
    }
}
