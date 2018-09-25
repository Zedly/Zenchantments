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
import org.bukkit.entity.EntityType;
import static org.bukkit.entity.EntityType.*;
import static org.bukkit.Material.*;
import org.bukkit.entity.Guardian;
import org.bukkit.event.block.BlockGrowEvent;

import org.bukkit.inventory.ItemStack;

public class CompatibilityAdapter {



    private static final CompatibilityAdapter INSTANCE = new CompatibilityAdapter();
    private static final Random RND = new Random();

    // Colors

    public static final EnumStorage<Material> BEDS = new EnumStorage<>(new Material[]{BLACK_BED, BLUE_BED, BROWN_BED,
        CYAN_BED, GRAY_BED, GREEN_BED, LIGHT_BLUE_BED, LIGHT_GRAY_BED, LIME_BED, MAGENTA_BED, ORANGE_BED, PINK_BED,
        PURPLE_BED, RED_BED, WHITE_BED, YELLOW_BED});

    public static final EnumStorage<Material> WOOL = new EnumStorage<>(new Material[]{BLACK_WOOL, BLUE_WOOL, BROWN_WOOL,
        CYAN_WOOL, GRAY_WOOL, GREEN_WOOL, LIGHT_BLUE_WOOL, LIGHT_GRAY_WOOL, LIME_WOOL, MAGENTA_WOOL, ORANGE_WOOL,
        PINK_WOOL, PURPLE_WOOL, RED_WOOL, WHITE_WOOL, YELLOW_WOOL});

    public static final EnumStorage<Material> SHULKER_BOXES = new EnumStorage<>(new Material[]{BLACK_SHULKER_BOX,
        BLUE_SHULKER_BOX, BROWN_SHULKER_BOX, CYAN_SHULKER_BOX, GRAY_SHULKER_BOX, GREEN_SHULKER_BOX,
        LIGHT_BLUE_SHULKER_BOX, LIGHT_GRAY_SHULKER_BOX, LIME_SHULKER_BOX, MAGENTA_SHULKER_BOX, ORANGE_SHULKER_BOX,
        PINK_SHULKER_BOX, PURPLE_SHULKER_BOX, RED_SHULKER_BOX, WHITE_SHULKER_BOX, YELLOW_SHULKER_BOX});

    public static final EnumStorage<Material> CONCRETE_POWDERS = new EnumStorage<>(new Material[]{BLACK_CONCRETE_POWDER,
        BLUE_CONCRETE_POWDER, BROWN_CONCRETE_POWDER, CYAN_CONCRETE_POWDER, GRAY_CONCRETE_POWDER, GREEN_CONCRETE_POWDER,
        LIGHT_BLUE_CONCRETE_POWDER, LIGHT_GRAY_CONCRETE_POWDER, LIME_CONCRETE_POWDER, MAGENTA_CONCRETE_POWDER,
        ORANGE_CONCRETE_POWDER, PINK_CONCRETE_POWDER, PURPLE_CONCRETE_POWDER, RED_CONCRETE_POWDER, WHITE_CONCRETE_POWDER,
        YELLOW_CONCRETE_POWDER});

    public static final EnumStorage<Material> CONCRETES = new EnumStorage<>(new Material[]{BLACK_CONCRETE, BLUE_CONCRETE,
        BROWN_CONCRETE, CYAN_CONCRETE, GRAY_CONCRETE, GREEN_CONCRETE, LIGHT_BLUE_CONCRETE, LIGHT_GRAY_CONCRETE,
        LIME_CONCRETE, MAGENTA_CONCRETE, ORANGE_CONCRETE, PINK_CONCRETE, PURPLE_CONCRETE, RED_CONCRETE, WHITE_CONCRETE,
        YELLOW_CONCRETE});

    public static final EnumStorage<Material> GLAZED_TERRACOTTAS = new EnumStorage<>(new Material[]{
        BLACK_GLAZED_TERRACOTTA, BLUE_GLAZED_TERRACOTTA, BROWN_GLAZED_TERRACOTTA, CYAN_GLAZED_TERRACOTTA,
        GRAY_GLAZED_TERRACOTTA, GREEN_GLAZED_TERRACOTTA, LIGHT_BLUE_GLAZED_TERRACOTTA, LIGHT_GRAY_GLAZED_TERRACOTTA,
        LIME_GLAZED_TERRACOTTA, MAGENTA_GLAZED_TERRACOTTA, ORANGE_GLAZED_TERRACOTTA, PINK_GLAZED_TERRACOTTA,
        PURPLE_GLAZED_TERRACOTTA, RED_GLAZED_TERRACOTTA, WHITE_GLAZED_TERRACOTTA, YELLOW_GLAZED_TERRACOTTA});

    public static final EnumStorage<Material> TERRACOTTAS = new EnumStorage<>(new Material[]{BLACK_TERRACOTTA,
        BLUE_TERRACOTTA, BROWN_TERRACOTTA, CYAN_TERRACOTTA, GRAY_TERRACOTTA, GREEN_TERRACOTTA, LIGHT_BLUE_TERRACOTTA,
        LIGHT_GRAY_TERRACOTTA, LIME_TERRACOTTA, MAGENTA_TERRACOTTA, ORANGE_TERRACOTTA, PINK_TERRACOTTA,
        PURPLE_TERRACOTTA, RED_TERRACOTTA, WHITE_TERRACOTTA, YELLOW_TERRACOTTA});

    public static final EnumStorage<Material> CARPETS = new EnumStorage<>(new Material[]{BLACK_CARPET,
        BLUE_CARPET, BROWN_CARPET, CYAN_CARPET, GRAY_CARPET, GREEN_CARPET,
        LIGHT_BLUE_CARPET, LIGHT_GRAY_CARPET, LIME_CARPET, MAGENTA_CARPET, ORANGE_CARPET,
        PINK_CARPET, PURPLE_CARPET, RED_CARPET, WHITE_CARPET, YELLOW_CARPET});

    public static final EnumStorage<Material> STAINED_GLASSES = new EnumStorage<>(new Material[]{BLACK_STAINED_GLASS,
        BLUE_STAINED_GLASS, BROWN_STAINED_GLASS, CYAN_STAINED_GLASS, GRAY_STAINED_GLASS, GREEN_STAINED_GLASS,
        LIGHT_BLUE_STAINED_GLASS, LIGHT_GRAY_STAINED_GLASS, LIME_STAINED_GLASS, MAGENTA_STAINED_GLASS,
        ORANGE_STAINED_GLASS, PINK_STAINED_GLASS, PURPLE_STAINED_GLASS, RED_STAINED_GLASS, WHITE_STAINED_GLASS,
        YELLOW_STAINED_GLASS});

    public static final EnumStorage<Material> STAINED_GLASS_PANES = new EnumStorage<>(new Material[]{
        BLACK_STAINED_GLASS_PANE, BLUE_STAINED_GLASS_PANE, BROWN_STAINED_GLASS_PANE, CYAN_STAINED_GLASS_PANE,
        GRAY_STAINED_GLASS_PANE, GREEN_STAINED_GLASS_PANE, LIGHT_BLUE_STAINED_GLASS_PANE, LIGHT_GRAY_STAINED_GLASS_PANE,
        LIME_STAINED_GLASS_PANE, MAGENTA_STAINED_GLASS_PANE, ORANGE_STAINED_GLASS_PANE, PINK_STAINED_GLASS_PANE,
        PURPLE_STAINED_GLASS_PANE, RED_STAINED_GLASS_PANE, WHITE_STAINED_GLASS_PANE, YELLOW_STAINED_GLASS_PANE});

    public static final EnumStorage<Material> BANNERS = new EnumStorage<>(new Material[]{BLACK_BANNER,
        BLUE_BANNER, BROWN_BANNER, CYAN_BANNER, GRAY_BANNER, GREEN_BANNER, LIGHT_BLUE_BANNER, LIGHT_GRAY_BANNER,
        LIME_BANNER, MAGENTA_BANNER, ORANGE_BANNER, PINK_BANNER, PURPLE_BANNER, RED_BANNER, WHITE_BANNER, YELLOW_BANNER});

    public static final EnumStorage<Material> WALL_BANNERS = new EnumStorage<>(new Material[]{BLACK_WALL_BANNER,
        BLUE_WALL_BANNER, BROWN_WALL_BANNER, CYAN_WALL_BANNER, GRAY_WALL_BANNER, GREEN_WALL_BANNER,
        LIGHT_BLUE_WALL_BANNER, LIGHT_GRAY_WALL_BANNER, LIME_WALL_BANNER, MAGENTA_WALL_BANNER, ORANGE_WALL_BANNER,
        PINK_WALL_BANNER, PURPLE_WALL_BANNER, RED_WALL_BANNER, WHITE_WALL_BANNER, YELLOW_WALL_BANNER});

    public static final EnumStorage<Material> DYES = new EnumStorage<>(new Material[]{INK_SAC,
        LAPIS_LAZULI, COCOA_BEANS, CYAN_DYE, GRAY_DYE, CACTUS_GREEN,
        LIGHT_BLUE_DYE, LIGHT_GRAY_DYE, LIME_DYE, MAGENTA_DYE, ORANGE_DYE,
        PINK_DYE, PURPLE_DYE, ROSE_RED, BONE_MEAL, DANDELION_YELLOW});

    // Woods

    public static final EnumStorage<Material> BOATS = new EnumStorage<>(new Material[]{ACACIA_BOAT, BIRCH_BOAT,
        DARK_OAK_BOAT, JUNGLE_BOAT, OAK_BOAT, SPRUCE_BOAT});

    public static final EnumStorage<Material> BUTTONS = new EnumStorage<>(new Material[]{ACACIA_BUTTON, BIRCH_BUTTON,
        DARK_OAK_BUTTON, JUNGLE_BUTTON, OAK_BUTTON, SPRUCE_BUTTON});

    public static final EnumStorage<Material> DOORS = new EnumStorage<>(new Material[]{ACACIA_DOOR, BIRCH_DOOR,
        DARK_OAK_DOOR, JUNGLE_DOOR, OAK_DOOR, SPRUCE_DOOR});

    public static final EnumStorage<Material> FENCES = new EnumStorage<>(new Material[]{ACACIA_FENCE, BIRCH_FENCE,
        DARK_OAK_FENCE, JUNGLE_FENCE, OAK_FENCE, SPRUCE_FENCE});

    public static final EnumStorage<Material> FENCE_GATES = new EnumStorage<>(new Material[]{ACACIA_FENCE_GATE,
        BIRCH_FENCE_GATE, DARK_OAK_FENCE_GATE, JUNGLE_FENCE_GATE, OAK_FENCE_GATE, SPRUCE_FENCE_GATE});

    public static final EnumStorage<Material> LEAVESS = new EnumStorage<>(new Material[]{ACACIA_LEAVES, BIRCH_LEAVES,
        DARK_OAK_LEAVES, JUNGLE_LEAVES, OAK_LEAVES, SPRUCE_LEAVES});

    public static final EnumStorage<Material> LOGS = new EnumStorage<>(new Material[]{ACACIA_LOG, BIRCH_LOG,
        DARK_OAK_LOG, JUNGLE_LOG, OAK_LOG, SPRUCE_LOG});

    public static final EnumStorage<Material> PLANKSS = new EnumStorage<>(new Material[]{ACACIA_PLANKS, BIRCH_PLANKS,
        DARK_OAK_PLANKS, JUNGLE_PLANKS, OAK_PLANKS, SPRUCE_PLANKS});

    public static final EnumStorage<Material> PRESSURE_PLATES = new EnumStorage<>(new Material[]{ACACIA_PRESSURE_PLATE,
        BIRCH_PRESSURE_PLATE, DARK_OAK_PRESSURE_PLATE, JUNGLE_PRESSURE_PLATE, OAK_PRESSURE_PLATE, SPRUCE_PRESSURE_PLATE});

    public static final EnumStorage<Material> SAPLINGS = new EnumStorage<>(new Material[]{ACACIA_SAPLING, BIRCH_SAPLING,
        DARK_OAK_SAPLING, JUNGLE_SAPLING, OAK_SAPLING, SPRUCE_SAPLING});

    public static final EnumStorage<Material> SLABS = new EnumStorage<>(new Material[]{ACACIA_SLAB, BIRCH_SLAB,
        DARK_OAK_SLAB, JUNGLE_SLAB, OAK_SLAB, SPRUCE_SLAB});

    public static final EnumStorage<Material> STAIRSS = new EnumStorage<>(new Material[]{ACACIA_STAIRS, BIRCH_STAIRS,
        DARK_OAK_STAIRS, JUNGLE_STAIRS, OAK_STAIRS, SPRUCE_STAIRS});

    public static final EnumStorage<Material> TRAPDOORS = new EnumStorage<>(new Material[]{ACACIA_TRAPDOOR, BIRCH_TRAPDOOR,
        DARK_OAK_TRAPDOOR, JUNGLE_TRAPDOOR, OAK_TRAPDOOR, SPRUCE_TRAPDOOR});

    public static final EnumStorage<Material> WOODS = new EnumStorage<>(new Material[]{ACACIA_WOOD, BIRCH_WOOD,
        DARK_OAK_WOOD, JUNGLE_WOOD, OAK_WOOD, SPRUCE_WOOD});


    // Plants

    public static final EnumStorage<Material> SMALL_FLOWERS = new EnumStorage<>(new Material[]{DANDELION, POPPY, BLUE_ORCHID,
        ALLIUM, AZURE_BLUET, RED_TULIP, ORANGE_TULIP, WHITE_TULIP, PINK_TULIP, OXEYE_DAISY});

    public static final EnumStorage<Material> LARGE_FLOWERS = new EnumStorage<>(new Material[]{SUNFLOWER, LILAC,
        TALL_GRASS, LARGE_FERN, ROSE_BUSH, PEONY});


    // Misc

    private static final Material[] UNBREAKABLE_BLOCKS = {AIR, BEDROCK, WATER,
        LAVA, PISTON_HEAD, MOVING_PISTON, END_GATEWAY, END_PORTAL_FRAME, END_PORTAL, DRAGON_EGG, BARRIER,
    COMMAND_BLOCK, CHAIN_COMMAND_BLOCK, REPEATING_COMMAND_BLOCK};

    private static final Material[] STORAGE_BLOCKS = {DISPENSER, SPAWNER, CHEST, FURNACE,
        JUKEBOX, ENDER_CHEST, COMMAND_BLOCK, CHAIN_COMMAND_BLOCK, REPEATING_COMMAND_BLOCK,
        BEACON, TRAPPED_CHEST, HOPPER, DROPPER};

    private static final Material[] INTERACTABLE_BLOCKS = {
        DISPENSER, NOTE_BLOCK, BED_BLOCK, CHEST, WORKBENCH, FURNACE, BURNING_FURNACE,
        WOODEN_DOOR, LEVER, STONE_BUTTON, JUKEBOX, DIODE_BLOCK_OFF, DIODE_BLOCK_ON, TRAP_DOOR,
        FENCE_GATE, ENCHANTMENT_TABLE, BREWING_STAND, ENDER_CHEST, COMMAND, BEACON, WOOD_BUTTON,
        ANVIL, TRAPPED_CHEST, REDSTONE_COMPARATOR_OFF, REDSTONE_COMPARATOR_ON, DAYLIGHT_DETECTOR,
        HOPPER, DROPPER};

    private static final Material ORES[] = new Material[]{COAL_ORE, REDSTONE_ORE, DIAMOND_ORE, GOLD_ORE,
        IRON_ORE, LAPIS_ORE, GLOWSTONE, NETHER_QUARTZ_ORE, EMERALD_ORE};

    private static final EntityType[] TRANSFORMATION_ENTITY_TYPES = new EntityType[]{BAT, SKELETON, ZOMBIE, SILVERFISH,
        ENDERMITE, ZOMBIE, PIG_ZOMBIE, VILLAGER, WITCH, COW, MUSHROOM_COW, SLIME, MAGMA_CUBE, WITHER_SKULL, SKELETON, OCELOT, WOLF};

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
