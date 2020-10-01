package zedly.zenchantments;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import zedly.zenchantments.enchantments.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Material.*;
import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

// This contains extraneous watcher methods that are not relevant to arrows or enchantments
public class Watcher implements Listener {
    // Fires a laser effect from dispensers if a tool with the Laser enchantment is dispensed
    @EventHandler
    public void onBlockDispense(BlockDispenseEvent event) {
        Config config = Config.get(event.getBlock().getWorld());

        if (event.getBlock().getType() != DISPENSER) {
            return;
        }

        ItemStack item = event.getItem();
        Laser laser = null;

        for (CustomEnchantment enchantment : config.getEnchants()) {
            if (enchantment.getClass().equals(Laser.class)) {
                laser = (Laser) enchantment;
            }
        }

        if (laser == null) {
            return;
        }

        if (!CustomEnchantment.getEnchants(item, config.getWorld()).containsKey(laser) || item.getType().equals(ENCHANTED_BOOK)) {
            return;
        }

        event.setCancelled(true);

        int level = CustomEnchantment.getEnchants(item, config.getWorld()).get(laser);
        int range = 6 + (int) Math.round(level * laser.power * 3);

        Block block = event.getBlock();
        Block relativeBlock = block.getRelative(((Directional) block.getState().getData()).getFacing(), range);

        Location play = Utilities.getCenter(block);
        Location target = Utilities.getCenter(relativeBlock);

        play.setY(play.getY() - 0.5);
        target.setY(target.getY() + 0.5);
        play.setY(play.getY() + 1.1);

        double d = target.distance(play);

        for (int i = 0; i < (int) d * 10; i++) {
            Location location = target.clone();
            location.setX(play.getX() + (i * ((target.getX() - play.getX()) / (d * 10))));
            location.setY(play.getY() + (i * ((target.getY() - play.getY()) / (d * 10))));
            location.setZ(play.getZ() + (i * ((target.getZ() - play.getZ()) / (d * 10))));
            location.getWorld().spawnParticle(Particle.REDSTONE, location, 1, new Particle.DustOptions(Color.RED, 0.75f));

            for (Entity entity : play.getWorld().getEntities()) {
                if (entity.getLocation().distance(location) < 0.75 && entity instanceof LivingEntity) {
                    int damageAmount = 1 + (level * 2);
                    EntityDamageEvent damageEvent = new EntityDamageEvent(entity, DamageCause.FIRE, damageAmount);
                    Bukkit.getPluginManager().callEvent(damageEvent);
                    entity.setLastDamageCause(damageEvent);

                    if (!event.isCancelled()) {
                        ((LivingEntity) entity).damage(damageAmount);
                    }
                }
            }
        }
    }

    // Prevents falling block entities from Anthropomorphism from becoming solid blocks or disappearing
    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (!(event.getEntity() instanceof FallingBlock)) {
            return;
        }

        FallingBlock entity = (FallingBlock) event.getEntity();
        if (Anthropomorphism.idleBlocks.containsKey(entity) || Anthropomorphism.attackBlocks.containsKey(entity)) {
            event.setCancelled(true);
        }
    }

    // Prevents mobs affected by Rainbow Slam from being hurt by generic "FALL" event. Damage is instead dealt via an
    // EDBEe in order to make protections and money drops work
    @EventHandler
    public void onEntityFall(EntityDamageEvent event) {
        if (event.getCause() == DamageCause.FALL && RainbowSlam.rainbowSlamNoFallEntities.contains(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    // Teleports item stacks to a certain location as they are created from breaking a block or killing an entity if
    //      a Grab or Vortex enchantment was used
    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        if (Fire.cancelledItemDrops.contains(event.getLocation().getBlock())) {
            event.setCancelled(true);
            return;
        }

        Location loc = event.getEntity().getLocation();

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
            for (Block block : Grab.grabLocs.keySet()) {
                if (block.getLocation().getBlockX() != loc.getBlockX()
                    || block.getLocation().getBlockY() != loc.getBlockY()
                    || block.getLocation().getBlockZ() != loc.getBlockZ()
                ) {
                    continue;
                }

                event.getEntity().teleport(Grab.grabLocs.get(block));
                event.getEntity().setPickupDelay(0);

                for (Entity entity : event.getEntity().getNearbyEntities(1, 1, 1)) {
                    if (entity instanceof ExperienceOrb) {
                        Storage.COMPATIBILITY_ADAPTER.collectXP(Grab.grabLocs.get(block), ((ExperienceOrb) entity).getExperience());
                        entity.remove();
                    }
                }
            }

            for (Block block : Vortex.vortexLocs.keySet()) {
                if (!block.getLocation().getWorld().equals(loc.getWorld())) {
                    continue;
                }

                if (!(block.getLocation().distance(loc) < 2)) {
                    continue;
                }

                event.getEntity().teleport(Vortex.vortexLocs.get(block));
                event.getEntity().setPickupDelay(0);

                for (Entity e : event.getEntity().getNearbyEntities(1, 1, 1)) {
                    if (e instanceof ExperienceOrb) {
                        Storage.COMPATIBILITY_ADAPTER.collectXP(Grab.grabLocs.get(block), ((ExperienceOrb) e).getExperience());
                        e.remove();
                    }
                }
            }
        }, 1);
    }

    // Prevents players from harvesting materials from the Water Walker and Fire Walker trails
    @EventHandler
    public void onIceOrLavaBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        if (FrozenStep.frozenLocs.containsKey(location) || NetherStep.netherstepLocs.containsKey(location)) {
            event.setCancelled(true);
        }
    }

    // Makes glowing shulkers on an ore block disappear if it is uncovered
    @EventHandler
    public void onOreUncover(BlockBreakEvent event) {
        for (BlockFace face : Storage.CARDINAL_BLOCK_FACES) {
            if (!Reveal.glowingBlocks.containsKey(event.getBlock().getRelative(face))) {
                continue;
            }

            int entityId = 2000000000 + (event.getBlock().getRelative(face).hashCode()) % 10000000;
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getWorld().equals(event.getBlock().getWorld())) {
                    Storage.COMPATIBILITY_ADAPTER.hideShulker(entityId, player);
                }
            }

            Reveal.glowingBlocks.remove(event.getBlock());
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/enchant ")) {
            Player player = event.getPlayer();
            PlayerInventory inventory = player.getInventory();
            boolean customEnch = !CustomEnchantment.getEnchants(
                inventory.getItemInMainHand(),
                player.getWorld()
            ).isEmpty();

            Bukkit.getScheduler().scheduleSyncDelayedTask(
                Storage.zenchantments,
                () -> CustomEnchantment.setGlow(inventory.getItemInMainHand(), customEnch, player.getWorld()),
                0
            );
        }
    }

    // Randomly adds CustomEnchantments to an item based off the overall probability, enchantments' relative
    //      probability, and the level at which the item is being enchanted if the player has permission
    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        if (!event.getEnchanter().hasPermission("zenchantments.enchant.get")) {
            return;
        }

        if (event.getItem().getType() == FISHING_ROD && event.getExpLevelCost() <= 4) {
            return;
        }

        Config config = Config.get(event.getEnchantBlock().getWorld());
        Map<CustomEnchantment, Integer> existingEnchants = CustomEnchantment.getEnchants(
            event.getItem(),
            event.getEnchantBlock().getWorld()
        );
        Map<CustomEnchantment, Integer> addedEnchants = new HashMap<>();
        ItemStack itemStack = event.getItem();

        for (int i = 1; i <= config.getMaxEnchants() - existingEnchants.size(); i++) {
            float totalChance = 0;
            List<CustomEnchantment> mainPool = new ArrayList<>(config.getEnchants());
            Set<CustomEnchantment> validPool = new HashSet<>();

            Collections.shuffle(mainPool);

            for (CustomEnchantment enchantment : mainPool) {
                boolean conflicts = false;
                for (CustomEnchantment addedEnchant : addedEnchants.keySet()) {
                    if (ArrayUtils.contains(enchantment.conflicting, addedEnchant.getClass())
                        || addedEnchants.containsKey(enchantment)
                        || addedEnchant.probability <= 0
                    ) {
                        conflicts = true;
                        break;
                    }
                }

                if (!conflicts
                    && (event.getItem().getType().equals(BOOK) || enchantment.validMaterial(event.getItem().getType()))
                ) {
                    validPool.add(enchantment);
                    totalChance += enchantment.probability;
                }
            }

            double decision = (ThreadLocalRandom.current().nextFloat() * totalChance) / Math.pow(config.getEnchantRarity(), i);
            float running = 0;
            for (CustomEnchantment ench : validPool) {
                running += ench.probability;
                if (running > decision) {
                    int level = Utilities.getEnchantLevel(ench.maxLevel, event.getExpLevelCost());
                    addedEnchants.put(ench, level);
                    break;
                }
            }
        }

        for (Map.Entry<CustomEnchantment, Integer> entry : addedEnchants.entrySet()) {
            entry.getKey().setEnchantment(itemStack, entry.getValue(), config.getWorld());
        }

        if (event.getItem().getType().equals(ENCHANTED_BOOK)) {
            List<String> finalLore = itemStack.getItemMeta().getLore();
            Inventory inv = event.getInventory();
            Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
                ItemStack book = inv.getItem(0);
                ItemMeta bookMeta = book.getItemMeta();
                bookMeta.setLore(finalLore);
                book.setItemMeta(bookMeta);
                inv.setItem(0, book);
            }, 0);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(
            Storage.zenchantments,
            () -> CustomEnchantment.setGlow(itemStack, !addedEnchants.isEmpty(), config.getWorld()),
            0
        );
    }

    // Removes certain potion effects given by enchantments when the enchanted items are removed
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getSlotType().equals(SlotType.ARMOR)) {
            return;
        }

        ItemStack item = event.getCurrentItem();

        if (item == null) {
            return;
        }

        if (!item.hasItemMeta()) {
            return;
        }

        if (!item.getItemMeta().hasLore()) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        for (CustomEnchantment enchantment : CustomEnchantment.getEnchants(event.getCurrentItem(), player.getWorld()).keySet()) {
            if (enchantment.getClass().equals(Jump.class) || enchantment.getClass().equals(Meador.class)) {
                player.removePotionEffect(PotionEffectType.JUMP);
            }

            if (enchantment.getClass().equals(NightVision.class)) {
                player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            }

            if (enchantment.getClass().equals(Weight.class)) {
                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
            }
        }
    }

    // Prevents players from being able to eat if they are stored within the 'hungerPlayers' set in Storage
    @EventHandler
    public void onEat(PlayerInteractEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand().getType().isEdible()
            && (event.getAction().equals(RIGHT_CLICK_AIR) || event.getAction().equals(RIGHT_CLICK_BLOCK))
            && Toxic.hungerPlayers.containsKey(event.getPlayer())
        ) {
            event.setCancelled(true);
        }
    }

    // Prevents arrows with the 'ze.arrow' metadata from being able to be picked up by removing them
    @EventHandler
    public void onArrowPickup(PlayerPickupArrowEvent event) {
        Item item = event.getItem();
        if (item.hasMetadata("ze.arrow")) {
            item.remove();
            event.setCancelled(true);
        }
    }
}