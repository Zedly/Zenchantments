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

import static java.util.Objects.requireNonNull;
import org.bukkit.Bukkit;
import static org.bukkit.Material.AIR;

public final class Switch extends Zenchantment {
    public static final String KEY = "switch";

    private static final String                             NAME        = "Switch";
    private static final String                             DESCRIPTION = "Replaces the clicked block with the leftmost block in your hotbar when sneaking";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of(Shred.class, Anthropomorphism.class, Fire.class, Extraction.class, Pierce.class, Reveal.class);
    private static final Hand                               HAND_USE    = Hand.RIGHT;

    private final NamespacedKey key;

    public Switch(
        final @NotNull Set<Tool> enchantable,
        final int maxLevel,
        final int cooldown,
        final double power,
        final float probability
    ) {
        super(enchantable, maxLevel, cooldown, power, probability);
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
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || !event.getPlayer().isSneaking()) {
            return false;
        }
        
        Block clickedBlock = event.getClickedBlock();
        if(clickedBlock == null) {
            return false;
        }

        // Make sure clicked block is okay to break.
        if (!ZenchantmentsPlugin.getInstance().getCompatibilityAdapter().isBlockSafeToBreak(clickedBlock)) {
            return false;
        }

        final Player player = event.getPlayer();
        ItemStack switchItem = null;
        int c = -1;

        // Find a suitable block in hotbar.
        for (int i = 0; i < 9; i++) {
            switchItem = player.getInventory().getItem(i);
            if (switchItem != null
                && switchItem.getType() != AIR
                && switchItem.getType().isSolid()
                && !MaterialList.UNBREAKABLE_BLOCKS.contains(switchItem.getType())
                && !MaterialList.INTERACTABLE_BLOCKS.contains(switchItem.getType())
                && !MaterialList.SHULKER_BOXES.contains(switchItem.getType())
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
        if (!ZenchantmentsPlugin.getInstance().getCompatibilityAdapter().breakBlock(clickedBlock, event.getPlayer())) {
            return false;
        }

        Grab.GRAB_LOCATIONS.put(clickedBlock, event.getPlayer());

        event.setCancelled(true);

        final Material material = switchItem.getType();

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(
            ZenchantmentsPlugin.getInstance(),
            () -> Grab.GRAB_LOCATIONS.remove(clickedBlock),
            3
        );

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(ZenchantmentsPlugin.getInstance(), () -> {
            // TODO: BlockData - whatever that means.
            ZenchantmentsPlugin.getInstance().getCompatibilityAdapter().placeBlock(clickedBlock, player, material, null);
        }, 1);

        Utilities.removeMaterialsFromPlayer(event.getPlayer(), material, 1);
        return true;
    }
}
