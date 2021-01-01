/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zedly.zenchantments.compatibility;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bamboo;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.entity.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import zedly.zenchantments.MaterialList;

import java.util.*;

import static org.bukkit.Material.TROPICAL_FISH;
import static org.bukkit.Material.*;
import static org.bukkit.entity.EntityType.PUFFERFISH;
import static org.bukkit.entity.EntityType.*;
import static org.bukkit.potion.PotionEffectType.*;

public class CompatibilityAdapter {
    private static final CompatibilityAdapter INSTANCE = new CompatibilityAdapter();
    private static final Random RND = new Random();

    private final EnumSet<EntityType> TRANSFORMATION_ENTITY_TYPES_FROM_E = EnumSet.of(
        HUSK, WITCH, EntityType.COD, PHANTOM, HORSE, SKELETON, EntityType.CHICKEN, SQUID, OCELOT, POLAR_BEAR, COW, PIG,
        SPIDER, SLIME, GUARDIAN, ENDERMITE, SKELETON_HORSE, EntityType.RABBIT, SHULKER, SNOWMAN, DROWNED, VINDICATOR,
        EntityType.SALMON, BLAZE, DONKEY, STRAY, PARROT, DOLPHIN, WOLF, SHEEP, MUSHROOM_COW, ZOMBIFIED_PIGLIN, CAVE_SPIDER,
        MAGMA_CUBE, ELDER_GUARDIAN, SILVERFISH, ZOMBIE_HORSE, EntityType.RABBIT, ENDERMAN, IRON_GOLEM, ZOMBIE, EVOKER,
        PUFFERFISH, VEX, MULE, WITHER_SKELETON, BAT, TURTLE, ZOMBIE_VILLAGER, VILLAGER, EntityType.TROPICAL_FISH, GHAST,
        LLAMA, CREEPER);

    public EnumSet<EntityType> TransformationEntityTypesFrom() {
        return TRANSFORMATION_ENTITY_TYPES_FROM_E;
    }

    private final EnumSet<EntityType> TRANSFORMATION_ENTITY_TYPES_TO_E = EnumSet.of(
        DROWNED, VINDICATOR, EntityType.SALMON, BLAZE, DONKEY, STRAY, PARROT, DOLPHIN, WOLF, SHEEP, MUSHROOM_COW,
        ZOMBIFIED_PIGLIN, CAVE_SPIDER, MAGMA_CUBE, ELDER_GUARDIAN, SILVERFISH, ZOMBIE_HORSE, EntityType.RABBIT, ENDERMAN,
        IRON_GOLEM, ZOMBIE, EVOKER, PUFFERFISH, VEX, MULE, WITHER_SKELETON, BAT, TURTLE, OCELOT, POLAR_BEAR, COW, PIG,
        SPIDER, SLIME, GUARDIAN, ENDERMITE, SKELETON_HORSE, EntityType.RABBIT, SHULKER, SNOWMAN, ZOMBIE_VILLAGER,
        VILLAGER, EntityType.TROPICAL_FISH, GHAST, LLAMA, SKELETON, EntityType.CHICKEN, SQUID, HUSK, WITCH,
        EntityType.COD, PHANTOM, HORSE, CREEPER);

    public EnumSet<EntityType> TransformationEntityTypesTo() {
        return TRANSFORMATION_ENTITY_TYPES_TO_E;
    }

    public LivingEntity TransformationCycle(LivingEntity ent, Random rnd) {
        int newTypeID = TransformationEntityTypesFrom().indexOf(ent.getType());
        if (newTypeID == -1) {
            return null;
        }
        EntityType newType = TransformationEntityTypesTo().get(newTypeID);
        LivingEntity newEnt = (LivingEntity) ent.getWorld().spawnEntity(ent.getLocation(), newType);

        switch (newType) {
            case HORSE:
                ((Horse) newEnt).setColor(Horse.Color.values()[rnd.nextInt(Horse.Color.values().length)]);
                ((Horse) newEnt).setStyle(Horse.Style.values()[rnd.nextInt(Horse.Style.values().length)]);
                break;
            case RABBIT:
                if (((Rabbit) ent).getRabbitType().equals(Rabbit.Type.THE_KILLER_BUNNY)) {
                    ((Rabbit) newEnt).setRabbitType(Rabbit.Type.values()[rnd.nextInt(Rabbit.Type.values().length - 1)]);
                } else {
                    ((Rabbit) newEnt).setRabbitType(Rabbit.Type.THE_KILLER_BUNNY);
                }
                break;
            case VILLAGER:
                ((Villager) newEnt).setProfession(
                        Villager.Profession.values()[rnd.nextInt(Villager.Profession.values().length)]);
                ((Villager) newEnt).setVillagerType(Villager.Type.values()[rnd.nextInt(Villager.Type.values().length)]);
                break;
            case LLAMA:
                ((Llama) newEnt).setColor(Llama.Color.values()[rnd.nextInt(Llama.Color.values().length)]);
                break;
            case TROPICAL_FISH:
                ((TropicalFish) newEnt).setBodyColor(DyeColor.values()[rnd.nextInt(DyeColor.values().length)]);
                ((TropicalFish) newEnt).setPatternColor(DyeColor.values()[rnd.nextInt(DyeColor.values().length)]);
                ((TropicalFish) newEnt).setPattern(
                        TropicalFish.Pattern.values()[rnd.nextInt(TropicalFish.Pattern.values().length)]);
                break;
            case PARROT:
                ((Parrot) newEnt).setVariant(Parrot.Variant.values()[rnd.nextInt(Parrot.Variant.values().length)]);
                break;
            case CAT:
                ((Cat) newEnt).setCatType(Cat.Type.values()[rnd.nextInt(Cat.Type.values().length)]);
                break;
            case SHEEP:
                ((Sheep) newEnt).setColor(DyeColor.values()[rnd.nextInt(DyeColor.values().length)]);
                break;
            case CREEPER:
                ((Creeper) newEnt).setPowered(!((Creeper) ent).isPowered());
                break;
            case MUSHROOM_COW:
                ((MushroomCow) newEnt).setVariant(
                        MushroomCow.Variant.values()[rnd.nextInt(MushroomCow.Variant.values().length)]);
                break;
            case FOX:
                ((Fox) newEnt).setFoxType(Fox.Type.values()[rnd.nextInt(Fox.Type.values().length)]);
                break;
            case ILLUSIONER:
                ((Panda) newEnt).setHiddenGene(Panda.Gene.values()[rnd.nextInt(Panda.Gene.values().length)]);
                ((Panda) newEnt).setMainGene(Panda.Gene.values()[rnd.nextInt(Panda.Gene.values().length)]);
                break;
        }

        newEnt.setCustomName(ent.getCustomName());
        newEnt.setCustomNameVisible(ent.isCustomNameVisible());
        return ent;
    }

    public static CompatibilityAdapter getInstance() {
        return INSTANCE;
    }

    // Removes the given ItemStack's durability by the given 'damage' and then sets the item direction the given
    // players hand.
    //      This also takes into account the unbreaking enchantment
    public static void damageTool(Player player, int damage, boolean handUsed) {
        if (!player.getGameMode().equals(GameMode.CREATIVE)) {
            ItemStack hand
                    = handUsed ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand();
            for (int i = 0; i < damage; i++) {
                if (RND.nextInt(100) <= (100 / (hand.getEnchantmentLevel(org.bukkit.enchantments.Enchantment.DURABILITY) + 1))) {
                    setDamage(hand, getDamage(hand) + 1);
                }
            }
            if (handUsed) {
                player.getInventory().setItemInMainHand(
                        getDamage(hand) > hand.getType().getMaxDurability() ? new ItemStack(AIR) : hand);
            } else {
                player.getInventory().setItemInOffHand(
                        getDamage(hand) > hand.getType().getMaxDurability() ? new ItemStack(AIR) : hand);
            }
        }
    }

    // Displays a particle with the given data
    public static void display(Location loc, Particle particle, int amount, double speed, double xO, double yO,
            double zO) {
        loc.getWorld().spawnParticle(particle, loc.getX(), loc.getY(), loc.getZ(), amount, (float) xO, (float) yO,
                (float) zO, (float) speed);
    }

    // Removes the given ItemStack's durability by the given 'damage'
    //      This also takes into account the unbreaking enchantment
    public static void addUnbreaking(Player player, ItemStack is, int damage) {
        if (!player.getGameMode().equals(GameMode.CREATIVE)) {
            for (int i = 0; i < damage; i++) {
                if (RND.nextInt(100) <= (100 / (is.getEnchantmentLevel(org.bukkit.enchantments.Enchantment.DURABILITY) + 1))) {
                    setDamage(is, getDamage(is) + 1);
                }
            }
        }
    }

    public static void setDamage(ItemStack is, int damage) {
        if (is.getItemMeta() instanceof org.bukkit.inventory.meta.Damageable) {
            org.bukkit.inventory.meta.Damageable dm = ((org.bukkit.inventory.meta.Damageable) is.getItemMeta());
            dm.setDamage(damage);
            is.setItemMeta((ItemMeta) dm);
        }
    }

    public static int getDamage(ItemStack is) {
        if (is.getItemMeta() instanceof org.bukkit.inventory.meta.Damageable) {
            org.bukkit.inventory.meta.Damageable dm = ((org.bukkit.inventory.meta.Damageable) is.getItemMeta());
            return dm.getDamage();
        }
        return 0;
    }

    protected CompatibilityAdapter() {
    }

    public void collectXP(Player player, int amount) {
        player.giveExp(amount);
    }

    public boolean breakBlockNMS(Block block, Player player) {
        BlockBreakEvent evt = new BlockBreakEvent(block, player);
        Bukkit.getPluginManager().callEvent(evt);
        if (!evt.isCancelled()) {
            block.breakNaturally(player.getInventory().getItemInMainHand());
            damageTool(player, 1, true);
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
     *
     * @return true if the block placement has been successful
     */
    public boolean placeBlock(Block blockPlaced, Player player, Material mat, BlockData d) {
        Block blockAgainst = blockPlaced.getRelative((blockPlaced.getY() == 0) ? BlockFace.UP : BlockFace.DOWN);
        ItemStack itemHeld = new ItemStack(mat);
        BlockPlaceEvent placeEvent
                = new BlockPlaceEvent(blockPlaced, blockPlaced.getState(), blockAgainst, itemHeld, player, true,
                        EquipmentSlot.HAND);

        Bukkit.getPluginManager().callEvent(placeEvent);
        if (!placeEvent.isCancelled()) {
            blockPlaced.setType(mat);
            BlockData data = blockPlaced.getBlockData();
            if (MaterialList.LEAVES.contains(mat)) {
                Leaves l = (Leaves) data;
                l.setPersistent(true);
                blockPlaced.setBlockData(l);
            }
            return true;
        }
        return false;
    }

    public boolean placeBlock(Block blockPlaced, Player player, ItemStack is) {
        return placeBlock(blockPlaced, player, is.getType(), (BlockData) is.getData());
    }

    public boolean attackEntity(LivingEntity target, Player attacker, double damage) {
        EntityDamageByEntityEvent damageEvent
                = new EntityDamageByEntityEvent(attacker, target, DamageCause.ENTITY_ATTACK, damage);
        Bukkit.getPluginManager().callEvent(damageEvent);
        if (damage == 0) {
            return !damageEvent.isCancelled();
        }
        if (!damageEvent.isCancelled()) {
            target.damage(damage, attacker);
            target.setLastDamageCause(damageEvent);
            damageTool(attacker, 1, true);
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
                    sheep.getLocation().getWorld().dropItem(sheep.getLocation(),
                            new ItemStack(MaterialList.WOOL.get(sheep.getColor().ordinal()), RND.nextInt(3) + 1));
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
        BlockPlaceEvent placeEvent = new BlockPlaceEvent(to, to.getRelative(face.getOppositeFace()).getState(),
                to.getRelative(face.getOppositeFace()), stack, player, true,
                EquipmentSlot.HAND);
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

    public boolean explodeCreeper(Creeper c, boolean damage) {
        float power;
        Location l = c.getLocation();
        if (c.isPowered()) {
            power = 6f;
        } else {
            power = 3.1f;
        }
        if (damage) {
            c.getWorld().createExplosion(l, power);
        } else {
            c.getWorld().createExplosion(l.getX(), l.getY(), l.getZ(), power, false, false);
        }
        c.remove();

        return true;
    }

    public boolean formBlock(Block block, Material mat, Player player) {
        BlockState bs = block.getState();
        bs.setType(mat);
        EntityBlockFormEvent evt = new EntityBlockFormEvent(player, block, bs);
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
        return e.getType() == EntityType.ZOMBIE || e.getType() == EntityType.ZOMBIE_VILLAGER || e.getType() == EntityType.HUSK;
    }

    public boolean isBlockSafeToBreak(Block b) {
        Material mat = b.getType();
        return mat.isSolid()
            && !b.isLiquid()
            && !MaterialList.INTERACTABLE_BLOCKS.contains(mat)
            && !MaterialList.UNBREAKABLE_BLOCKS.contains(mat)
            && !MaterialList.STORAGE_BLOCKS.contains(mat);
    }

    //endregion
    //endregion
    public boolean grow(Block cropBlock, Player player) {
        Material mat = cropBlock.getType();
        BlockData data = cropBlock.getBlockData();

        switch (mat) {
            case PUMPKIN_STEM:
            case MELON_STEM:
            case CARROTS:
            case WHEAT:
            case POTATOES:
            case COCOA:
            case NETHER_WART:
            case BEETROOTS:
            case SWEET_BERRY_BUSH:

                BlockData cropState = cropBlock.getBlockData();
                if (cropState instanceof Ageable) {
                    Ageable ag = (Ageable) cropState;
                    if (ag.getAge() >= ag.getMaximumAge()) {
                        return false;
                    }
                    ag.setAge(ag.getAge() + 1);
                    data = ag;
                }
                break;
            case BAMBOO_SAPLING: {
                if (player != null) {
                    boolean result = placeBlock(cropBlock, player, BAMBOO, null);
                    if (!result) {
                        return false;
                    }
                }

                Bamboo bamboo = (Bamboo) cropBlock.getBlockData();
                cropBlock = cropBlock.getRelative(BlockFace.UP);
                bamboo.setLeaves(Bamboo.Leaves.SMALL);
                data = bamboo;

                break;
            }
            case BAMBOO: {
                Bamboo bamboo = (Bamboo) cropBlock.getBlockData();

                int height = 1;
                if (cropBlock.getRelative(BlockFace.DOWN).getType() == mat) { // Only grow if argument is the base
                    // block
                    return false;
                }
                Block testBlock = cropBlock;
                while ((testBlock = testBlock.getRelative(BlockFace.UP)).getType() == mat) {
                    if (++height >= 16) { // Cancel if cactus/cane is fully grown
                        return false;
                    }
                }
                height++;

                boolean result;
                if (player != null) {
                    result = placeBlock(testBlock, player, mat, null);

                    if (!result) {
                        return false;
                    }
                }
                bamboo.setAge(0);

                if (height == 4) {
                    // Top piece
                    bamboo.setLeaves(Bamboo.Leaves.LARGE);
                    bamboo.setAge(1);
                    result = placeBlock(cropBlock.getRelative(0, 3, 0), player, mat, bamboo);

                    if (!result) {
                        return false;
                    }
                }
                if (height == 3 || height == 4) {
                    // Top piece (height = 3) or second from top (height = 4)
                    bamboo.setLeaves(Bamboo.Leaves.SMALL);
                    bamboo.setAge(height == 4 ? 1 : 0);
                    result = placeBlock(cropBlock.getRelative(0, 2, 0), player, mat, bamboo);

                    if (!result) {
                        return false;
                    }

                    // Second from bottom piece
                    bamboo.setAge(0);
                    bamboo.setLeaves(Bamboo.Leaves.NONE);
                    result = placeBlock(cropBlock, player, mat, bamboo);

                    if (!result) {
                        return false;
                    }

                    bamboo.setLeaves(Bamboo.Leaves.SMALL);
                    result = placeBlock(cropBlock.getRelative(0, 1, 0), player, mat, bamboo);

                    if (!result) {
                        return false;
                    }

                }

                if (height > 4) {
                    for (int i = height - 1; i >= 0; i--) {
                        Bamboo.Leaves leaves = i < height - 3 ? Bamboo.Leaves.NONE : i == height - 3 ? Bamboo.Leaves.SMALL : Bamboo.Leaves.LARGE;
                        bamboo.setLeaves(leaves);
                        bamboo.setAge(height == 5 && i < 2 ? 0 : 1);
                        result = placeBlock(cropBlock.getRelative(0, i, 0), player, mat, bamboo);

                        if (!result) {
                            return false;
                        }
                    }
                }
                return true;
            }
            case CACTUS:
            case SUGAR_CANE:
                int height = 1;
                if (cropBlock.getRelative(BlockFace.DOWN).getType() == mat) { // Only grow if argument is the base
                    // block
                    return false;
                }
                while ((cropBlock = cropBlock.getRelative(BlockFace.UP)).getType() == mat) {
                    if (++height >= 3) { // Cancel if cactus/cane is fully grown
                        return false;
                    }
                }
                if (!MaterialList.AIR.contains(cropBlock.getType())) { // Only grow if argument is the base block
                    return false;
                }

                break;
            default:
                return false;
        }

        if (player != null) {
            return placeBlock(cropBlock, player, mat, data);
        }

        BlockState bs = cropBlock.getState();
        bs.setType(mat);
        BlockGrowEvent evt = new BlockGrowEvent(cropBlock, bs);
        Bukkit.getPluginManager().callEvent(evt);
        if (!evt.isCancelled()) {
            cropBlock.setType(mat);
            cropBlock.setBlockData(data);
            return true;
        }
        return false;
    }

    public boolean pickBerries(Block berryBlock, Player player) {
        BlockData data = berryBlock.getBlockData();
        Ageable a = (Ageable) data;
        if (a.getAge() > 1) { // Age of ripe Berries
            PlayerInteractEvent pie = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, player.getInventory().getItemInMainHand(), berryBlock, player.getFacing());
            Bukkit.getPluginManager().callEvent(pie);
            if (!pie.isCancelled()) {
                int numDropped = (a.getAge() == 3 ? 2 : 1) + (RND.nextBoolean() ? 1 : 0); // Natural drop rate. Age 2 -> 1-2 berries, Age 3 -> 2-3 berries
                a.setAge(1); // Picked adult berry bush
                berryBlock.setBlockData(a);
                berryBlock.getWorld().dropItem(berryBlock.getLocation(),
                        new ItemStack(Material.SWEET_BERRIES, numDropped));
                return true;
            }
        }
        return false;
    }
}
