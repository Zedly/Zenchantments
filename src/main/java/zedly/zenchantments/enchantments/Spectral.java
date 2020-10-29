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
import zedly.zenchantments.compatibility.EnumStorage;

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
        return this.doEvent(event, usedHand);
    }

    @Override
    public boolean onBlockInteractInteractable(@NotNull PlayerInteractEvent event, int level, boolean usedHand) {
        return this.doEvent(event, usedHand);
    }

    private boolean cycleBlockType(@NotNull Set<Block> blocks) {
        CompatibilityAdapter adapter = Storage.COMPATIBILITY_ADAPTER;
        boolean change = false;

        for (Block block : blocks) {
            Material original = block.getType();
            Material newMat = original;

            // TODO: Can this even be fixed..?
            // Honestly, what happened here?
            // Where did it all go wrong?

            if (adapter.Wools().contains(original)) {
                newMat = adapter.Wools().getNext(original);
            } else if (adapter.StainedGlass().contains(original)) {
                newMat = adapter.StainedGlass().getNext(original);
            } else if (adapter.StainedGlassPanes().contains(original)) {
                newMat = adapter.StainedGlassPanes().getNext(original);
            } else if (adapter.Carpets().contains(original)) {
                newMat = adapter.Carpets().getNext(original);
            } else if (adapter.Terracottas().contains(original)) {
                newMat = adapter.Terracottas().getNext(original);
            } else if (adapter.GlazedTerracottas().contains(original)) {
                newMat = adapter.GlazedTerracottas().getNext(original);
            } else if (adapter.ConcretePowders().contains(original)) {
                newMat = adapter.ConcretePowders().getNext(original);
            } else if (adapter.Concretes().contains(original)) {
                newMat = adapter.Concretes().getNext(original);
            } else if (adapter.Woods().contains(original)) {
                newMat = adapter.Woods().getNext(original);
            } else if (adapter.StrippedLogs().contains(original)) {
                newMat = adapter.StrippedLogs().getNext(original);
            } else if (adapter.Planks().contains(original)) {
                newMat = adapter.Planks().getNext(original);
            } else if (adapter.Sands().contains(original)) {
                newMat = adapter.Sands().getNext(original);
            } else if (adapter.Saplings().contains(original)) {
                newMat = adapter.Saplings().getNext(original);
            } else if (adapter.Leaves().contains(original)) {
                newMat = adapter.Leaves().getNext(original);
            } else if (adapter.WoodFences().contains(original)) {
                newMat = adapter.WoodFences().getNext(original);
            } else if (adapter.WoodStairs().contains(original)) {
                newMat = adapter.WoodStairs().getNext(original);
            } else if (adapter.SmallFlowers().contains(original)) {
                newMat = adapter.SmallFlowers().getNext(original);
            } else if (adapter.Logs().contains(original)) {
                newMat = adapter.Logs().getNext(original);
            } else if (adapter.Sandstones().contains(original)) {
                newMat = adapter.Sandstones().getNext(original);
            } else if (adapter.Dirts().contains(original)) {
                newMat = adapter.Dirts().getNext(original);
            } else if (adapter.Stones().contains(original)) {
                newMat = adapter.Stones().getNext(original);
            } else if (adapter.Netherbricks().contains(original)) {
                newMat = adapter.Netherbricks().getNext(original);
            } else if (adapter.Cobblestones().contains(original)) {
                newMat = adapter.Cobblestones().getNext(original);
            } else if (adapter.Stonebricks().contains(original)) {
                newMat = adapter.Stonebricks().getNext(original);
            } else if (adapter.Ices().contains(original)) {
                newMat = adapter.Ices().getNext(original);
            } else if (adapter.Quartz().contains(original)) {
                newMat = adapter.Quartz().getNext(original);
            } else if (adapter.WoodPressurePlates().contains(original)) {
                newMat = adapter.WoodPressurePlates().getNext(original);
            } else if (adapter.PolishedStones().contains(original)) {
                newMat = adapter.PolishedStones().getNext(original);
            } else if (adapter.Prismarines().contains(original)) {
                newMat = adapter.Prismarines().getNext(original);
            } else if (adapter.StrippedWoods().contains(original)) {
                newMat = adapter.StrippedWoods().getNext(original);
            } else if (adapter.WoodSlabs().contains(original)) {
                newMat = adapter.WoodSlabs().getNext(original);
            } else if (adapter.WoodTrapdoors().contains(original)) {
                newMat = adapter.WoodTrapdoors().getNext(original);
            } else if (adapter.Endstones().contains(original)) {
                newMat = adapter.Endstones().getNext(original);
            } else if (adapter.Purpurs().contains(original)) {
                newMat = adapter.Purpurs().getNext(original);
            } else if (adapter.PrismarineStairs().contains(original)) {
                newMat = adapter.PrismarineStairs().getNext(original);
            } else if (adapter.PrismarineSlabs().contains(original)) {
                newMat = adapter.PrismarineSlabs().getNext(original);
            } else if (adapter.CobblestoneWalls().contains(original)) {
                newMat = adapter.CobblestoneWalls().getNext(original);
            } else if (adapter.CoralBlocks().contains(original)) {
                newMat = adapter.CoralBlocks().getNext(original);
            } else if (adapter.DeadCoralBlocks().contains(original)) {
                newMat = adapter.DeadCoralBlocks().getNext(original);
            } else if (adapter.DeadCorals().contains(original)) {
                newMat = adapter.DeadCorals().getNext(original);
            } else if (adapter.Corals().contains(original)) {
                newMat = adapter.Corals().getNext(original);
            } else if (adapter.CoralFans().contains(original)) {
                newMat = adapter.CoralFans().getNext(original);
            } else if (adapter.DeadCoralFans().contains(original)) {
                newMat = adapter.DeadCoralFans().getNext(original);
            } else if (adapter.DeadCoralWallFans().contains(original)) {
                newMat = adapter.DeadCoralWallFans().getNext(original);
            } else if (adapter.Mushrooms().contains(original)) {
                newMat = adapter.Mushrooms().getNext(original);
            } else if (adapter.MushroomBlocks().contains(original)) {
                newMat = adapter.MushroomBlocks().getNext(original);
            } else if (adapter.ShortGrasses().contains(original)) {
                newMat = adapter.ShortGrasses().getNext(original);
            } else if (adapter.LargeFlowers().contains(original)) {
                newMat = adapter.LargeFlowers().getNext(original);
            } else if (adapter.WoodTrapdoors().contains(original)) {
                newMat = adapter.WoodTrapdoors().getNext(original);
            } else if (adapter.WoodDoors().contains(original)) {
                newMat = adapter.WoodDoors().getNext(original);
            } else if (adapter.FenceGates().contains(original)) {
                newMat = adapter.FenceGates().getNext(original);
            } else if (adapter.WoodButtons().contains(original)) {
                newMat = adapter.WoodButtons().getNext(original);
            } else if (adapter.Beds().contains(original)) {
                newMat = adapter.Beds().getNext(original);
            } else if (adapter.StoneSlabs().contains(original)) {
                newMat = adapter.StoneSlabs().getNext(original);
            } else if (adapter.SandstoneSlabs().contains(original)) {
                newMat = adapter.SandstoneSlabs().getNext(original);
            } else if (adapter.StoneBrickSlabs().contains(original)) {
                newMat = adapter.StoneBrickSlabs().getNext(original);
            } else if (adapter.CobblestoneSlabs().contains(original)) {
                newMat = adapter.CobblestoneSlabs().getNext(original);
            } else if (adapter.QuartzSlabs().contains(original)) {
                newMat = adapter.QuartzSlabs().getNext(original);
            } else if (adapter.NetherBrickSlabs().contains(original)) {
                newMat = adapter.NetherBrickSlabs().getNext(original);
            } else if (adapter.StoneStairs().contains(original)) {
                newMat = adapter.StoneStairs().getNext(original);
            } else if (adapter.StoneBrickStairs().contains(original)) {
                newMat = adapter.StoneBrickStairs().getNext(original);
            } else if (adapter.SandstoneStairs().contains(original)) {
                newMat = adapter.SandstoneStairs().getNext(original);
            } else if (adapter.CobblestoneStairs().contains(original)) {
                newMat = adapter.CobblestoneStairs().getNext(original);
            } else if (adapter.QuartzStairs().contains(original)) {
                newMat = adapter.QuartzStairs().getNext(original);
            } else if (adapter.NetherBrickStairs().contains(original)) {
                newMat = adapter.NetherBrickStairs().getNext(original);
            } else if (adapter.StoneWalls().contains(original)) {
                newMat = adapter.StoneWalls().getNext(original);
            } else if (adapter.StoneBrickWalls().contains(original)) {
                newMat = adapter.StoneBrickWalls().getNext(original);
            } else if (adapter.Beds().contains(original)) {
                newMat = adapter.Beds().getNext(original);
            } else if (adapter.Beds().contains(original)) {
                newMat = adapter.Beds().getNext(original);
            }

            // oh my god it's over

            if (!newMat.equals(original)) {
                change = true;
                BlockData blockData = block.getBlockData();
                Material newMatFinal = newMat;
                this.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(this.getPlugin(), () -> {

                    block.setType(newMatFinal, false);

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
        }

        return change;
    }

    public boolean doEvent(@NotNull PlayerInteractEvent event, boolean usedHand) {
        if (event.getClickedBlock() == null) {
            return false;
        }

        if (event.getAction() != RIGHT_CLICK_BLOCK) {
            return false;
        }

        Set<Block> blocks = new HashSet<>();
        blocks.add(event.getClickedBlock());

        if (event.getPlayer().isSneaking()) {
            blocks.addAll(
                Utilities.bfs(
                    event.getClickedBlock(),
                    MAX_BLOCKS,
                    false,
                    Float.MAX_VALUE,
                    SEARCH_FACES,
                    new EnumStorage<>(new Material[] {event.getClickedBlock().getType()}),
                    new EnumStorage<>(new Material[0]),
                    false,
                    true
                )
            );
        }

        boolean result = this.cycleBlockType(blocks);

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