package zedly.zenchantments.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
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

import static org.bukkit.Material.*;
import static org.bukkit.event.block.Action.*;

public class Anthropomorphism extends Zenchantment {
    public static final  Map<FallingBlock, Pair<Double, Vector>> attackBlocks = new HashMap<>();
    public static final  Map<FallingBlock, Entity>               idleBlocks   = new HashMap<>();
    private static final List<Entity>                            anthVortex   = new ArrayList<>();
    private static final Material[]                              MAT          = new Material[] {STONE, GRAVEL, DIRT, GRASS_BLOCK};
    private static       boolean                                 fallBool     = false;

    private static final String     NAME        = "Anthropomorphism";
    private static final String     DESCRIPTION = "Spawns blocks to protect you when right sneak clicking, and attacks entities when left clicking";
    private static final Class<?>[] CONFLICTING = new Class<?>[] {Pierce.class, Switch.class};
    private static final Hand       HAND_USE    = Hand.BOTH;

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
        this.key = new NamespacedKey(plugin, "anthropomorphism");
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
    // Removes Anthropomorphism blocks when they are dead
    public static void removeCheck() {
        Iterator it = idleBlocks.keySet().iterator();
        while (it.hasNext()) {
            FallingBlock b = (FallingBlock) it.next();
            if (b.isDead()) {
                it.remove();
            }
        }
        it = attackBlocks.keySet().iterator();
        while (it.hasNext()) {
            FallingBlock b = (FallingBlock) it.next();
            if (b.isDead()) {
                it.remove();
            }
        }
    }

    // Moves Anthropomorphism blocks around depending on their state
    @EffectTask(Frequency.HIGH)
    public static void entityPhysics() {
        // Move agressive Anthropomorphism Blocks towards a target & attack
        Iterator<FallingBlock> anthroIterator = attackBlocks.keySet().iterator();
        while (anthroIterator.hasNext()) {
            FallingBlock blockEntity = anthroIterator.next();
            if (!anthVortex.contains(idleBlocks.get(blockEntity))) {
                for (Entity e : blockEntity.getNearbyEntities(7, 7, 7)) {
                    if (e instanceof Monster) {
                        LivingEntity targetEntity = (LivingEntity) e;

                        Vector playerDir = attackBlocks.get(blockEntity) == null
                            ? new Vector()
                            : attackBlocks.get(blockEntity).getValue();

                        blockEntity.setVelocity(e.getLocation().add(playerDir.multiply(.75)).subtract(blockEntity.getLocation()).toVector().multiply(0.25));

                        if (targetEntity.getLocation().getWorld().equals(blockEntity.getLocation().getWorld())) {
                            if (targetEntity.getLocation().distance(blockEntity.getLocation()) < 1.2
                                && blockEntity.hasMetadata("ze.anthrothrower")) {
                                Player attacker = (Player) blockEntity.getMetadata("ze.anthrothrower").get(0).value();

                                if (targetEntity.getNoDamageTicks() == 0 && attackBlocks.get(blockEntity) != null
                                    && Storage.COMPATIBILITY_ADAPTER.attackEntity(targetEntity, attacker,
                                    2.0 * attackBlocks.get(blockEntity).getKey())) {
                                    targetEntity.setNoDamageTicks(0);
                                    anthroIterator.remove();
                                    blockEntity.remove();
                                }
                            }
                        }
                    }
                }
            }
        }
        // Move passive Anthropomorphism Blocks around
        fallBool = !fallBool;
        for (FallingBlock b : idleBlocks.keySet()) {
            if (anthVortex.contains(idleBlocks.get(b))) {
                Location loc = idleBlocks.get(b).getLocation();
                Vector v;
                if (b.getLocation().getWorld().equals(idleBlocks.get(b).getLocation().getWorld())) {
                    if (fallBool && b.getLocation().distance(idleBlocks.get(b).getLocation()) < 10) {
                        v = b.getLocation().subtract(loc).toVector();
                    } else {
                        double x = 6f * Math.sin(b.getTicksLived() / 10f);
                        double z = 6f * Math.cos(b.getTicksLived() / 10f);
                        Location tLoc = loc.clone();
                        tLoc.setX(tLoc.getX() + x);
                        tLoc.setZ(tLoc.getZ() + z);
                        v = tLoc.subtract(b.getLocation()).toVector();
                    }
                    v.multiply(.05);
                    boolean close = false;
                    for (int x = -3; x < 0; x++) {
                        if (b.getLocation().getBlock().getRelative(0, x, 0).getType() != AIR) {
                            close = true;
                        }
                    }
                    if (close) {
                        v.setY(Math.abs(Math.sin(b.getTicksLived() / 10f)));
                    } else {
                        v.setY(0);
                    }
                    b.setVelocity(v);
                }
            }
        }
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent event, int level, boolean usedHand) {
        Player player = event.getPlayer();
        ItemStack hand = Utilities.usedStack(player, usedHand);

        if (event.getAction() == RIGHT_CLICK_AIR || event.getAction() == RIGHT_CLICK_BLOCK) {
            if (player.isSneaking()) {
                if (!anthVortex.contains(player)) {
                    anthVortex.add(player);
                }
                int counter = 0;
                for (Entity p : idleBlocks.values()) {
                    if (p.equals(player)) {
                        counter++;
                    }
                }
                if (counter < 64 && player.getInventory().contains(COBBLESTONE)) {
                    Utilities.removeItem(player, COBBLESTONE, 1);
                    Utilities.damageTool(player, 2, usedHand);
                    Location loc = player.getLocation();
                    FallingBlock blockEntity
                        = loc.getWorld().spawnFallingBlock(loc, Bukkit.createBlockData(MAT[Storage.rnd.nextInt(4)]));
                    blockEntity.setDropItem(false);
                    blockEntity.setGravity(false);
                    blockEntity
                        .setMetadata("ze.anthrothrower", new FixedMetadataValue(Storage.zenchantments, player));
                    idleBlocks.put(blockEntity, player);
                    return true;
                }
            }
            return false;
        } else if ((event.getAction() == LEFT_CLICK_AIR || event.getAction() == LEFT_CLICK_BLOCK)
            || hand.getType() == AIR) {
            anthVortex.remove(player);
            List<FallingBlock> toRemove = new ArrayList<>();
            for (FallingBlock blk : idleBlocks.keySet()) {
                if (idleBlocks.get(blk).equals(player)) {
                    attackBlocks.put(blk, new Pair<>(this.getPower(), player.getLocation().getDirection()));
                    toRemove.add(blk);
                    Block targetBlock = player.getTargetBlock(null, 7);
                    blk.setVelocity(targetBlock
                        .getLocation().subtract(player.getLocation()).toVector().multiply(.25));
                }
            }
            for (FallingBlock blk : toRemove) {
                idleBlocks.remove(blk);
                blk.setGravity(true);
                blk.setGlowing(true);
            }
        }
        return false;
    }

    private class Pair<K, V> {

        private K key;
        private V value;

        public Pair(K k, V v) {
            this.key = k;
            this.value = v;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }
    }

}
