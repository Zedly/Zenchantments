package zedly.zenchantments;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.network.syncher.DataWatcherSerializer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.EntityMushroomCow;
import net.minecraft.world.entity.animal.EntitySheep;
import net.minecraft.world.entity.monster.EntityCreeper;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bamboo;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftCreeper;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftMushroomCow;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftSheep;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Material.AIR;
import static org.bukkit.Material.BAMBOO;

public class CompatibilityAdapter {
    private final ZenchantmentsPlugin plugin;

    public CompatibilityAdapter(final @NotNull ZenchantmentsPlugin plugin) {
        this.plugin = plugin;
    }

    public static void damageTool(final @NotNull Player player, final int damage, final boolean handUsed) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        final PlayerInventory inventory = player.getInventory();
        final ItemStack hand = handUsed ? inventory.getItemInMainHand() : inventory.getItemInOffHand();
        for (int i = 0; i < damage; i++) {
            if (ThreadLocalRandom.current().nextInt(100) <= (100 / (hand.getEnchantmentLevel(Enchantment.DURABILITY) + 1))) {
                setDamage(hand, getDamage(hand) + 1);
            }
        }

        if (handUsed) {
            player.getInventory().setItemInMainHand(
                getDamage(hand) > hand.getType().getMaxDurability() ? new ItemStack(AIR) : hand
            );
        } else {
            player.getInventory().setItemInOffHand(
                getDamage(hand) > hand.getType().getMaxDurability() ? new ItemStack(AIR) : hand
            );
        }
    }

    public static void displayParticle(
        final @NotNull Location location,
        final @NotNull Particle particle,
        final int amount,
        final double speed,
        final double x,
        final double y,
        final double z
    ) {
        Objects.requireNonNull(location.getWorld()).spawnParticle(
            particle,
            location.getX(),
            location.getY(),
            location.getZ(),
            amount,
            (float) x,
            (float) y,
            (float) z,
            (float) speed
        );
    }

    public static void addUnbreaking(final @NotNull Player player, final @NotNull ItemStack itemStack, final int damage) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        for (int i = 0; i < damage; i++) {
            if (ThreadLocalRandom.current().nextInt(100) <= (100 / (itemStack.getEnchantmentLevel(Enchantment.DURABILITY) + 1))) {
                setDamage(itemStack, getDamage(itemStack) + 1);
            }
        }
    }

    public static void setDamage(final @NotNull ItemStack itemStack, final int damage) {
        if (!(itemStack.getItemMeta() instanceof Damageable)) {
            return;
        }

        final org.bukkit.inventory.meta.Damageable damageable = (org.bukkit.inventory.meta.Damageable) itemStack.getItemMeta();
        damageable.setDamage(damage);
        itemStack.setItemMeta((ItemMeta) damageable);
    }

    public static int getDamage(final @NotNull ItemStack itemStack) {
        if (!(itemStack.getItemMeta() instanceof org.bukkit.inventory.meta.Damageable)) {
            return 0;
        }

        final org.bukkit.inventory.meta.Damageable damageable = (org.bukkit.inventory.meta.Damageable) itemStack.getItemMeta();
        return damageable.getDamage();
    }

    public void collectExp(final @NotNull Player player, final int amount) {
        final EntityExperienceOrb orb = new EntityExperienceOrb(
            ((CraftWorld) player.getWorld()).getHandle(),
            player.getLocation().getX(),
            player.getLocation().getY(),
            player.getLocation().getZ(),
            amount
        );
        final EntityHuman human = ((CraftPlayer) player).getHandle();
        orb.pickup(human); // XP Orb Entity handles mending. Don't blame me, I didn't code it.
        human.ca = 0; // Reset XP Pickup Timer.
    }

    public boolean breakBlock(final @NotNull Block block, final @NotNull Player player) {
        final EntityPlayer ep = ((CraftPlayer) player).getHandle();
        return ep.d.breakBlock(new BlockPosition(block.getX(), block.getY(), block.getZ()));
    }

    public boolean placeBlock(
        final @NotNull Block blockPlaced,
        final @NotNull Player player,
        final @NotNull Material material,
        final @Nullable BlockData blockData
    ) {
        final Block blockAgainst = blockPlaced.getRelative(blockPlaced.getY() == 0 ? BlockFace.UP : BlockFace.DOWN);
        final ItemStack itemHeld = new ItemStack(material);
        final BlockPlaceEvent placeEvent = new BlockPlaceEvent(
            blockPlaced,
            blockPlaced.getState(),
            blockAgainst,
            itemHeld,
            player,
            true,
            EquipmentSlot.HAND
        );

        this.plugin.getServer().getPluginManager().callEvent(placeEvent);

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

    public boolean placeBlock(
        final @NotNull Block blockPlaced,
        final @NotNull Player player,
        final @NotNull ItemStack itemStack
    ) {
        return placeBlock(blockPlaced, player, itemStack.getType(), (BlockData) itemStack.getData());
    }

    public boolean attackEntity(
        final @NotNull LivingEntity target,
        final @NotNull Player attacker,
        final double damage
    ) {
        final EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(attacker, target, DamageCause.ENTITY_ATTACK, damage);

        this.plugin.getServer().getPluginManager().callEvent(damageEvent);

        if (damage == 0) {
            return !damageEvent.isCancelled();
        }

        if (damageEvent.isCancelled()) {
            return false;
        }

        target.damage(damage, attacker);
        target.setLastDamageCause(damageEvent);

        damageTool(attacker, 1, true);

        return true;
    }

    public boolean shearEntityNMS(
        final @NotNull Entity target,
        final @NotNull Player player,
        final boolean mainHand
    ) {
        if (target instanceof CraftSheep) {
            final EntitySheep entitySheep = ((CraftSheep) target).getHandle();
            final EnumInteractionResult result = entitySheep.a(((CraftPlayer) player).getHandle(), mainHand ? EnumHand.a : EnumHand.b);
            return result == EnumInteractionResult.a;
        } else if (target instanceof CraftMushroomCow) {
            final EntityMushroomCow entityMushroomCow = ((CraftMushroomCow) target).getHandle();
            final EnumInteractionResult result = entityMushroomCow.a(((CraftPlayer) player).getHandle(), mainHand ? EnumHand.a : EnumHand.b);
            return result == EnumInteractionResult.a;
        }

        return false;
    }

    public boolean haulOrBreakBlock(
        final @NotNull Block from,
        final @NotNull Block to,
        final @NotNull BlockFace face,
        final @NotNull Player player
    ) {
        final BlockState state = from.getState();
        if (state.getClass().getName().endsWith("CraftBlockState")) {
            return false;
        }

        final BlockBreakEvent breakEvent = new BlockBreakEvent(from, player);

        this.plugin.getServer().getPluginManager().callEvent(breakEvent);

        if (breakEvent.isCancelled()) {
            return false;
        }

        final ItemStack stack = new ItemStack(state.getType(), 1);

        from.setType(AIR);

        final BlockPlaceEvent placeEvent = new BlockPlaceEvent(
            to,
            to.getRelative(face.getOppositeFace()).getState(),
            to.getRelative(face.getOppositeFace()), stack, player, true,
            EquipmentSlot.HAND
        );

        this.plugin.getServer().getPluginManager().callEvent(placeEvent);

        if (placeEvent.isCancelled()) {
            from.getWorld().dropItem(from.getLocation(), stack);
            return true;
        }

        to.setType(state.getType());
        return true;
    }

    public boolean igniteEntity(final @NotNull Entity target, final @NotNull Player player, final int duration) {
        final EntityCombustByEntityEvent event = new EntityCombustByEntityEvent(target, player, duration);

        this.plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        target.setFireTicks(duration);
        return true;

    }

    public boolean damagePlayer(final @NotNull Player player, final double damage, final @NotNull DamageCause cause) {
        final EntityDamageEvent event = new EntityDamageEvent(player, cause, damage);

        this.plugin.getServer().getPluginManager().callEvent(event);

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

    public boolean explodeCreeper(final @NotNull Creeper creeper, final boolean damage) {
        final EntityCreeper nmsCreeper = ((CraftCreeper) creeper).getHandle();
        nmsCreeper.explode();
        return true;
    }

    public boolean formBlock(final @NotNull Block block, final @NotNull Material material, final @NotNull Player player) {
        final BlockState state = block.getState();
        state.setType(material);

        final EntityBlockFormEvent event = new EntityBlockFormEvent(player, block, state);

        this.plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        block.setType(material);

        return true;
    }

    public boolean showShulker(final @NotNull Block blockToHighlight, final int entityId, final @NotNull Player player) {
        return showHighlightBlock(blockToHighlight, entityId, player);
    }

    public boolean hideShulker(final int entityId, final @NotNull Player player) {
        final PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entityId);
        final EntityPlayer ep = ((CraftPlayer) player).getHandle();
        ep.b.a.sendPacket(packet);
        return true;
    }

    public boolean isZombie(final @NotNull Entity entity) {
        return entity.getType() == EntityType.ZOMBIE
            || entity.getType() == EntityType.ZOMBIE_VILLAGER
            || entity.getType() == EntityType.HUSK;
    }

    public boolean isBlockSafeToBreak(final @NotNull Block block) {
        final Material material = block.getType();
        return material.isSolid()
            && !block.isLiquid()
            && !MaterialList.INTERACTABLE_BLOCKS.contains(material)
            && !MaterialList.UNBREAKABLE_BLOCKS.contains(material)
            && !MaterialList.STORAGE_BLOCKS.contains(material);
    }

    public boolean grow(@NotNull Block cropBlock, final @NotNull Player player) {
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
                if (!this.placeBlock(cropBlock, player, BAMBOO, null)) {
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

                boolean result = this.placeBlock(testBlock, player, material, null);

                if (!result) {
                    return false;
                }

                bamboo.setAge(0);

                if (height == 4) {
                    // Top piece.
                    bamboo.setLeaves(Bamboo.Leaves.LARGE);
                    bamboo.setAge(1);

                    result = this.placeBlock(cropBlock.getRelative(0, 3, 0), player, material, bamboo);

                    if (!result) {
                        return false;
                    }
                }

                if (height == 3 || height == 4) {
                    // Top piece (height = 3) or second from top (height = 4).
                    bamboo.setLeaves(Bamboo.Leaves.SMALL);
                    bamboo.setAge(height == 4 ? 1 : 0);

                    result = this.placeBlock(cropBlock.getRelative(0, 2, 0), player, material, bamboo);

                    if (!result) {
                        return false;
                    }

                    // Second from bottom piece.
                    bamboo.setAge(0);
                    bamboo.setLeaves(Bamboo.Leaves.NONE);

                    result = this.placeBlock(cropBlock, player, material, bamboo);

                    if (!result) {
                        return false;
                    }

                    bamboo.setLeaves(Bamboo.Leaves.SMALL);

                    result = this.placeBlock(cropBlock.getRelative(0, 1, 0), player, material, bamboo);

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

                    result = this.placeBlock(cropBlock.getRelative(0, i, 0), player, material, bamboo);

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

        return this.placeBlock(cropBlock, player, material, data);
    }

    public boolean pickBerries(final @NotNull Block berryBlock, final @NotNull Player player) {
        final BlockData data = berryBlock.getBlockData();
        final Ageable ageable = (Ageable) data;

        // Age of ripe Berries.
        if (ageable.getAge() <= 1) {
            return false;
        }

        final PlayerInteractEvent event = new PlayerInteractEvent(
            player,
            Action.RIGHT_CLICK_BLOCK,
            player.getInventory().getItemInMainHand(),
            berryBlock,
            player.getFacing()
        );

        this.plugin.getServer().getPluginManager().callEvent(event);

        // TODO: Fix deprecation warning.
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

    private boolean showHighlightBlock(final @NotNull Block block, int entityId, final @NotNull Player player) {
        return showHighlightBlock(block.getX(), block.getY(), block.getZ(), entityId, player);
    }

    private boolean showHighlightBlock(
        final int x,
        final int y,
        final int z,
        final int entityId,
        final @NotNull Player player
    ) {
        try {
            final PacketPlayOutSpawnEntityLiving spawnPacket = generateShulkerSpawnPacket(x, y, z, entityId);
            final PacketPlayOutEntityMetadata metadataPacket = generateShulkerGlowPacket(entityId);
            final EntityPlayer ep = ((CraftPlayer) player).getHandle();
            ep.b.a.sendPacket(spawnPacket);
            ep.b.a.sendPacket(metadataPacket);
            return true;
        } catch (InstantiationException ex) {
            return false;
        }
    }

    @NotNull
    private static PacketPlayOutEntityMetadata generateShulkerGlowPacket(final int entityId) throws InstantiationException {
        final Class<? extends PacketPlayOutEntityMetadata> clazz = PacketPlayOutEntityMetadata.class;
        // Build data structure for Entity Metadata. Requires an index, a type and a value.
        // As of 1.15.2, an invisible + glowing LivingEntity is set by Index 0 Type Byte Value 0x60
        final DataWatcherSerializer<Byte> dws = DataWatcherRegistry.a; // Type (Byte)
        final DataWatcherObject<Byte> dwo = new DataWatcherObject<>(0, dws); // Index (0)
        final DataWatcher.Item<Byte> dwi = new DataWatcher.Item<>(dwo, (byte) 0x60); // Value (0x60)
        final List<DataWatcher.Item<?>> list = new ArrayList<>();
        FakeDataWatcher fdw = new FakeDataWatcher(list);
        list.add(dwi); // Pack it in a list
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(entityId, fdw, true);
        return packet;
    }

    @NotNull
    private static PacketPlayOutSpawnEntityLiving generateShulkerSpawnPacket(
        final int x, final int y, final int z, final int entityId) throws InstantiationException {
        final int mobTypeId = IRegistry.Y.getId(EntityTypes.ay);
        final UUID uuid = UUID.randomUUID();
        FakeEntityLiving fel = new FakeEntityLiving(EntityTypes.ay, entityId, uuid, x, y, z);
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(fel);
        return packet;
    }

    public static class FakeEntityLiving extends EntityLiving {
        private final int entityId;
        private final UUID uuid;
        private final EntityTypes<?> entityType;

        protected FakeEntityLiving(EntityTypes<? extends EntityLiving> entityType, int entityId, UUID uuid, double x, double y, double z) {
            super(entityType, null);
            this.entityId = entityId;
            this.uuid = uuid;
            this.entityType = entityType;
            super.setPositionRaw(x, y, z);
        }

        @Override
        public int getId() {
            return entityId;
        }

        @Override
        public UUID getUniqueID() {
            return uuid;
        }

        public EntityTypes<?> getEntityType() {
            return entityType;
        }

        // Useless abstract methods we need to implement to appease the compiler
        @Override
        public Iterable<net.minecraft.world.item.ItemStack> getArmorItems() {
            return null;
        }

        @Override
        public net.minecraft.world.item.ItemStack getEquipment(EnumItemSlot enumItemSlot) {
            return null;
        }

        @Override
        public void setSlot(EnumItemSlot enumItemSlot, net.minecraft.world.item.ItemStack itemStack) {
        }

        @Override
        public EnumMainHand getMainHand() {
            return null;
        }
    }

    public static class FakeDataWatcher extends DataWatcher {
        private final List<DataWatcher.Item<?>> list;

        public FakeDataWatcher(List<DataWatcher.Item<?>> list) {
            super(null);
            this.list = list;
        }

        public List<Item<?>> getAll() {
            return list;
        }
    }
}
