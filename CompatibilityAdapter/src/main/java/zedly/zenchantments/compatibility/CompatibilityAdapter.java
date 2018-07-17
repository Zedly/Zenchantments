/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.zenchantments.compatibility;

import java.util.Random;
import org.apache.commons.lang.ArrayUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import static org.bukkit.Material.*;
import org.bukkit.entity.EntityType;
import static org.bukkit.entity.EntityType.*;
import org.bukkit.entity.Guardian;
import org.bukkit.event.block.BlockGrowEvent;

import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Dennis
 */
public class CompatibilityAdapter {

    private static final CompatibilityAdapter INSTANCE = new CompatibilityAdapter();
    private static final Random RND = new Random();

    private static final Material[] UNBREAKABLE_BLOCKS = {AIR, BEDROCK, WATER, STATIONARY_WATER,
        LAVA, STATIONARY_LAVA, PISTON_EXTENSION, PISTON_MOVING_PIECE, PORTAL, ENDER_PORTAL,
        ENDER_PORTAL_FRAME, DRAGON_EGG};

    private static final Material[] STORAGE_BLOCKS = {DISPENSER, MOB_SPAWNER, CHEST, FURNACE,
        BURNING_FURNACE, JUKEBOX, ENDER_CHEST, COMMAND, BEACON, TRAPPED_CHEST, HOPPER, DROPPER};

    private static final Material[] INTERACTABLE_BLOCKS = {
        DISPENSER, NOTE_BLOCK, BED_BLOCK, CHEST, WORKBENCH, FURNACE, BURNING_FURNACE,
        WOODEN_DOOR, LEVER, STONE_BUTTON, JUKEBOX, DIODE_BLOCK_OFF, DIODE_BLOCK_ON, TRAP_DOOR,
        FENCE_GATE, ENCHANTMENT_TABLE, BREWING_STAND, ENDER_CHEST, COMMAND, BEACON, WOOD_BUTTON,
        ANVIL, TRAPPED_CHEST, REDSTONE_COMPARATOR_OFF, REDSTONE_COMPARATOR_ON, DAYLIGHT_DETECTOR,
        HOPPER, DROPPER};

    private static final Material ORES[] = new Material[]{COAL_ORE, REDSTONE_ORE, DIAMOND_ORE, GOLD_ORE,
        IRON_ORE, LAPIS_ORE, GLOWSTONE, QUARTZ_ORE, EMERALD_ORE, GLOWING_REDSTONE_ORE};

    private static final EntityType[] TRANSFORMATION_ENTITY_TYPES = new EntityType[]{BAT, SKELETON, ZOMBIE, SILVERFISH, ENDERMITE, ZOMBIE, PIG_ZOMBIE, VILLAGER, WITCH, COW, MUSHROOM_COW, SLIME, MAGMA_CUBE, WITHER_SKULL, SKELETON, OCELOT, WOLF};

    private static final Material[] TERRAFORMER_MATERIALS = {STONE, GRASS, DIRT, COBBLESTONE, WOOD, SAND, GRAVEL,
                        GOLD_ORE, IRON_ORE, COAL_ORE, LOG, LEAVES, LAPIS_ORE, SANDSTONE,
                        DOUBLE_STEP, BRICK, TNT, BOOKSHELF, MOSSY_COBBLESTONE, ICE, SNOW_BLOCK,
                        CLAY, NETHERRACK, SOUL_SAND, SMOOTH_BRICK, HUGE_MUSHROOM_1, HUGE_MUSHROOM_2,
                        MYCEL, NETHER_BRICK, ENDER_STONE, WOOD_DOUBLE_STEP, EMERALD_ORE, QUARTZ_ORE,
                        QUARTZ_BLOCK, STAINED_CLAY, LEAVES_2, LOG_2, SLIME_BLOCK, PRISMARINE, HARD_CLAY,
                        PACKED_ICE, RED_SANDSTONE, DOUBLE_STONE_SLAB2};
    
    public static CompatibilityAdapter getInstance() {
        return INSTANCE;
    }

    /**
     * @return the UNBREAKABLE_BLOCKS
     */
    public Material[] getUnbreakableBlocks() {
        return UNBREAKABLE_BLOCKS;
    }

    /**
     * @return the STORAGE_BLOCKS
     */
    public Material[] getStorageBlocks() {
        return STORAGE_BLOCKS;
    }

    /**
     * @return the INTERACTABLE_BLOCKS
     */
    public Material[] getInteractableBlocks() {
        return INTERACTABLE_BLOCKS;
    }

    /**
     * @return the ores
     */
    public Material[] getOres() {
        return ORES;
    }
    
    public static Material[] getTerraformerMaterials() {
        return TERRAFORMER_MATERIALS;
    }

    public EntityType[] getTransformationEntityTypes() {
        return TRANSFORMATION_ENTITY_TYPES;
    }

    protected CompatibilityAdapter() {
    }

    public boolean breakBlockNMS(Block block, Player player) {
        BlockBreakEvent evt = new BlockBreakEvent(block, player);
        Bukkit.getPluginManager().callEvent(evt);
        if (!evt.isCancelled()) {
            block.breakNaturally(player.getInventory().getItemInHand());
            // TODO: Apply tool damage
            return true;
        }
        return false;
    }

    /**
     * Places a block on the given player's behalf. Fires a BlockPlaceEvent with
     * (nearly) appropriate parameters to probe the legitimacy (permissions etc)
     * of the action and to communicate to other plugins where the block is
     * coming from.
     *
     * @param blockPlaced the block to be changed
     * @param player the player whose identity to use
     * @param mat the material to set the block to, if allowed
     * @param blockData the block data to set for the block, if allowed
     * @return true if the block placement has been successful
     */
    public boolean placeBlock(Block blockPlaced, Player player, Material mat, int blockData) {
        Block blockAgainst = blockPlaced.getRelative((blockPlaced.getY() == 0) ? BlockFace.UP : BlockFace.DOWN);
        ItemStack itemHeld = new ItemStack(mat, 1, (short) blockData);
        BlockPlaceEvent placeEvent = new BlockPlaceEvent(new MockBlock(blockPlaced, mat, (byte) blockData), blockPlaced.getState(), blockAgainst, itemHeld, player, true);
        Bukkit.getPluginManager().callEvent(placeEvent);
        if (!placeEvent.isCancelled()) {
            blockPlaced.setType(mat);
            blockPlaced.setData((byte) blockData);
            return true;
        }
        return false;
    }

    public boolean placeBlock(Block blockPlaced, Player player, ItemStack is) {
        return placeBlock(blockPlaced, player, is.getType(), is.getData().getData());
    }

    public boolean attackEntity(LivingEntity target, Player attacker, double damage) {
        EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(attacker, target, DamageCause.ENTITY_ATTACK, damage);
        Bukkit.getPluginManager().callEvent(damageEvent);
        if (damage == 0) {
            return !damageEvent.isCancelled();
        }
        if (!damageEvent.isCancelled()) {
            target.damage(damage, attacker);
            target.setLastDamageCause(damageEvent);
            return true;
        }
        return false;
    }

    public boolean shearEntityNMS(Entity target, Player player, boolean mainHand) {
        if ((target instanceof Sheep && !((Sheep) target).isSheared()) || target instanceof MushroomCow) {
            PlayerShearEntityEvent evt = new PlayerShearEntityEvent(player, target);
            Bukkit.getPluginManager().callEvent(evt);
            if (!evt.isCancelled()) {
                if (target instanceof Sheep) {
                    Sheep sheep = (Sheep) target;
                    sheep.getLocation().getWorld().dropItem(sheep.getLocation(), new ItemStack(Material.WOOL, RND.nextInt(3) + 1, sheep.getColor().getWoolData()));
                    ((Sheep) target).setSheared(true);
                    // TODO: Apply damage to tool
                }
                return true;
            }
        }
        return false;
    }

    public boolean haulOrBreakBlock(Block from, Block to, BlockFace face, Player player) {
        BlockState state = from.getState();
        if (state.getClass().getName().endsWith("CraftBlockState")) {
            return false;
        }
        BlockBreakEvent breakEvent = new BlockBreakEvent(from, player);
        Bukkit.getPluginManager().callEvent(breakEvent);
        if (breakEvent.isCancelled()) {
            return false;
        }
        ItemStack stack = new ItemStack(state.getType(), 1, state.getData().getData());
        from.setType(Material.AIR);
        from.setData((byte) 0);
        BlockPlaceEvent placeEvent = new BlockPlaceEvent(to, to.getRelative(face.getOppositeFace()).getState(), to.getRelative(face.getOppositeFace()), stack, player, true);
        Bukkit.getPluginManager().callEvent(placeEvent);
        if (placeEvent.isCancelled()) {
            from.getWorld().dropItem(from.getLocation(), stack);
            return true;
        }
        to.setType(state.getType());
        to.setData(state.getData().getData());
        return true;
    }

    public boolean igniteEntity(Entity target, Player player, int duration) {
        EntityCombustByEntityEvent evt = new EntityCombustByEntityEvent(target, player, duration);
        Bukkit.getPluginManager().callEvent(evt);
        if (!evt.isCancelled()) {
            target.setFireTicks(duration);
            return true;
        }
        return false;
    }

    public boolean damagePlayer(Player player, double damage, DamageCause cause) {
        EntityDamageEvent evt = new EntityDamageEvent(player, cause, damage);
        Bukkit.getPluginManager().callEvent(evt);
        if (damage == 0) {
            return !evt.isCancelled();
        }
        if (!evt.isCancelled()) {
            player.setLastDamageCause(evt);
            player.damage(damage);
            return true;
        }
        return false;
    }

    public boolean formBlock(Block block, Material mat, byte data, Player player) {
        EntityBlockFormEvent evt = new EntityBlockFormEvent(player, block, new MockBlockState(block, mat, (byte) 0));
        Bukkit.getPluginManager().callEvent(evt);
        if (!evt.isCancelled()) {
            block.setType(mat);
            block.setData(data);
            return true;
        }
        return false;
    }

    public boolean showShulker(Block blockToHighlight, int entityId, Player player) {
        // This cannot be done without NMS
        return false;
    }

    public boolean hideShulker(int entityId, Player player) {
        // This cannot be done without NMS
        return false;
    }

    public Entity spawnGuardian(Location loc, boolean elderGuardian) {
        Guardian g = (Guardian) loc.getWorld().spawnEntity(loc, EntityType.GUARDIAN);
        if (elderGuardian) {
            g.setElder(true);
        }
        return g;
    }

    public boolean isZombie(Entity e) {
        return e.getType() == EntityType.ZOMBIE;
    }

    public boolean isBlockSafeToBreak(Block b) {
        Material mat = b.getType();
        return mat.isSolid()
                && !b.isLiquid()
                && !ArrayUtils.contains(INTERACTABLE_BLOCKS, mat)
                && !ArrayUtils.contains(UNBREAKABLE_BLOCKS, mat)
                && !ArrayUtils.contains(STORAGE_BLOCKS, mat);
    }

    public boolean grow(Block cropBlock, Player player) {
        Material mat = cropBlock.getType();
        byte dataValue = cropBlock.getData();
        switch (mat) {
            case COCOA:
                if (dataValue / 4 < 2) {
                    dataValue = (byte) Math.min(8 + (dataValue % 4), dataValue + 4);
                    break;
                }
                return false;
            case PUMPKIN_STEM:
            case MELON_STEM:
            case CARROT:
            case CROPS:
            case POTATO:
                if (dataValue < 7) {
                    dataValue = (byte) Math.min(7, dataValue + 3);
                    break;
                }
                return false;
            case NETHER_WARTS:
            case BEETROOT_BLOCK:
                if (dataValue < 3) {
                    dataValue = (byte) Math.min(3, dataValue + 1);
                    break;
                }
                return false;
            case CACTUS:
            case SUGAR_CANE_BLOCK:
                int height = 1;
                if (cropBlock.getRelative(BlockFace.DOWN).getType() == mat) { // Only grow if argument is the base block
                    return false;
                }
                while ((cropBlock = cropBlock.getRelative(BlockFace.UP)).getType() == mat) {
                    if (++height >= 3) { // Cancel if cactus/cane is fully grown
                        return false;
                    }
                }
                if (cropBlock.getType() != Material.AIR) { // Only grow if argument is the base block
                    return false;
                }
                break;
            default:
                return false;
        }

        if (player != null) {
            return placeBlock(cropBlock, player, mat, dataValue);
        }

        BlockGrowEvent evt = new BlockGrowEvent(cropBlock, new MockBlockState(cropBlock, mat, dataValue));
        Bukkit.getPluginManager().callEvent(evt);
        if (!evt.isCancelled()) {
            cropBlock.setType(mat);
            cropBlock.setData(dataValue);
            return true;
        }
        return false;
    }
}
