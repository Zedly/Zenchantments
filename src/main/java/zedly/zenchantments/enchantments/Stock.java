package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.Set;

import static org.bukkit.Material.AIR;

public final class Stock extends Zenchantment {
    public static final String KEY = "stock";

    private static final String                             NAME        = "Stock";
    private static final String                             DESCRIPTION = "Refills the player's item in hand when they run out";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.NONE;

    private final NamespacedKey key;

    public Stock(
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
    public boolean onBlockInteract(final @NotNull PlayerInteractEvent event, final int level, final boolean usedHand) {
        final PlayerInventory inventory = event.getPlayer().getInventory();
        final ItemStack handItem = inventory.getItemInMainHand();

        if (handItem.getType() == AIR) {
            return false;
        }

        final Player player = event.getPlayer();

        this.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(this.getPlugin(), () -> {
            final ItemStack newHandItem = inventory.getItemInMainHand();

            if (newHandItem.getType() != AIR) {
                return;
            }

            int current = -1;

            for (int i = 0; i < event.getPlayer().getInventory().getContents().length; i++) {
                final ItemStack stack = player.getInventory().getContents()[i];
                if (stack.getType() == handItem.getType()) {
                    current = i;
                    break;
                }
            }

            if (current != -1) {
                inventory.setItemInMainHand(event.getPlayer().getInventory().getContents()[current]);
                inventory.setItem(current, new ItemStack(AIR));
            }
        }, 1);

        return false;
    }
}
