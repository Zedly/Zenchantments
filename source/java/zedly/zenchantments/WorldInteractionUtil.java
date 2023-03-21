package zedly.zenchantments;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.EntityAnimal;
import net.minecraft.world.entity.animal.EntityMushroomCow;
import net.minecraft.world.entity.animal.EntitySheep;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bamboo;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R2.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R2.entity.*;
import org.bukkit.craftbukkit.v1_19_R2.util.CraftChatMessage;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.*;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zedly.zenchantments.event.ZenBlockPlaceEvent;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Objects.requireNonNull;
import static org.bukkit.Material.BAMBOO;

public class WorldInteractionUtil {
    private WorldInteractionUtil() {
    }

    @NotNull
    public static String reproduceCorruptedInvisibleSequence(final @NotNull String original) {
        requireNonNull(original);
        return CraftChatMessage.fromJSONComponent(CraftChatMessage.fromStringToJSON(original, false));
    }


    public static void collectExp(final @NotNull Player player, final int amount) {
        final EntityExperienceOrb orb = new EntityExperienceOrb(
            ((CraftWorld) player.getWorld()).getHandle(),
            player.getLocation().getX(),
            player.getLocation().getY(),
            player.getLocation().getZ(),
            amount
        );
        final EntityHuman human = ((CraftPlayer) player).getHandle();
        ObfuscationUtil.experienceOrbPickup(orb, human); // XP Orb Entity handles mending. Don't blame me, I didn't code it.
        ObfuscationUtil.resetXPPickupTimer(human); // Reset XP Pickup Timer.
    }

    public static boolean breakBlock(final @NotNull Block block, final @NotNull Player player) {
        final EntityPlayer ep = ((CraftPlayer) player).getHandle();
        return ObfuscationUtil.breakBlockAsPlayer(ep, new BlockPosition(block.getX(), block.getY(), block.getZ()));
    }

    public static boolean placeBlock(
        final @NotNull Block blockPlaced,
        final @NotNull Player player,
        final @NotNull Material material,
        final @Nullable BlockData blockData
    ) {
        final Block blockAgainst = blockPlaced.getRelative(blockPlaced.getY() == 0 ? BlockFace.UP : BlockFace.DOWN);
        final ItemStack itemHeld = new ItemStack(material);
        final BlockPlaceEvent placeEvent = new ZenBlockPlaceEvent(
            blockPlaced,
            blockPlaced.getState(),
            blockAgainst,
            itemHeld,
            player,
            true,
            EquipmentSlot.HAND
        );

        Bukkit.getServer().getPluginManager().callEvent(placeEvent);

        if (placeEvent.isCancelled()) {
            return false;
        }

        blockPlaced.setType(material);
        if (blockData != null) {
            blockPlaced.setBlockData(blockData);
        }

        if (MaterialList.LEAVES.contains(material)) {
            final Leaves leaves = (Leaves) blockPlaced.getBlockData();
            leaves.setPersistent(true);
            blockPlaced.setBlockData(leaves);
        }

        return true;
    }

    public static boolean attackEntity(
        final @NotNull LivingEntity target,
        final @NotNull Player attacker,
        final double damage
    ) {
        final EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(attacker, target, DamageCause.ENTITY_ATTACK, damage);

        Bukkit.getServer().getPluginManager().callEvent(damageEvent);

        if (damage == 0) {
            return !damageEvent.isCancelled();
        }

        if (damageEvent.isCancelled()) {
            return false;
        }

        target.damage(damage, attacker);
        target.setLastDamageCause(damageEvent);
        return true;
    }

    public static boolean canAnimalEnterLoveMode(Animals animal) {
        if (animal.isAdult()) {
            EntityAnimal ea = ((CraftAnimals)animal).getHandle();
            int i = ObfuscationUtil.getAnimalsLoveModeTimer(ea);
            if (!ObfuscationUtil.isInAnimalsWorldBreedingDisabled(ea) && i == 0 && ObfuscationUtil.isAnimalNotInLove(ea)) {
                return true;
            }
        }
        return false;
    }

    public static void animalEnterLoveMode(Animals animal, Player feeder) {
        ObfuscationUtil.animalEnterLoveMode(((CraftAnimals)animal).getHandle(), ((CraftPlayer) feeder).getHandle());
    }

    public static boolean shearEntityNMS(
        final @NotNull Entity target,
        final @NotNull Player player,
        final EquipmentSlot slot
    ) {
        if (target instanceof CraftSheep) {
            final EntitySheep entitySheep = ((CraftSheep) target).getHandle();
            final EnumInteractionResult result = ObfuscationUtil.shearSheep(entitySheep, ((CraftPlayer) player).getHandle(), ObfuscationUtil.getNMSEnumHand(slot));
            return ObfuscationUtil.isInteractionResultAllowed(result);
        } else if (target instanceof CraftMushroomCow) {
            final EntityMushroomCow entityMushroomCow = ((CraftMushroomCow) target).getHandle();
            final EnumInteractionResult result = ObfuscationUtil.shearMooshroom(entityMushroomCow, ((CraftPlayer) player).getHandle(), ObfuscationUtil.getNMSEnumHand(slot));
            return ObfuscationUtil.isInteractionResultAllowed(result);
        }

        return false;
    }

    public static boolean igniteEntity(final @NotNull Entity target, final @NotNull Player player, final int duration) {
        final EntityCombustByEntityEvent event = new EntityCombustByEntityEvent(target, player, duration);

        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        target.setFireTicks(duration);
        return true;

    }

    public static boolean damagePlayer(final @NotNull Player player, final double damage, final @NotNull DamageCause cause) {
        final EntityDamageEvent event = new EntityDamageEvent(player, cause, damage);

        Bukkit.getServer().getPluginManager().callEvent(event);

        if (damage == 0) {
            return !event.isCancelled();
        }

        if (event.isCancelled()) {
            return false;
        }

        player.setLastDamageCause(event);
        player.damage(damage);
        return true;
    }

    public static boolean formBlock(final @NotNull Block block, final @NotNull Material material, final @NotNull Player player) {
        final BlockState state = block.getState();
        state.setType(material);

        final EntityBlockFormEvent event = new EntityBlockFormEvent(player, block, state);

        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        block.setType(material);

        return true;
    }

    public static boolean showShulker(final @NotNull Block blockToHighlight, final int entityId, final @NotNull Player player) {
        return showHighlightBlock(blockToHighlight, entityId, player);
    }

    public static boolean hideFakeEntity(final int entityId, final @NotNull Player player) {
        final PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entityId);
        final EntityPlayer ep = ((CraftPlayer) player).getHandle();
        ObfuscationUtil.sendPacketToPlayer(ep, packet);
        return true;
    }

    public static boolean isZombie(final @NotNull Entity entity) {
        return entity.getType() == EntityType.ZOMBIE
            || entity.getType() == EntityType.ZOMBIE_VILLAGER
            || entity.getType() == EntityType.HUSK;
    }

    public static boolean isBlockSafeToBreak(final @NotNull Block block) {
        final Material material = block.getType();
        return material.isSolid()
            && !block.isLiquid()
            && !MaterialList.INTERACTABLE_BLOCKS.contains(material)
            && !MaterialList.UNBREAKABLE_BLOCKS.contains(material)
            && !MaterialList.STORAGE_BLOCKS.contains(material);
    }

    public static boolean grow(@NotNull Block cropBlock, final @NotNull Player player) {
        final Material material = cropBlock.getType();

        BlockData data = cropBlock.getBlockData();

        switch (material) {
            case PUMPKIN_STEM:
            case MELON_STEM:
            case CARROTS:
            case WHEAT:
            case POTATOES:
            case COCOA:
            case NETHER_WART:
            case BEETROOTS:
            case SWEET_BERRY_BUSH:
                final BlockData cropState = cropBlock.getBlockData();
                if (cropState instanceof Ageable) {
                    final Ageable ageable = (Ageable) cropState;

                    if (ageable.getAge() >= ageable.getMaximumAge()) {
                        return false;
                    }

                    ageable.setAge(ageable.getAge() + 1);
                    data = ageable;
                }
                break;
            case BAMBOO_SAPLING: {
                if (!placeBlock(cropBlock, player, BAMBOO, null)) {
                    return false;
                }

                final Bamboo bamboo = (Bamboo) cropBlock.getBlockData();

                cropBlock = cropBlock.getRelative(BlockFace.UP);

                bamboo.setLeaves(Bamboo.Leaves.SMALL);

                data = bamboo;
                break;
            }
            case BAMBOO: {
                final Bamboo bamboo = (Bamboo) cropBlock.getBlockData();

                // Only grow if argument is the base block.
                if (cropBlock.getRelative(BlockFace.DOWN).getType() == material) {
                    return false;
                }

                int height = 1;

                Block testBlock = cropBlock;
                while ((testBlock = testBlock.getRelative(BlockFace.UP)).getType() == material) {
                    // Cancel if cactus/cane is fully grown.
                    if (++height >= 16) {
                        return false;
                    }
                }

                height++;

                boolean result = placeBlock(testBlock, player, material, null);

                if (!result) {
                    return false;
                }

                bamboo.setAge(0);

                if (height == 4) {
                    // Top piece.
                    bamboo.setLeaves(Bamboo.Leaves.LARGE);
                    bamboo.setAge(1);

                    result = placeBlock(cropBlock.getRelative(0, 3, 0), player, material, bamboo);

                    if (!result) {
                        return false;
                    }
                }

                if (height == 3 || height == 4) {
                    // Top piece (height = 3) or second from top (height = 4).
                    bamboo.setLeaves(Bamboo.Leaves.SMALL);
                    bamboo.setAge(height == 4 ? 1 : 0);

                    result = placeBlock(cropBlock.getRelative(0, 2, 0), player, material, bamboo);

                    if (!result) {
                        return false;
                    }

                    // Second from bottom piece.
                    bamboo.setAge(0);
                    bamboo.setLeaves(Bamboo.Leaves.NONE);

                    result = placeBlock(cropBlock, player, material, bamboo);

                    if (!result) {
                        return false;
                    }

                    bamboo.setLeaves(Bamboo.Leaves.SMALL);

                    result = placeBlock(cropBlock.getRelative(0, 1, 0), player, material, bamboo);

                    if (!result) {
                        return false;
                    }

                }

                if (height <= 4) {
                    return true;
                }

                for (int i = height - 1; i >= 0; i--) {
                    final Bamboo.Leaves leaves = i < height - 3 ? Bamboo.Leaves.NONE : i == height - 3
                        ? Bamboo.Leaves.SMALL
                        : Bamboo.Leaves.LARGE;

                    bamboo.setLeaves(leaves);
                    bamboo.setAge(height == 5 && i < 2 ? 0 : 1);

                    result = placeBlock(cropBlock.getRelative(0, i, 0), player, material, bamboo);

                    if (!result) {
                        return false;
                    }
                }

                return true;
            }
            case CACTUS:
            case SUGAR_CANE:
                // Only grow if argument is the base block.
                if (cropBlock.getRelative(BlockFace.DOWN).getType() == material) {
                    return false;
                }

                int height = 1;

                while ((cropBlock = cropBlock.getRelative(BlockFace.UP)).getType() == material) {
                    // Cancel if cactus/cane is fully grown.
                    if (++height >= 3) {
                        return false;
                    }
                }

                // Only grow if argument is the base block.
                if (!MaterialList.AIR.contains(cropBlock.getType())) {
                    return false;
                }

                break;
            default:
                return false;
        }

        return placeBlock(cropBlock, player, material, data);
    }

    public static boolean pickBerries(final @NotNull Block berryBlock, final @NotNull Player player) {
        final BlockData data = berryBlock.getBlockData();
        final Ageable ageable = (Ageable) data;

        // Age of ripe Berries.
        if (ageable.getAge() <= 1) {
            return false;
        }

        final PlayerHarvestBlockEvent event = new PlayerHarvestBlockEvent(
            player,
            berryBlock,
            List.of(new ItemStack(Material.SWEET_BERRIES, 2))
        );

        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        // Natural drop rate. Age 2 -> 1-2 berries, Age 3 -> 2-3 berries
        final int numDropped = (ageable.getAge() == 3 ? 2 : 1) + (ThreadLocalRandom.current().nextBoolean() ? 1 : 0);

        // Picked adult berry bush
        ageable.setAge(1);

        berryBlock.setBlockData(ageable);
        berryBlock.getWorld().dropItem(
            berryBlock.getLocation(),
            new ItemStack(Material.SWEET_BERRIES, numDropped)
        );

        return true;
    }

    public static Map<Enchantment, Integer> getPrematureEnchantments(ItemMeta meta) {
        try {
            Field f = meta.getClass().getDeclaredField((meta instanceof CrossbowMeta) ? "enchants" : "enchantments");
            f.setAccessible(true);
            Map enchantments = (Map) (f.get(meta));
            return enchantments;
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            System.out.println("Unable to handle premature ItemMeta " + meta);
        }
        return null;
    }

    private static boolean showHighlightBlock(final @NotNull Block block, int entityId, final @NotNull Player player) {
        return showHighlightBlock(block.getX(), block.getY(), block.getZ(), entityId, player);
    }

    private static boolean showHighlightBlock(
        final int x,
        final int y,
        final int z,
        final int entityId,
        final @NotNull Player player
    ) {
        try {
            final PacketPlayOutSpawnEntity spawnPacket = generateShulkerSpawnPacket(x, y, z, entityId);
            final PacketPlayOutEntityMetadata metadataPacket = ObfuscationUtil.generateShulkerGlowPacket(entityId);
            final EntityPlayer ep = ((CraftPlayer) player).getHandle();
            ObfuscationUtil.sendPacketToPlayer(ep, spawnPacket);
            ObfuscationUtil.sendPacketToPlayer(ep, metadataPacket);
            return true;
        } catch (InstantiationException ex) {
            return false;
        }
    }

    @NotNull
    private static PacketPlayOutSpawnEntity generateShulkerSpawnPacket(
        final int x, final int y, final int z, final int entityId) throws InstantiationException {
        final UUID uuid = UUID.randomUUID();
        ObfuscationUtil.FakeEntityLiving fel = new ObfuscationUtil.FakeEntityLiving(ObfuscationUtil.getShulkerEntityType(), entityId, uuid, x, y, z);
        PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity(fel);
        return packet;
    }

    public static void showQuakeBlock(Player player, final int entityId, final Block block) {
        final EntityPlayer ep = ((CraftPlayer) player).getHandle();
        PacketPlayOutSpawnEntity ppose = generateFallingBlockSpawnPacket(block.getX() + 0.5, block.getY(), block.getZ() + 0.5, entityId, block);
        PacketPlayOutEntityVelocity ppoev = new PacketPlayOutEntityVelocity(entityId, new Vec3D(0, 0.28, 0));
        ObfuscationUtil.sendPacketToPlayer(ep, ppose);
        ObfuscationUtil.sendPacketToPlayer(ep, ppoev);
    }

    @NotNull
    private static PacketPlayOutSpawnEntity generateFallingBlockSpawnPacket(
        final double x, final double y, final double z, final int entityId, final Block block) {
        final UUID uuid = UUID.randomUUID();
        IBlockData blockData = ((CraftBlockData) block.getBlockData()).getState();
        PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity(entityId, uuid, x, y, z, 0, 0, ObfuscationUtil.getFallingBlockEntityType(), ObfuscationUtil.getNumericalBlockType(blockData), new Vec3D(0, 0, 0), 0);
        return packet;
    }
}
