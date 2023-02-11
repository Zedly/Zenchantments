package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.arrows.ToxicArrow;
import zedly.zenchantments.arrows.ZenchantedArrow;
import zedly.zenchantments.task.EffectTask;
import zedly.zenchantments.task.Frequency;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.bukkit.potion.PotionEffectType.CONFUSION;
import static org.bukkit.potion.PotionEffectType.HUNGER;

public final class Toxic extends Zenchantment {
    public static final String KEY = "toxic";

    public static final Map<Player, Integer> HUNGER_PLAYERS = new HashMap<>();

    private static final String                             NAME        = "Toxic";
    private static final String                             DESCRIPTION = "Sickens the target, making them nauseous and unable to eat";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.BOTH;

    private final NamespacedKey key;

    public Toxic(
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
    public boolean onEntityHit(final @NotNull EntityDamageByEntityEvent event, final int level, final boolean usedHand) {
        if (event.getEntity() instanceof LivingEntity &&
            CompatibilityAdapter.instance().attackEntity((LivingEntity) event.getEntity(), (Player) event.getDamager(), 0)
        ) {
            return true;
        }

        final int value = (int) Math.round(level * this.getPower());

        Utilities.addPotionEffect((LivingEntity) event.getEntity(), CONFUSION, 80 + 60 * value, 4);
        Utilities.addPotionEffect((LivingEntity) event.getEntity(), HUNGER, 40 + 60 * value, 4);

        if (!(event.getEntity() instanceof Player)) {
            return true;
        }

        final Player player = (Player) event.getEntity();

        ZenchantmentsPlugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(ZenchantmentsPlugin.getInstance(), () -> {
            player.removePotionEffect(HUNGER);
            Utilities.addPotionEffect(player, HUNGER, 60 + 40 * value, 0);
        }, 20 + 60L * value);

        HUNGER_PLAYERS.put((Player) event.getEntity(), (1 + value) * 100);

        return true;
    }

    @Override
    public boolean onEntityShootBow(final @NotNull EntityShootBowEvent event, final int level, final boolean usedHand) {
        final ToxicArrow arrow = new ToxicArrow((AbstractArrow) event.getProjectile(), level, this.getPower());
        ZenchantedArrow.putArrow((AbstractArrow) event.getProjectile(), arrow, (Player) event.getEntity());
        return true;
    }

    @EffectTask(Frequency.HIGH)
    public static void hunger(final @NotNull ZenchantmentsPlugin plugin) {
        final Iterator<Player> iterator = HUNGER_PLAYERS.keySet().iterator();
        while (iterator.hasNext()) {
            final Player player = iterator.next();
            if (HUNGER_PLAYERS.get(player) < 1) {
                iterator.remove();
            } else {
                HUNGER_PLAYERS.put(player, HUNGER_PLAYERS.get(player) - 1);
            }
        }
    }
}
