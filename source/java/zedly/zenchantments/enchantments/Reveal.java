package zedly.zenchantments.enchantments;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.HashMap;
import java.util.Map;

@AZenchantment(runInSlots = Slots.MAIN_HAND, conflicting = {Switch.class, Pierce.class, Spectral.class})
public final class Reveal extends Zenchantment {
    public static final Map<Block, Integer> GLOWING_BLOCKS = new HashMap<>();

    @Override
    public boolean onBlockInteract(final @NotNull PlayerInteractEvent event, final int level, final EquipmentSlot slot) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }

        final Player player = event.getPlayer();

        if (!player.isSneaking()) {
            return false;
        }

        final int radius = (int) Math.max(2, Math.round((2 + level) * this.getPower()));

        int found = 0;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    final Block block = player.getLocation().getBlock().getRelative(x, y, z);

                    if (!MaterialList.ORES.contains(block.getType())) {
                        continue;
                    }

                    boolean exposed = false;

                    for (final BlockFace face : Utilities.CARDINAL_BLOCK_FACES) {
                        if (MaterialList.AIR.contains(block.getRelative(face).getType())) {
                            exposed = true;
                        }
                    }

                    if (exposed) {
                        continue;
                    }

                    found++;

                    final int entityId = 2000000000 + (block.hashCode()) % 10000000;

                    if (GLOWING_BLOCKS.containsKey(block)) {
                        GLOWING_BLOCKS.put(block, GLOWING_BLOCKS.get(block) + 1);
                    } else {
                        GLOWING_BLOCKS.put(block, 1);
                    }

                    if (!CompatibilityAdapter.instance().showShulker(block, entityId, player)) {
                        return false;
                    }

                    ZenchantmentsPlugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(ZenchantmentsPlugin.getInstance(), () -> {
                        CompatibilityAdapter.instance().hideFakeEntity(entityId, player);
                        if (GLOWING_BLOCKS.containsKey(block) && GLOWING_BLOCKS.get(block) > 1) {
                            GLOWING_BLOCKS.put(block, GLOWING_BLOCKS.get(block) - 1);
                        } else {
                            GLOWING_BLOCKS.remove(block);
                        }
                    }, 100);
                }
            }
        }

        Utilities.damageItemStackRespectUnbreaking(player, Math.max(16, (int) Math.round(found * 1.3)), slot);
        return true;
    }
}
