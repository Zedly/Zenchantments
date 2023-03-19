package zedly.zenchantments.enchantments;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.configuration.WorldConfiguration;
import zedly.zenchantments.configuration.WorldConfigurationProvider;
import zedly.zenchantments.event.BlockShredEvent;

import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static org.bukkit.Material.*;
import static zedly.zenchantments.MaterialList.*;

@AZenchantment(runInSlots = Slots.MAIN_HAND, conflicting = {Pierce.class, Switch.class})
public final class Shred extends Zenchantment {
    @Override
    public boolean onBlockBreak(final @NotNull BlockBreakEvent event, final int level, final EquipmentSlot slot) {
        final Block block = event.getBlock();

        if (!SHRED_PICKS.contains(block.getType()) && !SHRED_SHOVELS.contains(block.getType())) {
            return false;
        }

        final ItemStack hand = event.getPlayer().getInventory().getItem(slot);

        this.shred(
            block,
            block,
            new int[] { level + 3, level + 3, level + 3 },
            0,
            4.6 + (level * 0.22),
            new HashSet<>(),
            event.getPlayer(),
            WorldConfigurationProvider.getInstance().getConfigurationForWorld(block.getWorld()),
            hand.getType(),
            slot
        );

        return true;
    }

    private void shred(
        final @NotNull Block centerBlock,
        final @NotNull Block relativeBlock,
        final int[] coordinates,
        final int time,
        final double size,
        final @NotNull Set<Block> used,
        final @NotNull Player player,
        final @NotNull WorldConfiguration config,
        final @NotNull Material itemType,
        final EquipmentSlot usedHand
    ) {
        if (MaterialList.AIR.contains(relativeBlock.getType()) || used.contains(relativeBlock)) {
            return;
        }

        Material originalType = relativeBlock.getType();

        if ((Tool.PICKAXE.contains(itemType) && !SHRED_PICKS.contains(relativeBlock.getType()))
            || (Tool.SHOVEL.contains(itemType) && !SHRED_SHOVELS.contains(relativeBlock.getType()))
        ) {
            return;
        }

        if (config.getShredDropType() == 0) {
            CompatibilityAdapter.instance().breakBlock(relativeBlock, player);
        } else {
            final BlockShredEvent event = new BlockShredEvent(relativeBlock, player);
            ZenchantmentsPlugin.getInstance().getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }
            // In case another plugin changed the block
            originalType = relativeBlock.getType();

            if (config.getShredDropType() == 1) {
                if (NETHER_ORES.contains(originalType)) {
                    relativeBlock.setType(NETHERRACK);
                } else if (DEEPSLATE_ORES.contains(originalType)) {
                    relativeBlock.setType(DEEPSLATE);
                } else if (MaterialList.ORES.contains(relativeBlock.getType())) {
                    relativeBlock.setType(STONE);
                }

                if (event.isCancelled() || event.getBlock().getType() == Material.AIR) {
                    return;
                }

                Zenchantment.applyForTool(
                    player,
                    usedHand,
                    (ench, level, slot) -> ench.onBlockBreak(event, level, slot)
                );

                if (event.isCancelled()) {
                    return;
                }

                relativeBlock.breakNaturally();
            } else {
                relativeBlock.setType(Material.AIR);
            }
        }

        Sound sound = null;
        switch (originalType) {
            case GRASS_BLOCK:
                sound = Sound.BLOCK_GRASS_BREAK;
                break;
            case DIRT:
            case GRAVEL:
            case CLAY:
                sound = Sound.BLOCK_GRAVEL_BREAK;
                break;
            case SAND:
                sound = Sound.BLOCK_SAND_BREAK;
                break;
            case AIR:
                break;
            default:
                sound = Sound.BLOCK_STONE_BREAK;
                break;
        }

        // Technically sound should never be null because the default case assigns to it,
        // but whatever. Compiler can't see that right now.
        if (sound != null) {
            requireNonNull(relativeBlock.getLocation().getWorld())
                .playSound(relativeBlock.getLocation(), sound, 1, 1);
        }

        Utilities.damageItemStackRespectUnbreaking(player, 1, usedHand);

        used.add(relativeBlock);

        for (int i = 0; i < 3; i++) {
            if (coordinates[i] > 0) {
                coordinates[i] -= 1;

                final Block block1 = relativeBlock.getRelative(i == 0 ? -1 : 0, i == 1 ? -1 : 0, i == 2 ? -1 : 0);
                final Block block2 = relativeBlock.getRelative(i == 0 ? 1 : 0, i == 1 ? 1 : 0, i == 2 ? 1 : 0);

                if (block1.getLocation().distanceSquared(centerBlock.getLocation()) < size + (-1 + 2 * Math.random())) {
                    this.shred(centerBlock, block1, coordinates, time + 2, size, used, player, config, itemType, usedHand);
                }

                if (block2.getLocation().distanceSquared(centerBlock.getLocation()) < size + (-1 + 2 * Math.random())) {
                    this.shred(centerBlock, block2, coordinates, time + 2, size, used, player, config, itemType, usedHand);
                }

                coordinates[i] += 1;
            }
        }
    }
}
