package zedly.zenchantments;

import java.util.HashSet;
import java.util.LinkedList;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import static org.bukkit.Material.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import static org.bukkit.enchantments.Enchantment.DURABILITY;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import particles.ParticleEffect;

public class Watcher implements Listener {

    @EventHandler
    public void onLaserDispense(BlockDispenseEvent evt) {
        if (!Storage.laser_in_dispensers) {
            return;
        }
        if (evt.getBlock().getType() == DISPENSER) {
            ItemStack stk = (ItemStack) evt.getItem();
            if (stk != null) {
                if (!Storage.originalEnchantClasses.containsKey("Laser")) {
                    return;
                }
                Enchantment ench = (Enchantment) Storage.originalEnchantClasses.get("Laser");
                if (Utilities.getEnchants(stk).containsKey(ench)) {
                    evt.setCancelled(true);
                    BlockState state = evt.getBlock().getState();
                    String str = "" + state.getData();
                    Block blk = evt.getBlock();
                    int level = Utilities.getEnchants(stk).get(ench);
                    int range = 6 + (level * 3);
                    if (str.contains("UP")) {
                        blk = evt.getBlock().getRelative(BlockFace.UP, range);
                    } else if (str.contains("DOWN")) {
                        blk = evt.getBlock().getRelative(BlockFace.DOWN, range);
                    } else if (str.contains("SOUTH")) {
                        blk = evt.getBlock().getRelative(BlockFace.SOUTH, range);
                    } else if (str.contains("NORTH")) {
                        blk = evt.getBlock().getRelative(BlockFace.NORTH, range);
                    } else if (str.contains("WEST")) {
                        blk = evt.getBlock().getRelative(BlockFace.WEST, range);
                    } else if (str.contains("EAST")) {
                        blk = evt.getBlock().getRelative(BlockFace.EAST, range);
                    }
                    Location play = Utilities.getCenter(evt.getBlock().getLocation());
                    Location target = Utilities.getCenter(blk.getLocation());
                    play.setY(play.getY() - .5);
                    target.setY(target.getY() + .5);
                    Location c = play;
                    c.setY(c.getY() + 1.1);
                    double d = target.distance(c);
                    for (int i = 0; i < (int) d * 5; i++) {
                        Location tempLoc = target.clone();
                        tempLoc.setX(c.getX() + (i * ((target.getX() - c.getX()) / (d * 5))));
                        tempLoc.setY(c.getY() + (i * ((target.getY() - c.getY()) / (d * 5))));
                        tempLoc.setZ(c.getZ() + (i * ((target.getZ() - c.getZ()) / (d * 5))));
                        ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(255, 0, 0), tempLoc, 32);
                        for (Entity ent : Bukkit.getWorld(play.getWorld().getName()).getEntities()) {
                            if (ent.getLocation().distance(tempLoc) < .75) {
                                if (ent instanceof LivingEntity) {
                                    EntityDamageEvent event = new EntityDamageEvent(ent, EntityDamageEvent.DamageCause.FIRE, (double) (1 + (level * 2)));
                                    Bukkit.getPluginManager().callEvent(event);
                                    LivingEntity theE = (LivingEntity) event.getEntity();
                                    theE.setLastDamageCause(event);
                                    if (!event.isCancelled()) {
                                        theE.damage((double) (1 + (level * 2)));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent evt) {
        if ((evt.getEntityType() == EntityType.FALLING_BLOCK)) {
            if (Storage.anthMobs.containsKey((FallingBlock) evt.getEntity()) || Storage.anthMobs2.contains((FallingBlock) evt.getEntity())) {
                evt.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent evt) {
        if (Storage.reset_speed_on_login) {
            evt.getPlayer().setWalkSpeed(.2f);
            evt.getPlayer().setFlySpeed(.1f);
        }
    }

    @EventHandler
    public void onItemSpawn(final ItemSpawnEvent evt) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
            @Override
            public void run() {
                for (Block block : Storage.grabbedBlocks.keySet()) {
                    Location loc = evt.getEntity().getLocation();
                    for (Entity e : evt.getEntity().getNearbyEntities(1, 1, 1)) {
                        if (e instanceof ExperienceOrb) {
                            e.teleport(Storage.grabbedBlocks.get(block));
                        }
                    }
                    if (block.getLocation().getBlockX() == loc.getBlockX() && block.getLocation().getBlockY() == loc.getBlockY() && block.getLocation().getBlockZ() == loc.getBlockZ()) {
                        evt.getEntity().teleport(Storage.grabbedBlocks.get(block));
                        evt.getEntity().setPickupDelay(0);
                    }
                }
                for (Block block : Storage.vortexLocs.keySet()) {
                    Location loc = evt.getEntity().getLocation();
                    if (block.getLocation().getWorld().equals(loc.getWorld())) {
                        for (Entity e : evt.getEntity().getNearbyEntities(1, 1, 1)) {
                            if (e instanceof ExperienceOrb) {
                                e.teleport(Storage.vortexLocs.get(block));
                            }
                        }
                        if (block.getLocation().distance(loc) < 2) {
                            evt.getEntity().teleport(Storage.vortexLocs.get(block));
                            evt.getEntity().setPickupDelay(0);
                        }
                    }
                }
            }
        }, 1);
    }

    @EventHandler
    public void onIceOrLavaBreak(BlockBreakEvent evt) {
        if (Storage.waterLocs.containsKey(evt.getBlock().getLocation()) || Storage.fireLocs.containsKey(evt.getBlock().getLocation())) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent evt) {
        int max = Math.min(Storage.max_enchants_per_item, 3);
        if (!evt.getEnchanter().hasPermission("zenchantments.enchant.get")) {
            return;
        }
        if (evt.getItem().getType() == FISHING_ROD && evt.getExpLevelCost() <= 4) {
            return;
        }
        HashSet<Enchantment> enchAdd = new HashSet<>();
        for (int l = 1; l <= max; l++) {
            float totalChance = 0;
            HashSet<Enchantment> enchs = new HashSet<>();
            for (Enchantment ench : Storage.enchantClasses.values()) {
                boolean b = true;
                for (Enchantment e : enchAdd) {
                    if (ArrayUtils.contains(ench.conflicting, Storage.originalEnchantClassesReverse.get(e)) || enchAdd.contains(ench)) {
                        b = false;
                    }
                }
                if (b && (ArrayUtils.contains(ench.enchantable, evt.getItem().getType()) || evt.getItem().getType().equals(BOOK))) {
                    enchs.add(ench);
                    totalChance += ench.chance;
                }
            }
            double decision = (Storage.rnd.nextFloat() * totalChance) / Math.pow(Storage.enchantRarity, l);
            float running = 0;
            for (Enchantment ench : enchs) {
                running += ench.chance;
                if (running > decision) {
                    ItemMeta meta = evt.getItem().getItemMeta();
                    LinkedList<String> lore = new LinkedList<>();
                    if (meta.hasLore()) {
                        lore.addAll(meta.getLore());
                    }
                    String level = Utilities.getRomanString(Utilities.getEnchantLevel(ench.maxLevel, evt.getExpLevelCost()));
                    lore.add(ChatColor.GRAY + ench.loreName + " " + level);
                    meta.setLore(lore);
                    if (evt.getItem().getType().equals(BOOK)) {
                        evt.getEnchantsToAdd().clear();
                        int i;
                        if (evt.getExpLevelCost() > 20) {
                            i = 3;
                        } else if (evt.getExpLevelCost() > 10) {
                            i = 2;
                        } else {
                            i = 1;
                        }
                        evt.getEnchantsToAdd().put(DURABILITY, i);
                    }
                    evt.getItem().setItemMeta(meta);
                    enchAdd.add(ench);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent evt) {
        if (evt.getInventory() != null) {
            if (evt.getSlotType().equals(SlotType.ARMOR)) {
                if (evt.getCurrentItem().hasItemMeta()) {
                    if (evt.getCurrentItem().getItemMeta().hasLore()) {
                        Player player = (Player) evt.getWhoClicked();
                        for (Enchantment e : Utilities.getEnchants(evt.getCurrentItem()).keySet()) {
                            String realName = Storage.originalEnchantClassesReverse.get(e);
                            if (realName.equals("Jump") || realName.equals("Meador")) {
                                player.removePotionEffect(PotionEffectType.JUMP);
                            }
                            if (realName.equals("Night Vision")) {
                                player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                            }
                            if (realName.equals("Weight")) {
                                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                            }
                        }
                    }
                }
            }
        }
    }
}
