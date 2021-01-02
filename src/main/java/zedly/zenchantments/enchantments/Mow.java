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
    public boolean onBlockInteract(@NotNull PlayerInteractEvent event, int level, boolean usedHand) {
        if (event.getAction() == RIGHT_CLICK_AIR || event.getAction() == RIGHT_CLICK_BLOCK) {
            return this.shear(event, level, usedHand);
        }

        return false;
    }

    @Override
    public boolean onShear(@NotNull PlayerShearEntityEvent event, int level, boolean usedHand) {
        return this.shear(event, level, usedHand);
    }

    private boolean shear(@NotNull PlayerEvent event, int level, boolean usedHand) {
        boolean shearedEntity = false;
        int radius = (int) Math.round(level * this.getPower() + 2);
        Player player = event.getPlayer();

        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof Sheep) {
                Sheep sheep = (Sheep) entity;
                if (sheep.isAdult()) {
                    this.getPlugin().getCompatibilityAdapter().shearEntityNMS(sheep, player, usedHand);
                    shearedEntity = true;
                }
            } else if (entity instanceof MushroomCow) {
                MushroomCow mooshroom = (MushroomCow) entity;
                if (mooshroom.isAdult()) {
                    this.getPlugin().getCompatibilityAdapter().shearEntityNMS(mooshroom, player, usedHand);
                    shearedEntity = true;
                }
            }
        }

        return shearedEntity;
    }
}
