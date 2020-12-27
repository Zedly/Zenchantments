package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Arrow;
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
        if (event.getEntity() instanceof LivingEntity &&
            ADAPTER.attackEntity((LivingEntity) event.getEntity(), (Player) event.getDamager(), 0)
        ) {
            return true;
        }

        int value = (int) Math.round(level * this.getPower());

        Utilities.addPotionEffect((LivingEntity) event.getEntity(), CONFUSION, 80 + 60 * value, 4);
        Utilities.addPotionEffect((LivingEntity) event.getEntity(), HUNGER, 40 + 60 * value, 4);

        if (!(event.getEntity() instanceof Player)) {
            return true;
        }

        Player player = (Player) event.getEntity();

        this.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(this.getPlugin(), () -> {
            player.removePotionEffect(HUNGER);
            Utilities.addPotionEffect(player, HUNGER, 60 + 40 * value, 0);
        }, 20 + 60 * value);

        HUNGER_PLAYERS.put((Player) event.getEntity(), (1 + value) * 100);

        return true;
    }

    @Override
    public boolean onEntityShootBow(@NotNull EntityShootBowEvent event, int level, boolean usedHand) {
        ToxicArrow arrow = new ToxicArrow(this.getPlugin(), (Arrow) event.getProjectile(), level, this.getPower());
        ZenchantedArrow.putArrow((Arrow) event.getProjectile(), arrow, (Player) event.getEntity());
        return true;
    }

    @EffectTask(Frequency.HIGH)
    public static void hunger() {
        Iterator<Player> iterator = HUNGER_PLAYERS.keySet().iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            if (HUNGER_PLAYERS.get(player) < 1) {
                iterator.remove();
            } else {
                HUNGER_PLAYERS.put(player, HUNGER_PLAYERS.get(player) - 1);
            }
        }
    }
}
