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
import zedly.zenchantments.compatibility.CompatibilityAdapter;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

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
        return this.doEvent(event, usedHand);
    }

    @Override
    public boolean onBlockInteractInteractable(final @NotNull PlayerInteractEvent event, final int level, final boolean usedHand) {
        return this.doEvent(event, usedHand);
    }

    private boolean cycleBlockType(final @NotNull Set<Block> blocks) {
        final CompatibilityAdapter adapter = Storage.COMPATIBILITY_ADAPTER;

        boolean change = false;

        for (final Block block : blocks) {
            final Material original = block.getType();
            final Material newMaterial;

            // TODO: Can this even be fixed..?
            // Honestly, what happened here?
            // Where did it all go wrong?

            if (adapter.Wools().contains(original)) {
                newMaterial = adapter.Wools().getNext(original);
            } else if (adapter.StainedGlass().contains(original)) {
                newMaterial = adapter.StainedGlass().getNext(original);
            } else if (adapter.StainedGlassPanes().contains(original)) {
                newMaterial = adapter.StainedGlassPanes().getNext(original);
            } else if (adapter.Carpets().contains(original)) {
                newMaterial = adapter.Carpets().getNext(original);
            } else if (adapter.Terracottas().contains(original)) {
                newMaterial = adapter.Terracottas().getNext(original);
            } else if (adapter.GlazedTerracottas().contains(original)) {
                newMaterial = adapter.GlazedTerracottas().getNext(original);
            } else if (adapter.ConcretePowders().contains(original)) {
                newMaterial = adapter.ConcretePowders().getNext(original);
            } else if (adapter.Concretes().contains(original)) {
                newMaterial = adapter.Concretes().getNext(original);
            } else if (adapter.Woods().contains(original)) {
                newMaterial = adapter.Woods().getNext(original);
            } else if (adapter.StrippedLogs().contains(original)) {
                newMaterial = adapter.StrippedLogs().getNext(original);
            } else if (adapter.Planks().contains(original)) {
                newMaterial = adapter.Planks().getNext(original);
            } else if (adapter.Sands().contains(original)) {
                newMaterial = adapter.Sands().getNext(original);
            } else if (adapter.Saplings().contains(original)) {
                newMaterial = adapter.Saplings().getNext(original);
            } else if (adapter.Leaves().contains(original)) {
                newMaterial = adapter.Leaves().getNext(original);
            } else if (adapter.WoodFences().contains(original)) {
                newMaterial = adapter.WoodFences().getNext(original);
            } else if (adapter.WoodStairs().contains(original)) {
                newMaterial = adapter.WoodStairs().getNext(original);
            } else if (adapter.SmallFlowers().contains(original)) {
                newMaterial = adapter.SmallFlowers().getNext(original);
            } else if (adapter.Logs().contains(original)) {
                newMaterial = adapter.Logs().getNext(original);
            } else if (adapter.Sandstones().contains(original)) {
                newMaterial = adapter.Sandstones().getNext(original);
            } else if (adapter.Dirts().contains(original)) {
                newMaterial = adapter.Dirts().getNext(original);
            } else if (adapter.Stones().contains(original)) {
                newMaterial = adapter.Stones().getNext(original);
            } else if (adapter.Netherbricks().contains(original)) {
                newMaterial = adapter.Netherbricks().getNext(original);
            } else if (adapter.Cobblestones().contains(original)) {
                newMaterial = adapter.Cobblestones().getNext(original);
            } else if (adapter.Stonebricks().contains(original)) {
                newMaterial = adapter.Stonebricks().getNext(original);
            } else if (adapter.Ices().contains(original)) {
                newMaterial = adapter.Ices().getNext(original);
            } else if (adapter.Quartz().contains(original)) {
                newMaterial = adapter.Quartz().getNext(original);
            } else if (adapter.WoodPressurePlates().contains(original)) {
                newMaterial = adapter.WoodPressurePlates().getNext(original);
            } else if (adapter.PolishedStones().contains(original)) {
                newMaterial = adapter.PolishedStones().getNext(original);
            } else if (adapter.Prismarines().contains(original)) {
                newMaterial = adapter.Prismarines().getNext(original);
            } else if (adapter.StrippedWoods().contains(original)) {
                newMaterial = adapter.StrippedWoods().getNext(original);
            } else if (adapter.WoodSlabs().contains(original)) {
                newMaterial = adapter.WoodSlabs().getNext(original);
            } else if (adapter.WoodTrapdoors().contains(original)) {
                newMaterial = adapter.WoodTrapdoors().getNext(original);
            } else if (adapter.Endstones().contains(original)) {
                newMaterial = adapter.Endstones().getNext(original);
            } else if (adapter.Purpurs().contains(original)) {
                newMaterial = adapter.Purpurs().getNext(original);
            } else if (adapter.PrismarineStairs().contains(original)) {
                newMaterial = adapter.PrismarineStairs().getNext(original);
            } else if (adapter.PrismarineSlabs().contains(original)) {
                newMaterial = adapter.PrismarineSlabs().getNext(original);
            } else if (adapter.CobblestoneWalls().contains(original)) {
                newMaterial = adapter.CobblestoneWalls().getNext(original);
            } else if (adapter.CoralBlocks().contains(original)) {
                newMaterial = adapter.CoralBlocks().getNext(original);
            } else if (adapter.DeadCoralBlocks().contains(original)) {
                newMaterial = adapter.DeadCoralBlocks().getNext(original);
            } else if (adapter.DeadCorals().contains(original)) {
                newMaterial = adapter.DeadCorals().getNext(original);
            } else if (adapter.Corals().contains(original)) {
                newMaterial = adapter.Corals().getNext(original);
            } else if (adapter.CoralFans().contains(original)) {
                newMaterial = adapter.CoralFans().getNext(original);
            } else if (adapter.DeadCoralFans().contains(original)) {
                newMaterial = adapter.DeadCoralFans().getNext(original);
            } else if (adapter.DeadCoralWallFans().contains(original)) {
                newMaterial = adapter.DeadCoralWallFans().getNext(original);
            } else if (adapter.Mushrooms().contains(original)) {
                newMaterial = adapter.Mushrooms().getNext(original);
            } else if (adapter.MushroomBlocks().contains(original)) {
                newMaterial = adapter.MushroomBlocks().getNext(original);
            } else if (adapter.ShortGrasses().contains(original)) {
                newMaterial = adapter.ShortGrasses().getNext(original);
            } else if (adapter.LargeFlowers().contains(original)) {
                newMaterial = adapter.LargeFlowers().getNext(original);
            } else if (adapter.WoodTrapdoors().contains(original)) {
                newMaterial = adapter.WoodTrapdoors().getNext(original);
            } else if (adapter.WoodDoors().contains(original)) {
                newMaterial = adapter.WoodDoors().getNext(original);
            } else if (adapter.FenceGates().contains(original)) {
                newMaterial = adapter.FenceGates().getNext(original);
            } else if (adapter.WoodButtons().contains(original)) {
                newMaterial = adapter.WoodButtons().getNext(original);
            } else if (adapter.Beds().contains(original)) {
                newMaterial = adapter.Beds().getNext(original);
            } else if (adapter.StoneSlabs().contains(original)) {
                newMaterial = adapter.StoneSlabs().getNext(original);
            } else if (adapter.SandstoneSlabs().contains(original)) {
                newMaterial = adapter.SandstoneSlabs().getNext(original);
            } else if (adapter.StoneBrickSlabs().contains(original)) {
                newMaterial = adapter.StoneBrickSlabs().getNext(original);
            } else if (adapter.CobblestoneSlabs().contains(original)) {
                newMaterial = adapter.CobblestoneSlabs().getNext(original);
            } else if (adapter.QuartzSlabs().contains(original)) {
                newMaterial = adapter.QuartzSlabs().getNext(original);
            } else if (adapter.NetherBrickSlabs().contains(original)) {
                newMaterial = adapter.NetherBrickSlabs().getNext(original);
            } else if (adapter.StoneStairs().contains(original)) {
                newMaterial = adapter.StoneStairs().getNext(original);
            } else if (adapter.StoneBrickStairs().contains(original)) {
                newMaterial = adapter.StoneBrickStairs().getNext(original);
            } else if (adapter.SandstoneStairs().contains(original)) {
                newMaterial = adapter.SandstoneStairs().getNext(original);
            } else if (adapter.CobblestoneStairs().contains(original)) {
                newMaterial = adapter.CobblestoneStairs().getNext(original);
            } else if (adapter.QuartzStairs().contains(original)) {
                newMaterial = adapter.QuartzStairs().getNext(original);
            } else if (adapter.NetherBrickStairs().contains(original)) {
                newMaterial = adapter.NetherBrickStairs().getNext(original);
            } else if (adapter.StoneWalls().contains(original)) {
                newMaterial = adapter.StoneWalls().getNext(original);
            } else if (adapter.StoneBrickWalls().contains(original)) {
                newMaterial = adapter.StoneBrickWalls().getNext(original);
            } else if (adapter.Beds().contains(original)) {
                newMaterial = adapter.Beds().getNext(original);
            } else if (adapter.Beds().contains(original)) {
                newMaterial = adapter.Beds().getNext(original);
            } else {
                continue;
            }

            // oh my god it's over

            change = true;

            final BlockData blockData = block.getBlockData();

            this.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(this.getPlugin(), () -> {
                block.setType(newMaterial, false);

                if (blockData instanceof Bisected) {
                    Bisected newBlockData = (Bisected) block.getBlockData();
                    newBlockData.setHalf(((Bisected) blockData).getHalf());
                    block.setBlockData(newBlockData, false);

                    // Set the second half's data.
                    if (block.getRelative(BlockFace.UP).getType().equals(original)) {
                        newBlockData.setHalf(Bisected.Half.TOP);
                        block.getRelative(BlockFace.UP).setBlockData(newBlockData, false);
                    }
                    if (block.getRelative(BlockFace.DOWN).getType().equals(original)) {
                        newBlockData.setHalf(Bisected.Half.BOTTOM);
                        block.getRelative(BlockFace.DOWN).setBlockData(newBlockData, false);
                    }
                }

                if (blockData instanceof Bed) {
                    Bed newBlockData = (Bed) block.getBlockData();
                    newBlockData.setPart(((Bed) blockData).getPart());
                    block.setBlockData(newBlockData, false);

                    // Set the second bed's part.
                    BlockFace facing = !newBlockData.getPart().equals(Bed.Part.HEAD)
                        ? ((Bed) blockData).getFacing()
                        : ((Bed) blockData).getFacing().getOppositeFace();
                    newBlockData.setPart(((Bed) block.getRelative(facing).getBlockData()).getPart());
                    block.getRelative(facing).setBlockData(newBlockData, false);

                    // Set the second bed's direction since we never do that later on.
                    Directional secondaryBlockData = (Directional) block.getRelative(facing).getBlockData();
                    secondaryBlockData.setFacing(((Directional) blockData).getFacing());
                    block.getRelative(facing).setBlockData(secondaryBlockData, true);
                }

                if (blockData instanceof Gate) {
                    Gate newBlockData = (Gate) block.getBlockData();
                    newBlockData.setInWall(((Gate) blockData).isInWall());
                    block.setBlockData(newBlockData, true);
                }

                if (blockData instanceof Door) {
                    Door newBlockData = (Door) block.getBlockData();
                    newBlockData.setHinge(((Door) blockData).getHinge());
                    block.setBlockData(newBlockData, true);
                }

                if (blockData instanceof Orientable) {
                    Orientable newBlockData = (Orientable) block.getBlockData();
                    newBlockData.setAxis(((Orientable) blockData).getAxis());
                    block.setBlockData(newBlockData, true);
                }

                if (blockData instanceof Powerable) {
                    Powerable newBlockData = (Powerable) block.getBlockData();
                    newBlockData.setPowered(((Powerable) blockData).isPowered());
                    block.setBlockData(newBlockData, true);
                }

                if (blockData instanceof Openable) {
                    Openable newBlockData = (Openable) block.getBlockData();
                    newBlockData.setOpen(((Openable) blockData).isOpen());
                    block.setBlockData(newBlockData, true);
                }

                if (blockData instanceof Stairs) {
                    Stairs newBlockData = (Stairs) block.getBlockData();
                    newBlockData.setShape(((Stairs) blockData).getShape());
                    block.setBlockData(newBlockData, true);
                }

                if (blockData instanceof Slab) {
                    Slab newBlockData = (Slab) block.getBlockData();
                    newBlockData.setType(((Slab) blockData).getType());
                    block.setBlockData(newBlockData, true);
                }

                if (blockData instanceof MultipleFacing) {
                    MultipleFacing newBlockData = (MultipleFacing) block.getBlockData();
                    for (BlockFace bf : ((MultipleFacing) blockData).getFaces()) {
                        newBlockData.setFace(bf, true);
                    }
                    block.setBlockData(newBlockData, true);
                }

                if (blockData instanceof Directional) {
                    Directional newBlockData = (Directional) block.getBlockData();
                    newBlockData.setFacing(((Directional) blockData).getFacing());
                    block.setBlockData(newBlockData, true);
                }

                if (blockData instanceof Waterlogged) {
                    Waterlogged newBlockData = (Waterlogged) block.getBlockData();
                    newBlockData.setWaterlogged(((Waterlogged) blockData).isWaterlogged());
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
                    EnumSet.of(event.getClickedBlock().getType()),
                    EnumSet.noneOf(Material.class),
                    false,
                    true
                )
            );
        }

        final boolean result = this.cycleBlockType(blocks);

        // We only damage the tool they used if the enchantment gets used.
        if (result) {
            Utilities.damageItemStack(
                event.getPlayer(),
                (int) Math.ceil(Math.log(blocks.size() + 1) / Math.log(2)),
                usedHand
            );
        }

        event.setCancelled(true);

        return result;
    }
}
