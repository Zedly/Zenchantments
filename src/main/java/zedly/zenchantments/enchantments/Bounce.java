package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.Set;

import static org.bukkit.Material.SLIME_BLOCK;

public class Bounce extends Zenchantment {
    public static final String KEY = "bounce";

    private static final String                             NAME        = "Bounce";
    private static final String                             DESCRIPTION = "Preserves momentum when on slime blocks";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.RIGHT;

    private final NamespacedKey key;

    public Bounce(
        @NotNull ZenchantmentsPlugin plugin,
        @NotNull Set<Tool> enchantable,
        int maxLevel,
        int cooldown,
        double power,
        float probability
    ) {
        super(plugin, enchantable, maxLevel, cooldown, power, probability);
        this.key = new NamespacedKey(plugin, Bounce.KEY);
    }

    @Override
    @NotNull
    public NamespacedKey getKey() {
        return this.key;
    }

    @Override
    @NotNull
    public String getName() {
        return Bounce.NAME;
    }

    @Override
    @NotNull
    public String getDescription() {
        return Bounce.DESCRIPTION;
    }

    @Override
    @NotNull
    public Set<Class<? extends Zenchantment>> getConflicting() {
        return Bounce.CONFLICTING;
    }

    @Override
    @NotNull
    public Hand getHandUse() {
        return Bounce.HAND_USE;
    }

    @Override
    public boolean onFastScan(@NotNull Player player, int level, boolean usedHand) {
        if (player.getVelocity().getY() >= 0) {
            return false;
        }

        Block block = player.getLocation().getBlock();
        if (block.getRelative(0, -1, 0).getType() == SLIME_BLOCK
            || block.getType() == SLIME_BLOCK
            || block.getRelative(0, -2, 0).getType() == SLIME_BLOCK
            && (level * this.getPower()) > 2.0
        ) {
            if (!player.isSneaking()) {
                player.setVelocity(player.getVelocity().setY(0.56 * level * this.getPower()));
                return true;
            }

            player.setFallDistance(0);
        }
        return false;
    }
}