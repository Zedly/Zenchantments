package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.arrows.VortexArrow;
import zedly.zenchantments.arrows.ZenchantedArrow;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public final class Vortex extends Zenchantment {
    public static final String KEY = "vortex";

    public static final Map<Block, Player> VORTEX_LOCATIONS = new HashMap<>();

    private static final String                             NAME        = "Vortex";
    private static final String                             DESCRIPTION = "Teleports mob loot and XP directly to the player";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.BOTH;

    private final NamespacedKey key;

    public Vortex(
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
    public boolean onEntityKill(final @NotNull EntityDeathEvent event, final int level, final boolean usedHand) {
        final Block deathBlock = event.getEntity().getLocation().getBlock();
        final Player killer = event.getEntity().getKiller();

        VORTEX_LOCATIONS.put(deathBlock, killer);

        final int experience = event.getDroppedExp();

        event.setDroppedExp(0);

        ZenchantmentsPlugin.getInstance().getCompatibilityAdapter().collectExp(requireNonNull(killer), experience);
        ZenchantmentsPlugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(
            ZenchantmentsPlugin.getInstance(),
            () -> VORTEX_LOCATIONS.remove(deathBlock),
            3
        );

        return true;
    }

    @Override
    public boolean onEntityShootBow(final @NotNull EntityShootBowEvent event, final int level, final boolean usedHand) {
        final VortexArrow arrow = new VortexArrow(ZenchantmentsPlugin.getInstance(), (Arrow) event.getProjectile());
        ZenchantedArrow.putArrow((Arrow) event.getProjectile(), arrow, (Player) event.getEntity());
        return true;
    }
}
