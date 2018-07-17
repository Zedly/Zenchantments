/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.zenchantments.compatibility;

import java.util.List;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

/**
 * Wrapper class for firing more accurate events
 * @author Dennis
 */
public class MockBlockState implements BlockState {

    private final Block block;
    private final Material newType;
    private final byte newData;

    public MockBlockState(Block block, Material newType, byte newData) {
        this.block = block;
        this.newType = newType;
        this.newData = newData;
    }

    @Override
    public Block getBlock() {
        return block;
    }

    @Override
    public MaterialData getData() {
        return newType.getNewData(newData);
    }

    @Override
    public Material getType() {
        return newType;
    }

    @Override
    public int getTypeId() {
        return newType.getId();
    }

    @Override
    public byte getLightLevel() {
        return block.getState().getLightLevel();
    }

    @Override
    public World getWorld() {
        return block.getState().getWorld();
    }

    @Override
    public int getX() {
        return block.getX();
    }

    @Override
    public int getY() {
        return block.getY();
    }

    @Override
    public int getZ() {
        return block.getZ();
    }

    @Override
    public Location getLocation() {
        return block.getLocation();
    }

    @Override
    public Location getLocation(Location lctn) {
        return block.getLocation(lctn);
    }

    @Override
    public Chunk getChunk() {
        return block.getChunk();
    }

    @Override
    public void setData(MaterialData md) {
        block.getState().setData(md);
    }

    @Override
    public void setType(Material mtrl) {
        block.getState().setType(mtrl);
    }

    @Deprecated
    @Override
    public boolean setTypeId(int i) {
        return block.getState().setTypeId(i);
    }

    @Override
    public boolean update() {
        return block.getState().update();
    }

    @Override
    public boolean update(boolean bln) {
        return block.getState().update(bln);
    }

    @Override
    public boolean update(boolean bln, boolean bln1) {
        return block.getState().update(bln, bln1);
    }

    @Deprecated
    @Override
    public byte getRawData() {
        return block.getState().getRawData();
    }

    @Override
    public void setRawData(byte b) {
        block.getState().setRawData(b);
    }

    @Override
    public boolean isPlaced() {
        return block.getState().isPlaced();
    }

    @Override
    public void setMetadata(String string, MetadataValue mv) {
        block.getState().setMetadata(string, mv);
    }

    @Override
    public List<MetadataValue> getMetadata(String string) {
        return block.getState().getMetadata(string);
    }

    @Override
    public boolean hasMetadata(String string) {
        return block.getState().hasMetadata(string);
    }

    @Override
    public void removeMetadata(String string, Plugin plugin) {
        block.getState().removeMetadata(string, plugin);
    }
}
