/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.zenchantments.compatibility;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
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
import org.bukkit.event.block.BlockGrowEvent;

import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class CompatibilityAdapter {
    private static final CompatibilityAdapter INSTANCE = new CompatibilityAdapter();
    private static final Random RND = new Random();

    //region Enums
    //region Colors
    public final EnumStorage<Material> BEDS = new EnumStorage<>(new Material[]{WHITE_BED, ORANGE_BED, MAGENTA_BED,
        LIGHT_BLUE_BED, YELLOW_BED, LIME_BED, PINK_BED, GRAY_BED, LIGHT_GRAY_BED, CYAN_BED, PURPLE_BED, BLUE_BED,
        BROWN_BED, GREEN_BED, RED_BED, BLACK_BED});

    public final EnumStorage<Material> WOOL = new EnumStorage<>(new Material[]{WHITE_WOOL, ORANGE_WOOL, MAGENTA_WOOL,
        LIGHT_BLUE_WOOL, YELLOW_WOOL, LIME_WOOL, PINK_WOOL, GRAY_WOOL, LIGHT_GRAY_WOOL, CYAN_WOOL, PURPLE_WOOL,
        BLUE_WOOL, BROWN_WOOL, GREEN_WOOL, RED_WOOL, BLACK_WOOL});

    public final EnumStorage<Material> SHULKER_BOXES = new EnumStorage<>(new Material[]{WHITE_SHULKER_BOX,
        ORANGE_SHULKER_BOX, MAGENTA_SHULKER_BOX, LIGHT_BLUE_SHULKER_BOX, YELLOW_SHULKER_BOX, LIME_SHULKER_BOX,
        PINK_SHULKER_BOX, GRAY_SHULKER_BOX, LIGHT_GRAY_SHULKER_BOX, CYAN_SHULKER_BOX, PURPLE_SHULKER_BOX,
        BLUE_SHULKER_BOX, BROWN_SHULKER_BOX, GREEN_SHULKER_BOX, RED_SHULKER_BOX, BLACK_SHULKER_BOX});

    public final EnumStorage<Material> CONCRETE_POWDERS = new EnumStorage<>(new Material[]{WHITE_CONCRETE_POWDER,
        ORANGE_CONCRETE_POWDER, MAGENTA_CONCRETE_POWDER, LIGHT_BLUE_CONCRETE_POWDER, YELLOW_CONCRETE_POWDER,
        LIME_CONCRETE_POWDER, PINK_CONCRETE_POWDER, GRAY_CONCRETE_POWDER, LIGHT_GRAY_CONCRETE_POWDER,
        CYAN_CONCRETE_POWDER, PURPLE_CONCRETE_POWDER, BLUE_CONCRETE_POWDER, BROWN_CONCRETE_POWDER,
        GREEN_CONCRETE_POWDER, RED_CONCRETE_POWDER, BLACK_CONCRETE_POWDER});

    public final EnumStorage<Material> CONCRETES = new EnumStorage<>(new Material[]{WHITE_CONCRETE, ORANGE_CONCRETE,
        MAGENTA_CONCRETE, LIGHT_BLUE_CONCRETE, YELLOW_CONCRETE, LIME_CONCRETE, PINK_CONCRETE, GRAY_CONCRETE,
        LIGHT_GRAY_CONCRETE, CYAN_CONCRETE, PURPLE_CONCRETE, BLUE_CONCRETE, BROWN_CONCRETE, GREEN_CONCRETE,
        RED_CONCRETE, BLACK_CONCRETE});

    public final EnumStorage<Material> GLAZED_TERRACOTTAS = new EnumStorage<>(new Material[]{WHITE_GLAZED_TERRACOTTA,
        ORANGE_GLAZED_TERRACOTTA, MAGENTA_GLAZED_TERRACOTTA, LIGHT_BLUE_GLAZED_TERRACOTTA, YELLOW_GLAZED_TERRACOTTA,
        LIME_GLAZED_TERRACOTTA, PINK_GLAZED_TERRACOTTA, GRAY_GLAZED_TERRACOTTA, LIGHT_GRAY_GLAZED_TERRACOTTA,
        CYAN_GLAZED_TERRACOTTA, PURPLE_GLAZED_TERRACOTTA, BLUE_GLAZED_TERRACOTTA, BROWN_GLAZED_TERRACOTTA,
        GREEN_GLAZED_TERRACOTTA, RED_GLAZED_TERRACOTTA, BLACK_GLAZED_TERRACOTTA});

    public final EnumStorage<Material> TERRACOTTAS = new EnumStorage<>(new Material[]{WHITE_TERRACOTTA,
        ORANGE_TERRACOTTA, MAGENTA_TERRACOTTA, LIGHT_BLUE_TERRACOTTA, YELLOW_TERRACOTTA, LIME_TERRACOTTA,
        PINK_TERRACOTTA, GRAY_TERRACOTTA, LIGHT_GRAY_TERRACOTTA, CYAN_TERRACOTTA, PURPLE_TERRACOTTA, BLUE_TERRACOTTA,
        BROWN_TERRACOTTA, GREEN_TERRACOTTA, RED_TERRACOTTA, BLACK_TERRACOTTA});

    public final EnumStorage<Material> CARPETS = new EnumStorage<>(new Material[]{WHITE_CARPET, ORANGE_CARPET,
        MAGENTA_CARPET, LIGHT_BLUE_CARPET, YELLOW_CARPET, LIME_CARPET, PINK_CARPET, GRAY_CARPET, LIGHT_GRAY_CARPET,
        CYAN_CARPET, PURPLE_CARPET, BLUE_CARPET, BROWN_CARPET, GREEN_CARPET, RED_CARPET, BLACK_CARPET});

    public final EnumStorage<Material> STAINED_GLASSES = new EnumStorage<>(new Material[]{WHITE_STAINED_GLASS,
        ORANGE_STAINED_GLASS, MAGENTA_STAINED_GLASS, LIGHT_BLUE_STAINED_GLASS, YELLOW_STAINED_GLASS, LIME_STAINED_GLASS,
        PINK_STAINED_GLASS, GRAY_STAINED_GLASS, LIGHT_GRAY_STAINED_GLASS, CYAN_STAINED_GLASS, PURPLE_STAINED_GLASS,
        BLUE_STAINED_GLASS, BROWN_STAINED_GLASS, GREEN_STAINED_GLASS, RED_STAINED_GLASS, BLACK_STAINED_GLASS});

    public final EnumStorage<Material> STAINED_GLASS_PANES = new EnumStorage<>(new Material[]{WHITE_STAINED_GLASS_PANE,
        ORANGE_STAINED_GLASS_PANE, MAGENTA_STAINED_GLASS_PANE, LIGHT_BLUE_STAINED_GLASS_PANE, YELLOW_STAINED_GLASS_PANE,
        LIME_STAINED_GLASS_PANE, PINK_STAINED_GLASS_PANE, GRAY_STAINED_GLASS_PANE, LIGHT_GRAY_STAINED_GLASS_PANE,
        CYAN_STAINED_GLASS_PANE, PURPLE_STAINED_GLASS_PANE, BLUE_STAINED_GLASS_PANE, BROWN_STAINED_GLASS_PANE,
        GREEN_STAINED_GLASS_PANE, RED_STAINED_GLASS_PANE, BLACK_STAINED_GLASS_PANE});

    public final EnumStorage<Material> BANNERS = new EnumStorage<>(new Material[]{WHITE_BANNER, ORANGE_BANNER,
        MAGENTA_BANNER, LIGHT_BLUE_BANNER, YELLOW_BANNER, LIME_BANNER, PINK_BANNER, GRAY_BANNER, LIGHT_GRAY_BANNER,
        CYAN_BANNER, PURPLE_BANNER, BLUE_BANNER, BROWN_BANNER, GREEN_BANNER, RED_BANNER, BLACK_BANNER});

    public final EnumStorage<Material> WALL_BANNERS = new EnumStorage<>(new Material[]{WHITE_WALL_BANNER,
        ORANGE_WALL_BANNER, MAGENTA_WALL_BANNER, LIGHT_BLUE_WALL_BANNER, YELLOW_WALL_BANNER, LIME_WALL_BANNER,
        PINK_WALL_BANNER, GRAY_WALL_BANNER, LIGHT_GRAY_WALL_BANNER, CYAN_WALL_BANNER, PURPLE_WALL_BANNER,
        BLUE_WALL_BANNER, BROWN_WALL_BANNER, GREEN_WALL_BANNER, RED_WALL_BANNER, BLACK_WALL_BANNER});

    public final EnumStorage<Material> DYES = new EnumStorage<>(new Material[]{BONE_MEAL, ORANGE_DYE, MAGENTA_DYE,
        LIGHT_BLUE_DYE, DANDELION_YELLOW, LIME_DYE, PINK_DYE, GRAY_DYE, LIGHT_GRAY_DYE, CYAN_DYE, PURPLE_DYE,
        LAPIS_LAZULI, COCOA_BEANS, CACTUS_GREEN, ROSE_RED, INK_SAC});
    //endregion

    //region Woods
    public final EnumStorage<Material> BOATS = new EnumStorage<>(new Material[]{ACACIA_BOAT, BIRCH_BOAT,
        DARK_OAK_BOAT, JUNGLE_BOAT, OAK_BOAT, SPRUCE_BOAT});

    public final EnumStorage<Material> WOOD_BUTTONS = new EnumStorage<>(new Material[]{ACACIA_BUTTON, BIRCH_BUTTON,
        DARK_OAK_BUTTON, JUNGLE_BUTTON, OAK_BUTTON, SPRUCE_BUTTON});

    public final EnumStorage<Material> WOOD_DOORS = new EnumStorage<>(new Material[]{ACACIA_DOOR, BIRCH_DOOR,
        DARK_OAK_DOOR, JUNGLE_DOOR, OAK_DOOR, SPRUCE_DOOR});

    public final EnumStorage<Material> WOOD_FENCES = new EnumStorage<>(new Material[]{ACACIA_FENCE, BIRCH_FENCE,
        DARK_OAK_FENCE, JUNGLE_FENCE, OAK_FENCE, SPRUCE_FENCE});

    public final EnumStorage<Material> FENCE_GATES = new EnumStorage<>(new Material[]{ACACIA_FENCE_GATE,
        BIRCH_FENCE_GATE, DARK_OAK_FENCE_GATE, JUNGLE_FENCE_GATE, OAK_FENCE_GATE, SPRUCE_FENCE_GATE});

    public final EnumStorage<Material> LEAVESS = new EnumStorage<>(new Material[]{ACACIA_LEAVES, BIRCH_LEAVES,
        DARK_OAK_LEAVES, JUNGLE_LEAVES, OAK_LEAVES, SPRUCE_LEAVES});

    public final EnumStorage<Material> LOGS = new EnumStorage<>(new Material[]{ACACIA_LOG, BIRCH_LOG,
        DARK_OAK_LOG, JUNGLE_LOG, OAK_LOG, SPRUCE_LOG});

    public final EnumStorage<Material> PLANKSS = new EnumStorage<>(new Material[]{ACACIA_PLANKS, BIRCH_PLANKS,
        DARK_OAK_PLANKS, JUNGLE_PLANKS, OAK_PLANKS, SPRUCE_PLANKS});

    public final EnumStorage<Material> WOOD_PRESSURE_PLATES = new EnumStorage<>(new Material[]{ACACIA_PRESSURE_PLATE,
        BIRCH_PRESSURE_PLATE, DARK_OAK_PRESSURE_PLATE, JUNGLE_PRESSURE_PLATE, OAK_PRESSURE_PLATE, SPRUCE_PRESSURE_PLATE});

    public final EnumStorage<Material> SAPLINGS = new EnumStorage<>(new Material[]{ACACIA_SAPLING, BIRCH_SAPLING,
        DARK_OAK_SAPLING, JUNGLE_SAPLING, OAK_SAPLING, SPRUCE_SAPLING});

    public final EnumStorage<Material> WOOD_SLABS = new EnumStorage<>(new Material[]{ACACIA_SLAB, BIRCH_SLAB,
        DARK_OAK_SLAB, JUNGLE_SLAB, OAK_SLAB, SPRUCE_SLAB});

    public final EnumStorage<Material> WOOD_STAIRSS = new EnumStorage<>(new Material[]{ACACIA_STAIRS, BIRCH_STAIRS,
        DARK_OAK_STAIRS, JUNGLE_STAIRS, OAK_STAIRS, SPRUCE_STAIRS});

    public final EnumStorage<Material> WOOD_TRAPDOORS = new EnumStorage<>(new Material[]{ACACIA_TRAPDOOR, BIRCH_TRAPDOOR,
        DARK_OAK_TRAPDOOR, JUNGLE_TRAPDOOR, OAK_TRAPDOOR, SPRUCE_TRAPDOOR});

    public final EnumStorage<Material> WOODS = new EnumStorage<>(new Material[]{ACACIA_WOOD, BIRCH_WOOD,
        DARK_OAK_WOOD, JUNGLE_WOOD, OAK_WOOD, SPRUCE_WOOD});

    public final EnumStorage<Material> STRIPPED_WOODS = new EnumStorage<>(new Material[]{STRIPPED_ACACIA_LOG, STRIPPED_BIRCH_LOG,
        STRIPPED_DARK_OAK_LOG, STRIPPED_JUNGLE_LOG, STRIPPED_OAK_LOG, STRIPPED_SPRUCE_LOG});
    //endregion

    //region Plants
    public final EnumStorage<Material> SMALL_FLOWERS = new EnumStorage<>(new Material[]{DANDELION, POPPY, BLUE_ORCHID,
        ALLIUM, AZURE_BLUET, RED_TULIP, ORANGE_TULIP, WHITE_TULIP, PINK_TULIP, OXEYE_DAISY});

    public final EnumStorage<Material> LARGE_FLOWERS = new EnumStorage<>(new Material[]{SUNFLOWER, LILAC,
        TALL_GRASS, LARGE_FERN, ROSE_BUSH, PEONY});
    //endregion

    //region Misc
    public final EnumStorage<Material> BUTTONS = new EnumStorage<>(new Material[]{STONE_BUTTON}, WOOD_BUTTONS);

    public final EnumStorage<Material> DOORSS = new EnumStorage<>(new Material[]{IRON_DOOR}, WOOD_DOORS);

    public final EnumStorage<Material> TRAPDOORSS = new EnumStorage<>(new Material[]{IRON_TRAPDOOR}, WOOD_TRAPDOORS);

    public final EnumStorage<Material> PRESSURE_PLATES = new EnumStorage<>(new Material[]{STONE_PRESSURE_PLATE,
        LIGHT_WEIGHTED_PRESSURE_PLATE, HEAVY_WEIGHTED_PRESSURE_PLATE}, WOOD_PRESSURE_PLATES);

    public final EnumStorage<Material> AIRS = new EnumStorage<>(new Material[]{AIR, CAVE_AIR, VOID_AIR});

    public final EnumStorage<Material> COMMAND_BLOCKS = new EnumStorage<>(new Material[]{COMMAND_BLOCK,
        CHAIN_COMMAND_BLOCK, REPEATING_COMMAND_BLOCK});

    public final EnumStorage<Material> UNBREAKABLE_BLOCKS = new EnumStorage<>(new Material[] {BARRIER, BEDROCK,
        BUBBLE_COLUMN, DRAGON_BREATH, DRAGON_EGG, END_CRYSTAL, END_GATEWAY, END_PORTAL, END_PORTAL_FRAME, LAVA,
        STRUCTURE_VOID, STRUCTURE_BLOCK, WATER, PISTON_HEAD, MOVING_PISTON}, AIRS, COMMAND_BLOCKS);

    public final EnumStorage<Material> STORAGE_BLOCKS = new EnumStorage<>(new Material[]{DISPENSER, SPAWNER,
        CHEST, FURNACE, JUKEBOX, ENDER_CHEST, BEACON, TRAPPED_CHEST, HOPPER, DROPPER, BREWING_STAND, ANVIL}, SHULKER_BOXES, COMMAND_BLOCKS);

    public final EnumStorage<Material> INTERACTABLE_BLOCKS = new EnumStorage<>(new Material[]{
        NOTE_BLOCK, CRAFTING_TABLE, LEVER, REPEATER, ENCHANTING_TABLE, COMPARATOR, DAYLIGHT_DETECTOR, OBSERVER},
	    BEDS, DOORSS, TRAPDOORSS, FENCE_GATES, COMMAND_BLOCKS, BUTTONS, SHULKER_BOXES, STORAGE_BLOCKS);

	public final EnumStorage<Material> ORES = new EnumStorage<>(new Material[]{COAL_ORE, REDSTONE_ORE, DIAMOND_ORE,
		GOLD_ORE, IRON_ORE, LAPIS_ORE, GLOWSTONE, NETHER_QUARTZ_ORE, EMERALD_ORE});

    public final EnumStorage<Material> SANDS = new EnumStorage<>(new Material[]{SAND, RED_SAND});

    public final EnumStorage<Material> SANDSTONES = new EnumStorage<>(new Material[]{SANDSTONE, CUT_SANDSTONE,
        CHISELED_SANDSTONE, SMOOTH_SANDSTONE, RED_SANDSTONE, CUT_RED_SANDSTONE, CHISELED_RED_SANDSTONE,
        SMOOTH_RED_SANDSTONE});

	public final EnumStorage<Material> TERRAFORMER_MATERIALS = new EnumStorage<>(new Material[]{STONE, GRASS_BLOCK,
		DIRT, COBBLESTONE, SAND, GRAVEL, SANDSTONE, BRICK, TNT, BOOKSHELF, MOSSY_COBBLESTONE, ICE, SNOW_BLOCK, CLAY,
		NETHERRACK, SOUL_SAND, STONE_BRICKS, MYCELIUM, NETHER_BRICK, END_STONE, EMERALD_ORE, QUARTZ_BLOCK, SLIME_BLOCK,
		PRISMARINE, PACKED_ICE, RED_SANDSTONE}, ORES, TERRACOTTAS, GLAZED_TERRACOTTAS, WOOL, WOODS, PLANKSS,
		STRIPPED_WOODS, LOGS, CONCRETES, CONCRETE_POWDERS, STAINED_GLASSES);


    public final EnumStorage<Material> TRUNK_BLOCKS = new EnumStorage<>(new Material[]{MUSHROOM_STEM,
        BROWN_MUSHROOM_BLOCK, RED_MUSHROOM_BLOCK}, LOGS);

    public final EnumStorage<Material> LUMBER_WHITELIST = new EnumStorage<>(new Material[]{
        DIRT, GRASS, VINE, SNOW, COCOA, GRAVEL, STONE, WATER, LAVA, SAND, BROWN_MUSHROOM, RED_MUSHROOM,
        MOSSY_COBBLESTONE, CLAY, BROWN_MUSHROOM, RED_MUSHROOM, MYCELIUM, TORCH, SUGAR_CANE, GRASS_BLOCK},
        TRUNK_BLOCKS, LEAVESS, SMALL_FLOWERS, LARGE_FLOWERS, SAPLINGS, AIRS);

    public final EnumStorage<Material> GROWN_CROPS = new EnumStorage<>(new Material[]{WHEAT, POTATOES, CARROTS, COCOA, BEETROOTS, NETHER_WART});
    public final EnumStorage<Material> CROP_YEILDS = new EnumStorage<>(new Material[]{WHEAT, POTATO, CARROT, COCOA_BEANS, BEETROOT, NETHER_WART});
    public final EnumStorage<Material> GROWN_MELON = new EnumStorage<>(new Material[]{MELON, PUMPKIN});
    public final EnumStorage<Material> MELON_YEILDS = new EnumStorage<>(new Material[]{MELON_SLICE, PUMPKIN});

	//endregion
    //endregion

	public final EnumStorage<EntityType> TRANSFORMATION_ENTITY_TYPES = new EnumStorage<>(new EntityType[] {
	    SKELETON, WITHER_SKELETON, ZOMBIE, DROWNED, WITCH, VILLAGER, COW, MUSHROOM_COW, PIG, PIG_ZOMBIE, SILVERFISH,
        ENDERMITE, OCELOT, WOLF, SLIME, MAGMA_CUBE, GUARDIAN, ELDER_GUARDIAN, PARROT, BAT, SPIDER, CAVE_SPIDER, COW,
        MUSHROOM_COW, DONKEY, LLAMA, HORSE, SKELETON_HORSE, BLAZE, VEX});


    public final EnumStorage<Material> FIRE_RAW = new EnumStorage<>(new Material[]{STONE, DIORITE, ANDESITE, GRANITE,
    IRON_ORE, GOLD_ORE, COBBLESTONE, MOSSY_COBBLESTONE, NETHERRACK, STONE_BRICKS}, TERRACOTTAS);

    public final EnumStorage<Material> FIRE_COOKED = new EnumStorage<>(new Material[]{STONE_BRICKS, POLISHED_DIORITE,
        POLISHED_ANDESITE, POLISHED_GRANITE, IRON_INGOT, GOLD_INGOT, STONE, MOSSY_STONE_BRICKS, NETHER_BRICK,
        CRACKED_STONE_BRICKS}, GLAZED_TERRACOTTAS);

    public static CompatibilityAdapter getInstance() {
        return INSTANCE;
    }

    protected CompatibilityAdapter() {
    }

    public boolean breakBlockNMS(Block block, Player player) {
        BlockBreakEvent evt = new BlockBreakEvent(block, player);
        Bukkit.getPluginManager().callEvent(evt);
        if (!evt.isCancelled()) {
            block.breakNaturally(player.getInventory().getItemInMainHand());
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
     * @param data the block data to set for the block, if allowed
     * @return true if the block placement has been successful
     */
    public boolean placeBlock(Block blockPlaced, Player player, Material mat, BlockData data) {
        Block blockAgainst = blockPlaced.getRelative((blockPlaced.getY() == 0) ? BlockFace.UP : BlockFace.DOWN);
        ItemStack itemHeld = new ItemStack(mat);
        BlockPlaceEvent placeEvent = new BlockPlaceEvent(blockPlaced, blockPlaced.getState(), blockAgainst, itemHeld, player, true, EquipmentSlot.HAND);

        Bukkit.getPluginManager().callEvent(placeEvent);
        if (!placeEvent.isCancelled()) {
            blockPlaced.setType(mat);
            if (data != null) {
                blockPlaced.setBlockData(data);
            }
            return true;
        }
        return false;
    }

    public boolean placeBlock(Block blockPlaced, Player player, ItemStack is) {
        return placeBlock(blockPlaced, player, is.getType(), (BlockData) is.getData());
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
                    sheep.getLocation().getWorld().dropItem(sheep.getLocation(), new ItemStack(WOOL.get(sheep.getColor().ordinal()), RND.nextInt(3) + 1));
                    ((Sheep) target).setSheared(true);

                    // TODO: Apply damage to tool
                } else if (target instanceof MushroomCow) {
                    MushroomCow cow = (MushroomCow) target;
                    // TODO: DO
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
        ItemStack stack = new ItemStack(state.getType(), 1);
        from.setType(AIR);
        BlockPlaceEvent placeEvent = new BlockPlaceEvent(to, to.getRelative(face.getOppositeFace()).getState(), to.getRelative(face.getOppositeFace()), stack, player, true);
        Bukkit.getPluginManager().callEvent(placeEvent);
        if (placeEvent.isCancelled()) {
            from.getWorld().dropItem(from.getLocation(), stack);
            return true;
        }
        to.setType(state.getType());
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

    public boolean formBlock(Block block, Material mat, Player player) {
        EntityBlockFormEvent evt = new EntityBlockFormEvent(player, block, new MockBlockState(block, mat, (byte) 0));
        Bukkit.getPluginManager().callEvent(evt);
        if (!evt.isCancelled()) {
            block.setType(mat);
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
        return loc.getWorld().spawnEntity(loc, elderGuardian ? EntityType.ELDER_GUARDIAN : EntityType.GUARDIAN);
    }

    public boolean isZombie(Entity e) {
        return e.getType() == EntityType.ZOMBIE || e.getType() == EntityType.ZOMBIE_VILLAGER;
    }

    public boolean isBlockSafeToBreak(Block b) {
        Material mat = b.getType();
        return mat.isSolid() && !b.isLiquid() && !INTERACTABLE_BLOCKS.contains(mat) && !UNBREAKABLE_BLOCKS.contains(mat)
                && !STORAGE_BLOCKS.contains(mat);
    }

    public boolean grow(Block cropBlock, Player player) {
        Material mat = cropBlock.getType();
        BlockData data = cropBlock.getBlockData();
        int age = 0;
        switch (mat) {
            case PUMPKIN_STEM:
            case MELON_STEM:
            case CARROTS:
            case WHEAT:
            case POTATOES:
            case COCOA:
            case NETHER_WART:
            case BEETROOTS:

                BlockData cropState = cropBlock.getBlockData();
                if (cropState instanceof Ageable) {
                    Ageable ag = (Ageable) cropState;
                    if (ag.getAge() >= ag.getMaximumAge()) {
                        return false;
                    }
                    ag.setAge(ag.getAge() + 1);
                    age = ag.getAge();
                    data = ag;
                }
                break;
            case CACTUS:
            case SUGAR_CANE:
                int height = 1;
                if (cropBlock.getRelative(BlockFace.DOWN).getType() == mat) { // Only grow if argument is the base block
                    return false;
                }
                while ((cropBlock = cropBlock.getRelative(BlockFace.UP)).getType() == mat) {
                    if (++height >= 3) { // Cancel if cactus/cane is fully grown
                        return false;
                    }
                }
                if (!AIRS.contains(cropBlock.getType())) { // Only grow if argument is the base block
                    return false;
                }

                break;
            default:
                return false;
        }

        if (player != null) {
            return placeBlock(cropBlock, player, mat, data);
        }

        BlockGrowEvent evt = new BlockGrowEvent(cropBlock, new MockBlockState(cropBlock, mat, (byte) age));
        Bukkit.getPluginManager().callEvent(evt);
        if (!evt.isCancelled()) {
            cropBlock.setType(mat);
            cropBlock.setBlockData(data);
            return true;
        }
        return false;
    }
}
