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

public class EnchantedArrow {
    public static final Map<Entity, EnchantedArrow>     KILLED_ENTITIES      = new HashMap<>();
    public static final Map<Arrow, Set<EnchantedArrow>> ADVANCED_PROJECTILES = new HashMap<>();

    private static final Set<EnchantedArrow> DIE_QUEUE = new HashSet<>();

    private final ZenchantmentsPlugin plugin;
    private final Arrow               arrow;
    private final int                 level;
    private final double              power;

    private int tick;

    public EnchantedArrow(@NotNull ZenchantmentsPlugin plugin, @NotNull Arrow arrow, int level, double power) {
        this.plugin = plugin;
        this.arrow = arrow;
        this.level = level;
        this.power = power;
    }

    public EnchantedArrow(@NotNull ZenchantmentsPlugin plugin, @NotNull Arrow arrow, int level) {
        this(plugin, arrow, level, 1);
    }

    public EnchantedArrow(@NotNull ZenchantmentsPlugin plugin, @NotNull Arrow arrow) {
        this(plugin, arrow, 0);
    }

    public static void putArrow(@NotNull Arrow arrow, @NotNull EnchantedArrow enchantedArrow, @NotNull Player player) {
        Set<EnchantedArrow> arrows;

        if (ADVANCED_PROJECTILES.containsKey(arrow)) {
            arrows = ADVANCED_PROJECTILES.get(arrow);
        } else {
            arrows = new HashSet<>();
        }

        arrows.add(enchantedArrow);
        ADVANCED_PROJECTILES.put(arrow, arrows);
        enchantedArrow.onLaunch(player, null);
    }

    @NotNull
    public ZenchantmentsPlugin getPlugin() {
        return this.plugin;
    }

    @NotNull
    public Arrow getArrow() {
        return this.arrow;
    }

    public int getLevel() {
        return this.level;
    }

    public double getPower() {
        return this.power;
    }

    public int getTick() {
        return this.tick;
    }

    protected void die() {
        this.die(true);
    }

    protected void die(boolean removeArrow) {
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

    public void onLaunch(@NotNull LivingEntity player, @Nullable List<String> lore) {
    }

    protected void onTick() {
    }

    public void onImpact() {
        die(true);
    }

    public void onKill(@NotNull EntityDeathEvent event) {
    }

    public boolean onImpact(@NotNull EntityDamageByEntityEvent event) {
        this.onImpact();
        return true;
    }

    protected void onDie() {
    }

    @EffectTask(Frequency.MEDIUM_HIGH)
    public static void scanAndReap() {
        synchronized (ADVANCED_PROJECTILES) {
            for (Arrow arrow : ADVANCED_PROJECTILES.keySet()) {
                if (arrow.isDead()) {
                    DIE_QUEUE.addAll(ADVANCED_PROJECTILES.get(arrow));
                }
                for (EnchantedArrow enchantedArrow : ADVANCED_PROJECTILES.get(arrow)) {
                    if (enchantedArrow.getTick() > 600) {
                        DIE_QUEUE.add(enchantedArrow);
                    }
                }
            }

            for (EnchantedArrow enchantedArrow : DIE_QUEUE) {
                enchantedArrow.die();
            }
            DIE_QUEUE.clear();
        }
    }

    @EffectTask(Frequency.HIGH)
    public static void doTick() {
        synchronized (ADVANCED_PROJECTILES) {
            ADVANCED_PROJECTILES.values().forEach((set) -> set.forEach(EnchantedArrow::tick));
        }
    }
}