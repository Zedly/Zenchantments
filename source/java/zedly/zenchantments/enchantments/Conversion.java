package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Collection;
import java.util.Set;

import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public final class Conversion extends Zenchantment {
    public static final String KEY = "conversion";

    private static final String                             NAME        = "Conversion";
    private static final String                             DESCRIPTION = "Converts XP to health when right clicking and sneaking";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.RIGHT;

    private final NamespacedKey key;

    public Conversion(
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
    public boolean onBlockInteract(final @NotNull PlayerInteractEvent event, final int level, final EquipmentSlot slot) {
        if (event.getAction() != RIGHT_CLICK_AIR && event.getAction() != RIGHT_CLICK_BLOCK) {
            return false;
        }

        final Player player = event.getPlayer();
        if (!player.isSneaking()) {
            return false;
        }

        if (player.getLevel() <= 1) {
            return false;
        }

        if (!(player.getHealth() < 20)) {
            return false;
        }

        player.setLevel((player.getLevel() - 1));
        player.setHealth(Math.min(20, player.getHealth() + 2 * this.getPower() * level));

        for (int i = 0; i < 3; i++) {
            ZenchantmentsPlugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(
                ZenchantmentsPlugin.getInstance(),
                () -> Utilities.displayParticle(
                    Utilities.getCenter(player.getLocation()),
                    Particle.HEART,
                    10,
                    0.1f,
                    0.5f,
                    0.5f,
                    0.5f
                ),
                i * 5 + 1
            );
        }

        return true;
    }
}
