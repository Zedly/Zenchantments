package zedly.zenchantments.enchantments;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.task.EffectTask;
import zedly.zenchantments.task.Frequency;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Material.*;
import static org.bukkit.event.block.Action.*;

public class Anthropomorphism extends Zenchantment {
    public static final  Map<FallingBlock, Pair<Double, Vector>> ATTACK_BLOCKS = new HashMap<>();
    public static final  Map<FallingBlock, Entity>               IDLE_BLOCKS   = new HashMap<>();
    private static final List<Entity>                            VORTEX        = new ArrayList<>();
    private static final Material[]                              MATERIALS     = new Material[] {STONE, GRAVEL, DIRT, GRASS_BLOCK};

    private static final String     KEY         = "anthropomorphism";
    private static final String     NAME        = "Anthropomorphism";
    private static final String     DESCRIPTION = "Spawns blocks to protect you when right sneak clicking, and attacks entities when left clicking";
    private static final Class<?>[] CONFLICTING = new Class<?>[] {Pierce.class, Switch.class};
    private static final Hand       HAND_USE    = Hand.BOTH;

    private static boolean fallBool = false;

    private final NamespacedKey key;

    public Anthropomorphism(
        @NotNull ZenchantmentsPlugin plugin,
        @NotNull Tool[] enchantable,
        int maxLevel,
        int cooldown,
        double probability,
        float power
    ) {
        super(plugin, enchantable, maxLevel, cooldown, probability, power);
        this.key = new NamespacedKey(plugin, Anthropomorphism.KEY);
    }

    @Override
    @NotNull
    public NamespacedKey getKey() {
        return this.key;
    }

    @Override
    @NotNull
    public String getName() {
        return Anthropomorphism.NAME;
    }

    @Override
    @NotNull
    public String getDescription() {
        return Anthropomorphism.DESCRIPTION;
    }

    @Override
    public Class<?>[] getConflicting() {
        return Anthropomorphism.CONFLICTING;
    }

    @Override
    @NotNull
    public Hand getHandUse() {
        return Anthropomorphism.HAND_USE;
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
        Iterator<FallingBlock> iterator = ATTACK_BLOCKS.keySet().iterator();
        while (iterator.hasNext()) {
            FallingBlock blockEntity = iterator.next();
            if (VORTEX.contains(IDLE_BLOCKS.get(blockEntity))) {
                continue;
            }

            for (Entity entity : blockEntity.getNearbyEntities(7, 7, 7)) {
                if (!(entity instanceof Monster)) {
                    continue;
                }

                LivingEntity targetEntity = (LivingEntity) entity;

                Vector playerDir = ATTACK_BLOCKS.get(blockEntity) == null
                    ? new Vector()
                    : ATTACK_BLOCKS.get(blockEntity).getValue();

                blockEntity.setVelocity(
                    entity.getLocation()
                        .add(playerDir.multiply(.75))
                        .subtract(blockEntity.getLocation())
                        .toVector()
                        .multiply(0.25)
                );

                if (!targetEntity.getLocation().getWorld().equals(blockEntity.getLocation().getWorld())) {
                    continue;
                }

                if (targetEntity.getLocation().distance(blockEntity.getLocation()) < 1.2
                    && blockEntity.hasMetadata("ze.anthrothrower")
                ) {
                    Player attacker = (Player) blockEntity.getMetadata("ze.anthrothrower").get(0).value();

                    if (targetEntity.getNoDamageTicks() == 0 && ATTACK_BLOCKS.get(blockEntity) != null
                        && Storage.COMPATIBILITY_ADAPTER.attackEntity(targetEntity, attacker,
                        2.0 * ATTACK_BLOCKS.get(blockEntity).getKey())) {
                        targetEntity.setNoDamageTicks(0);
                        iterator.remove();
                        blockEntity.remove();
                    }
                }
            }
        }

        // Move passive Anthropomorphism Blocks around
        fallBool = !fallBool;

        for (FallingBlock block : IDLE_BLOCKS.keySet()) {
            if (!VORTEX.contains(IDLE_BLOCKS.get(block))) {
                continue;
            }

            Location location = IDLE_BLOCKS.get(block).getLocation();
            Vector vector;

            if (!block.getLocation().getWorld().equals(IDLE_BLOCKS.get(block).getLocation().getWorld())) {
                continue;
            }

            if (fallBool && block.getLocation().distance(IDLE_BLOCKS.get(block).getLocation()) < 10) {
                vector = block.getLocation().subtract(location).toVector();
            } else {
                double x = 6f * Math.sin(block.getTicksLived() / 10f);
                double z = 6f * Math.cos(block.getTicksLived() / 10f);
                Location tLoc = location.clone();
                tLoc.setX(tLoc.getX() + x);
                tLoc.setZ(tLoc.getZ() + z);
                vector = tLoc.subtract(block.getLocation()).toVector();
            }

            vector.multiply(.05);
            boolean close = false;

            for (int x = -3; x < 0; x++) {
                if (block.getLocation().getBlock().getRelative(0, x, 0).getType() != AIR) {
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
    public boolean onBlockInteract(@NotNull PlayerInteractEvent event, int level, boolean usedHand) {
        Player player = event.getPlayer();
        ItemStack hand = Utilities.usedStack(player, usedHand);

        if (event.getAction() == RIGHT_CLICK_AIR || event.getAction() == RIGHT_CLICK_BLOCK) {
            if (player.isSneaking()) {
                if (!VORTEX.contains(player)) {
                    VORTEX.add(player);
                }

                int counter = 0;
                for (Entity p : IDLE_BLOCKS.values()) {
                    if (p.equals(player)) {
                        counter++;
                    }
                }

                if (counter < 64 && player.getInventory().contains(COBBLESTONE)) {
                    Utilities.removeItem(player, COBBLESTONE, 1);
                    Utilities.damageTool(player, 2, usedHand);

                    Location location = player.getLocation();
                    FallingBlock blockEntity = location.getWorld().spawnFallingBlock(
                        location,
                        this.getPlugin().getServer().createBlockData(MATERIALS[ThreadLocalRandom.current().nextInt(4)])
                    );

                    blockEntity.setDropItem(false);
                    blockEntity.setGravity(false);
                    blockEntity.setMetadata("ze.anthrothrower", new FixedMetadataValue(this.getPlugin(), player));
                    IDLE_BLOCKS.put(blockEntity, player);
                    return true;
                }
            }

            return false;
        } else if ((event.getAction() == LEFT_CLICK_AIR || event.getAction() == LEFT_CLICK_BLOCK) || hand.getType() == AIR) {
            VORTEX.remove(player);

            List<FallingBlock> toRemove = new ArrayList<>();

            for (FallingBlock block : IDLE_BLOCKS.keySet()) {
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

            for (FallingBlock block : toRemove) {
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

        public Pair(K key, V value) {
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