package zedly.zenchantments.event.listener;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
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
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.configuration.WorldConfiguration;
import zedly.zenchantments.enchantments.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Material.*;
import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public class GeneralListener implements Listener {
    private final ZenchantmentsPlugin plugin;

    public GeneralListener(@NotNull ZenchantmentsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockDispense(@NotNull BlockDispenseEvent event) {
        World world = event.getBlock().getWorld();
        WorldConfiguration config = this.plugin.getWorldConfigurationProvider().getConfigurationForWorld(world);

        if (event.getBlock().getType() != DISPENSER) {
            return;
        }

        ItemStack item = event.getItem();
        Laser laser = null;

        for (Zenchantment enchantment : config.getEnchants()) {
            if (enchantment.getClass().equals(Laser.class)) {
                laser = (Laser) enchantment;
            }
        }

        if (laser == null) {
            return;
        }

        if (!Zenchantment.getEnchants(item, world).containsKey(laser) || item.getType() == ENCHANTED_BOOK) {
            return;
        }

        event.setCancelled(true);

        int level = Zenchantment.getEnchants(item, world).get(laser);
        int range = 6 + (int) Math.round(level * laser.getPower() * 3);

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
                    this.plugin.getServer().getPluginManager().callEvent(damageEvent);
                    entity.setLastDamageCause(damageEvent);

                    if (!event.isCancelled()) {
                        ((LivingEntity) entity).damage(damageAmount);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityChangeBlock(@NotNull EntityChangeBlockEvent event) {
        if (!(event.getEntity() instanceof FallingBlock)) {
            return;
        }

        FallingBlock entity = (FallingBlock) event.getEntity();
        if (Anthropomorphism.IDLE_BLOCKS.containsKey(entity) || Anthropomorphism.ATTACK_BLOCKS.containsKey(entity)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityFall(@NotNull EntityDamageEvent event) {
        if (event.getCause() == DamageCause.FALL && RainbowSlam.rainbowSlamNoFallEntities.contains(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemSpawn(@NotNull ItemSpawnEvent event) {
        if (Fire.cancelledItemDrops.contains(event.getLocation().getBlock())) {
            event.setCancelled(true);
            return;
        }

        Location location = event.getEntity().getLocation();

        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            for (Block block : Grab.grabLocs.keySet()) {
                if (block.getLocation().getBlockX() != location.getBlockX()
                    || block.getLocation().getBlockY() != location.getBlockY()
                    || block.getLocation().getBlockZ() != location.getBlockZ()
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
                if (!block.getLocation().getWorld().equals(location.getWorld())) {
                    continue;
                }

                if (!(block.getLocation().distance(location) < 2)) {
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

    @EventHandler
    public void onIceOrLavaBreak(@NotNull BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        if (FrozenStep.frozenLocs.containsKey(location) || NetherStep.netherstepLocs.containsKey(location)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onOreUncover(@NotNull BlockBreakEvent event) {
        for (BlockFace face : Storage.CARDINAL_BLOCK_FACES) {
            if (!Reveal.glowingBlocks.containsKey(event.getBlock().getRelative(face))) {
                continue;
            }

            int entityId = 2000000000 + (event.getBlock().getRelative(face).hashCode()) % 10000000;
            for (Player player : this.plugin.getServer().getOnlinePlayers()) {
                if (player.getWorld().equals(event.getBlock().getWorld())) {
                    Storage.COMPATIBILITY_ADAPTER.hideShulker(entityId, player);
                }
            }

            Reveal.glowingBlocks.remove(event.getBlock());
        }
    }

    @EventHandler
    public void onCommand(@NotNull PlayerCommandPreprocessEvent event) {
        if (!event.getMessage().startsWith("/enchant ")) {
            return;
        }

        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        boolean customEnch = !Zenchantment.getEnchants(
            inventory.getItemInMainHand(),
            player.getWorld()
        ).isEmpty();

        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(
            this.plugin,
            () -> Zenchantment.setGlow(inventory.getItemInMainHand(), customEnch, player.getWorld()),
            0
        );
    }

    @EventHandler
    public void onEnchantItem(@NotNull EnchantItemEvent event) {
        if (!event.getEnchanter().hasPermission("zenchantments.enchant.get")) {
            return;
        }

        if (event.getItem().getType() == FISHING_ROD && event.getExpLevelCost() <= 4) {
            return;
        }

        ItemStack item = event.getItem();
        World world = event.getEnchantBlock().getWorld();
        WorldConfiguration config = this.plugin.getWorldConfigurationProvider().getConfigurationForWorld(world);
        Map<Zenchantment, Integer> existingEnchants = Zenchantment.getEnchants(item, world);
        Map<Zenchantment, Integer> addedEnchants = new HashMap<>();

        for (int i = 1; i <= config.getMaxEnchants() - existingEnchants.size(); i++) {
            float totalChance = 0;
            List<Zenchantment> mainPool = new ArrayList<>(config.getEnchants());
            Set<Zenchantment> validPool = new HashSet<>();

            Collections.shuffle(mainPool);

            for (Zenchantment enchantment : mainPool) {
                boolean conflicts = false;
                for (Zenchantment addedEnchant : addedEnchants.keySet()) {
                    if (enchantment.getConflicting().contains(addedEnchant.getClass())
                        || addedEnchants.containsKey(enchantment)
                        || addedEnchant.getProbability() <= 0
                    ) {
                        conflicts = true;
                        break;
                    }
                }

                if (!conflicts
                    && (event.getItem().getType() == BOOK || enchantment.validMaterial(item.getType()))
                ) {
                    validPool.add(enchantment);
                    totalChance += enchantment.getProbability();
                }
            }

            double decision = (ThreadLocalRandom.current().nextFloat() * totalChance) / Math.pow(config.getEnchantRarity(), i);
            float running = 0;
            for (Zenchantment zenchantment : validPool) {
                running += zenchantment.getProbability();
                if (running > decision) {
                    int level = Utilities.getEnchantLevel(zenchantment.getMaxLevel(), event.getExpLevelCost());
                    addedEnchants.put(zenchantment, level);
                    break;
                }
            }
        }

        for (Map.Entry<Zenchantment, Integer> entry : addedEnchants.entrySet()) {
            entry.getKey().setEnchantment(item, entry.getValue(), world);
        }

        if (event.getItem().getType() == ENCHANTED_BOOK) {
            List<String> finalLore = item.getItemMeta().getLore();
            Inventory inventory = event.getInventory();
            this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
                ItemStack book = inventory.getItem(0);
                ItemMeta bookMeta = book.getItemMeta();
                bookMeta.setLore(finalLore);
                book.setItemMeta(bookMeta);
                inventory.setItem(0, book);
            }, 0);
        }

        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(
            this.plugin,
            () -> Zenchantment.setGlow(item, !addedEnchants.isEmpty(), world),
            0
        );
    }

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (event.getSlotType() != SlotType.ARMOR) {
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
        for (Zenchantment enchantment : Zenchantment.getEnchants(event.getCurrentItem(), player.getWorld()).keySet()) {
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

    @EventHandler
    public void onEat(@NotNull PlayerInteractEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand().getType().isEdible()
            && (event.getAction() == RIGHT_CLICK_AIR || event.getAction() == RIGHT_CLICK_BLOCK)
            && Toxic.hungerPlayers.containsKey(event.getPlayer())
        ) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onArrowPickup(@NotNull PlayerPickupArrowEvent event) {
        Item item = event.getItem();
        if (item.hasMetadata("ze.arrow")) {
            item.remove();
            event.setCancelled(true);
        }
    }
}