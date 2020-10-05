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

public class Bind extends Zenchantment {
    public static final String KEY = "bind";

    private static final String                             NAME        = "Bind";
    private static final String                             DESCRIPTION = "Keeps items with the enchantment in your inventory after death";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.NONE;

    private final NamespacedKey key;

    public Bind(
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
    public boolean onPlayerDeath(@NotNull PlayerDeathEvent event, int level, boolean usedHand) {
        if (event.getKeepInventory()) {
            return false;
        }

        Player player = event.getEntity();
        final ItemStack[] contents = player.getInventory().getContents().clone();
        final List<ItemStack> removed = new ArrayList<>();

        for (int i = 0; i < contents.length; i++) {
            if (!Zenchantment.getEnchants(contents[i], player.getWorld()).containsKey(this)) {
                contents[i] = null;
            } else {
                removed.add(contents[i]);
                event.getDrops().remove(contents[i]);
            }
        }

        this.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(this.getPlugin(), () -> {
            if (event.getKeepInventory()) {
                event.getDrops().addAll(removed);
            } else {
                player.getInventory().setContents(contents);
            }
        }, 1);

        return true;
    }
}