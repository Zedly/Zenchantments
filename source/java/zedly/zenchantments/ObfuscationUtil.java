package zedly.zenchantments;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.network.syncher.DataWatcherSerializer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.EntityAnimal;
import net.minecraft.world.entity.animal.EntityMushroomCow;
import net.minecraft.world.entity.animal.EntitySheep;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ObfuscationUtil {

    public static void experienceOrbPickup(EntityExperienceOrb orb, EntityHuman human) {
        orb.b_(human);
    }

    public static void resetXPPickupTimer(EntityHuman human) {
        human.bW = 0;
    }

    public static boolean breakBlockAsPlayer(EntityPlayer ep, BlockPosition bp) {
        return ep.e.a(bp);
    }

    public static int getAnimalsLoveModeTimer(EntityAnimal ea) {
        return ea.h();
    }

    public static boolean isInAnimalsWorldBreedingDisabled(EntityAnimal ea) {
        return ea.dI().B;
    }

    public static boolean isAnimalNotInLove(EntityAnimal ea) {
        return ea.fZ();
    }

    public static void animalEnterLoveMode(EntityAnimal animal, EntityHuman feeder) {
        animal.g(feeder);
    }

    public static EnumHand getNMSEnumHand(EquipmentSlot slot) {
        return slot==EquipmentSlot.HAND ? EnumHand.a : EnumHand.b;
    }

    public static EnumInteractionResult shearSheep(EntitySheep sheep, EntityHuman human, EnumHand hand) {
        return sheep.b(human, hand);
    }

    public static EnumInteractionResult shearMooshroom(EntityMushroomCow mooshroom, EntityHuman human, EnumHand hand) {
        return mooshroom.b(human, hand);
    }

    public static boolean isInteractionResultAllowed(EnumInteractionResult result) {
        return result == EnumInteractionResult.a;
    }

    public static void sendPacketToPlayer(EntityPlayer ep, Packet<?> packet) {
        ep.c.a(packet);
    }

    public static DataWatcherSerializer<Byte> getDataWatcherByte() {
        return DataWatcherRegistry.a;
    }

    public static EntityTypes getShulkerEntityType() {
        return EntityTypes.aG;
    }

    public static EntityTypes getFallingBlockEntityType() {
        return EntityTypes.L;
    }

    public static int getNumericalBlockType(IBlockData blockData) {
        return net.minecraft.world.level.block.Block.i(blockData);
    }

    @NotNull
    public static PacketPlayOutEntityMetadata generateShulkerGlowPacket(final int entityId) {
        final DataWatcherSerializer<Byte> byteSerializer = ObfuscationUtil.getDataWatcherByte(); // Type (Byte)
        final List<DataWatcher.b<?>> list = new ArrayList<>();

        // Add a record of Entity Metadata. Requires an id, a Serializer and a value.
        // As of 1.19, setting a LivingEntity to be glowing and invisible is done via a bitmask in a single record.
        // This record is at id 0, is of type Byte (uses the Byte Serializer) and has a value of 0x60
        list.add(new DataWatcher.b<>(0, byteSerializer, (byte) 0x60));
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(entityId, list);
        return packet;
    }

    /*

    1.17 removed empty constructors for Packets and the only available constructors for PacketPlayOutSpawnEneityLiving requires an Entity parameter.
    Luckily it's a reasonable amount of effort to make some mock data to construct the necessary packets.

     */
    public static class FakeEntityLiving extends EntityLiving {
        private final int entityId;
        private final UUID uuid;
        private final EntityTypes<?> entityType;

        protected FakeEntityLiving(EntityTypes<? extends EntityLiving> entityType, int entityId, UUID uuid, double x, double y, double z) {
            super(entityType, null);
            this.entityId = entityId;
            this.uuid = uuid;
            this.entityType = entityType;
            super.p(x, y, z);
        }

        @Override
        public int af() {
            return entityId;
        }

        @Override
        public UUID ct() {
            return uuid;
        }

        public EntityTypes<?> getEntityType() {
            return entityType;
        }

        // Useless abstract methods we need to implement to appease the compiler
        @Override
        public Iterable<net.minecraft.world.item.ItemStack> bJ() {
            return null;
        }

        @Override
        public net.minecraft.world.item.ItemStack c(EnumItemSlot enumItemSlot) {
            return null;
        }

        @Override
        public void a(EnumItemSlot enumItemSlot, net.minecraft.world.item.ItemStack itemStack) {
        }

        @Override
        public EnumMainHand fh() {
            return null;
        }
    }
}
