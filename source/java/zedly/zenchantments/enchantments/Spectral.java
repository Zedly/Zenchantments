package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.*;
import org.bukkit.block.data.type.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.HashSet;
import java.util.Set;

import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import static zedly.zenchantments.MaterialList.*;

public final class Spectral extends Zenchantment {
    public static final String KEY = "spectral";

    private static final String                             NAME        = "Spectral";
    private static final String                             DESCRIPTION = "Allows for cycling through a block's types";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.RIGHT;

    private static final int     MAX_BLOCKS   = 1024;
    private static final int[][] SEARCH_FACES = new int[0][0];

    private final NamespacedKey key;

    public Spectral(
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
        return this.doEvent(event, usedHand);
    }

    @Override
    public boolean onBlockInteractInteractable(final @NotNull PlayerInteractEvent event, final int level, final boolean usedHand) {
        return this.doEvent(event, usedHand);
    }

    private boolean cycleBlockType(final @NotNull Set<Block> blocks) {
        boolean change = false;

        for (final Block block : blocks) {
            final Material original = block.getType();
            final Material newMaterial;

            // TODO: Can this even be fixed..?
            // Honestly, what happened here?
            // Where did it all go wrong?

            if (WOOL.contains(original)) {
                newMaterial = WOOL.getNext(original);
            } else if (STAINED_GLASS.contains(original)) {
                newMaterial = STAINED_GLASS.getNext(original);
            } else if (STAINED_GLASS_PANES.contains(original)) {
                newMaterial = STAINED_GLASS_PANES.getNext(original);
            } else if (CARPETS.contains(original)) {
                newMaterial = CARPETS.getNext(original);
            } else if (TERRACOTTA.contains(original)) {
                newMaterial = TERRACOTTA.getNext(original);
            } else if (GLAZED_TERRACOTTA.contains(original)) {
                newMaterial = GLAZED_TERRACOTTA.getNext(original);
            } else if (CONCRETE_POWDER.contains(original)) {
                newMaterial = CONCRETE_POWDER.getNext(original);
            } else if (CONCRETE.contains(original)) {
                newMaterial = CONCRETE.getNext(original);
            } else if (WOOD.contains(original)) {
                newMaterial = WOOD.getNext(original);
            } else if (STRIPPED_LOGS.contains(original)) {
                newMaterial = STRIPPED_LOGS.getNext(original);
            } else if (WOODEN_PLANKS.contains(original)) {
                newMaterial = WOODEN_PLANKS.getNext(original);
            } else if (SANDS.contains(original)) {
                newMaterial = SANDS.getNext(original);
            } else if (SAPLINGS.contains(original)) {
                newMaterial = SAPLINGS.getNext(original);
            } else if (LEAVES.contains(original)) {
                newMaterial = LEAVES.getNext(original);
            } else if (WOODEN_FENCES.contains(original)) {
                newMaterial = WOODEN_FENCES.getNext(original);
            } else if (WOODEN_STAIRS.contains(original)) {
                newMaterial = WOODEN_STAIRS.getNext(original);
            } else if (SMALL_FLOWERS.contains(original)) {
                newMaterial = SMALL_FLOWERS.getNext(original);
            } else if (LOGS.contains(original)) {
                newMaterial = LOGS.getNext(original);
            } else if (SANDSTONE.contains(original)) {
                newMaterial = SANDSTONE.getNext(original);
            } else if (DIRT.contains(original)) {
                newMaterial = DIRT.getNext(original);
            } else if (STONES.contains(original)) {
                newMaterial = STONES.getNext(original);
            } else if (NETHER_BRICKS.contains(original)) {
                newMaterial = NETHER_BRICKS.getNext(original);
            } else if (COBBLESTONES.contains(original)) {
                newMaterial = COBBLESTONES.getNext(original);
            } else if (STONE_BRICKS.contains(original)) {
                newMaterial = STONE_BRICKS.getNext(original);
            } else if (ICE.contains(original)) {
                newMaterial = ICE.getNext(original);
            } else if (QUARTZ_BLOCKS.contains(original)) {
                newMaterial = QUARTZ_BLOCKS.getNext(original);
            } else if (WOODEN_PRESSURE_PLATES.contains(original)) {
                newMaterial = WOODEN_PRESSURE_PLATES.getNext(original);
            } else if (POLISHED_STONES.contains(original)) {
                newMaterial = POLISHED_STONES.getNext(original);
            } else if (PRISMARINE.contains(original)) {
                newMaterial = PRISMARINE.getNext(original);
            } else if (STRIPPED_WOOD.contains(original)) {
                newMaterial = STRIPPED_WOOD.getNext(original);
            } else if (WOODEN_SLABS.contains(original)) {
                newMaterial = WOODEN_SLABS.getNext(original);
            } else if (WOODEN_TRAPDOORS.contains(original)) {
                newMaterial = WOODEN_TRAPDOORS.getNext(original);
            } else if (END_STONES.contains(original)) {
                newMaterial = END_STONES.getNext(original);
            } else if (PURPUR.contains(original)) {
                newMaterial = PURPUR.getNext(original);
            } else if (PRISMARINE_STAIRS.contains(original)) {
                newMaterial = PRISMARINE_STAIRS.getNext(original);
            } else if (PRISMARINE_SLABS.contains(original)) {
                newMaterial = PRISMARINE_SLABS.getNext(original);
            } else if (COBBLESTONE_WALLS.contains(original)) {
                newMaterial = COBBLESTONE_WALLS.getNext(original);
            } else if (CORAL_BLOCKS.contains(original)) {
                newMaterial = CORAL_BLOCKS.getNext(original);
            } else if (DEAD_CORAL_BLOCKS.contains(original)) {
                newMaterial = DEAD_CORAL_BLOCKS.getNext(original);
            } else if (DEAD_CORAL.contains(original)) {
                newMaterial = DEAD_CORAL.getNext(original);
            } else if (CORAL.contains(original)) {
                newMaterial = CORAL.getNext(original);
            } else if (CORAL_FANS.contains(original)) {
                newMaterial = CORAL_FANS.getNext(original);
            } else if (DEAD_CORAL_FANS.contains(original)) {
                newMaterial = DEAD_CORAL_FANS.getNext(original);
            } else if (DEAD_CORAL_WALL_FANS.contains(original)) {
                newMaterial = DEAD_CORAL_WALL_FANS.getNext(original);
            } else if (MUSHROOMS.contains(original)) {
                newMaterial = MUSHROOMS.getNext(original);
            } else if (MUSHROOM_BLOCKS.contains(original)) {
                newMaterial = MUSHROOM_BLOCKS.getNext(original);
            } else if (SHORT_GRASS.contains(original)) {
                newMaterial = SHORT_GRASS.getNext(original);
            } else if (LARGE_FLOWERS.contains(original)) {
                newMaterial = LARGE_FLOWERS.getNext(original);
            } else if (WOODEN_TRAPDOORS.contains(original)) {
                newMaterial = WOODEN_TRAPDOORS.getNext(original);
            } else if (WOODEN_DOORS.contains(original)) {
                newMaterial = WOODEN_DOORS.getNext(original);
            } else if (FENCE_GATES.contains(original)) {
                newMaterial = FENCE_GATES.getNext(original);
            } else if (WOODEN_BUTTONS.contains(original)) {
                newMaterial = WOODEN_BUTTONS.getNext(original);
            } else if (STONE_SLABS.contains(original)) {
                newMaterial = STONE_SLABS.getNext(original);
            } else if (SANDSTONE_SLABS.contains(original)) {
                newMaterial = SANDSTONE_SLABS.getNext(original);
            } else if (STONE_BRICK_SLABS.contains(original)) {
                newMaterial = STONE_BRICK_SLABS.getNext(original);
            } else if (COBBLESTONE_SLABS.contains(original)) {
                newMaterial = COBBLESTONE_SLABS.getNext(original);
            } else if (QUARTZ_SLABS.contains(original)) {
                newMaterial = QUARTZ_SLABS.getNext(original);
            } else if (NETHER_BRICK_SLABS.contains(original)) {
                newMaterial = NETHER_BRICK_SLABS.getNext(original);
            } else if (STONE_STAIRS.contains(original)) {
                newMaterial = STONE_STAIRS.getNext(original);
            } else if (STONE_BRICK_STAIRS.contains(original)) {
                newMaterial = STONE_BRICK_STAIRS.getNext(original);
            } else if (STONE_STAIRS.contains(original)) {
                newMaterial = STONE_STAIRS.getNext(original);
            } else if (COBBLESTONE_STAIRS.contains(original)) {
                newMaterial = COBBLESTONE_STAIRS.getNext(original);
            } else if (QUARTZ_STAIRS.contains(original)) {
                newMaterial = QUARTZ_STAIRS.getNext(original);
            } else if (NETHER_BRICK_STAIRS.contains(original)) {
                newMaterial = NETHER_BRICK_STAIRS.getNext(original);
            } else if (STONE_WALLS.contains(original)) {
                newMaterial = STONE_WALLS.getNext(original);
            } else if (STONE_BRICK_WALLS.contains(original)) {
                newMaterial = STONE_BRICK_WALLS.getNext(original);
            } else if (BEDS.contains(original)) {
                newMaterial = BEDS.getNext(original);
            } else {
                continue;
            }

            // oh my god it's over

            change = true;

            final BlockData blockData = block.getBlockData();

            ZenchantmentsPlugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(ZenchantmentsPlugin.getInstance(), () -> {
                block.setType(newMaterial, false);

                if (blockData instanceof Bisected) {
                    final Bisected oldBlockData = (Bisected) blockData;
                    final Bisected newBlockData = (Bisected) block.getBlockData();
                    newBlockData.setHalf(oldBlockData.getHalf());
                    block.setBlockData(newBlockData, false);

                    // Set the second half's data.
                    if (block.getRelative(BlockFace.UP).getType() == original) {
                        newBlockData.setHalf(Bisected.Half.TOP);
                        block.getRelative(BlockFace.UP).setBlockData(newBlockData, false);
                    }
                    if (block.getRelative(BlockFace.DOWN).getType() == original) {
                        newBlockData.setHalf(Bisected.Half.BOTTOM);
                        block.getRelative(BlockFace.DOWN).setBlockData(newBlockData, false);
                    }
                }

                if (blockData instanceof Bed) {
                    final Bed oldBlockData = (Bed) blockData;
                    final Bed newBlockData = (Bed) block.getBlockData();

                    newBlockData.setPart(oldBlockData.getPart());

                    block.setBlockData(newBlockData, false);

                    // Set the second bed's part.
                    final BlockFace facing = newBlockData.getPart() != Bed.Part.HEAD
                        ? oldBlockData.getFacing()
                        : oldBlockData.getFacing().getOppositeFace();
                    final Block relative = block.getRelative(facing);
                    final Bed relativeData = (Bed) relative.getBlockData();

                    newBlockData.setPart(relativeData.getPart());
                    relative.setBlockData(newBlockData, false);

                    // Set the second bed's direction since we never do that later on.
                    relativeData.setFacing(oldBlockData.getFacing());
                    relative.setBlockData(relativeData, true);
                }

                if (blockData instanceof Gate) {
                    final Gate oldBlockData = (Gate) blockData;
                    final Gate newBlockData = (Gate) block.getBlockData();

                    newBlockData.setInWall(oldBlockData.isInWall());

                    block.setBlockData(newBlockData, true);
                }

                if (blockData instanceof Door) {
                    final Door oldBlockData = (Door) blockData;
                    final Door newBlockData = (Door) block.getBlockData();

                    newBlockData.setHinge(oldBlockData.getHinge());

                    block.setBlockData(newBlockData, true);
                }

                if (blockData instanceof Orientable) {
                    final Orientable oldBlockData = (Orientable) blockData;
                    final Orientable newBlockData = (Orientable) block.getBlockData();

                    newBlockData.setAxis(oldBlockData.getAxis());

                    block.setBlockData(newBlockData, true);
                }

                if (blockData instanceof Powerable) {
                    final Powerable oldBlockData = (Powerable) blockData;
                    final Powerable newBlockData = (Powerable) block.getBlockData();

                    newBlockData.setPowered(oldBlockData.isPowered());

                    block.setBlockData(newBlockData, true);
                }

                if (blockData instanceof Openable) {
                    final Openable oldBlockData = (Openable) blockData;
                    final Openable newBlockData = (Openable) block.getBlockData();

                    newBlockData.setOpen(oldBlockData.isOpen());

                    block.setBlockData(newBlockData, true);
                }

                if (blockData instanceof Stairs) {
                    final Stairs oldBlockData = (Stairs) blockData;
                    final Stairs newBlockData = (Stairs) block.getBlockData();

                    newBlockData.setShape(oldBlockData.getShape());

                    block.setBlockData(newBlockData, true);
                }

                if (blockData instanceof Slab) {
                    final Slab oldBlockData = (Slab) blockData;
                    final Slab newBlockData = (Slab) block.getBlockData();

                    newBlockData.setType(oldBlockData.getType());

                    block.setBlockData(newBlockData, true);
                }

                if (blockData instanceof MultipleFacing) {
                    final MultipleFacing oldBlockData = (MultipleFacing) blockData;
                    final MultipleFacing newBlockData = (MultipleFacing) block.getBlockData();

                    for (final BlockFace face : oldBlockData.getFaces()) {
                        newBlockData.setFace(face, true);
                    }

                    block.setBlockData(newBlockData, true);
                }

                if (blockData instanceof Directional) {
                    final Directional oldBlockData = (Directional) blockData;
                    final Directional newBlockData = (Directional) block.getBlockData();

                    newBlockData.setFacing(oldBlockData.getFacing());

                    block.setBlockData(newBlockData, true);
                }

                if (blockData instanceof Waterlogged) {
                    final Waterlogged oldBlockData = (Waterlogged) blockData;
                    final Waterlogged newBlockData = (Waterlogged) block.getBlockData();

                    newBlockData.setWaterlogged(oldBlockData.isWaterlogged());

                    block.setBlockData(newBlockData, true);
                }
            }, 0);
        }

        return change;
    }

    private boolean doEvent(final @NotNull PlayerInteractEvent event, final boolean usedHand) {
        if (event.getClickedBlock() == null) {
            return false;
        }

        if (event.getAction() != RIGHT_CLICK_BLOCK) {
            return false;
        }

        final Set<Block> blocks = new HashSet<>();

        blocks.add(event.getClickedBlock());

        if (event.getPlayer().isSneaking()) {
            blocks.addAll(
                Utilities.bfs(
                    event.getClickedBlock(),
                    MAX_BLOCKS,
                    false,
                    Float.MAX_VALUE,
                    SEARCH_FACES,
                    new MaterialList(event.getClickedBlock().getType()),
                    EMPTY,
                    false,
                    true
                )
            );
        }

        final boolean result = this.cycleBlockType(blocks);

        // We only damage the tool they used if the enchantment gets used.
        if (result) {
            Utilities.damageItemStackRespectUnbreaking(
                event.getPlayer(),
                (int) Math.ceil(Math.log(blocks.size() + 1) / Math.log(2)),
                usedHand
            );
        }

        event.setCancelled(true);

        return result;
    }
}
