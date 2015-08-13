package zedly.zenchantments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import static org.bukkit.GameMode.CREATIVE;
import org.bukkit.Location;
import org.bukkit.Material;
import static org.bukkit.Material.*;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import static org.bukkit.block.Biome.*;
import org.bukkit.block.Block;
import static org.bukkit.enchantments.Enchantment.LOOT_BONUS_BLOCKS;
import static org.bukkit.enchantments.Enchantment.SILK_TOUCH;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import static org.bukkit.entity.EntityType.*;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Sheep;
import org.bukkit.event.block.Action;
import static org.bukkit.event.block.Action.LEFT_CLICK_AIR;
import static org.bukkit.event.block.Action.LEFT_CLICK_BLOCK;
import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import static org.bukkit.potion.PotionEffectType.INCREASE_DAMAGE;
import static org.bukkit.potion.PotionEffectType.JUMP;
import static org.bukkit.potion.PotionEffectType.NIGHT_VISION;
import org.bukkit.util.Vector;
import static zedly.zenchantments.Storage.rnd;
import static zedly.zenchantments.Storage.speed;

public class Enchantment {

    protected int maxLevel;
    protected String loreName;
    protected float chance;
    protected Material[] enchantable;
    protected String[] conflicting;
    protected String description;

//Empty Methods
    public void onBlockBreak(BlockBreakEvent evt, int level) {
    }

    public void onBlockInteract(PlayerInteractEvent evt, int level) {
    }

    public void onEntityInteract(PlayerInteractEntityEvent evt, int level) {
    }

    public void onEntityKill(EntityDeathEvent evt, int level) {
    }

    public void onHitting(EntityDamageByEntityEvent evt, int level) {
    }

    public void onBeingHit(EntityDamageByEntityEvent evt, int level) {
    }

    public void onEntityDamage(EntityDamageEvent evt, int level) {
    }

    public void onPlayerFish(PlayerFishEvent evt, int level) {
    }

    public void onHungerChange(FoodLevelChangeEvent evt, int level) {
    }

    public void onShear(PlayerShearEntityEvent evt, int level) {
    }

    public void onProjectileHit(ProjectileHitEvent evt, int level) {
    }

    public void onEntityShootBow(EntityShootBowEvent evt, int level) {
    }

    public void onPotionSplash(PotionSplashEvent evt, int level) {
    }

    public void onProjectileLaunch(ProjectileLaunchEvent evt, int level) {
    }

    public void onScan(Player player, int level) {
    }

    public void onScanHand(Player player, int level) {
    }

    public void onFastScan(Player player, int level) {
    }

    public void onFastScanHand(Player player, int level) {
    }

//Enchantments
    public static class Anthropomorphism extends Enchantment {

        public Anthropomorphism() {
            maxLevel = 1;
            loreName = "Anthropomorphism";
            chance = 0;
            enchantable = Storage.picks;
            conflicting = new String[]{"Pierce", "Switch"};
            description = "Spawns blocks to protect you when right sneak clicking, and attacks entities when left clicking";
        }

        @Override
        public void onBlockInteract(PlayerInteractEvent evt, int level) {
            Player player = evt.getPlayer();
            if (evt.getAction() == RIGHT_CLICK_AIR || evt.getAction() == RIGHT_CLICK_BLOCK) {
                if (player.isSneaking()) {
                    if (!Storage.anthVortex.contains(player)) {
                        Storage.anthVortex.add(player);
                    }
                    int counter = 0;
                    for (Entity p : Storage.anthMobs.values()) {
                        if (p.equals(player)) {
                            counter++;
                        }
                    }
                    if (counter < 64 && player.getInventory().contains(COBBLESTONE)) {
                        Utilities.removeItem(player.getInventory(), COBBLESTONE, 1);
                        Utilities.addUnbreaking(player.getItemInHand(), 2, player);
                        player.updateInventory();
                        Location loc = player.getLocation();
                        Material mat[] = new Material[]{STONE, GRAVEL, DIRT, GRASS};
                        FallingBlock bk = loc.getWorld().spawnFallingBlock(loc, mat[Storage.rnd.nextInt(4)], (byte) 0x0);
                        bk.setDropItem(false);
                        Storage.anthMobs.put(bk, player);
                    }
                }
            }
            if ((evt.getAction() == LEFT_CLICK_AIR || evt.getAction() == LEFT_CLICK_BLOCK) || player.getItemInHand().getType() == AIR) {
                Storage.anthVortex.remove(player);
                ArrayList<FallingBlock> toRemove = new ArrayList<>();
                for (FallingBlock blk : Storage.anthMobs.keySet()) {
                    if (Storage.anthMobs.get(blk).equals(player)) {
                        Storage.anthMobs2.add(blk);
                        toRemove.add(blk);
                        blk.setVelocity(player.getTargetBlock(null, 7).getLocation().subtract(player.getLocation()).toVector().multiply(.25));
                    }
                }
                for (FallingBlock blk : toRemove) {
                    Storage.anthMobs.remove(blk);
                }
            }
        }
    }

    public static class Arborist extends Enchantment {

        public Arborist() {
            maxLevel = 3;
            loreName = "Arborist";
            chance = 0;
            enchantable = Storage.axes;
            conflicting = new String[]{};
            description = "Drops more apples, sticks, and saplings when used on leaves and wood";
        }

        @Override
        public void onBlockBreak(BlockBreakEvent evt, int level) {
            if (!evt.isCancelled()) {
                Block blk = evt.getBlock();
                if (blk.getType() == LOG || blk.getType() == LOG_2 || blk.getType() == LEAVES_2 || blk.getType() == LEAVES) {
                    short s = (short) blk.getData();
                    if (s >= 8) {
                        s -= 8;
                    }
                    ItemStack stk;
                    if (blk.getType() == LOG_2 || blk.getType() == LEAVES_2) {
                        stk = new ItemStack(SAPLING, 1, (short) (s + 4));
                    } else {
                        stk = new ItemStack(SAPLING, 1, s);
                    }
                    if (Storage.rnd.nextInt(100) >= (90 - (level * 10))) {
                        if (Storage.rnd.nextInt(100) % 3 == 0) {
                            evt.getBlock().getWorld().dropItemNaturally(Utilities.getCenter(evt.getBlock().getLocation()), stk);
                        }
                        if (Storage.rnd.nextInt(100) % 3 == 0) {
                            evt.getBlock().getWorld().dropItemNaturally(Utilities.getCenter(evt.getBlock().getLocation()), new ItemStack(STICK, 1));
                        }
                        if (Storage.rnd.nextInt(100) % 3 == 0) {
                            evt.getBlock().getWorld().dropItemNaturally(Utilities.getCenter(evt.getBlock().getLocation()), new ItemStack(APPLE, 1));
                        }
                        if (Storage.rnd.nextInt(65) == 25) {
                            evt.getBlock().getWorld().dropItemNaturally(Utilities.getCenter(evt.getBlock().getLocation()), new ItemStack(GOLDEN_APPLE, 1));
                        }
                    }
                }
            }
        }
    }

    public static class Archaeology extends Enchantment {

        public Archaeology() {
            maxLevel = 3;
            loreName = "Archaeology";
            chance = 0;
            enchantable = (Material[]) ArrayUtils.addAll(Storage.picks, Storage.spades);
            conflicting = new String[]{};
            description = "Occasionally drops ancient artifacts when mining";
        }

        @Override
        public void onBlockBreak(BlockBreakEvent evt, int level) {
            if ((evt.getBlock().getType() == STONE || evt.getBlock().getType() == DIRT) && !evt.isCancelled()) {
                if (Storage.rnd.nextInt((int) (300 / level)) == 20) {
                    ArchDrop.Drop(evt.getBlock());
                }
            }
        }
    }

    public static class BlazesCurse extends Enchantment {

        public BlazesCurse() {
            maxLevel = 1;
            loreName = "Blaze's Curse";
            chance = 0;
            enchantable = Storage.chestplates;
            conflicting = new String[]{};
            description = "Causes the player to be unharmed in lava and fire, but damages them in water and rain";
        }

        @Override
        public void onEntityDamage(EntityDamageEvent evt, int level) {
            if (evt.getCause() == EntityDamageEvent.DamageCause.LAVA || evt.getCause() == EntityDamageEvent.DamageCause.FIRE || evt.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
                evt.setCancelled(true);
            }
        }

        @Override
        public void onScan(Player player, int level) {
            Location loc = player.getLocation();
            Material mat = player.getLocation().getBlock().getType();
            if (mat == STATIONARY_WATER || mat == WATER) {
                EntityDamageEvent evt = new EntityDamageEvent(player, DamageCause.DROWNING, 100);
                Bukkit.getPluginManager().callEvent(evt);
                player.setLastDamageCause(evt);
                if (!evt.isCancelled()) {
                    player.damage(1.5f);
                }
            }
            if (player.getWorld().hasStorm() == true) {
                ArrayList<Boolean> b = new ArrayList<>();
                if (player.getLocation().getBlockY() > 256) {
                    return;
                }
                for (int x = player.getLocation().getBlockY() + 1; x <= 256; x++) {
                    loc.setY(x);
                    if (loc.getBlock().getType() != AIR) {
                        b.add(Boolean.FALSE);
                    } else {
                        b.add(Boolean.TRUE);
                    }
                }
                if (!b.contains(Boolean.FALSE)) {
                    Biome[] biomes = new Biome[]{DESERT, SAVANNA, COLD_BEACH, COLD_TAIGA, COLD_TAIGA_HILLS, COLD_TAIGA_MOUNTAINS, DESERT_HILLS, DESERT_MOUNTAINS, HELL,
                        FROZEN_OCEAN, FROZEN_RIVER, ICE_MOUNTAINS, ICE_PLAINS, ICE_PLAINS_SPIKES, SAVANNA_PLATEAU_MOUNTAINS, SAVANNA_PLATEAU, SAVANNA_MOUNTAINS, SAVANNA,
                        MESA, MESA_BRYCE, MESA_PLATEAU, MESA_PLATEAU_FOREST, MESA_PLATEAU_FOREST_MOUNTAINS, MESA_PLATEAU_MOUNTAINS};
                    if (!ArrayUtils.contains(biomes, player.getLocation().getBlock().getBiome())) {
                        EntityDamageEvent evt = new EntityDamageEvent(player, DamageCause.DROWNING, .5);
                        Bukkit.getPluginManager().callEvent(evt);
                        player.setLastDamageCause(evt);
                        if (!evt.isCancelled()) {
                            player.damage(.5f);
                        }
                    }
                }
            }
        }
    }

    public static class Blizzard extends Enchantment {

        public Blizzard() {
            maxLevel = 3;
            loreName = "Blizzard";
            chance = 0;
            enchantable = Storage.bows;
            conflicting = new String[]{};
            description = "Spawns a blizzard where the arrow strikes freezing nearby entities";
        }

        @Override
        public void onEntityShootBow(EntityShootBowEvent evt, int level) {
            Arrow.ArrowEnchantBlizzard arrow = new Arrow.ArrowEnchantBlizzard(level);
            arrow.entity = (Projectile) evt.getProjectile();
            Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
        }
    }

    public static class Bounce extends Enchantment {

        public Bounce() {
            maxLevel = 5;
            loreName = "Bounce";
            chance = 0;
            enchantable = Storage.boots;
            conflicting = new String[]{};
            description = "Preserves momentum when on slime blocks";
        }

        @Override
        public void onFastScan(Player player, int level) {
            if (player.getVelocity().getY() < 0 && (player.getLocation().getBlock().getRelative(0, -1, 0).getType() == SLIME_BLOCK
                    || player.getLocation().getBlock().getType() == SLIME_BLOCK || player.getLocation().getBlock().getRelative(0, -2, 0).getType() == SLIME_BLOCK)) {
                if (!player.isSneaking()) {
                    player.setVelocity(player.getVelocity().setY(.56 * level));
                }
                player.setFallDistance(0);
            }
        }
    }

    public static class Combustion extends Enchantment {

        public Combustion() {
            maxLevel = 4;
            loreName = "Combustion";
            chance = 0;
            enchantable = Storage.chestplates;
            conflicting = new String[]{};
            description = "Lights attacking entities on fire when player is attacked";
        }

        @Override
        public void onBeingHit(EntityDamageByEntityEvent evt, int level) {
            Entity ent;
            if (evt.getDamager().getType() == EntityType.ARROW) {
                org.bukkit.entity.Arrow arrow = (org.bukkit.entity.Arrow) evt.getDamager();
                if (arrow.getShooter() instanceof LivingEntity) {
                    ent = (Entity) arrow.getShooter();
                } else {
                    return;
                }
            } else {
                ent = evt.getDamager();
            }
            ent.setFireTicks(50 * level);
        }
    }

    public static class Conversion extends Enchantment {

        public Conversion() {
            maxLevel = 1;
            loreName = "Conversion";
            chance = 0;
            enchantable = Storage.swords;
            conflicting = new String[]{};
            description = "Converts XP to health when right clicking and sneaking";
        }

        @Override
        public void onBlockInteract(PlayerInteractEvent evt, int level) {
            if (evt.getAction() == Action.RIGHT_CLICK_AIR || evt.getAction() == Action.RIGHT_CLICK_BLOCK) {
                final Player player = (Player) evt.getPlayer();
                if (player.isSneaking()) {
                    if (player.getLevel() > 1) {
                        if (player.getHealth() < 20) {
                            player.setLevel((int) (player.getLevel() - 2));
                            player.setHealth(20);
                            for (int i = 0; i < 3; i++) {
                                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                                    @Override
                                    public void run() {
                                        player.getWorld().spigot().playEffect(Utilities.getCenter(player.getLocation()), Effect.HEART, 0, 1, .5f, .5f, .5f, .1f, 10, 16);
                                    }
                                }, ((i * 5) + 1));
                            }
                        }
                    }
                }
            }
        }
    }

    public static class Extraction extends Enchantment {

        public Extraction() {
            maxLevel = 3;
            loreName = "Extraction";
            chance = 0;
            enchantable = Storage.picks;
            conflicting = new String[]{"Switch"};
            description = "Smelts and yields more product from ores";
        }

        @Override
        public void onBlockBreak(BlockBreakEvent evt, final int level) {
            if (evt.isCancelled()) {
                return;
            }
            if (evt.getBlock().getType() == GOLD_ORE) {
                Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                evt.setCancelled(true);
                evt.getBlock().setType(AIR);
                for (int x = 0; x < Storage.rnd.nextInt(level + 1) + 1; x++) {
                    evt.getBlock().getWorld().dropItemNaturally(Utilities.getCenter(evt.getBlock().getLocation()), new ItemStack(GOLD_INGOT));
                }
                evt.getBlock().getLocation().getWorld().spigot().playEffect(Utilities.getCenter(evt.getBlock().getLocation()), Effect.FLAME, 0, 1, .5f, .5f, .5f, .1f, 10, 16);
            } else if (evt.getBlock().getType() == IRON_ORE) {
                Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                evt.setCancelled(true);
                evt.getBlock().setType(AIR);
                for (int x = 0; x < Storage.rnd.nextInt(level + 1) + 1; x++) {
                    evt.getBlock().getWorld().dropItemNaturally(Utilities.getCenter(evt.getBlock().getLocation()), new ItemStack(IRON_INGOT));
                }
                evt.getBlock().getLocation().getWorld().spigot().playEffect(Utilities.getCenter(evt.getBlock().getLocation()), Effect.FLAME, 0, 1, .5f, .5f, .5f, .1f, 10, 16);
            }
        }
    }

    public static class Fire extends Enchantment {

        public Fire() {
            maxLevel = 1;
            loreName = "Fire";
            chance = 0;
            enchantable = (Material[]) ArrayUtils.addAll(Storage.picks, ArrayUtils.addAll(Storage.axes, Storage.spades));
            conflicting = new String[]{"Switch", "Variety"};
            description = "Drops the smelted version of the block broken";
        }

        @Override
        public void onBlockBreak(BlockBreakEvent evt, int level) {
            if (evt.isCancelled()) {
                return;
            }
            Material mat = AIR;
            short itemInfo = 0;
            if (ArrayUtils.contains(Storage.picks, evt.getPlayer().getItemInHand().getType())) {
                if (evt.getBlock().getType() == STONE) {
                    short s = evt.getBlock().getData();
                    if (s == 1 || s == 3 || s == 5) {
                        s++;
                        mat = STONE;
                        itemInfo = s;
                    } else if (s == 2 || s == 4 || s == 6) {
                        return;
                    } else {
                        mat = SMOOTH_BRICK;
                    }
                } else if (evt.getBlock().getType() == IRON_ORE) {
                    mat = IRON_INGOT;
                } else if (evt.getBlock().getType() == GOLD_ORE) {
                    mat = GOLD_INGOT;
                } else if (evt.getBlock().getType() == COBBLESTONE) {
                    mat = STONE;
                } else if (evt.getBlock().getType() == MOSSY_COBBLESTONE) {
                    mat = SMOOTH_BRICK;
                    itemInfo = 1;
                } else if (evt.getBlock().getType() == NETHERRACK) {
                    mat = NETHER_BRICK_ITEM;
                } else if (evt.getBlock().getType() == SMOOTH_BRICK && evt.getBlock().getData() == 0) {
                    mat = SMOOTH_BRICK;
                    itemInfo = 2;
                }
            }
            if (evt.getBlock().getType() == SAND) {
                mat = GLASS;
            } else if (evt.getBlock().getType() == LOG || evt.getBlock().getType() == LOG_2) {
                mat = COAL;
                itemInfo = 1;
            } else if (evt.getBlock().getType() == CLAY) {
                Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                evt.setCancelled(true);
                evt.getBlock().setType(AIR);
                evt.getBlock().getLocation().getWorld().spigot().playEffect(Utilities.getCenter(evt.getBlock().getLocation()), Effect.FLAME, 0, 1, .5f, .5f, .5f, .1f, 10, 16);
                for (int x = 0; x < 4; x++) {
                    evt.getBlock().getWorld().dropItemNaturally(Utilities.getCenter(evt.getBlock().getLocation()), new ItemStack(CLAY_BRICK));
                }
            } else if (evt.getBlock().getType() == CACTUS) {
                Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                evt.setCancelled(true);
                Location location = evt.getBlock().getLocation().clone();
                double height = location.getY();
                for (double i = location.getY(); i <= 256; i++) {
                    location.setY(i);
                    if (location.getBlock().getType() == CACTUS) {
                        height++;
                    } else {
                        break;
                    }
                }
                for (double i = height - 1; i >= evt.getBlock().getLocation().getY(); i--) {
                    location.setY(i);
                    evt.getBlock().getLocation().getWorld().spigot().playEffect(Utilities.getCenter(evt.getBlock().getLocation()), Effect.FLAME, 0, 1, .5f, .5f, .5f, .1f, 10, 16);
                    evt.getBlock().setType(AIR);
                    evt.getBlock().getWorld().dropItemNaturally(Utilities.getCenter(location), new ItemStack(INK_SACK, 1, (short) 2));
                }
            }
            if (mat != AIR) {
                Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                evt.setCancelled(true);
                evt.getBlock().setType(AIR);
                evt.getBlock().getWorld().dropItemNaturally(Utilities.getCenter(evt.getBlock().getLocation()), new ItemStack((mat), 1, itemInfo));
                evt.getBlock().getLocation().getWorld().spigot().playEffect(Utilities.getCenter(evt.getBlock().getLocation()), Effect.FLAME, 0, 1, .5f, .5f, .5f, .1f, 10, 16);
            }
        }
    }

    public static class Firestorm extends Enchantment {

        public Firestorm() {
            maxLevel = 3;
            loreName = "Firestorm";
            chance = 0;
            enchantable = Storage.bows;
            conflicting = new String[]{};
            description = "Spawns a firestorm where the arrow strikes burning nearby entities";
        }

        @Override
        public void onEntityShootBow(EntityShootBowEvent evt, int level) {
            Arrow.ArrowEnchantFirestorm arrow = new Arrow.ArrowEnchantFirestorm(level);
            arrow.entity = (Projectile) evt.getProjectile();
            Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
        }

    }

    public static class Fireworks extends Enchantment {

        public Fireworks() {
            maxLevel = 4;
            loreName = "Fireworks";
            chance = 0;
            enchantable = Storage.bows;
            conflicting = new String[]{};
            description = "Shoots arrows that burst into fireworks upon impact";
        }

        @Override
        public void onEntityShootBow(EntityShootBowEvent evt, int level) {
            Arrow.ArrowEnchantFirework arrow = new Arrow.ArrowEnchantFirework(level);
            arrow.entity = (Projectile) evt.getProjectile();
            Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
        }

    }

    public static class Force extends Enchantment {

        public Force() {
            maxLevel = 3;
            loreName = "Force";
            chance = 0;
            enchantable = Storage.swords;
            conflicting = new String[]{"Rainbow Slam", "Gust"};
            description = "Pushes and pulls nearby mobs, configurable through shift clicking";
        }

        @Override
        public void onBlockInteract(PlayerInteractEvent evt, int level) {
            if (!Storage.forceModes.containsKey(evt.getPlayer())) {
                Storage.forceModes.put(evt.getPlayer(), 1);
            }
            if (evt.getPlayer().isSneaking()) {
                if (evt.getAction() == RIGHT_CLICK_AIR || evt.getAction() == RIGHT_CLICK_BLOCK) {
                    if (Storage.forceModes.get(evt.getPlayer()) < 3) {
                        Storage.forceModes.put(evt.getPlayer(), Storage.forceModes.get(evt.getPlayer()) + 1);
                    } else {
                        Storage.forceModes.put(evt.getPlayer(), 1);
                    }
                    switch (Storage.forceModes.get(evt.getPlayer())) {
                        case 1:
                            evt.getPlayer().sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Push Mode");
                            break;
                        case 2:
                            evt.getPlayer().sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Pull Mode");
                            break;
                    }
                }
            }
            int mode = Storage.forceModes.get(evt.getPlayer());
            if (evt.getAction() == Action.RIGHT_CLICK_AIR || evt.getAction() == Action.RIGHT_CLICK_BLOCK) {
                List<Entity> nearEnts = evt.getPlayer().getNearbyEntities(5, 5, 5);
                if (!nearEnts.isEmpty()) {
                    if (evt.getPlayer().getFoodLevel() >= 2) {
                        if (rnd.nextInt(10) == 5) {
                            FoodLevelChangeEvent event = new FoodLevelChangeEvent(evt.getPlayer(), 2);
                            Bukkit.getServer().getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                evt.getPlayer().setFoodLevel(evt.getPlayer().getFoodLevel() - 2);
                            }
                        }
                        for (Entity ent : nearEnts) {
                            boolean tester = true;
                            if (ent instanceof Player && Storage.force_rainbow_slam_players) {
                                tester = false;
                            }
                            if (ent instanceof LivingEntity) {
                                if ((!(ent instanceof Player)) || !tester) {
                                    Location playLoc = evt.getPlayer().getLocation();
                                    Location entLoc = ent.getLocation();
                                    Location total;
                                    if (mode == 1) {
                                        total = entLoc.subtract(playLoc);
                                    } else {
                                        total = playLoc.subtract(entLoc);
                                    }
                                    Vector vect = new Vector(total.getX(), total.getY(), total.getZ()).multiply((.1f + (level * .2f)));
                                    if (vect.getY() > 1) {
                                        vect.setY(1);
                                    }
                                    if (vect.getY() < -1) {
                                        vect.setY(-1);
                                    }
                                    ent.setVelocity(vect);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static class Germination extends Enchantment {

        public Germination() {
            maxLevel = 3;
            loreName = "Germination";
            chance = 0;
            enchantable = Storage.hoes;
            conflicting = new String[]{};
            description = "Uses bonemeal from the player's inventory to grow nearby plants";
        }

        @Override
        public void onBlockInteract(PlayerInteractEvent evt, int level) {
            if (evt.getAction() == RIGHT_CLICK_BLOCK) {
                Location loc = evt.getClickedBlock().getLocation();
                int radiusXZ = level + 2;
                int radiusY = 2;
                for (int x = -(radiusXZ); x <= radiusXZ; x++) {
                    for (int y = -(radiusY) - 1; y <= radiusY - 1; y++) {
                        for (int z = -(radiusXZ); z <= radiusXZ; z++) {
                            Block block = (Block) loc.getBlock();
                            if (block.getRelative(x, y, z).getLocation().distanceSquared(loc) < radiusXZ * radiusXZ) {
                                if (((block.getRelative(x, y, z).getType() == CROPS || block.getRelative(x, y, z).getType() == POTATO
                                        || block.getRelative(x, y, z).getType() == CARROT || block.getRelative(x, y, z).getType() == MELON_STEM
                                        || block.getRelative(x, y, z).getType() == PUMPKIN_STEM) && block.getRelative(x, y, z).getData() < 7)
                                        || ((block.getRelative(x, y, z).getType() == COCOA) && block.getRelative(x, y, z).getData() < 8)) {
                                    BlockBreakEvent event = new BlockBreakEvent(block.getRelative(x, y, z), evt.getPlayer());
                                    Bukkit.getServer().getPluginManager().callEvent(event);
                                    if (event.isCancelled()) {
                                        continue;
                                    }
                                    if (Utilities.removeItemCheck(evt.getPlayer().getInventory(), INK_SACK, (short) 15, 1) || evt.getPlayer().getGameMode().equals(CREATIVE)) {
                                        if (Storage.rnd.nextBoolean()) {
                                            Utilities.grow(block.getRelative(x, y, z));
                                            Utilities.grow(block.getRelative(x, y, z));
                                        } else {
                                            Utilities.grow(block.getRelative(x, y, z));
                                        }
                                        evt.getPlayer().getWorld().spigot().playEffect(Utilities.getCenter(block.getRelative(x, y, z).getLocation()), Effect.HAPPY_VILLAGER, 0, 1, .3f, .3f, .3f, 1f, 30, 16);
                                        evt.getPlayer().updateInventory();
                                        if (Storage.rnd.nextInt(10) == 3) {
                                            Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }

        }
    }

    public static class Glide extends Enchantment {

        public Glide() {
            maxLevel = 3;
            loreName = "Glide";
            chance = 0;
            enchantable = Storage.leggings;
            conflicting = new String[]{};
            description = "Gently brings the player back to the ground when sneaking";
        }

        @Override
        public void onFastScan(Player player, int level) {
            if (!Storage.sneakGlide.containsKey(player)) {
                Storage.sneakGlide.put(player, player.getLocation().getY());
            }
            if (!player.isSneaking() || Storage.sneakGlide.get(player) == player.getLocation().getY()) {
                return;
            }
            boolean b = false;
            for (int i = -5; i < 0; i++) {
                if (player.getLocation().getBlock().getRelative(0, i, 0).getType() != AIR) {
                    b = true;
                }
            }
            if (!b) {
                double sinPitch = Math.sin(Math.toRadians(player.getLocation().getPitch()));
                double cosPitch = Math.cos(Math.toRadians(player.getLocation().getPitch()));
                double sinYaw = Math.sin(Math.toRadians(player.getLocation().getYaw()));
                double cosYaw = Math.cos(Math.toRadians(player.getLocation().getYaw()));
                double y = -1 * (sinPitch);
                Vector v = new Vector(-cosPitch * sinYaw, 0, -1 * (-cosPitch * cosYaw));
                v.multiply(level / 2);
                v.setY(-1);
                player.setVelocity(v);
                player.setFallDistance(6 - level);
                Location l = player.getLocation().clone();
                l.setY(l.getY() - 3);
                player.getLocation().getWorld().spigot().playEffect(l, Effect.CLOUD, 0, 1, 0, 0, 0, .1f, 1, 32);
            }
            if (Storage.rnd.nextInt(5 * level) == 5) {
                ItemStack[] s = player.getInventory().getArmorContents();
                for (int i = 0; i < 4; i++) {
                    if (s[i] != null) {
                        HashMap<Enchantment, Integer> map = Utilities.getEnchant(s[i]);
                        if (map.containsKey(this)) {
                            Utilities.addUnbreaking(s[i], 1);
                        }
                        if (s[i].getDurability() > s[i].getType().getMaxDurability()) {
                            s[i] = null;
                        }
                    }
                }
                player.getInventory().setArmorContents(s);
            }
            Storage.sneakGlide.put(player, player.getLocation().getY());
        }

    }

    public static class Gluttony extends Enchantment {

        public Gluttony() {
            maxLevel = 1;
            loreName = "Gluttony";
            chance = 0;
            enchantable = Storage.helmets;
            conflicting = new String[]{};
            description = "Automatically eats for the player";
        }

        @Override
        public void onScan(Player player, int level) {
            Material[] mat = new Material[]{RABBIT_STEW, COOKED_BEEF, PUMPKIN_PIE, GRILLED_PORK, BAKED_POTATO, COOKED_CHICKEN, COOKED_MUTTON,
                MUSHROOM_SOUP, COOKED_FISH, COOKED_FISH, BREAD, APPLE, CARROT_ITEM, COOKIE, MELON};
            int[] ints = {10, 8, 8, 8, 6, 6, 6, 6, 6, 5, 5, 4, 3, 2, 2};
            int check = 0;
            for (int i = 0; i < mat.length; i++) {
                if (mat[i] == COOKED_FISH) {
                    check = (check + 1) % 2;
                }
                if (player.getInventory().containsAtLeast(new ItemStack(mat[i], 1, (short) check), 1) && player.getFoodLevel() <= 20 - ints[i]) {
                    Utilities.removeItem(player.getInventory(), mat[i], (short) check, 1);
                    player.setFoodLevel(player.getFoodLevel() + ints[i]);
                    if (mat[i] == RABBIT_STEW || mat[i] == MUSHROOM_SOUP) {
                        player.getInventory().addItem(new ItemStack(BOWL));
                    }
                }
            }
        }
    }

    public static class GoldRush extends Enchantment {

        public GoldRush() {
            maxLevel = 3;
            loreName = "Gold Rush";
            chance = 0;
            enchantable = Storage.spades;
            conflicting = new String[]{};
            description = "Randomly drops gold nuggets when mining sand";
        }

        @Override
        public void onBlockBreak(BlockBreakEvent evt, int level) {
            if (evt.getBlock().getType() == SAND && rnd.nextInt(100) >= (100 - (level * 3))) {
                evt.getBlock().getWorld().dropItemNaturally(Utilities.getCenter(evt.getBlock().getLocation()), new ItemStack(GOLD_NUGGET));
            }
        }
    }

    public static class Grab extends Enchantment {

        public Grab() {
            maxLevel = 1;
            loreName = "Grab";
            chance = 0;
            enchantable = (Material[]) ArrayUtils.addAll(Storage.picks, ArrayUtils.addAll(Storage.axes, Storage.spades));
            conflicting = new String[]{};
            description = "Teleports mined items and XP directly to the player";
        }

        @Override
        public void onBlockBreak(final BlockBreakEvent evt, int level) {
            Storage.grabbedBlocks.put(evt.getBlock(), evt.getPlayer().getLocation());
            final Block block = evt.getBlock();
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                @Override
                public void run() {
                    Storage.grabbedBlocks.remove(block);
                }
            }, 3);
        }
    }

    public static class GreenThumb extends Enchantment {

        public GreenThumb() {
            maxLevel = 3;
            loreName = "Green Thumb";
            chance = 0;
            enchantable = Storage.leggings;
            conflicting = new String[]{};
            description = "Grows the foliage around the player";
        }

        @Override
        public void onScan(Player player, int level) {
            Location loc = player.getLocation().clone();
            int radius = level + 2;
            for (int x = -(radius); x <= radius; x++) {
                for (int y = -(radius) - 1; y <= radius - 1; y++) {
                    for (int z = -(radius); z <= radius; z++) {
                        Block block = (Block) loc.getBlock();
                        if (block.getRelative(x, y, z).getLocation().distanceSquared(loc) < radius * radius) {
                            int pr = rnd.nextInt(400);
                            if (pr == 50 || level == 10) {
                                byte data = 0;
                                Material mat = AIR;
                                boolean test = false;
                                int t = 0;
                                switch (block.getRelative(x, y, z).getType()) {
                                    case DIRT:
                                        if (block.getRelative(x, y, z).getData() != 2) {
                                            if (block.getRelative(x, y + 1, z).getType() == AIR) {
                                                test = true;
                                                switch (block.getBiome()) {
                                                    case MUSHROOM_ISLAND:
                                                    case MUSHROOM_SHORE:
                                                        mat = MYCEL;
                                                        break;
                                                    case MEGA_SPRUCE_TAIGA:
                                                    case MEGA_SPRUCE_TAIGA_HILLS:
                                                    case MEGA_TAIGA:
                                                    case MEGA_TAIGA_HILLS:
                                                        mat = GRASS;
                                                        data = (byte) 2;
                                                        break;
                                                    default:
                                                        mat = GRASS;
                                                        data = (byte) 0;
                                                }
                                            }
                                        }
                                        break;
                                    case POTATO:
                                    case CROPS:
                                    case CARROT:
                                    case NETHER_WARTS:
                                    case PUMPKIN_STEM:
                                    case MELON_STEM:
                                    case COCOA:
                                        if (((block.getRelative(x, y, z).getType() == CROPS || block.getRelative(x, y, z).getType() == POTATO
                                                || block.getRelative(x, y, z).getType() == CARROT || block.getRelative(x, y, z).getType() == MELON_STEM
                                                || block.getRelative(x, y, z).getType() == PUMPKIN_STEM) && block.getRelative(x, y, z).getData() < 7)
                                                || ((block.getRelative(x, y, z).getType() == NETHER_WARTS) && block.getRelative(x, y, z).getData() < 3)
                                                || ((block.getRelative(x, y, z).getType() == COCOA) && block.getRelative(x, y, z).getData() < 8)) {
                                            t = Utilities.grow(block.getRelative(x, y, z));
                                        }
                                        break;
                                }
                                if (t == 1) {
                                    test = true;
                                }
                                if (block.getRelative(x, y, z).getType() == DIRT && test) {
                                    block.getRelative(x, y, z).setType(mat);
                                    block.getRelative(x, y, z).setData(data);
                                }
                                if (test) {
                                    loc.getWorld().spigot().playEffect(Utilities.getCenter(block.getRelative(x, y + 1, z).getLocation()), Effect.HAPPY_VILLAGER, 0, 1, .3f, .3f, .3f, 1f, 20, 16);
                                }
                                if (test) {
                                    int chc = rnd.nextInt(50);
                                    if (chc > 44 && level != 10) {
                                        ItemStack[] s = player.getInventory().getArmorContents();
                                        for (int i = 0; i < 4; i++) {
                                            if (s[i] != null) {
                                                HashMap<Enchantment, Integer> map = Utilities.getEnchant(s[i]);
                                                if (map.containsKey(this)) {
                                                    Utilities.addUnbreaking(s[i], 1);
                                                }
                                                if (s[i].getDurability() > s[i].getType().getMaxDurability()) {
                                                    s[i] = null;
                                                }
                                            }
                                        }
                                        player.getInventory().setArmorContents(s);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static class Gust extends Enchantment {

        public Gust() {
            maxLevel = 1;
            loreName = "Gust";
            chance = 0;
            enchantable = Storage.swords;
            conflicting = new String[]{"Force", "Rainbow Slam"};
            description = "Pushes the user through the air at the cost of their health";
        }

        @Override
        public void onBlockInteract(final PlayerInteractEvent evt, int level) {
            if (evt.getAction().equals(RIGHT_CLICK_AIR) || evt.getAction().equals(RIGHT_CLICK_BLOCK)) {
                if (evt.getPlayer().getHealth() > 2) {
                    final Block blk = evt.getPlayer().getTargetBlock(null, 10);
                    evt.getPlayer().setVelocity(blk.getLocation().toVector().subtract(evt.getPlayer().getLocation().toVector()).multiply(.25));
                    evt.getPlayer().setFallDistance(0);
                    EntityDamageEvent event = new EntityDamageEvent(evt.getPlayer(), DamageCause.MAGIC, 2);
                    Bukkit.getPluginManager().callEvent(event);
                    evt.getPlayer().setLastDamageCause(event);
                    if (!event.isCancelled()) {
                        evt.getPlayer().damage(2f);
                    }
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                        @Override
                        public void run() {
                            Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 5, evt.getPlayer());
                        }
                    }, 1);
                }
            }
        }
    }

    public static class Harvest extends Enchantment {

        public Harvest() {
            maxLevel = 3;
            loreName = "Harvest";
            chance = 0;
            enchantable = Storage.hoes;
            conflicting = new String[]{};
            description = "Harvests fully grown crops within a radius when clicked";
        }

        @Override
        public void onBlockInteract(PlayerInteractEvent evt, int level) {
            if (evt.getAction() == RIGHT_CLICK_BLOCK) {
                Location loc = evt.getClickedBlock().getLocation();
                int radiusXZ = level + 2;
                int radiusY = 1;
                for (int x = -(radiusXZ); x <= radiusXZ; x++) {
                    for (int y = -(radiusY) - 1; y <= radiusY - 1; y++) {
                        for (int z = -(radiusXZ); z <= radiusXZ; z++) {
                            Block block = (Block) loc.getBlock();
                            if (block.getRelative(x, y, z).getLocation().distanceSquared(loc) < radiusXZ * radiusXZ) {
                                if (((block.getRelative(x, y + 1, z).getType() == MELON_BLOCK) || block.getRelative(x, y + 1, z).getType() == PUMPKIN) || ((block.getRelative(x, y + 1, z).getType() == NETHER_WARTS && block.getRelative(x, y + 1, z).getData() == 3) || ((block.getRelative(x, y + 1, z).getType() == CROPS || block.getRelative(x, y + 1, z).getType() == POTATO || block.getRelative(x, y + 1, z).getType() == CARROT)) && block.getRelative(x, y + 1, z).getData() == 7)) {
                                    BlockBreakEvent event = new BlockBreakEvent(block.getRelative(x, y + 1, z), evt.getPlayer());
                                    Bukkit.getServer().getPluginManager().callEvent(event);
                                    if (event.isCancelled()) {
                                        continue;
                                    }
                                    if (Storage.rnd.nextBoolean()) {
                                        Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                                    }
                                    Storage.grabbedBlocks.put(block.getRelative(x, y + 1, z), evt.getPlayer().getLocation());
                                    final Block blk = block.getRelative(x, y + 1, z);
                                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                                        @Override
                                        public void run() {
                                            Storage.grabbedBlocks.remove(blk);
                                        }
                                    }, 3);
                                    block.getRelative(x, y + 1, z).breakNaturally();
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    public static class IceAspect extends Enchantment {

        public IceAspect() {
            maxLevel = 2;
            loreName = "Ice Aspect";
            chance = 0;
            enchantable = Storage.swords;
            conflicting = new String[]{};
            description = "Temporarily freezes the target";
        }

        @Override
        public void onHitting(EntityDamageByEntityEvent evt, int level) {
            if (!evt.isCancelled()) {
                ((LivingEntity) evt.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40 + (level * 40), (level * 2)));
            }
        }
    }

    public static class Jump extends Enchantment {

        public Jump() {
            maxLevel = 4;
            loreName = "Jump";
            chance = 0;
            enchantable = Storage.boots;
            conflicting = new String[]{};
            description = "Gives the player a jump boost";
        }

        @Override
        public void onScan(Player player, int level) {
            player.removePotionEffect(JUMP);
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 610, level));
        }
    }

    public static class Laser extends Enchantment {

        public Laser() {
            maxLevel = 3;
            loreName = "Laser";
            chance = 0;
            enchantable = (Material[]) ArrayUtils.addAll(Storage.picks, Storage.axes);
            conflicting = new String[]{};
            description = "Breaks blocks and damages mobs using a powerful beam of light";
        }

        private void Shoot(Location blk, Player player, int level) {
            Location playLoc = player.getLocation();
            Location target = Utilities.getCenter(blk);
            target.setY(target.getY() + .5);
            Location c = playLoc;
            c.setY(c.getY() + 1.1);
            double d = target.distance(c);
            for (int i = 0; i < (int) d * 5; i++) {
                Location tempLoc = target.clone();
                tempLoc.setX(c.getX() + (i * ((target.getX() - c.getX()) / (d * 5))));
                tempLoc.setY(c.getY() + (i * ((target.getY() - c.getY()) / (d * 5))));
                tempLoc.setZ(c.getZ() + (i * ((target.getZ() - c.getZ()) / (d * 5))));
                blk.getWorld().spigot().playEffect(tempLoc, Effect.COLOURED_DUST, 0, 1, 0f, 0f, 0f, 10f, 1, 32);
                for (Entity ent : Bukkit.getWorld(playLoc.getWorld().getName()).getEntities()) {
                    if (ent.getLocation().distance(tempLoc) < 1.5) {
                        if (ent instanceof LivingEntity) {
                            LivingEntity e = (LivingEntity) ent;
                            if (!e.equals(player)) {
                                if (!(e instanceof Player) || Storage.laser_pvp) {
                                    EntityDamageByEntityEvent evt = new EntityDamageByEntityEvent(player, e, DamageCause.ENTITY_ATTACK, (double) (1 + (level * 2)));
                                    Bukkit.getPluginManager().callEvent(evt);
                                    LivingEntity theE = (LivingEntity) evt.getEntity();
                                    theE.setLastDamageCause(evt);
                                    if (!evt.isCancelled()) {
                                        if (Storage.rnd.nextInt(20) == 6) {
                                            Utilities.addUnbreaking(player.getItemInHand(), 1, player);
                                        }
                                        theE.damage((double) (1 + (level * 2)));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void onEntityInteract(PlayerInteractEntityEvent evt, int level) {
            if (!evt.getPlayer().isSneaking()) {
                Storage.laserTimes.put(evt.getPlayer(), System.nanoTime());
                Shoot(evt.getRightClicked().getLocation(), evt.getPlayer(), level);
            }
        }

        @Override
        public void onBlockInteract(PlayerInteractEvent evt, int level) {
            if (Storage.laserTimes.containsKey(evt.getPlayer()) && System.nanoTime() - Storage.laserTimes.get(evt.getPlayer()) < 500000000) {
                Storage.laserTimes.remove(evt.getPlayer());
                return;
            }
            boolean b = false;
            for (Enchantment e : Utilities.getEnchant(evt.getPlayer().getItemInHand()).keySet()) {
                if (e.loreName.equals("Lumber")) {
                    b = true;
                }
            }
            if ((!evt.getPlayer().isSneaking() || b) && (evt.getAction() == RIGHT_CLICK_AIR || evt.getAction() == RIGHT_CLICK_BLOCK)) {
                final Block blk = evt.getPlayer().getTargetBlock(null, 6 + (level * 3)).getRelative(0, 0, 0);
                Shoot(blk.getLocation(), evt.getPlayer(), level);
                int[] nobreak = new int[]{0, 7, 23, 52, 54, 61, 62, 63, 64, 68, 69, 71, 77, 90, 96, 107, 116, 117, 119, 120, 130, 137, 138, 143, 145, 146, 158, 166, 167, 183, 184, 185, 186, 187, 193, 194, 195, 196, 197};
                if (ArrayUtils.contains(nobreak, blk.getTypeId())) {
                    return;
                }
                final BlockBreakEvent event = new BlockBreakEvent(blk, evt.getPlayer());
                Bukkit.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return;
                }
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                    @Override
                    public void run() {
                        if (event.getPlayer().getItemInHand().getEnchantments().containsKey(SILK_TOUCH)) {
                            for (ItemStack st : Utilities.silktouchDrops(blk)) {
                                event.getPlayer().getWorld().dropItem(Utilities.getCenter(blk.getLocation()), st);
                            }
                            blk.setType(AIR);
                        } else if (event.getPlayer().getItemInHand().getEnchantments().containsKey(LOOT_BONUS_BLOCKS)) {
                            for (ItemStack st : Utilities.fortuneDrops(event.getPlayer().getItemInHand().getEnchantments().get(LOOT_BONUS_BLOCKS), blk)) {
                                event.getPlayer().getWorld().dropItem(Utilities.getCenter(blk.getLocation()), st);
                            }
                            blk.setType(AIR);
                        } else {
                            blk.breakNaturally();
                        }
                    }
                }, 1);
                Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
            }
        }
    }

    public static class Level extends Enchantment {

        public Level() {
            maxLevel = 3;
            loreName = "Level";
            chance = 0;
            enchantable = (Material[]) ArrayUtils.addAll(Storage.picks, ArrayUtils.addAll(Storage.bows, Storage.swords));
            conflicting = new String[]{};
            description = "Drops more XP when killing mobs or mining ores";
        }

        @Override
        public void onEntityKill(EntityDeathEvent evt, int level) {
            if (Storage.rnd.nextBoolean()) {
                evt.setDroppedExp((int) (evt.getDroppedExp() * (1.3 + (level * .5))));
            }
        }

        @Override
        public void onBlockBreak(BlockBreakEvent evt, int level) {
            if (Storage.rnd.nextBoolean()) {
                evt.setExpToDrop((int) (evt.getExpToDrop() * (1.3 + (level * .5))));
            }
        }

        @Override
        public void onEntityShootBow(EntityShootBowEvent evt, int level) {
            Arrow.ArrowEnchantLevel arrow = new Arrow.ArrowEnchantLevel(level);
            arrow.entity = (Projectile) evt.getProjectile();
            Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
        }

    }

    public static class LongCast extends Enchantment {

        public LongCast() {
            maxLevel = 2;
            loreName = "Long Cast";
            chance = 0;
            enchantable = Storage.rods;
            conflicting = new String[]{"Short Cast"};
            description = "Launches fishing hooks farther out when casting";
        }

        @Override
        public void onProjectileLaunch(ProjectileLaunchEvent evt, int level) {
            if (evt.getEntity().getType() == EntityType.FISHING_HOOK) {
                evt.getEntity().setVelocity(evt.getEntity().getVelocity().normalize().multiply(1.9 + (level - 1.2)));
            }
        }
    }

    public static class Lumber extends Enchantment {

        private void bk(Block blk, ArrayList<Block> bks, ArrayList<Block> total, ArrayList<Block> tester, int i) {
            i++;
            if (i >= 150 || total.size() >= 150) {
                return;
            }
            bks.add(blk);
            Location loc = blk.getLocation().clone();
            for (int y = -1; y <= 1; y++) {
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        Location loc2 = loc.clone();
                        loc2.setX(loc2.getX() + x);
                        loc2.setY(loc2.getY() + y);
                        loc2.setZ(loc2.getZ() + z);
                        Material mat = loc2.getBlock().getType();
                        if (mat != LOG && mat != LOG_2 && mat != LEAVES && mat != LEAVES_2 && mat != DIRT && mat != GRASS && mat != VINE && mat != SNOW
                                && mat != COCOA && mat != AIR && mat != RED_ROSE && mat != YELLOW_FLOWER && mat != LONG_GRASS && mat != GRAVEL
                                && mat != STONE && mat != DOUBLE_PLANT && mat != WATER && mat != STATIONARY_WATER && mat != SAND && mat != SAPLING
                                && mat != BROWN_MUSHROOM && mat != RED_MUSHROOM && mat != MOSSY_COBBLESTONE && mat != CLAY && mat != HUGE_MUSHROOM_1 && mat != HUGE_MUSHROOM_2
                                && mat != SUGAR_CANE_BLOCK) {
                            tester.add(loc2.getBlock());
                        }
                        if (!bks.contains(loc2.getBlock()) && (loc2.getBlock().getType() == LOG || loc2.getBlock().getType() == LOG_2)) {
                            bk(loc2.getBlock(), bks, total, tester, i);
                            total.add(loc2.getBlock());
                        }
                    }
                }
            }
        }

        public Lumber() {
            maxLevel = 1;
            loreName = "Lumber";
            chance = 0;
            enchantable = Storage.axes;
            conflicting = new String[]{};
            description = "Breaks the entire tree at once";
        }

        @Override
        public void onBlockBreak(BlockBreakEvent evt, int level) {
            if (!evt.getPlayer().isSneaking()) {
                return;
            }
            if (!Utilities.eventStart(evt.getPlayer(), loreName)) {
                evt.setCancelled(true);
                ArrayList<Block> used = new ArrayList<>();
                ArrayList<Block> total = new ArrayList<>();
                ArrayList<Block> tester = new ArrayList<>();
                if (evt.getBlock().getType() == LOG || evt.getBlock().getType() == LOG_2) {
                    bk(evt.getBlock(), used, total, tester, 0);
                }
                if ((!(total.size() >= 150)) && tester.isEmpty()) {
                    int i = 1;
                    if (evt.getBlock().getType() == LOG || evt.getBlock().getType() == LOG_2) {
                        total.add(evt.getBlock());
                    }
                    for (Block b : total) {
                        final int i2 = i + 1;
                        Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                        final BlockBreakEvent event = new BlockBreakEvent(b, evt.getPlayer());
                        Bukkit.getServer().getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                                @Override
                                public void run() {
                                    final Block block = event.getBlock();
                                    if (event.getBlock().getType() != AIR) {
                                        event.getBlock().breakNaturally();
                                    }
                                }
                            }, 1);
                            i++;
                        }
                    }
                }
                Utilities.eventEnd(evt.getPlayer(), loreName);
            }
        }
    }

    public static class Magnetism extends Enchantment {

        public Magnetism() {
            maxLevel = 1;
            loreName = "Magnetism";
            chance = 0;
            enchantable = Storage.leggings;
            conflicting = new String[]{};
            description = "Slowly attracts nearby items to the players inventory";
        }

        @Override
        public void onFastScan(Player player, int level) {
            for (Entity e : player.getNearbyEntities(5, 5, 5)) {
                if (e.getType().equals(DROPPED_ITEM) && e.getTicksLived() > 160) {
                    e.setVelocity(player.getLocation().toVector().subtract(e.getLocation().toVector()).multiply(.05));
                }
            }
        }
    }

    public static class Meador extends Enchantment {

        public Meador() {
            maxLevel = 1;
            loreName = "Meador";
            chance = 0;
            enchantable = Storage.boots;
            conflicting = new String[]{"Weight", "Speed", "Jump"};
            description = "Gives the player both a speed and jump boost";
        }

        @Override
        public void onScan(Player player, int level) {
            player.setWalkSpeed(.5f + (level * .05f));
            player.setFlySpeed(.5f + (level * .05f));
            speed.add(player);
            player.removePotionEffect(JUMP);
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 610, (level + 2)));
        }
    }

    public static class Mow extends Enchantment {

        public Mow() {
            maxLevel = 1;
            loreName = "Mow";
            chance = 0;
            enchantable = Storage.shears;
            conflicting = new String[]{};
            description = "Shears all nearby sheep";
        }

        @Override
        public void onBlockInteract(final PlayerInteractEvent evt, int level) {
            if (evt.getAction() == RIGHT_CLICK_AIR || evt.getAction() == RIGHT_CLICK_BLOCK) {
                for (Entity ent : evt.getPlayer().getNearbyEntities(5, 5, 5)) {
                    final Entity e = ent;
                    if (ent.getType() == SHEEP) {
                        final Sheep sheep = (Sheep) ent;
                        final PlayerShearEntityEvent event = new PlayerShearEntityEvent(evt.getPlayer(), ent);
                        Bukkit.getServer().getPluginManager().callEvent(event);
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                            @Override
                            public void run() {
                                if (sheep.isAdult() && !sheep.isSheared() && !event.isCancelled()) {
                                    short s = sheep.getColor().getData();
                                    int number = rnd.nextInt(3) + 1;
                                    Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                                    sheep.setSheared(true);
                                    evt.getPlayer().getWorld().dropItemNaturally(Utilities.getCenter(e.getLocation()), new ItemStack(WOOL, number, s));
                                }
                            }
                        }, 1);
                    }
                }
            }
        }

        @Override
        public void onShear(final PlayerShearEntityEvent evt, int level) {
            if (!Utilities.eventStart(evt.getPlayer(), loreName)) {
                if (!evt.isCancelled()) {
                    for (Entity ent : evt.getEntity().getNearbyEntities(5, 5, 5)) {
                        final Entity e = ent;
                        if (ent.getType() == SHEEP) {
                            final Sheep sheep = (Sheep) ent;
                            final PlayerShearEntityEvent event = new PlayerShearEntityEvent(evt.getPlayer(), ent);
                            Bukkit.getServer().getPluginManager().callEvent(event);
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                                @Override
                                public void run() {
                                    if (sheep.isAdult() && !sheep.isSheared() && !event.isCancelled()) {
                                        short s = sheep.getColor().getData();
                                        int number = rnd.nextInt(3) + 1;
                                        Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                                        sheep.setSheared(true);
                                        evt.getPlayer().getWorld().dropItemNaturally(Utilities.getCenter(e.getLocation()), new ItemStack(WOOL, number, s));
                                    }
                                }
                            }, 1);
                        }
                    }
                }
                Utilities.eventEnd(evt.getPlayer(), loreName);
            }
        }
    }

    public static class MysteryFish extends Enchantment {

        public MysteryFish() {
            maxLevel = 1;
            loreName = "Mystery Fish";
            chance = 0;
            enchantable = Storage.rods;
            conflicting = new String[]{};
            description = "Catches water mobs like Squid and Guardians";
        }

        @Override
        public void onPlayerFish(final PlayerFishEvent evt, int level) {
            if (rnd.nextInt(10) < level) {
                if (evt.getCaught() != null) {
                    Location location = evt.getCaught().getLocation();
                    final Entity ent;
                    if (Storage.rnd.nextBoolean()) {
                        ent = evt.getPlayer().getWorld().spawnEntity(location, SQUID);
                    } else {
                        ent = evt.getPlayer().getWorld().spawnEntity(location, GUARDIAN);
                        Guardian g = (Guardian) evt.getPlayer().getWorld().spawnEntity(location, GUARDIAN);
                        g.setElder(Storage.rnd.nextBoolean());
                        Storage.guardianMove.put(g, evt.getPlayer());
                    }
                    evt.getCaught().setPassenger(ent);
                }
            }
        }
    }

    public static class NightVision extends Enchantment {

        public NightVision() {
            maxLevel = 1;
            loreName = "Night Vision";
            chance = 0;
            enchantable = Storage.helmets;
            conflicting = new String[]{};
            description = "Lets the player see in the darkness";
        }

        @Override
        public void onScan(Player player, int level) {
            player.removePotionEffect(NIGHT_VISION);
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 610, 5));
        }
    }

    public static class Persephone extends Enchantment {

        public Persephone() {
            maxLevel = 3;
            loreName = "Persephone";
            chance = 0;
            enchantable = Storage.hoes;
            conflicting = new String[]{};
            description = "Plants seeds from the player's inventory around them";
        }

        @Override
        public void onBlockInteract(PlayerInteractEvent evt, int level) {
            if (evt.getAction() == RIGHT_CLICK_BLOCK) {
                Material mats[] = new Material[]{CARROT_ITEM, POTATO_ITEM, SEEDS, NETHER_STALK};
                Material mat = AIR;
                for (int i = 0; i < 36; i++) {
                    if (evt.getPlayer().getInventory().getItem(i) != null) {
                        if (ArrayUtils.contains(mats, evt.getPlayer().getInventory().getItem(i).getType())) {
                            mat = evt.getPlayer().getInventory().getItem(i).getType();
                            break;
                        }
                    }
                }
                Location loc = evt.getClickedBlock().getLocation();
                int radiusXZ = level + 2;
                int radiusY = 1;
                for (int x = -(radiusXZ); x <= radiusXZ; x++) {
                    for (int y = -(radiusY) - 1; y <= radiusY - 1; y++) {
                        for (int z = -(radiusXZ); z <= radiusXZ; z++) {
                            Block block = (Block) loc.getBlock();
                            if (block.getRelative(x, y, z).getLocation().distanceSquared(loc) < radiusXZ * radiusXZ) {
                                BlockBreakEvent event = new BlockBreakEvent(block.getRelative(x, y, z), evt.getPlayer());
                                Bukkit.getServer().getPluginManager().callEvent(event);
                                if (event.isCancelled()) {
                                    continue;
                                }
                                if ((block.getRelative(x, y, z).getType() == SOUL_SAND || block.getRelative(x, y, z).getType() == SOIL) && block.getRelative(x, y + 1, z).getType() == AIR) {
                                    if (evt.getPlayer().getInventory().contains(mat)) {
                                        Utilities.removeItem(evt.getPlayer().getInventory(), mat, 1);
                                        evt.getPlayer().updateInventory();
                                    } else {
                                        continue;
                                    }
                                    Material m = AIR;
                                    switch (mat) {
                                        case CARROT_ITEM:
                                            m = CARROT;
                                            break;
                                        case POTATO_ITEM:
                                            m = POTATO;
                                            break;
                                        case SEEDS:
                                            m = CROPS;
                                            break;
                                        case NETHER_STALK:
                                            m = NETHER_WARTS;
                                            break;
                                    }
                                    Boolean b = true;
                                    if (block.getRelative(x, y, z).getType() == SOUL_SAND && m == NETHER_WARTS) {
                                        block.getRelative(x, y + 1, z).setType(m);
                                        b = false;
                                    } else if (ArrayUtils.contains(mats, mat) && m != NETHER_WARTS && block.getRelative(x, y, z).getType() == SOIL) {
                                        block.getRelative(x, y + 1, z).setType(m);
                                        b = false;
                                    }
                                    evt.getPlayer().updateInventory();
                                    if (Storage.rnd.nextBoolean() && !b) {
                                        Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static class Pierce extends Enchantment {

        private void bk(Block blk, ArrayList<Block> bks, ArrayList<Block> total, Material[] mat, int i) {
            i++;
            if (i >= 128 || total.size() >= 128) {
                return;
            }
            bks.add(blk);
            Location loc = blk.getLocation().clone();
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        Location loc2 = loc.clone();
                        loc2.setX(loc2.getX() + x);
                        loc2.setY(loc2.getY() + y);
                        loc2.setZ(loc2.getZ() + z);
                        if (!bks.contains(loc2.getBlock()) && (ArrayUtils.contains(mat, loc2.getBlock().getType()))) {
                            bk(loc2.getBlock(), bks, total, mat, i);
                            total.add(loc2.getBlock());
                        }
                    }
                }
            }
        }

        public Pierce() {
            maxLevel = 1;
            loreName = "Pierce";
            chance = 0;
            enchantable = Storage.picks;
            conflicting = new String[]{"Anthropomorphism", "Switch", "Shred"};
            description = "Lets the player mine in several modes which can be changed through shift clicking";
        }

        @Override
        public void onBlockInteract(PlayerInteractEvent evt, int level) {
            if (!Storage.pierceModes.containsKey(evt.getPlayer())) {
                Storage.pierceModes.put(evt.getPlayer(), 1);
            }
            if (evt.getPlayer().isSneaking()) {
                if (evt.getAction() == RIGHT_CLICK_AIR || evt.getAction() == RIGHT_CLICK_BLOCK) {
                    if (Storage.pierceModes.get(evt.getPlayer()) < 5) {
                        Storage.pierceModes.put(evt.getPlayer(), Storage.pierceModes.get(evt.getPlayer()) + 1);
                    } else {
                        Storage.pierceModes.put(evt.getPlayer(), 1);
                    }
                    switch (Storage.pierceModes.get(evt.getPlayer())) {
                        case 1:
                            evt.getPlayer().sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "1x Normal Mode");
                            break;
                        case 2:
                            evt.getPlayer().sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "3x Wide Mode");
                            break;
                        case 3:
                            evt.getPlayer().sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "3x Long Mode");
                            break;
                        case 4:
                            evt.getPlayer().sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "3x Tall Mode");
                            break;
                        case 5:
                            evt.getPlayer().sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Ore Mode");
                            break;
                    }
                }
            }
        }

        @Override
        @SuppressWarnings("empty-statement")
        public void onBlockBreak(final BlockBreakEvent evt, int level) {
            //1 = normal; 2 = wide; 3 = deep; 4 = tall; 5 = ore
            if (!Storage.pierceModes.containsKey(evt.getPlayer())) {
                Storage.pierceModes.put(evt.getPlayer(), 1);
            }
            final int mode = Storage.pierceModes.get(evt.getPlayer());
            if (!Utilities.eventStart(evt.getPlayer(), loreName)) {
                int counter = 0;
                Player player = (Player) evt.getPlayer();
                final Location blkLoc = evt.getBlock().getLocation();
                if (mode != 1 && mode != 5) {
                    float direction = evt.getPlayer().getLocation().getYaw();
                    if (direction < 0) {
                        direction += 360;
                    }
                    direction %= 360;
                    double i = (double) ((direction + 8) / 18);
                    int add = -1;
                    boolean b = false;
                    int[][] ints = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
                    if (i >= 8 && i < 13) {//North-
                        ints = new int[][]{{1, 0, 0}, {0, 0, 1}, {0, 1, 0}};
                        b = true;
                    } else if (i < 8 && i >= 3) {//West-
                        ints = new int[][]{{0, 0, 1}, {1, 0, 0}, {0, 1, 0}};
                    } else if (i < 3 || i >= 18) {//South+
                        ints = new int[][]{{1, 0, 0}, {0, 0, 1}, {0, 1, 0}};
                        add = 1;
                        b = true;
                    } else if (i < 18 && i >= 13) {//East+
                        ints = new int[][]{{0, 0, 1}, {1, 0, 0}, {0, 1, 0}};
                        add = 1;
                    }
                    int[] rads = ints[mode - 2];
                    if (mode == 3) {
                        if (b) {
                            blkLoc.setZ(blkLoc.getZ() + add);
                        } else {
                            blkLoc.setX(blkLoc.getX() + add);
                        }
                    }
                    if (mode == 4) {
                        if (evt.getPlayer().getLocation().getPitch() > 65) {
                            blkLoc.setY(blkLoc.getY() - 1);
                        } else if (evt.getPlayer().getLocation().getPitch() < -65) {
                            blkLoc.setY(blkLoc.getY() + 1);
                        }
                    }
                    for (int x = -(rads[0]); x <= rads[0]; x++) {
                        for (int y = -(rads[1]); y <= rads[1]; y++) {
                            for (int z = -(rads[2]); z <= rads[2]; z++) {
                                evt.setCancelled(true);
                                final BlockBreakEvent event = new BlockBreakEvent(blkLoc.getBlock().getRelative(x, y, z), player);
                                Bukkit.getServer().getPluginManager().callEvent(event);
                                if (!event.isCancelled()) {
                                    if (event.getBlock().getType() != BEDROCK && event.getBlock().getType() != ENDER_PORTAL_FRAME
                                            && event.getBlock().getType() != ENDER_PORTAL && event.getBlock().getType() != BARRIER && event.getBlock().getType() != PORTAL) {
                                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                                            @Override
                                            public void run() {
                                                if (event.getBlock().getType() != AIR) {
                                                    if (event.getPlayer().getItemInHand().getEnchantments().containsKey(SILK_TOUCH)) {
                                                        for (ItemStack st : Utilities.silktouchDrops(event.getBlock())) {
                                                            event.getPlayer().getWorld().dropItem(Utilities.getCenter(event.getBlock().getLocation()), st);
                                                        }
                                                        event.getBlock().setType(AIR);
                                                    } else if (event.getPlayer().getItemInHand().getEnchantments().containsKey(LOOT_BONUS_BLOCKS)) {
                                                        for (ItemStack st : Utilities.fortuneDrops(event.getPlayer().getItemInHand().getEnchantments().get(LOOT_BONUS_BLOCKS), event.getBlock())) {
                                                            event.getPlayer().getWorld().dropItem(Utilities.getCenter(event.getBlock().getLocation()), st);
                                                        }
                                                        event.getBlock().setType(AIR);
                                                    } else {
                                                        event.getBlock().breakNaturally();
                                                    }
                                                }
                                            }
                                        }, 1);
                                        counter++;
                                    }
                                }
                            }
                        }
                    }
                } else if (mode == 5) {
                    Material mats[] = new Material[]{COAL_ORE, REDSTONE_ORE, DIAMOND_ORE, GOLD_ORE,
                        IRON_ORE, LAPIS_ORE, GLOWSTONE, QUARTZ_ORE, EMERALD_ORE, GLOWING_REDSTONE_ORE};
                    ArrayList<Block> used = new ArrayList<>();
                    ArrayList<Block> total = new ArrayList<>();
                    if (ArrayUtils.contains(mats, evt.getBlock().getType())) {
                        Material mat[];
                        if (evt.getBlock().getType() != REDSTONE_ORE && evt.getBlock().getType() != GLOWING_REDSTONE_ORE) {
                            mat = new Material[]{evt.getBlock().getType()};
                        } else {
                            mat = new Material[]{REDSTONE_ORE, GLOWING_REDSTONE_ORE};
                        }
                        bk(evt.getBlock(), used, total, mat, 0);
                    } else {
                        Utilities.eventEnd(evt.getPlayer(), loreName);
                        return;
                    }
                    if (!(total.size() >= 128)) {
                        for (Block b : total) {
                            Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                            final BlockBreakEvent event = new BlockBreakEvent(b, evt.getPlayer());
                            Bukkit.getServer().getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                if (evt.getBlock().getType() != AIR) {
                                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                                        @Override
                                        public void run() {
                                            if (event.getPlayer().getItemInHand().getEnchantments().containsKey(SILK_TOUCH)) {
                                                for (ItemStack st : Utilities.silktouchDrops(event.getBlock())) {
                                                    event.getPlayer().getWorld().dropItem(Utilities.getCenter(event.getBlock().getLocation()), st);
                                                }
                                                event.getBlock().setType(AIR);
                                            } else if (event.getPlayer().getItemInHand().getEnchantments().containsKey(LOOT_BONUS_BLOCKS)) {
                                                for (ItemStack st : Utilities.fortuneDrops(event.getPlayer().getItemInHand().getEnchantments().get(LOOT_BONUS_BLOCKS), event.getBlock())) {
                                                    event.getPlayer().getWorld().dropItem(Utilities.getCenter(event.getBlock().getLocation()), st);
                                                }
                                                event.getBlock().setType(AIR);
                                            } else {
                                                event.getBlock().breakNaturally();
                                            }
                                        }
                                    }, 1);
                                }
                            }
                        }
                    }
                }
                Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), (int) (counter / (float) 1.5), evt.getPlayer());
                Utilities.eventEnd(evt.getPlayer(), loreName);
            }
        }
    }

    public static class Plough extends Enchantment {

        public Plough() {
            maxLevel = 3;
            loreName = "Plough";
            chance = 0;
            enchantable = Storage.hoes;
            conflicting = new String[]{};
            description = "Tills all soil within a radius";
        }

        @Override
        public void onBlockInteract(PlayerInteractEvent evt, int level) {
            if (evt.getAction() == RIGHT_CLICK_BLOCK) {
                Location loc = evt.getClickedBlock().getLocation();
                int radiusXZ = level + 2;
                int radiusY = 1;
                for (int x = -(radiusXZ); x <= radiusXZ; x++) {
                    for (int y = -(radiusY) - 1; y <= radiusY - 1; y++) {
                        for (int z = -(radiusXZ); z <= radiusXZ; z++) {
                            Block block = (Block) loc.getBlock();
                            if (block.getRelative(x, y, z).getLocation().distanceSquared(loc) < radiusXZ * radiusXZ) {
                                BlockBreakEvent event = new BlockBreakEvent(block.getRelative(x, y, z), evt.getPlayer());
                                Bukkit.getServer().getPluginManager().callEvent(event);
                                if (event.isCancelled()) {
                                    continue;
                                }
                                if ((block.getRelative(x, y, z).getType() == DIRT || block.getRelative(x, y, z).getType() == GRASS || block.getRelative(x, y, z).getType() == MYCEL) && block.getRelative(x, y + 1, z).getType() == AIR) {
                                    block.getRelative(x, y, z).setType(SOIL);
                                    if (Storage.rnd.nextBoolean()) {
                                        Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static class Potion extends Enchantment {

        PotionEffectType[] potions;

        public Potion() {
            maxLevel = 3;
            loreName = "Potion";
            chance = 0;
            enchantable = Storage.bows;
            conflicting = new String[]{};
            description = "Gives the shooter random positive potion effects when attacking";
        }

        @Override
        public void onEntityShootBow(EntityShootBowEvent evt, int level) {
            Arrow.ArrowEnchantPotion arrow = new Arrow.ArrowEnchantPotion(level);
            arrow.entity = (Projectile) evt.getProjectile();
            Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
        }
    }

    public static class PotionResistance extends Enchantment {

        public PotionResistance() {
            maxLevel = 4;
            loreName = "Potion Resistance";
            chance = 0;
            enchantable = (Material[]) ArrayUtils.addAll(Storage.helmets, ArrayUtils.addAll(Storage.chestplates, ArrayUtils.addAll(Storage.leggings, Storage.boots)));
            conflicting = new String[]{};
            description = "Lessens the effects of all potions on players";
        }

        @Override
        public void onPotionSplash(PotionSplashEvent evt, int level) {
            for (LivingEntity ent : evt.getAffectedEntities()) {
                if (ent instanceof Player) {
                    int effect = 0;
                    for (ItemStack stk : ((Player) ent).getInventory().getArmorContents()) {
                        for (Enchantment e : Utilities.getEnchant(stk).keySet()) {
                            if (e.equals(this)) {
                                effect += Utilities.getEnchant(stk).get(e);
                            }
                        }
                    }
                    evt.setIntensity(ent, evt.getIntensity(ent) / ((effect + 1.3) / 2));
                }
            }
        }
    }

    public static class QuickShot extends Enchantment {

        public QuickShot() {
            maxLevel = 1;
            loreName = "Quick Shot";
            chance = 0;
            enchantable = Storage.bows;
            conflicting = new String[]{};
            description = "Shoots arrows at full speed, instantly";
        }

        @Override
        public void onEntityShootBow(EntityShootBowEvent evt, int level) {
            Arrow.ArrowEnchantQuickShot arrow = new Arrow.ArrowEnchantQuickShot();
            arrow.entity = (Projectile) evt.getProjectile();
            Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
        }
    }

    public static class Rainbow extends Enchantment {

        public Rainbow() {
            maxLevel = 1;
            loreName = "Rainbow";
            chance = 0;
            enchantable = Storage.shears;
            conflicting = new String[]{};
            description = "Drops random flowers and wool colors when used";
        }

        @Override
        public void onBlockBreak(BlockBreakEvent evt, int level) {
            if (!evt.isCancelled()) {
                short itemInfo;
                Material dropMaterial;
                if (evt.getBlock().getType() == RED_ROSE || evt.getBlock().getType() == YELLOW_FLOWER) {
                    short sh = (short) rnd.nextInt(9);
                    dropMaterial = (sh == 7) ? YELLOW_FLOWER : RED_ROSE;
                    itemInfo = (sh == 7) ? 0 : (short) rnd.nextInt(9);
                } else if (evt.getBlock().getType() == DOUBLE_PLANT && (evt.getBlock().getData() == 0 || evt.getBlock().getData() == 1 || evt.getBlock().getData() == 4 || evt.getBlock().getData() == 5)) {
                    short[] shorts = new short[]{0, 1, 4, 5};
                    dropMaterial = DOUBLE_PLANT;
                    itemInfo = (short) rnd.nextInt(4);
                    itemInfo = shorts[itemInfo];
                } else {
                    Utilities.eventEnd(evt.getPlayer(), loreName);
                    return;
                }
                evt.setCancelled(true);
                evt.getBlock().setType(AIR);
                Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                evt.getPlayer().getWorld().dropItem(Utilities.getCenter(evt.getBlock().getLocation()), new ItemStack(dropMaterial, 1, itemInfo));
            }
        }

        @Override
        public void onShear(PlayerShearEntityEvent evt, int level) {
            Sheep sheep = (Sheep) evt.getEntity();
            if (!sheep.isSheared() && !evt.isCancelled()) {
                int color = rnd.nextInt(16);
                int number = rnd.nextInt(3) + 1;
                Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                evt.setCancelled(true);
                sheep.setSheared(true);
                evt.getEntity().getWorld().dropItemNaturally(Utilities.getCenter(evt.getEntity().getLocation()), new ItemStack(WOOL, number, (short) color));
            }
        }
    }

    public static class RainbowSlam extends Enchantment {

        public RainbowSlam() {
            maxLevel = 4;
            loreName = "Rainbow Slam";
            chance = 0;
            enchantable = Storage.swords;
            conflicting = new String[]{"Force", "Gust"};
            description = "Attacks enemy mobs with a powerful swirling slam";
        }

        @Override
        public void onEntityInteract(final PlayerInteractEntityEvent evt, final int level) {
            if ((evt.getRightClicked() instanceof Monster) || (Storage.force_rainbow_slam_players && evt.getRightClicked() instanceof Player)) {
                Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 9, evt.getPlayer());
                final LivingEntity ent = (LivingEntity) evt.getRightClicked();
                final Location l = ent.getLocation().clone();
                ent.teleport(l);
                for (int i = 0; i < 2000; i++) {
                    final float j = i;
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                        @Override
                        public void run() {
                            if (ent.isDead()) {
                                return;
                            }
                            float x, y, z;
                            Location loc = l.clone();
                            float j1 = j;
                            loc.setY(loc.getY() + (j1 / 100));
                            loc.setX(loc.getX() + Math.sin(Math.toRadians(j1)) * j1 / 330);
                            loc.setZ(loc.getZ() + Math.cos(Math.toRadians(j1)) * j1 / 330);
                            ent.getWorld().spigot().playEffect(loc, Effect.COLOURED_DUST, 0, 1, 0, 0, 0, 10f, 1, 32);
                            loc.setY(loc.getY() + 1.3);
                            ent.setVelocity(loc.toVector().subtract(ent.getLocation().toVector()));
                            ent.setFallDistance(-20 + ((level * 2) + 8));
                        }
                    }, (int) (i / 20));
                }
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                    @Override
                    public void run() {
                        if (ent.isDead()) {
                            return;
                        }
                        ent.setVelocity(l.toVector().subtract(ent.getLocation().toVector()).multiply(.2));
                    }
                }, 110);
            }
        }
    }

    public static class Reaper extends Enchantment {

        public Reaper() {
            maxLevel = 4;
            loreName = "Reaper";
            chance = 0;
            enchantable = (Material[]) ArrayUtils.addAll(Storage.bows, Storage.swords);
            conflicting = new String[]{};
            description = "Gives the target temporary wither effect and blindness";
        }

        @Override
        public void onEntityShootBow(EntityShootBowEvent evt, int level) {
            Arrow.ArrowEnchantReaper arrow = new Arrow.ArrowEnchantReaper(level);
            arrow.entity = (Projectile) evt.getProjectile();
            Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
        }

        @Override
        public void onHitting(EntityDamageByEntityEvent evt, int level) {
            if (!evt.isCancelled()) {
                ((LivingEntity) evt.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.WITHER, (10 + (level * 20)), level));
                ((LivingEntity) evt.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (10 + (level * 20)), level));
            }
        }
    }

    public static class Saturation extends Enchantment {

        public Saturation() {
            maxLevel = 3;
            loreName = "Saturation";
            chance = 0;
            enchantable = Storage.leggings;
            conflicting = new String[]{};
            description = "Uses less of the player's hunger";
        }

        @Override
        public void onHungerChange(FoodLevelChangeEvent evt, int level) {
            if (evt.getFoodLevel() < ((Player) evt.getEntity()).getFoodLevel() && Storage.rnd.nextInt(10) > 10 - 2 * level) {
                evt.setCancelled(true);
            }
        }
    }

    public static class ShortCast extends Enchantment {

        public ShortCast() {
            maxLevel = 2;
            loreName = "Short Cast";
            chance = 0;
            enchantable = Storage.rods;
            conflicting = new String[]{"Long Cast"};
            description = "Launches fishing hooks closer in when casting";
        }

        @Override
        public void onProjectileLaunch(ProjectileLaunchEvent evt, int level) {
            if (evt.getEntity().getType() == EntityType.FISHING_HOOK) {
                evt.getEntity().setVelocity(evt.getEntity().getVelocity().normalize().multiply((.8f / level)));
            }
        }
    }

    public static class Shred extends Enchantment {

        public Shred() {
            maxLevel = 5;
            loreName = "Shred";
            chance = 0;
            enchantable = (Material[]) ArrayUtils.addAll(Storage.picks, Storage.spades);
            conflicting = new String[]{"Pierce", "Switch"};
            description = "Breaks the blocks within a radius of the original block mined but does not drop them";
        }

        @Override
        public void onBlockBreak(BlockBreakEvent evt, int level) {
            int original = level;
            final int l = level;
            level++;
            if (!Utilities.eventStart(evt.getPlayer(), loreName)) {
                Material mats[] = new Material[]{STONE, COAL_ORE, REDSTONE_ORE, DIAMOND_ORE, GOLD_ORE, IRON_ORE,
                    NETHERRACK, LAPIS_ORE, GLOWSTONE, QUARTZ_ORE, EMERALD_ORE, GRASS, SOUL_SAND, GLOWING_REDSTONE_ORE,
                    DIRT, MYCEL, SAND, GRAVEL, SOUL_SAND, CLAY, HARD_CLAY, STAINED_CLAY, SANDSTONE, RED_SANDSTONE};
                final Material shovel[] = new Material[]{GLOWSTONE, GRASS, DIRT, MYCEL, SOUL_SAND, SAND, GRAVEL, SOUL_SAND, CLAY};
                int counter = 0;
                if (!ArrayUtils.contains(mats, evt.getBlock().getType()) && !evt.getBlock().getType().equals(AIR)) {
                    Utilities.eventEnd(evt.getPlayer(), loreName);
                    return;
                }
                evt.setCancelled(true);
                Player player = (Player) evt.getPlayer();
                final Material itemType = player.getItemInHand().getType();
                int radius = (((level) * 2) + 4);
                final Block block = evt.getBlock();
                int x1, x2, x3, y1, y2, y3, z1, z2, z3;
                x1 = x2 = x3 = y1 = y2 = y3 = z1 = z2 = z3 = 1;
                int x, y, z;
                x = y = z = radius;
                int j = Utilities.getSimpleDirection(player);
                switch (j) {
                    case 3:
                        x3 = radius;
                        y3 = radius;
                        z1 = radius;
                        break;
                    case 1:
                        x3 = radius;
                        y3 = radius;
                        z2 = radius;
                        break;
                    case 2:
                        x1 = radius;
                        y3 = radius;
                        z3 = radius;
                        break;
                    case 4:
                        x2 = radius;
                        y3 = radius;
                        z3 = radius;
                        break;
                    case 6:
                        x3 = radius;
                        y1 = radius;
                        z3 = radius;
                        break;
                    case 5:
                        x3 = radius;
                        y2 = radius;
                        z3 = radius;
                        break;
                }
                for (int x01 = 0; x01 < x1; x01++) {
                    for (int x02 = x2; x02 > 0; x02--) {
                        for (int y01 = 0; y01 < y1; y01++) {
                            for (int y02 = y2; y02 > 0; y02--) {
                                for (int z01 = 0; z01 < z1; z01++) {
                                    for (int z02 = z2; z02 > 0; z02--) {
                                        for (int z03 = z3; z03 > 0; z03--) {
                                            for (int x03 = x3; x03 > 0; x03--) {
                                                for (int y03 = y3; y03 > 0; y03--) {
                                                    switch (j) {
                                                        case 3:
                                                            x = x03;
                                                            y = y03;
                                                            z = z01;
                                                            break;
                                                        case 1:
                                                            x = x03;
                                                            y = y03;
                                                            z = z02;
                                                            break;
                                                        case 2:
                                                            x = x01;
                                                            y = y03;
                                                            z = z03;
                                                            break;
                                                        case 4:
                                                            x = x02;
                                                            y = y03;
                                                            z = z03;
                                                            break;
                                                        case 6:
                                                            x = x03;
                                                            y = y01;
                                                            z = z03;
                                                            break;
                                                        case 5:
                                                            x = x03;
                                                            y = y02;
                                                            z = z03;
                                                            break;
                                                    }
                                                    x = level - x + 1;
                                                    y = level - y + 1;
                                                    z = level - z + 1;
                                                    int r = Storage.rnd.nextInt(2);
                                                    float limit;
                                                    if (original > 2) {
                                                        limit = (float) ((level) + 3 + r + 1.7);
                                                    } else if (level == 3) {
                                                        limit = (float) ((level) + 3 + r + 1);
                                                    } else {
                                                        limit = (float) ((level) + 3 + r);
                                                    }
                                                    int timer = (int) (counter / (l * 1.6));
                                                    if (Utilities.getEnchant(player.getItemInHand()).size() > 1 && Storage.item_drop_shred != 2) {
                                                        timer = 1;
                                                    }
                                                    if (block.getRelative(x, y, z).getLocation().distanceSquared(block.getLocation()) < limit) {
                                                        if (ArrayUtils.contains(mats, block.getRelative(x, y, z).getType())) {
                                                            final BlockBreakEvent event = new BlockBreakEvent(block.getRelative(x, y, z), player);
                                                            Bukkit.getServer().getPluginManager().callEvent(event);
                                                            if (!event.isCancelled()) {
                                                                counter++;
                                                                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        if (Storage.item_drop_shred == 1) {
                                                                            Material[] ores = new Material[]{COAL_ORE, REDSTONE_ORE, DIAMOND_ORE, GOLD_ORE, IRON_ORE, LAPIS_ORE, EMERALD_ORE, GLOWING_REDSTONE_ORE};
                                                                            if (ArrayUtils.contains(ores, event.getBlock().getType())) {
                                                                                event.getBlock().setType(STONE);
                                                                            } else if (event.getBlock().getType().equals(QUARTZ_ORE)) {
                                                                                event.getBlock().setType(NETHERRACK);
                                                                            }
                                                                        } else if (Storage.item_drop_shred == 2) {
                                                                            event.getBlock().setType(AIR);
                                                                        }
                                                                        if (event.getBlock().getType() == GRASS) {
                                                                            block.getLocation().getWorld().playSound(event.getBlock().getLocation(), Sound.DIG_GRASS, 10, 1);
                                                                        } else if (event.getBlock().getType() == DIRT || event.getBlock().getType() == GRAVEL || event.getBlock().getType() == CLAY) {
                                                                            block.getLocation().getWorld().playSound(event.getBlock().getLocation(), Sound.DIG_GRAVEL, 10, 1);
                                                                        } else if (event.getBlock().getType() == SAND) {
                                                                            block.getLocation().getWorld().playSound(event.getBlock().getLocation(), Sound.DIG_SAND, 10, 1);
                                                                        } else {
                                                                            block.getLocation().getWorld().playSound(event.getBlock().getLocation(), Sound.DIG_STONE, 10, 1);
                                                                        }
                                                                        if (ArrayUtils.contains(Storage.picks, itemType) || ArrayUtils.contains(shovel, event.getBlock().getType())) {
                                                                            if (event.getPlayer().getItemInHand().getEnchantments().containsKey(SILK_TOUCH)) {
                                                                                for (ItemStack st : Utilities.silktouchDrops(event.getBlock())) {
                                                                                    event.getPlayer().getWorld().dropItem(Utilities.getCenter(event.getBlock().getLocation()), st);
                                                                                }
                                                                                event.getBlock().setType(AIR);
                                                                            } else if (event.getPlayer().getItemInHand().getEnchantments().containsKey(LOOT_BONUS_BLOCKS)) {
                                                                                for (ItemStack st : Utilities.fortuneDrops(event.getPlayer().getItemInHand().getEnchantments().get(LOOT_BONUS_BLOCKS), event.getBlock())) {
                                                                                    event.getPlayer().getWorld().dropItem(Utilities.getCenter(event.getBlock().getLocation()), st);
                                                                                }
                                                                                event.getBlock().setType(AIR);
                                                                            } else {
                                                                                event.getBlock().breakNaturally();
                                                                            }
                                                                        }
                                                                    }
                                                                }, timer);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), (int) ((int) counter / (float) 4), evt.getPlayer());
                Utilities.eventEnd(evt.getPlayer(), loreName);
            }
        }
    }

    public static class Siphon extends Enchantment {

        public Siphon() {
            maxLevel = 4;
            loreName = "Siphon";
            chance = 0;
            enchantable = (Material[]) ArrayUtils.addAll(Storage.swords, Storage.bows);
            conflicting = new String[]{};
            description = "Drains the health of the mob that you attack, giving it to you";
        }

        @Override
        public void onHitting(EntityDamageByEntityEvent evt, int level) {
            if (!evt.isCancelled()) {
                if (evt.getDamager() instanceof Player) {
                    Player p = (Player) evt.getDamager();
                    LivingEntity ent = (LivingEntity) evt.getEntity();
                    int difference = level;
                    if (Storage.rnd.nextInt(4) == 2) {
                        while (difference > 0) {
                            if (p.getHealth() < 20) {
                                p.setHealth(p.getHealth() + 1);
                            }
                            if (ent.getHealth() > 2) {
                                ent.setHealth(ent.getHealth() - 1);
                            }
                            difference--;
                        }
                    }
                }
            }
        }

        @Override
        public void onEntityShootBow(EntityShootBowEvent evt, int level) {
            Arrow.ArrowEnchantSiphon arrow = new Arrow.ArrowEnchantSiphon(level);
            arrow.entity = (Projectile) evt.getProjectile();
            Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
        }
    }

    public static class Speed extends Enchantment {

        public Speed() {
            maxLevel = 4;
            loreName = "Speed";
            chance = 0;
            enchantable = Storage.boots;
            conflicting = new String[]{"Meador", "Weight"};
            description = "Gives the player a speed boost";
        }

        @Override
        public void onScan(Player player, int level) {
            player.setWalkSpeed((.05f * level) + .2f);
            player.setFlySpeed((.05f * level) + .2f);
            speed.add(player);
        }
    }

    public static class Stationary extends Enchantment {

        public Stationary() {
            maxLevel = 1;
            loreName = "Stationary";
            chance = 0;
            enchantable = (Material[]) ArrayUtils.addAll(Storage.swords, Storage.bows);
            conflicting = new String[]{};
            description = "Negates any knockback when attacking mobs, leaving them clueless as to who is attacking";
        }

        @Override
        public void onHitting(EntityDamageByEntityEvent evt, int level) {
            if (!evt.isCancelled()) {
                if (evt.getEntity() instanceof LivingEntity) {
                    LivingEntity ent = (LivingEntity) evt.getEntity();
                    if (evt.getDamage() < ent.getHealth()) {
                        evt.setCancelled(true);
                        Utilities.addUnbreaking(((Player) evt.getDamager()).getItemInHand(), 1, ((Player) evt.getDamager()));
                        ent.damage(evt.getDamage());
                    }
                }
            }
        }

        @Override
        public void onEntityShootBow(EntityShootBowEvent evt, int level) {
            Arrow.ArrowEnchantStationary arrow = new Arrow.ArrowEnchantStationary();
            arrow.entity = (Projectile) evt.getProjectile();
            Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
        }

    }

    public static class Switch extends Enchantment {

        public Switch() {
            maxLevel = 1;
            loreName = "Switch";
            chance = 0;
            enchantable = Storage.picks;
            conflicting = new String[]{"Shred", "Anthropomorphism", "Fire", "Extraction", "Pierce"};
            description = "Replaces the clicked block with the leftmost block in your inventory when sneaking";
        }

        @Override
        public void onBlockInteract(final PlayerInteractEvent evt, int level) {
            if (evt.getAction() == Action.RIGHT_CLICK_BLOCK && evt.getPlayer().isSneaking()) {
                BlockBreakEvent event = new BlockBreakEvent(evt.getClickedBlock(), evt.getPlayer());
                Bukkit.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return;
                }
                int ints[] = {6, 7, 23, 25, 29, 31, 32, 33, 37, 38, 39, 40, 49, 52, 54, 58, 61, 62, 63, 65, 68,
                    69, 77, 81, 83, 84, 90, 106, 107, 111, 119, 120, 130, 131, 143, 145, 146, 151, 158, 166, 166, 175, 178,
                    183, 184, 185, 186, 187, 323, 324, 330, 355, 397, 427, 428, 429, 430, 431};
                Material mat = AIR;
                byte bt = 0;
                int c = -1;
                for (int i = 0; i < 9; i++) {
                    if (evt.getPlayer().getInventory().getItem(i) != null) {
                        if (evt.getPlayer().getInventory().getItem(i).getType().isBlock() && !ArrayUtils.contains(ints, evt.getPlayer().getInventory().getItem(i).getType().getId())) {
                            mat = evt.getPlayer().getInventory().getItem(i).getType();
                            c = i;
                            bt = evt.getPlayer().getInventory().getItem(i).getData().getData();
                            break;
                        }
                    }
                }
                if (mat == HUGE_MUSHROOM_1 || mat == HUGE_MUSHROOM_2) {
                    bt = 14;
                }
                if (ArrayUtils.contains(ints, mat.getId()) || ArrayUtils.contains(ints, evt.getClickedBlock().getTypeId())) {
                    return;
                }
                if (!(mat == evt.getClickedBlock().getType() && evt.getClickedBlock().getData() == bt)) {
                    if ((!evt.getClickedBlock().isLiquid()) || evt.getClickedBlock().getType().isSolid()) {
                        Storage.grabbedBlocks.put(event.getBlock(), event.getPlayer().getLocation());
                        final Block block = event.getBlock();
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                            @Override
                            public void run() {
                                Storage.grabbedBlocks.remove(block);
                            }
                        }, 3);
                        evt.setCancelled(true);
                        if (event.getPlayer().getItemInHand().getEnchantments().containsKey(SILK_TOUCH)) {
                            for (ItemStack st : Utilities.silktouchDrops(event.getBlock())) {
                                event.getPlayer().getWorld().dropItem(Utilities.getCenter(event.getBlock().getLocation()), st);
                            }
                        } else if (event.getPlayer().getItemInHand().getEnchantments().containsKey(LOOT_BONUS_BLOCKS)) {
                            for (ItemStack st : Utilities.fortuneDrops(event.getPlayer().getItemInHand().getEnchantments().get(LOOT_BONUS_BLOCKS), event.getBlock())) {
                                event.getPlayer().getWorld().dropItem(Utilities.getCenter(event.getBlock().getLocation()), st);
                            }
                        } else {
                            event.getBlock().breakNaturally();
                        }
                        Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                        final Material m = mat;
                        final Byte b = bt;
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                            @Override
                            public void run() {
                                evt.getClickedBlock().setType(m);
                                evt.getClickedBlock().setData(b);
                            }
                        }, 1);
                        Utilities.removeItem(evt.getPlayer().getInventory(), evt.getPlayer().getInventory().getItem(c).getType(), (short) bt, 1);
                        evt.getPlayer().updateInventory();
                    }
                }
            }
        }
    }

    public static class Terraformer extends Enchantment {

        private void bk(Block blk, ArrayList<Block> bks, ArrayList<Block> total, int i) {
            i++;
            if (i > 16 || total.size() > 64) {
                return;
            }
            bks.add(blk);
            Location[] locs = new Location[]{blk.getRelative(-1, 0, 0).getLocation(), blk.getRelative(1, 0, 0).getLocation(),
                blk.getRelative(0, 0, -1).getLocation(), blk.getRelative(0, 0, 1).getLocation(), blk.getRelative(0, -1, 0).getLocation()};
            for (Location l : locs) {
                if (!bks.contains(l.getBlock()) && l.getBlock().getType().equals(AIR)) {
                    if (total.size() > 64) {
                        continue;
                    }
                    bk(l.getBlock(), bks, total, i);
                    if (l.distance(bks.get(0).getLocation()) < 6) {
                        total.add(l.getBlock());
                    }
                }
            }
        }

        public Terraformer() {
            maxLevel = 1;
            loreName = "Terraformer";
            chance = 0;
            enchantable = Storage.spades;
            conflicting = new String[]{};
            description = "Places the leftmost blocks in the players inventory within a 7 block radius";
        }

        @Override
        public void onBlockInteract(PlayerInteractEvent evt, int level) {
            if (evt.getPlayer().isSneaking()) {
                if (evt.getAction().equals(RIGHT_CLICK_BLOCK)) {
                    ArrayList<Block> used = new ArrayList<>();
                    ArrayList<Block> total = new ArrayList<>();
                    Location l = evt.getClickedBlock().getLocation();
                    if (evt.getClickedBlock().getRelative(0, 0, 1).getType() != AIR
                            && evt.getClickedBlock().getRelative(0, 0, -1).getType() != AIR
                            && evt.getClickedBlock().getRelative(-1, 0, 0).getType() != AIR
                            && evt.getClickedBlock().getRelative(1, 0, 0).getType() != AIR) {
                        l.setY(l.getY() + 1);
                    }
                    if (l.getBlock().getType().equals(AIR)) {
                        total.add(l.getBlock());
                    }
                    bk(l.getBlock(), used, total, 0);
                    int ints[] = {1, 2, 3, 4, 5, 12, 13, 14, 15, 16, 17, 18, 21, 24, 35, 43, 45, 46, 47, 48, 79, 80, 82, 87, 88, 99,
                        100, 110, 112, 121, 125, 129, 153, 155, 159, 161, 162, 165, 168, 172, 174, 179, 181};
                    Material mat = AIR;
                    byte bt = 0;
                    int c = -1;
                    for (int i = 0; i < 9; i++) {
                        if (evt.getPlayer().getInventory().getItem(i) != null) {
                            if (evt.getPlayer().getInventory().getItem(i).getType().isBlock() && ArrayUtils.contains(ints, evt.getPlayer().getInventory().getItem(i).getType().getId())) {
                                mat = evt.getPlayer().getInventory().getItem(i).getType();
                                c = i;
                                bt = evt.getPlayer().getInventory().getItem(i).getData().getData();
                                break;
                            }
                        }
                    }
                    if (mat == HUGE_MUSHROOM_1 || mat == HUGE_MUSHROOM_2) {
                        bt = 14;
                    }
                    for (Block b : total) {
                        BlockBreakEvent event = new BlockBreakEvent(b, evt.getPlayer());
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled() && event.getBlock().getType() == AIR) {
                            if (Utilities.removeItemCheck(evt.getPlayer().getInventory(), mat, bt, 1)) {
                                b.setType(mat);
                                b.setData(bt);
                                evt.getPlayer().updateInventory();
                                if (Storage.rnd.nextInt(10) == 5) {
                                    Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static class Tracer extends Enchantment {

        public Tracer() {
            maxLevel = 4;
            loreName = "Tracer";
            chance = 0;
            enchantable = Storage.bows;
            conflicting = new String[]{};
            description = "Guides the arrow to targets and then attacks";
        }

        @Override
        public void onEntityShootBow(EntityShootBowEvent evt, int level) {
            Arrow.ArrowEnchantTracer arrow = new Arrow.ArrowEnchantTracer(level);
            arrow.entity = (Projectile) evt.getProjectile();
            Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
        }

    }

    public static class Transformation extends Enchantment {

        private final EntityType[] entityTypes = new EntityType[]{PIG_ZOMBIE, PIG, VILLAGER, WITCH, COW, MUSHROOM_COW, SLIME, MAGMA_CUBE, CHICKEN, SKELETON, OCELOT, WOLF};

        //wisconsin is cold in chicago
        public Transformation() {
            maxLevel = 3;
            loreName = "Transformation";
            chance = 0;
            enchantable = Storage.swords;
            conflicting = new String[]{};
            description = "Occasionally causes the attacked mob to be transformed into its similar cousin";
        }

        @Override
        public void onHitting(EntityDamageByEntityEvent evt, int level) {
            if (evt.getEntity() instanceof LivingEntity) {
                if (Storage.rnd.nextInt(100) > (100 - (level * 5))) {
                    int position = ArrayUtils.indexOf(entityTypes, evt.getEntity().getType());
                    if (position != -1) {
                        if (evt.getDamage() > ((LivingEntity) evt.getEntity()).getHealth()) {
                            evt.setCancelled(true);
                        }
                        int newPosition = position + 1 - 2 * (position % 2);
                        evt.getEntity().getLocation().getWorld().spigot().playEffect(Utilities.getCenter(evt.getEntity().getLocation()), Effect.CLOUD, 0, 1, .5f, 2f, .5f, .1f, 70, 16);
                        evt.getEntity().remove();
                        ((Player) evt.getDamager()).getWorld().spawnEntity(evt.getEntity().getLocation(), entityTypes[newPosition]);
                    }
                }
            }
        }
    }

    public static class Variety extends Enchantment {

        ItemStack[] logs = new ItemStack[]{new ItemStack(LOG, 1, (short) 0), new ItemStack(LOG, 1, (short) 1), new ItemStack(LOG, 1, (short) 2), new ItemStack(LOG, 1, (short) 3), new ItemStack(LOG_2, 1, (short) 0), new ItemStack(LOG_2, 1, (short) 1)};
        ItemStack[] leaves = new ItemStack[]{new ItemStack(LEAVES, 1, (short) 0), new ItemStack(LEAVES, 1, (short) 1), new ItemStack(LEAVES, 1, (short) 2), new ItemStack(LEAVES, 1, (short) 3), new ItemStack(LEAVES_2, 1, (short) 0), new ItemStack(LEAVES_2, 1, (short) 1)};

        public Variety() {
            maxLevel = 1;
            loreName = "Variety";
            chance = 0;
            enchantable = Storage.axes;
            conflicting = new String[]{"Fire"};
            description = "Drops random types of wood or leaves";
        }

        @Override
        public void onBlockBreak(BlockBreakEvent evt, int level) {
            if (evt.getBlock().getType() == LOG || evt.getBlock().getType() == LOG_2) {
                evt.setCancelled(true);
                evt.getBlock().setType(AIR);
                evt.getBlock().getWorld().dropItemNaturally(Utilities.getCenter(evt.getBlock().getLocation()), logs[Storage.rnd.nextInt(6)]);
                Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
            } else if (evt.getBlock().getType() == LEAVES || evt.getBlock().getType() == LEAVES_2) {
                evt.setCancelled(true);
                evt.getBlock().setType(AIR);
                evt.getBlock().getWorld().dropItemNaturally(Utilities.getCenter(evt.getBlock().getLocation()), leaves[Storage.rnd.nextInt(6)]);
                Utilities.addUnbreaking(evt.getPlayer().getItemInHand(), 1, evt.getPlayer());
            }
        }
    }

    public static class Vortex extends Enchantment {

        public Vortex() {
            maxLevel = 1;
            loreName = "Vortex";
            chance = 0;
            enchantable = (Material[]) ArrayUtils.addAll(Storage.bows, Storage.swords);
            conflicting = new String[]{};
            description = "Teleports mob loot and XP directly to the player";
        }

        @Override
        public void onEntityKill(final EntityDeathEvent evt, int level) {
            Storage.vortexLocs.put(evt.getEntity().getLocation().getBlock(), evt.getEntity().getKiller().getLocation());
            int i = evt.getDroppedExp();
            evt.setDroppedExp(0);
            evt.getEntity().getKiller().giveExp(i);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
                @Override
                public void run() {
                    Storage.vortexLocs.remove(evt.getEntity().getLocation().getBlock());
                }
            }, 3);
        }

        @Override
        public void onEntityShootBow(EntityShootBowEvent evt, int level) {
            Arrow.ArrowEnchantVortex arrow = new Arrow.ArrowEnchantVortex();
            arrow.entity = (Projectile) evt.getProjectile();
            Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
        }

    }

    public static class Weight extends Enchantment {

        public Weight() {
            maxLevel = 4;
            loreName = "Weight";
            chance = 0;
            enchantable = Storage.boots;
            conflicting = new String[]{"Meador", "Speed"};
            description = "Slows the player down but makes them stronger and more resistant to knockback";
        }

        @Override
        public void onBeingHit(EntityDamageByEntityEvent evt, int level) {
            if (!evt.isCancelled()) {
                if (evt.getEntity() instanceof Player) {
                    Player p = (Player) evt.getEntity();
                    if (evt.getDamage() < p.getHealth()) {
                        evt.setCancelled(true);
                        p.damage(evt.getDamage());
                        p.setVelocity(p.getLocation().subtract(evt.getDamager().getLocation()).toVector().multiply((float) (1 / (level + 1.5))));
                        ItemStack[] s = p.getInventory().getArmorContents();
                        for (int i = 0; i < 4; i++) {
                            if (s[i] != null) {
                                Utilities.addUnbreaking(s[i], 1);
                                if (s[i].getDurability() > s[i].getType().getMaxDurability()) {
                                    s[i] = null;
                                }
                            }
                        }
                        p.getInventory().setArmorContents(s);
                    }
                }
            }
        }

        @Override
        public void onScan(Player player, int level) {
            player.setWalkSpeed(.164f - (level * .014f));
            speed.add(player);
            player.removePotionEffect(INCREASE_DAMAGE);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 610, level));
        }
    }

//In-Development
//OP-Enchantments
    public static class Apocalypse extends Enchantment {

        public Apocalypse() {
            maxLevel = 1;
            loreName = "Apocalypse";
            chance = 0;
            enchantable = Storage.bows;
            conflicting = new String[]{};
            description = "Unleashes hell";
        }

        @Override
        public void onEntityShootBow(EntityShootBowEvent evt, int level) {
            Arrow.ArrowAdminApocalypse arrow = new Arrow.ArrowAdminApocalypse();
            arrow.entity = (Projectile) evt.getProjectile();
            Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
        }
    }

    public static class Ethereal extends Enchantment {

        public Ethereal() {
            maxLevel = 1;
            loreName = "Ethereal";
            chance = 0;
            enchantable = (Material[]) ArrayUtils.addAll(Storage.axes, ArrayUtils.addAll(Storage.boots, ArrayUtils.addAll(Storage.bows, ArrayUtils.addAll(Storage.chestplates, ArrayUtils.addAll(Storage.helmets, ArrayUtils.addAll(Storage.hoes, ArrayUtils.addAll(Storage.leggings, ArrayUtils.addAll(Storage.lighters, ArrayUtils.addAll(Storage.picks, ArrayUtils.addAll(Storage.rods, ArrayUtils.addAll(Storage.shears, ArrayUtils.addAll(Storage.spades, Storage.swords))))))))))));
            conflicting = new String[]{};
            description = "Prevents tools from breaking";
        }

        @Override
        public void onScanHand(Player player, int level) {
            player.getItemInHand().setDurability((short) 0);
        }

        @Override
        public void onScan(Player player, int level) {
            for (ItemStack s : player.getInventory().getArmorContents()) {
                if (s != null) {
                    HashMap<Enchantment, Integer> map = Utilities.getEnchant(s);
                    if (map.containsKey(Enchantment.Ethereal.this)) {
                        s.setDurability((short) 0);
                    }
                }
            }
        }
    }

    public static class FireWalker extends Enchantment {

        public FireWalker() {
            maxLevel = 1;
            loreName = "Fire Walker";
            chance = 0;
            enchantable = Storage.boots;
            conflicting = new String[]{"Water Walker"};
            description = "Allows the player to slowly but safely walk on lava";
        }

        @Override
        public void onScan(Player player, int level) {
            if (player.isSneaking() && player.getLocation().getBlock().getType() == STATIONARY_LAVA && !player.isFlying()) {
                player.setVelocity(player.getVelocity().setY(.4));
            }
            Block block = (Block) player.getLocation().getBlock();
            int radius = 2;
            for (int x = -(radius); x <= radius; x++) {
                for (int z = -(radius); z <= radius; z++) {
                    if (block.getRelative(x, -1, z).getLocation().distanceSquared(block.getLocation()) < 8) {
                        if (Storage.fireLocs.containsKey(block.getRelative(x, -1, z).getLocation())) {
                            Storage.fireLocs.put(block.getRelative(x, -1, z).getLocation(), System.nanoTime());
                        }
                        if (block.getRelative(x, -1, z).getType() == STATIONARY_LAVA && block.getRelative(x, 0, z).getType() == AIR) {
                            block.getRelative(x, -1, z).setType(OBSIDIAN);
                            Storage.fireLocs.put(block.getRelative(x, -1, z).getLocation(), System.nanoTime());
                        }
                    }
                }
            }
        }
    }

    public static class Missile extends Enchantment {

        public Missile() {
            maxLevel = 1;
            loreName = "Missile";
            chance = 0;
            enchantable = Storage.bows;
            conflicting = new String[]{};
            description = "Shoots a missile from the bow";
        }

        @Override
        public void onEntityShootBow(EntityShootBowEvent evt, int level) {
            Arrow.ArrowAdminMissile arrow = new Arrow.ArrowAdminMissile();
            arrow.entity = (Projectile) evt.getProjectile();
            Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
            evt.setCancelled(true);
            Utilities.addUnbreaking(((Player) evt.getEntity()).getItemInHand(), 1, (Player) evt.getEntity());
            Utilities.removeItem(((Player) evt.getEntity()).getInventory(), Material.ARROW, 1);
        }
    }

    public static class WaterWalker extends Enchantment {

        public WaterWalker() {
            maxLevel = 1;
            loreName = "Water Walker";
            chance = 0;
            enchantable = Storage.boots;
            conflicting = new String[]{"Fire Walker"};
            description = "Allows the player to walk on water and safely emerge from it when sneaking";
        }

        @Override
        public void onScan(Player player, int level) {
            if (player.isSneaking() && player.getLocation().getBlock().getType() == STATIONARY_WATER && !player.isFlying()) {
                player.setVelocity(player.getVelocity().setY(.4));
            }
            Block block = (Block) player.getLocation().getBlock();
            int radius = 2;
            for (int x = -(radius); x <= radius; x++) {
                for (int z = -(radius); z <= radius; z++) {
                    if (block.getRelative(x, -1, z).getLocation().distanceSquared(block.getLocation()) < 8) {
                        if (Storage.waterLocs.containsKey(block.getRelative(x, -1, z).getLocation())) {
                            Storage.waterLocs.put(block.getRelative(x, -1, z).getLocation(), System.nanoTime());
                        }
                        if (block.getRelative(x, -1, z).getType() == STATIONARY_WATER && block.getRelative(x, 0, z).getType() == AIR) {
                            block.getRelative(x, -1, z).setType(PACKED_ICE);
                            Storage.waterLocs.put(block.getRelative(x, -1, z).getLocation(), System.nanoTime());
                        }
                    }
                }
            }
        }
    }
}
