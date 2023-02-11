package zedly.zenchantments.arrows;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.task.EffectTask;
import zedly.zenchantments.task.Frequency;

import java.util.*;

public class ZenchantedArrow {
    public static final Map<Entity, ZenchantedArrow>     KILLED_ENTITIES      = new HashMap<>();
    public static final Map<Arrow, Set<ZenchantedArrow>> ADVANCED_PROJECTILES = new HashMap<>();

    private static final Set<ZenchantedArrow> DIE_QUEUE = new HashSet<>();

    private final AbstractArrow arrow;
    private final int level;
    private final double power;

    private int tick;

    public ZenchantedArrow(
        final @NotNull AbstractArrow arrow,
        final int level,
        final double power
    ) {
        this.arrow = arrow;
        this.level = level;
        this.power = power;
    }

    public ZenchantedArrow(final @NotNull AbstractArrow arrow, final int level) {
        this(arrow, level, 1);
    }

    public ZenchantedArrow(final @NotNull AbstractArrow arrow) {
        this(arrow, 0);
    }

    public static void putArrow(
        final @NotNull AbstractArrow arrow,
        final @NotNull ZenchantedArrow zenchantedArrow,
        final @NotNull Player player
    ) {
        final Set<ZenchantedArrow> arrows;

        if (ADVANCED_PROJECTILES.containsKey(arrow)) {
            arrows = ADVANCED_PROJECTILES.get(arrow);
        } else {
            arrows = new HashSet<>();
        }

        arrows.add(zenchantedArrow);
        ADVANCED_PROJECTILES.put(arrow, arrows);
        zenchantedArrow.onLaunch(player, null);
    }

    @NotNull
    public final AbstractArrow getArrow() {
        return this.arrow;
    }

    public final int getLevel() {
        return this.level;
    }

    public final double getPower() {
        return this.power;
    }

    public final int getTick() {
        return this.tick;
    }

    protected final void die() {
        this.die(true);
    }

    protected final void die(boolean removeArrow) {
        this.onDie();

        if (removeArrow) {
            arrow.remove();
        }

        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            if (ADVANCED_PROJECTILES.containsKey(arrow)) {
                ADVANCED_PROJECTILES.get(arrow).remove(this);
                if (ADVANCED_PROJECTILES.get(arrow).isEmpty()) {
                    ADVANCED_PROJECTILES.remove(arrow);
                }
            }
        }, 1);
    }

    private void tick() {
        this.tick++;
        this.onTick();
    }

    public void onLaunch(final @NotNull LivingEntity player, final @Nullable List<String> lore) { }

    protected void onTick() { }

    public void onImpact() {
        this.die(true);
    }

    public void onKill(final @NotNull EntityDeathEvent event) { }

    public boolean onImpact(final @NotNull EntityDamageByEntityEvent event) {
        this.onImpact();
        return true;
    }

    protected void onDie() { }

    @EffectTask(Frequency.MEDIUM_HIGH)
    public static void scanAndReap(final @NotNull ZenchantmentsPlugin plugin) {
        synchronized (ADVANCED_PROJECTILES) {
            for (final Arrow arrow : ADVANCED_PROJECTILES.keySet()) {
                if (arrow.isDead()) {
                    DIE_QUEUE.addAll(ADVANCED_PROJECTILES.get(arrow));
                }

                for (final ZenchantedArrow zenchantedArrow : ADVANCED_PROJECTILES.get(arrow)) {
                    if (zenchantedArrow.getTick() > 600) {
                        DIE_QUEUE.add(zenchantedArrow);
                    }
                }
            }

            for (final ZenchantedArrow zenchantedArrow : DIE_QUEUE) {
                zenchantedArrow.die();
            }

            DIE_QUEUE.clear();
        }
    }

    @EffectTask(Frequency.HIGH)
    public static void doTick(final @NotNull ZenchantmentsPlugin plugin) {
        synchronized (ADVANCED_PROJECTILES) {
            ADVANCED_PROJECTILES.values().forEach((set) -> set.forEach(ZenchantedArrow::tick));
        }
    }
}
