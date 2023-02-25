package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.arrows.VortexArrow;
import zedly.zenchantments.arrows.ZenchantedArrow;

import java.util.Collection;
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
    public Collection<EquipmentSlot> getApplyToSlots() {
        return Slots.HANDS;
    }

    @Override
    public boolean onEntityKill(final @NotNull EntityDeathEvent event, final int level, final EquipmentSlot slot) {
        final Block deathBlock = event.getEntity().getLocation().getBlock();
        final Player killer = event.getEntity().getKiller();

        VORTEX_LOCATIONS.put(deathBlock, killer);

        final int experience = event.getDroppedExp();

        event.setDroppedExp(0);

        CompatibilityAdapter.instance().collectExp(requireNonNull(killer), experience);
        ZenchantmentsPlugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(
            ZenchantmentsPlugin.getInstance(),
            () -> VORTEX_LOCATIONS.remove(deathBlock),
            3
        );

        return true;
    }

    @Override
    public boolean onEntityShootBow(final @NotNull EntityShootBowEvent event, final int level, final EquipmentSlot slot) {
        final VortexArrow arrow = new VortexArrow((AbstractArrow) event.getProjectile());
        ZenchantedArrow.putArrow((AbstractArrow) event.getProjectile(), arrow, (Player) event.getEntity());
        return true;
    }
}
