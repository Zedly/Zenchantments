package zedly.zenchantments;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bamboo;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.enchantments.Enchantment;
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
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
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
        player.giveExp(amount);
    }

    public boolean breakBlock(final @NotNull Block block, final @NotNull Player player) {
        BlockBreakEvent evt = new BlockBreakEvent(block, player);
        Bukkit.getPluginManager().callEvent(evt);
        if (!evt.isCancelled()) {
            block.breakNaturally(player.getInventory().getItemInMainHand());
            damageTool(player, 1, true);
            return true;
        }
        return false;
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
        if ((target instanceof Sheep && !((Sheep) target).isSheared()) || target instanceof MushroomCow) {
            PlayerShearEntityEvent evt = new PlayerShearEntityEvent(player, target);
            Bukkit.getPluginManager().callEvent(evt);
            if (!evt.isCancelled()) {
                if (target instanceof Sheep) {
                    Sheep sheep = (Sheep) target;
                    sheep.getLocation().getWorld().dropItem(
                        sheep.getLocation(),
                        new ItemStack(MaterialList.WOOL.get(sheep.getColor().ordinal()), ThreadLocalRandom.current().nextInt(3) + 1)
                    );
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
        final Location location = creeper.getLocation();
        final float power;

        if (creeper.isPowered()) {
            power = 6f;
        } else {
            power = 3.1f;
        }

        if (damage) {
            creeper.getWorld().createExplosion(location, power);
        } else {
            creeper.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), power, false, false);
        }

        creeper.remove();

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
        // This cannot be done without NMS
        return false;
    }

    public boolean hideShulker(final int entityId, final @NotNull Player player) {
        // This cannot be done without NMS
        return false;
    }

    public Entity spawnGuardian(final @NotNull Location location, final boolean elderGuardian) {
        return Objects.requireNonNull(location.getWorld()).spawnEntity(
            location,
            elderGuardian ? EntityType.ELDER_GUARDIAN : EntityType.GUARDIAN
        );
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
}
