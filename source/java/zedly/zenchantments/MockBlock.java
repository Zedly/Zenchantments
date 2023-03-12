package zedly.zenchantments;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.bukkit.util.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class MockBlock implements Block {
    private final Block original;
    private final Material material;

    public MockBlock(Block original, Material mockMaterial) {
        this.original = original;
        this.material = mockMaterial;
    }

    @Override
    @Deprecated
    public byte getData() {
        return original.getData();
    }

    @NotNull
    @Override
    public BlockData getBlockData() {
        return original.getBlockData();
    }

    @NotNull
    @Override
    public Block getRelative(int i, int i1, int i2) {
        return original.getRelative(i, i1, i2);
    }

    @NotNull
    @Override
    public Block getRelative(@NotNull BlockFace blockFace) {
        return original.getRelative(blockFace);
    }

    @NotNull
    @Override
    public Block getRelative(@NotNull BlockFace blockFace, int i) {
        return original.getRelative(blockFace, i);
    }

    @NotNull
    @Override
    public Material getType() {
        return material;
    }

    @Override
    public byte getLightLevel() {
        return original.getLightLevel();
    }

    @Override
    public byte getLightFromSky() {
        return original.getLightFromSky();
    }

    @Override
    public byte getLightFromBlocks() {
        return original.getLightFromBlocks();
    }

    @NotNull
    @Override
    public World getWorld() {
        return original.getWorld();
    }

    @Override
    public int getX() {
        return original.getX();
    }

    @Override
    public int getY() {
        return original.getY();
    }

    @Override
    public int getZ() {
        return original.getZ();
    }

    @NotNull
    @Override
    public Location getLocation() {
        return original.getLocation();
    }

    @Nullable
    @Override
    public Location getLocation(@Nullable Location location) {
        return original.getLocation(location);
    }

    @NotNull
    @Override
    public Chunk getChunk() {
        return original.getChunk();
    }

    @Override
    public void setBlockData(@NotNull BlockData blockData) {
        original.setBlockData(blockData);
    }

    @Override
    public void setBlockData(@NotNull BlockData blockData, boolean b) {
        original.setBlockData(blockData, b);
    }

    @Override
    public void setType(@NotNull Material material) {
        original.setType(material);
    }

    @Override
    public void setType(@NotNull Material material, boolean b) {
        original.setType(material, b);
    }

    @Nullable
    @Override
    public BlockFace getFace(@NotNull Block block) {
        return original.getFace(block);
    }

    @NotNull
    @Override
    public BlockState getState() {
        return original.getState();
    }

    @NotNull
    @Override
    public Biome getBiome() {
        return original.getBiome();
    }

    @Override
    public void setBiome(@NotNull Biome biome) {
        original.setBiome(biome);
    }

    @Override
    public boolean isBlockPowered() {
        return original.isBlockPowered();
    }

    @Override
    public boolean isBlockIndirectlyPowered() {
        return original.isBlockIndirectlyPowered();
    }

    @Override
    public boolean isBlockFacePowered(@NotNull BlockFace blockFace) {
        return original.isBlockFacePowered(blockFace);
    }

    @Override
    public boolean isBlockFaceIndirectlyPowered(@NotNull BlockFace blockFace) {
        return original.isBlockFaceIndirectlyPowered(blockFace);
    }

    @Override
    public int getBlockPower(@NotNull BlockFace blockFace) {
        return original.getBlockPower(blockFace);
    }

    @Override
    public int getBlockPower() {
        return original.getBlockPower();
    }

    @Override
    public boolean isEmpty() {
        return original.isEmpty();
    }

    @Override
    public boolean isLiquid() {
        return original.isLiquid();
    }

    @Override
    public double getTemperature() {
        return original.getTemperature();
    }

    @Override
    public double getHumidity() {
        return original.getHumidity();
    }

    @NotNull
    @Override
    public PistonMoveReaction getPistonMoveReaction() {
        return original.getPistonMoveReaction();
    }

    @Override
    public boolean breakNaturally() {
        return original.breakNaturally();
    }

    @Override
    public boolean breakNaturally(@Nullable ItemStack itemStack) {
        return original.breakNaturally(itemStack);
    }

    @Override
    public boolean applyBoneMeal(@NotNull BlockFace blockFace) {
        return original.applyBoneMeal(blockFace);
    }

    @NotNull
    @Override
    public Collection<ItemStack> getDrops() {
        return original.getDrops();
    }

    @NotNull
    @Override
    public Collection<ItemStack> getDrops(@Nullable ItemStack itemStack) {
        return original.getDrops(itemStack);
    }

    @NotNull
    @Override
    public Collection<ItemStack> getDrops(@NotNull ItemStack itemStack, @Nullable Entity entity) {
        return original.getDrops(itemStack, entity);
    }

    @Override
    public boolean isPreferredTool(@NotNull ItemStack itemStack) {
        return original.isPreferredTool(itemStack);
    }

    @Override
    public float getBreakSpeed(@NotNull Player player) {
        return original.getBreakSpeed(player);
    }

    @Override
    public boolean isPassable() {
        return original.isPassable();
    }

    @Nullable
    @Override
    public RayTraceResult rayTrace(@NotNull Location location, @NotNull Vector vector, double v, @NotNull FluidCollisionMode fluidCollisionMode) {
        return original.rayTrace(location, vector, v, fluidCollisionMode);
    }

    @NotNull
    @Override
    public BoundingBox getBoundingBox() {
        return original.getBoundingBox();
    }

    @NotNull
    @Override
    public VoxelShape getCollisionShape() {
        return original.getCollisionShape();
    }

    @Override
    public boolean canPlace(@NotNull BlockData blockData) {
        return original.canPlace(blockData);
    }

    @Override
    public void setMetadata(@NotNull String s, @NotNull MetadataValue metadataValue) {
        original.setMetadata(s, metadataValue);
    }

    @NotNull
    @Override
    public List<MetadataValue> getMetadata(@NotNull String s) {
        return original.getMetadata(s);
    }

    @Override
    public boolean hasMetadata(@NotNull String s) {
        return original.hasMetadata(s);
    }

    @Override
    public void removeMetadata(@NotNull String s, @NotNull Plugin plugin) {
        original.removeMetadata(s, plugin);
    }
}
