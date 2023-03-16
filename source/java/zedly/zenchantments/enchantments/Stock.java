package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Collection;
import java.util.Set;

import static org.bukkit.Material.AIR;

public final class Stock extends Zenchantment {
    public static final String KEY = "stock";

    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();

    public Stock(
        final @NotNull Set<Tool> enchantable,
        final int maxLevel,
        final int cooldown,
        final double probability,
        final float power
    ) {
        super(enchantable, maxLevel, cooldown, probability, power, CONFLICTING, KEY);
    }

    @Override
    public Collection<EquipmentSlot> getApplyToSlots() {
        return Slots.ARMOR;
    }

    @Override
    public boolean onBlockInteract(final @NotNull PlayerInteractEvent event, final int level, final EquipmentSlot slot) {
        final PlayerInventory inventory = event.getPlayer().getInventory();
        final Material handItem = inventory.getItemInMainHand().getType();

        if (handItem == AIR) {
            return false;
        }

        final Player player = event.getPlayer();

        ZenchantmentsPlugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(ZenchantmentsPlugin.getInstance(), () -> {
            final ItemStack newHandItem = inventory.getItemInMainHand();

            if (newHandItem.getType() != AIR) {
                return;
            }

            int current = -1;

            final ItemStack[] invContents = player.getInventory().getContents();
            for (int i = 0; i < event.getPlayer().getInventory().getContents().length; i++) {
                final ItemStack stack = invContents[i];
                if (stack != null && stack.getType() == handItem) {
                    current = i;
                    break;
                }
            }

            if (current != -1) {
                inventory.setItemInMainHand(invContents[current]);
                inventory.setItem(current, new ItemStack(AIR));
            }
        }, 1);

        return false;
    }
}
