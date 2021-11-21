package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class Bind extends Zenchantment {
    public static final String KEY = "bind";

    private static final String                             NAME        = "Bind";
    private static final String                             DESCRIPTION = "Keeps items with the enchantment in your inventory after death";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.NONE;

    private final NamespacedKey key;

    public Bind(
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
    public boolean onPlayerDeath(final @NotNull PlayerDeathEvent event, final int level, final boolean usedHand) {
        if (event.getKeepInventory()) {
            return false;
        }

        final Player player = event.getEntity();
        final ItemStack[] contents = player.getInventory().getContents().clone();
        final List<ItemStack> removed = new ArrayList<>();

        for (int i = 0; i < contents.length; i++) {
            if (
                !Zenchantment.getZenchantmentsOnItemStack(
                    contents[i],
                    ZenchantmentsPlugin.getInstance().getGlobalConfiguration(),
                    ZenchantmentsPlugin.getInstance().getWorldConfigurationProvider().getConfigurationForWorld(player.getWorld())
                ).containsKey(this)
            ) {
                contents[i] = null;
            } else {
                removed.add(contents[i]);
                event.getDrops().remove(contents[i]);
            }
        }

        ZenchantmentsPlugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(ZenchantmentsPlugin.getInstance(), () -> {
            if (event.getKeepInventory()) {
                event.getDrops().addAll(removed);
            } else {
                player.getInventory().setContents(contents);
            }
        }, 1);

        return true;
    }
}
