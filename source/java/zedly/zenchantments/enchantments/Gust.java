package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Set;

import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public final class Gust extends Zenchantment {
    public static final String KEY = "gust";

    private static final String                             NAME        = "Gust";
    private static final String                             DESCRIPTION = "Pushes the user through the air at the cost of their health";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of(Force.class, RainbowSlam.class);
    private static final Hand                               HAND_USE    = Hand.RIGHT;

    private final NamespacedKey key;

    public Gust(
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
        if (event.getAction() != RIGHT_CLICK_AIR && event.getAction() != RIGHT_CLICK_BLOCK) {
            return false;
        }

        final Player player = event.getPlayer();

        if (player.getHealth() <= 2
            || (event.getClickedBlock() != null && !(event.getClickedBlock().getLocation().distance(player.getLocation()) > 2))
        ) {
            return false;
        }

        player.setFallDistance(-40);
        player.setVelocity(
            player.getTargetBlock(null, 10)
                .getLocation()
                .toVector()
                .subtract(player.getLocation().toVector())
                .multiply(.25 * this.getPower())
        );

        ZenchantmentsPlugin.getInstance().getCompatibilityAdapter().damagePlayer(player, 3, EntityDamageEvent.DamageCause.MAGIC);

        ZenchantmentsPlugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(
            ZenchantmentsPlugin.getInstance(),
            () -> Utilities.damageItemStack(event.getPlayer(), 1, usedHand),
            1
        );

        return true;
    }
}
