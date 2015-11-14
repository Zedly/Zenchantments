package zedly.zenchantments;

import java.util.HashSet;
import java.util.LinkedList;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import static org.bukkit.Material.*;
import org.bukkit.block.Block;
import static org.bukkit.enchantments.Enchantment.DURABILITY;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dispenser;
import org.bukkit.potion.PotionEffectType;
import particles.ParticleEffect;

public class Watcher implements Listener {

    @EventHandler
    public void onLaserDispense(BlockDispenseEvent evt) {
        Config config = Config.get(evt.getBlock().getWorld());
        if (evt.getBlock().getType() == DISPENSER) {
            ItemStack stk = evt.getItem();
            if (stk != null) {
                CustomEnchantment ench = null;
                for (CustomEnchantment e : config.getEnchants().values()) {
                    if (e.getClass().equals(CustomEnchantment.Laser.class)) {
                        ench = e;
                    }
                }
                if (ench == null) {
                    return;
                }
                evt.setCancelled(true);
                int level = config.getEnchants(stk).get(ench);
                int range = 6 + (level * 3);
                Block blk = evt.getBlock().getRelative(((Dispenser) evt.getBlock().getState().getData()).getFacing(), range);
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
                                ent.setLastDamageCause(event);
                                if (!event.isCancelled()) {
                                    ((LivingEntity) ent).damage((double) (1 + (level * 2)));
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

    @EventHandler
    public void onItemSpawn(final ItemSpawnEvent evt) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, new Runnable() {
            @Override
            public void run() {
                for (Block block : Storage.grabLocs.keySet()) {
                    Location loc = evt.getEntity().getLocation();
                    for (Entity e : evt.getEntity().getNearbyEntities(1, 1, 1)) {
                        if (e instanceof ExperienceOrb) {
                            e.teleport(Storage.grabLocs.get(block));
                        }
                    }
                    if (block.getLocation().getBlockX() == loc.getBlockX() && block.getLocation().getBlockY() == loc.getBlockY() && block.getLocation().getBlockZ() == loc.getBlockZ()) {
                        evt.getEntity().teleport(Storage.grabLocs.get(block));
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
        Config config = Config.get(evt.getEnchantBlock().getWorld());
        int max = Math.min(config.getMaxEnchants(), 3);
        if (!evt.getEnchanter().hasPermission("zenchantments.enchant.get")) {
            return;
        }
        if (evt.getItem().getType() == FISHING_ROD && evt.getExpLevelCost() <= 4) {
            return;
        }
        HashSet<CustomEnchantment> enchAdd = new HashSet<>();
        ItemMeta meta = evt.getItem().getItemMeta();
        LinkedList<String> lore = new LinkedList<>();
        if (meta.hasLore()) {
            lore.addAll(meta.getLore());
        }
        for (int l = 1; l <= max; l++) {
            float totalChance = 0;
            HashSet<CustomEnchantment> enchs = new HashSet<>();
            for (CustomEnchantment ench : config.getEnchants().values()) {
                boolean b = true;
                for (CustomEnchantment e : enchAdd) {
                    if (ArrayUtils.contains(ench.conflicting, e.getClass()) || enchAdd.contains(ench) || e.chance <= 0) {
                        b = false;
                    }
                }
                if (b && (ArrayUtils.contains(ench.enchantable, evt.getItem().getType()) || evt.getItem().getType().equals(BOOK))) {
                    enchs.add(ench);
                    totalChance += ench.chance;
                }
            }
            double decision = (Storage.rnd.nextFloat() * totalChance) / Math.pow(config.getEnchantRarity(), l);
            float running = 0;
            for (CustomEnchantment ench : enchs) {
                running += ench.chance;
                if (running > decision) {
                    String level = Utilities.getRomanString(Utilities.getEnchantLevel(ench.maxLevel, evt.getExpLevelCost()));
                    lore.add(ChatColor.GRAY + ench.loreName + " " + level);
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
                    enchAdd.add(ench);
                    break;
                }
            }
        }
        meta.setLore(lore);
        evt.getItem().setItemMeta(meta);
        ItemStack toSet = config.descriptionLore() ? config.addDescriptions(evt.getItem().clone(), null) : evt.getItem();
        evt.getItem().setItemMeta(toSet.getItemMeta());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent evt) {
        if (evt.getInventory() != null) {
            if (evt.getSlotType().equals(SlotType.ARMOR)) {
                if (evt.getCurrentItem().hasItemMeta()) {
                    if (evt.getCurrentItem().getItemMeta().hasLore()) {
                        Player player = (Player) evt.getWhoClicked();
                        for (CustomEnchantment e : Config.get(evt.getWhoClicked().getWorld()).getEnchants(evt.getCurrentItem()).keySet()) {
                            if (e.getClass().equals(CustomEnchantment.Jump.class) || e.getClass().equals(CustomEnchantment.Meador.class)) {
                                player.removePotionEffect(PotionEffectType.JUMP);
                            }
                            if (e.getClass().equals(CustomEnchantment.NightVision.class)) {
                                player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                            }
                            if (e.getClass().equals(CustomEnchantment.Weight.class)) {
                                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onArrowCraft(CraftItemEvent evt) {
        Config config = Config.get(evt.getWhoClicked().getWorld());
        if (evt.getRecipe().getResult().hasItemMeta()) {
            if (evt.getRecipe().getResult().getItemMeta().hasLore()) {
                if (config.getArrows().keySet().contains(evt.getRecipe().getResult().getItemMeta().getLore().get(0))) {
                    if (!evt.getWhoClicked().hasPermission("zenchantments.arrow.get")) {
                        evt.setCancelled(true);
                    }
                } else {
                    evt.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onEat(PlayerInteractEvent evt) {
        if (evt.getPlayer().getItemInHand().getType().isEdible() && (evt.getAction().equals(RIGHT_CLICK_AIR)
                || evt.getAction().equals(RIGHT_CLICK_BLOCK)) && Storage.hungerPlayers.keySet().contains(evt.getPlayer())) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void ArrowPickup(PlayerPickupItemEvent evt) {
        if (evt.getItem().hasMetadata("ze.arrow")) {
            evt.getItem().remove();
            evt.setCancelled(true);
        }
    }
}
