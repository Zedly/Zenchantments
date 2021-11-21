package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.Set;

import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public final class Mow extends Zenchantment {
    public static final String KEY = "mow";

    private static final String                             NAME        = "Mow";
    private static final String                             DESCRIPTION = "Shears all nearby sheep";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.RIGHT;

    private final NamespacedKey key;

    public Mow(
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
    public boolean onBlockInteract(final @NotNull PlayerInteractEvent event, final int level, final boolean usedHand) {
        if (event.getAction() == RIGHT_CLICK_AIR || event.getAction() == RIGHT_CLICK_BLOCK) {
            return this.shear(event, level, usedHand);
        }

        return false;
    }

    @Override
    public boolean onShear(final @NotNull PlayerShearEntityEvent event, final int level, final boolean usedHand) {
        return this.shear(event, level, usedHand);
    }

    private boolean shear(final @NotNull PlayerEvent event, final int level, final boolean usedHand) {
        final int radius = (int) Math.round(level * this.getPower() + 2);
        final Player player = event.getPlayer();

        boolean shearedEntity = false;

        for (final Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof Sheep) {
                final Sheep sheep = (Sheep) entity;
                if (sheep.isAdult()) {
                    ZenchantmentsPlugin.getInstance().getCompatibilityAdapter().shearEntityNMS(sheep, player, usedHand);
                    shearedEntity = true;
                }
            } else if (entity instanceof MushroomCow) {
                final MushroomCow mooshroom = (MushroomCow) entity;
                if (mooshroom.isAdult()) {
                    ZenchantmentsPlugin.getInstance().getCompatibilityAdapter().shearEntityNMS(mooshroom, player, usedHand);
                    shearedEntity = true;
                }
            }
        }

        return shearedEntity;
    }
}
