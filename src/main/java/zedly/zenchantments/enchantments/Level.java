package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.arrows.LevelArrow;
import zedly.zenchantments.arrows.ZenchantedArrow;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public final class Level extends Zenchantment {
    public static final String KEY = "level";

    private static final String                             NAME        = "Level";
    private static final String                             DESCRIPTION = "Drops more XP when killing mobs or mining ores";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.BOTH;

    private final NamespacedKey key;

    public Level(
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
    public boolean onEntityKill(@NotNull EntityDeathEvent event, int level, boolean usedHand) {
        if (ThreadLocalRandom.current().nextBoolean()) {
            event.setDroppedExp((int) (event.getDroppedExp() * (1.3 + (level * this.getPower() * .5))));
            return true;
        }

        return false;
    }

    @Override
    public boolean onBlockBreak(@NotNull BlockBreakEvent event, int level, boolean usedHand) {
        if (ThreadLocalRandom.current().nextBoolean()) {
            event.setExpToDrop((int) (event.getExpToDrop() * (1.3 + (level * this.getPower() * .5))));
            return true;
        }

        return false;
    }

    @Override
    public boolean onEntityShootBow(@NotNull EntityShootBowEvent event, int level, boolean usedHand) {
        if (ThreadLocalRandom.current().nextBoolean()) {
            LevelArrow arrow = new LevelArrow(this.getPlugin(), (Arrow) event.getProjectile(), level, this.getPower());
            ZenchantedArrow.putArrow((Arrow) event.getProjectile(), arrow, (Player) event.getEntity());
            return true;
        }

        return false;
    }
}
