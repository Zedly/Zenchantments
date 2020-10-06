package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Set;

import static org.bukkit.Material.AIR;

public final class Switch extends Zenchantment {
    public static final String KEY = "switch";

    private static final String                             NAME        = "Switch";
    private static final String                             DESCRIPTION = "Replaces the clicked block with the leftmost block in your hotbar when sneaking";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of(Shred.class, Anthropomorphism.class, Fire.class, Extraction.class, Pierce.class, Reveal.class);
    private static final Hand                               HAND_USE    = Hand.RIGHT;

    private final NamespacedKey key;

    public Switch(
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
    public boolean onBlockInteract(@NotNull PlayerInteractEvent event, int level, boolean usedHand) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || !event.getPlayer().isSneaking()) {
            return false;
        }

        // Make sure clicked block is okay to break.
        if (!ADAPTER.isBlockSafeToBreak(event.getClickedBlock())) {
            return false;
        }

        Player player = event.getPlayer();
        ItemStack switchItem = null;
        int c = -1;

        // Find a suitable block in hotbar.
        for (int i = 0; i < 9; i++) {
            switchItem = player.getInventory().getItem(i);
            if (switchItem != null
                && switchItem.getType() != AIR
                && switchItem.getType().isSolid()
                && !Storage.COMPATIBILITY_ADAPTER.UnbreakableBlocks().contains(switchItem.getType())
                && !Storage.COMPATIBILITY_ADAPTER.InteractableBlocks().contains(switchItem.getType())
                && !Storage.COMPATIBILITY_ADAPTER.ShulkerBoxes().contains(switchItem.getType())
            ) {
                c = i;
                break;
            }
        }

        if (c == -1) {
            // No suitable block in inventory.
            return false;
        }

        // Block has been selected, attempt breaking.
        if (!ADAPTER.breakBlockNMS(event.getClickedBlock(), event.getPlayer())) {
            return false;
        }

        // Breaking succeeded, begin invasive operations.
        Block clickedBlock = event.getClickedBlock();

        Grab.GRAB_LOCATIONS.put(clickedBlock, event.getPlayer());

        event.setCancelled(true);

        Material material = switchItem.getType();

        this.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(this.getPlugin(), () -> {
            Grab.GRAB_LOCATIONS.remove(clickedBlock);
        }, 3);

        this.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(this.getPlugin(), () -> {
            ADAPTER.placeBlock(clickedBlock, player, material, null); // TODO: BlockData - whatever that means.
        }, 1);

        Utilities.removeItem(event.getPlayer(), material, 1);
        return true;
    }
}