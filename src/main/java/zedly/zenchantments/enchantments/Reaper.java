package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Set;

import static org.bukkit.potion.PotionEffectType.BLINDNESS;

public final class Reaper extends Zenchantment {
    public static final String KEY = "reaper";

    private static final String                             NAME        = "Reaper";
    private static final String                             DESCRIPTION = "Gives the target temporary wither effect and blindness";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.BOTH;

    private final NamespacedKey key;

    public Reaper(
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
    public boolean onEntityShootBow(@NotNull EntityShootBowEvent event, int level, boolean usedHand) {
        ReaperArrow arrow = new ReaperArrow((Arrow) event.getProjectile(), level, this.getPower());
        EnchantedArrow.putArrow((Arrow) event.getProjectile(), arrow, (Player) event.getEntity());
        return true;
    }

    @Override
    public boolean onEntityHit(@NotNull EntityDamageByEntityEvent event, int level, boolean usedHand) {
        if (!(event.getEntity() instanceof LivingEntity)
            || !ADAPTER.attackEntity((LivingEntity) event.getEntity(), (Player) event.getDamager(), 0)
        ) {
            return true;
        }

        int pow = (int) Math.round(level * this.getPower());
        int dur = (int) Math.round(10 + level * 20 * this.getPower());
        Utilities.addPotion((LivingEntity) event.getEntity(), PotionEffectType.WITHER, dur, pow);
        Utilities.addPotion((LivingEntity) event.getEntity(), BLINDNESS, dur, pow);

        return true;
    }
}