package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.arrows.VortexArrow;
import zedly.zenchantments.arrows.ZenchantedArrow;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class Vortex extends Zenchantment {
    public static final String KEY = "vortex";

    public static final Map<Block, Player> VORTEX_LOCATIONS = new HashMap<>();

    private static final String                             NAME        = "Vortex";
    private static final String                             DESCRIPTION = "Teleports mob loot and XP directly to the player";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.BOTH;

    private final NamespacedKey key;

    public Vortex(
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
    public boolean onEntityKill(@NotNull EntityDeathEvent event, int level, boolean usedHand) {
        Block deathBlock = event.getEntity().getLocation().getBlock();
        Player killer = event.getEntity().getKiller();

        VORTEX_LOCATIONS.put(deathBlock, killer);

        int experience = event.getDroppedExp();

        event.setDroppedExp(0);

        Storage.COMPATIBILITY_ADAPTER.collectXP(killer, experience);

        this.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(this.getPlugin(), () -> VORTEX_LOCATIONS.remove(deathBlock), 3);

        return true;
    }

    @Override
    public boolean onEntityShootBow(@NotNull EntityShootBowEvent event, int level, boolean usedHand) {
        VortexArrow arrow = new VortexArrow(this.getPlugin(), (Arrow) event.getProjectile());
        ZenchantedArrow.putArrow((Arrow) event.getProjectile(), arrow, (Player) event.getEntity());
        return true;
    }
}
