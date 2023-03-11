package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.bukkit.util.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zedly.zenchantments.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public class MasterKey extends Zenchantment {
    public static final String KEY = "master_key";

    private static final String NAME = "Master Key";
    private static final String DESCRIPTION = "Opens Iron Doors when clicked";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand HAND_USE = Hand.RIGHT;

    private final NamespacedKey key;

    public MasterKey(
        final @NotNull Set<Tool> enchantable,
        final int maxLevel,
        final int cooldown,
        final double probability,
        final float power
    ) {
        super(enchantable, maxLevel, cooldown, probability, power);
        this.key = new NamespacedKey(ZenchantmentsPlugin.getInstance(), KEY);
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
    public Collection<EquipmentSlot> getApplyToSlots() {
        return Slots.ARMOR;
    }

    @Override
    public boolean onBlockInteract(final @NotNull PlayerInteractEvent event, final int level, final EquipmentSlot slot) {
        if (event.getAction() == RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block.getType() == Material.IRON_DOOR || block.getType() == Material.IRON_TRAPDOOR) {

                MockBlock mockBlock = new MockBlock(block, block.getType() == Material.IRON_DOOR ? Material.OAK_DOOR : Material.OAK_TRAPDOOR);
                PlayerInteractEvent mockEvent = new PlayerInteractEvent(event.getPlayer(), event.getAction(), event.getItem(), mockBlock, event.getBlockFace());
                Bukkit.getPluginManager().callEvent(mockEvent);


                if (mockEvent.useInteractedBlock() != Event.Result.DENY && mockEvent.useItemInHand() != Event.Result.DENY) {
                    Openable door = (Openable) block.getBlockData();
                    block.getWorld().playSound(block.getLocation(),
                        door instanceof TrapDoor ? (door.isOpen() ? Sound.BLOCK_IRON_TRAPDOOR_CLOSE : Sound.BLOCK_IRON_TRAPDOOR_OPEN) :
                            (door.isOpen() ? Sound.BLOCK_IRON_DOOR_CLOSE : Sound.BLOCK_IRON_DOOR_OPEN), 1, ThreadLocalRandom.current().nextFloat() * 1.5f + 0.5f);
                    door.setOpen(!door.isOpen());
                    block.setBlockData(door);
                }
            }
        }
        return false;
    }
}
