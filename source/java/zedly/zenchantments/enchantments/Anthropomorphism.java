package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.task.EffectTask;
import zedly.zenchantments.task.Frequency;

import java.util.*;

import static java.util.Objects.requireNonNull;
import static org.bukkit.Material.*;
import static org.bukkit.event.block.Action.*;

public final class Anthropomorphism extends Zenchantment {
    public static final String KEY = "anthropomorphism";

    public static final Map<FallingBlock, Pair<Double, Vector>> ATTACK_BLOCKS = new HashMap<>();
    public static final Map<FallingBlock, Entity>               IDLE_BLOCKS   = new HashMap<>();

    private static final String                             NAME        = "Anthropomorphism";
    private static final String                             DESCRIPTION = "Spawns blocks to protect you when right sneak clicking, and attacks entities when left clicking";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of(Pierce.class, Switch.class);
    private static final Hand                               HAND_USE    = Hand.BOTH;

    private static final MaterialList ANTHRO_SOURCES = new MaterialList(MaterialList.STONES, MaterialList.COBBLESTONES, MaterialList.DIRT);
    private static final List<Entity> VORTEX    = new ArrayList<>();
    private static boolean fallBool = false;

    private final NamespacedKey key;

    public Anthropomorphism(
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
    @NotNull
    public Hand getHandUse() {
        return HAND_USE;
    }

    @EffectTask(Frequency.MEDIUM_HIGH)
    public static void removeOldBlocks() {
        Iterator<FallingBlock> iterator = IDLE_BLOCKS.keySet().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().isDead()) {
                iterator.remove();
            }
        }

        iterator = ATTACK_BLOCKS.keySet().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().isDead()) {
                iterator.remove();
            }
        }
    }

    @EffectTask(Frequency.HIGH)
    public static void moveBlocks() {
        // Move aggressive Anthropomorphism Blocks towards a target & attack.
        final Iterator<FallingBlock> iterator = ATTACK_BLOCKS.keySet().iterator();
        while (iterator.hasNext()) {
            final FallingBlock blockEntity = iterator.next();
            if (VORTEX.contains(IDLE_BLOCKS.get(blockEntity))) {
                continue;
            }

            for (final Entity entity : blockEntity.getNearbyEntities(7, 7, 7)) {
                if (!(entity instanceof Monster)) {
                    continue;
                }

                final LivingEntity targetEntity = (LivingEntity) entity;

                final Vector playerDir = ATTACK_BLOCKS.get(blockEntity) == null
                    ? new Vector()
                    : ATTACK_BLOCKS.get(blockEntity).getValue();

                blockEntity.setVelocity(
                    entity.getLocation()
                        .add(playerDir.multiply(.75))
                        .subtract(blockEntity.getLocation())
                        .toVector()
                        .multiply(0.25)
                );

                if (!Objects.equals(targetEntity.getLocation().getWorld(), blockEntity.getLocation().getWorld())) {
                    continue;
                }

                if (targetEntity.getLocation().distance(blockEntity.getLocation()) < 1.2 && blockEntity.hasMetadata("ze.anthrothrower")) {
                    final Player attacker = (Player) blockEntity.getMetadata("ze.anthrothrower").get(0).value();

                    if (targetEntity.getNoDamageTicks() == 0 && ATTACK_BLOCKS.get(blockEntity) != null) {
                        final boolean result = CompatibilityAdapter.instance().attackEntity(
                            targetEntity,
                            requireNonNull(attacker),
                            2.0 * ATTACK_BLOCKS.get(blockEntity).getKey()
                        );
                        if (result) {
                            targetEntity.setNoDamageTicks(0);
                            iterator.remove();
                            blockEntity.remove();
                        }
                    }
                }
            }
        }

        // Move passive Anthropomorphism Blocks around
        fallBool = !fallBool;

        for (final FallingBlock block : IDLE_BLOCKS.keySet()) {
            if (!VORTEX.contains(IDLE_BLOCKS.get(block))) {
                continue;
            }

            final Location location = IDLE_BLOCKS.get(block).getLocation();
            final Vector vector;

            if (!Objects.equals(block.getLocation().getWorld(), IDLE_BLOCKS.get(block).getLocation().getWorld())) {
                continue;
            }

            if (fallBool && block.getLocation().distance(IDLE_BLOCKS.get(block).getLocation()) < 10) {
                vector = block.getLocation().subtract(location).toVector();
            } else {
                final double x = 6f * Math.sin(block.getTicksLived() / 10f);
                final double z = 6f * Math.cos(block.getTicksLived() / 10f);
                final Location tLoc = location.clone();
                tLoc.setX(tLoc.getX() + x);
                tLoc.setZ(tLoc.getZ() + z);
                vector = tLoc.subtract(block.getLocation()).toVector();
            }

            vector.multiply(.05);
            boolean close = false;

            for (int y = -3; y < 0; y++) {
                if (block.getLocation().getBlock().getRelative(0, y, 0).getType() != AIR) {
                    close = true;
                }
            }

            if (close) {
                vector.setY(Math.abs(Math.sin(block.getTicksLived() / 10f)));
            } else {
                vector.setY(0);
            }

            block.setVelocity(vector);
        }
    }

    @Override
    public boolean onBlockInteract(final @NotNull PlayerInteractEvent event, final int level, final EquipmentSlot slot) {
        final Player player = event.getPlayer();
        final ItemStack hand = player.getInventory().getItem(slot);

        if (event.getAction() == RIGHT_CLICK_AIR || event.getAction() == RIGHT_CLICK_BLOCK) {
            if (player.isSneaking()) {
                if (!VORTEX.contains(player)) {
                    VORTEX.add(player);
                }

                int counter = 0;
                for (final Entity idleBlockPlayer : IDLE_BLOCKS.values()) {
                    if (idleBlockPlayer.equals(player)) {
                        counter++;
                    }
                }

                for(Material mat : ANTHRO_SOURCES) {
                    if (counter < 64 && player.getInventory().contains(mat)) {
                        Utilities.removeMaterialsFromPlayer(player, mat, 1);
                        Utilities.damageItemStackRespectUnbreaking(player, 2, slot);

                        final Location location = player.getLocation();
                        final FallingBlock blockEntity = requireNonNull(location.getWorld()).spawnFallingBlock(
                            location,
                            ZenchantmentsPlugin.getInstance().getServer().createBlockData(mat)
                        );

                        blockEntity.setDropItem(false);
                        blockEntity.setGravity(false);
                        blockEntity.setMetadata("ze.anthrothrower", new FixedMetadataValue(ZenchantmentsPlugin.getInstance(), player));
                        IDLE_BLOCKS.put(blockEntity, player);
                        return true;
                    }
                }
            }

            return false;
        } else if ((event.getAction() == LEFT_CLICK_AIR || event.getAction() == LEFT_CLICK_BLOCK) || hand.getType() == AIR) {
            VORTEX.remove(player);

            final List<FallingBlock> toRemove = new ArrayList<>();

            for (final FallingBlock block : IDLE_BLOCKS.keySet()) {
                if (IDLE_BLOCKS.get(block).equals(player)) {
                    ATTACK_BLOCKS.put(block, new Pair<>(this.getPower(), player.getLocation().getDirection()));
                    toRemove.add(block);
                    block.setVelocity(
                        player.getTargetBlock(null, 7)
                            .getLocation()
                            .subtract(player.getLocation())
                            .toVector()
                            .multiply(.25)
                    );
                }
            }

            for (final FallingBlock block : toRemove) {
                IDLE_BLOCKS.remove(block);
                block.setGravity(true);
                block.setGlowing(true);
            }
        }

        return false;
    }

    private static class Pair<K, V> {
        private final K key;
        private final V value;

        public Pair(final K key, final V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }
}
