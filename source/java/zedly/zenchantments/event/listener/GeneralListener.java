package zedly.zenchantments.event.listener;

import net.minecraft.util.Tuple;
import org.bukkit.*;
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
import org.bukkit.event.entity.EntitySpawnEvent;
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
import zedly.zenchantments.*;
import zedly.zenchantments.configuration.WorldConfiguration;
import zedly.zenchantments.enchantments.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.requireNonNull;
import static org.bukkit.Material.*;
import static org.bukkit.entity.EntityType.EXPERIENCE_ORB;
import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public class GeneralListener implements Listener {
    private final ZenchantmentsPlugin plugin;

    public GeneralListener(final @NotNull ZenchantmentsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockDispense(final @NotNull BlockDispenseEvent event) {
        if (event.getBlock().getType() != DISPENSER) {
            return;
        }

        final World world = event.getBlock().getWorld();
        final WorldConfiguration config = ZenchantmentsPlugin.getInstance().getWorldConfigurationProvider().getConfigurationForWorld(world);
        final ItemStack item = event.getItem();
        Laser laser = (Laser) config.getZenchantmentFromName("Laser");

        if (laser == null) {
            return;
        }

        if (
            !Zenchantment.getZenchantmentsOnItemStack(
                item,
                config
            ).containsKey(laser) || item.getType() == ENCHANTED_BOOK
        ) {
            return;
        }

        event.setCancelled(true);

        final int level = Zenchantment.getZenchantmentsOnItemStack(item, config).get(laser);
        final int range = 6 + (int) Math.round(level * laser.getPower() * 3);

        final Block block = event.getBlock();
        final Block relativeBlock = block.getRelative(((Directional) block.getBlockData()).getFacing(), range);

        final Location play = Utilities.getCenter(block);
        final Location target = Utilities.getCenter(relativeBlock);

        play.setY(play.getY() - 0.5);
        target.setY(target.getY() + 0.5);
        play.setY(play.getY() + 1.1);

        final double distance = target.distance(play);

        for (int i = 0; i < (int) distance * 10; i++) {
            final Location location = target.clone();
            location.setX(play.getX() + (i * ((target.getX() - play.getX()) / (distance * 10))));
            location.setY(play.getY() + (i * ((target.getY() - play.getY()) / (distance * 10))));
            location.setZ(play.getZ() + (i * ((target.getZ() - play.getZ()) / (distance * 10))));
            requireNonNull(location.getWorld()).spawnParticle(Particle.REDSTONE, location, 1, new Particle.DustOptions(Color.RED, 0.75f));

            for (final Entity entity : requireNonNull(play.getWorld()).getEntities()) {
                if (entity.getLocation().distance(location) >= 0.75 || !(entity instanceof LivingEntity)) {
                    continue;
                }

                final int damageAmount = 1 + (level * 2);
                final EntityDamageEvent damageEvent = new EntityDamageEvent(entity, DamageCause.FIRE, damageAmount);
                this.plugin.getServer().getPluginManager().callEvent(damageEvent);
                entity.setLastDamageCause(damageEvent);

                if (!event.isCancelled()) {
                    ((LivingEntity) entity).damage(damageAmount);
                }
            }
        }
    }

    @EventHandler
    public void onEntityChangeBlock(final @NotNull EntityChangeBlockEvent event) {
        if (!(event.getEntity() instanceof FallingBlock)) {
            return;
        }

        final FallingBlock entity = (FallingBlock) event.getEntity();
        if (Anthropomorphism.IDLE_BLOCKS.containsKey(entity) || Anthropomorphism.ATTACK_BLOCKS.containsKey(entity)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityFall(final @NotNull EntityDamageEvent event) {
        if (event.getCause() == DamageCause.FALL && RainbowSlam.RAINBOW_SLAM_ENTITIES.contains(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemSpawn(final @NotNull ItemSpawnEvent event) {
        Block b = event.getLocation().getBlock();
        final Location location = event.getEntity().getLocation();

        AtomicInteger itemsDropped;
        if ((itemsDropped = Fire.ITEM_DROP_REPLACEMENTS.get(b)) != null) {
            ItemStack is = event.getEntity().getItemStack();
            Tuple<Material, Double> product;
            if ((product = MaterialList.FIRE_SMELT_MAP.get(is.getType())) != null) {
                itemsDropped.addAndGet(is.getAmount());
                is.setType(product.a());
                event.getEntity().setItemStack(is);
                Utilities.displayParticle(Utilities.getCenter(b), Particle.FLAME, 10, 0.1f, 0.5f, 0.5f, 0.5f);

                int experience = product.b().intValue();
                double remainder = product.b() - experience;
                if (ThreadLocalRandom.current().nextDouble() >= remainder) {
                    experience++;
                }
                if (experience > 0) {
                    final ExperienceOrb experienceOrb = (ExperienceOrb) event.getLocation().getWorld().spawnEntity(
                        event.getLocation(),
                        EXPERIENCE_ORB
                    );
                    experienceOrb.setExperience(experience);
                }
            }
        }

        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            for (final Block block : Grab.GRAB_LOCATIONS.keySet()) {
                if (block.getLocation().getBlockX() != location.getBlockX()
                    || block.getLocation().getBlockY() != location.getBlockY()
                    || block.getLocation().getBlockZ() != location.getBlockZ()
                ) {
                    continue;
                }

                event.getEntity().teleport(Grab.GRAB_LOCATIONS.get(block));
                event.getEntity().setPickupDelay(0);

                for (final Entity entity : event.getEntity().getNearbyEntities(1, 1, 1)) {
                    if (entity instanceof ExperienceOrb orb) {
                        CompatibilityAdapter.instance()
                            .collectExp(Grab.GRAB_LOCATIONS.get(block), orb.getExperience());
                        entity.remove();
                    }
                }
            }

            for (final Block block : Vortex.VORTEX_LOCATIONS.keySet()) {
                if (!block.getLocation().getWorld().equals(location.getWorld())) {
                    continue;
                }

                if (block.getLocation().distance(location) >= 2) {
                    continue;
                }

                event.getEntity().teleport(Vortex.VORTEX_LOCATIONS.get(block));
                event.getEntity().setPickupDelay(0);

                for (final Entity entity : event.getEntity().getNearbyEntities(1, 1, 1)) {
                    if (entity instanceof ExperienceOrb orb) {
                        CompatibilityAdapter.instance()
                            .collectExp(Vortex.VORTEX_LOCATIONS.get(block), orb.getExperience());
                        entity.remove();
                    }
                }
            }
        }, 1);
    }

    @EventHandler
    public void onIceOrLavaBreak(final @NotNull BlockBreakEvent event) {
        final Location location = event.getBlock().getLocation();
        if (NetherStep.NETHERSTEP_LOCATIONS.containsKey(location)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onOreUncover(final @NotNull BlockBreakEvent event) {
        for (final BlockFace face : Utilities.CARDINAL_BLOCK_FACES) {
            if (!Reveal.GLOWING_BLOCKS.containsKey(event.getBlock().getRelative(face))) {
                continue;
            }

            final int entityId = 2000000000 + event.getBlock().getRelative(face).hashCode() % 10000000;
            for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
                if (player.getWorld().equals(event.getBlock().getWorld())) {
                    CompatibilityAdapter.instance().hideFakeEntity(entityId, player);
                }
            }

            Reveal.GLOWING_BLOCKS.remove(event.getBlock());
        }
    }

    @EventHandler
    public void onCommand(final @NotNull PlayerCommandPreprocessEvent event) {
        if (!event.getMessage().startsWith("/enchant ")) {
            return;
        }

        final Player player = event.getPlayer();
        final PlayerInventory inventory = player.getInventory();
        final WorldConfiguration worldConfiguration = this.plugin
            .getWorldConfigurationProvider()
            .getConfigurationForWorld(player.getWorld());
        final boolean zenchantment = !Zenchantment.getZenchantmentsOnItemStack(
            inventory.getItemInMainHand(),
            worldConfiguration
        ).isEmpty();

        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(
            this.plugin,
            () -> Zenchantment.updateEnchantmentGlowForItemStack(inventory.getItemInMainHand(), zenchantment, worldConfiguration),
            0
        );
    }

    @EventHandler
    public void onEnchantItem(final @NotNull EnchantItemEvent event) {
        if (!event.getEnchanter().hasPermission("zenchantments.enchant.get")) {
            return;
        }

        if (event.getItem().getType() == FISHING_ROD && event.getExpLevelCost() <= 4) {
            return;
        }

        final ItemStack item = event.getItem();
        final World world = event.getEnchantBlock().getWorld();
        final WorldConfiguration worldConfiguration = this.plugin.getWorldConfigurationProvider().getConfigurationForWorld(world);
        final Map<Zenchantment, Integer> existingEnchants = Zenchantment.getZenchantmentsOnItemStack(
            item,
            worldConfiguration
        );
        final Map<Zenchantment, Integer> addedEnchants = new HashMap<>();

        for (int i = 1; i <= worldConfiguration.getMaxZenchantments() - existingEnchants.size(); i++) {
            float totalChance = 0;
            final List<Zenchantment> mainPool = new ArrayList<>(worldConfiguration.getZenchantments());
            final Set<Zenchantment> validPool = new HashSet<>();

            Collections.shuffle(mainPool);

            for (final Zenchantment enchantment : mainPool) {
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

                if (!conflicts && (event.getItem().getType() == BOOK || enchantment.isValidMaterial(item.getType()))) {
                    validPool.add(enchantment);
                    totalChance += enchantment.getProbability();
                }
            }

            float running = 0;
            final double decision = (ThreadLocalRandom.current().nextFloat() * totalChance) / Math.pow(worldConfiguration.getZenchantmentRarity(), i);
            for (Zenchantment zenchantment : validPool) {
                running += zenchantment.getProbability();
                if (running > decision) {
                    final int level = Utilities.getEnchantmentLevel(zenchantment.getMaxLevel(), event.getExpLevelCost());
                    addedEnchants.put(zenchantment, level);
                    break;
                }
            }
        }

        for (final Map.Entry<Zenchantment, Integer> entry : addedEnchants.entrySet()) {
            entry.getKey().setForItemStack(item, entry.getValue(), worldConfiguration);
        }

        if (event.getItem().getType() == ENCHANTED_BOOK) {
            final List<String> finalLore = item.getItemMeta().getLore();
            final Inventory inventory = event.getInventory();
            this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
                final ItemStack book = inventory.getItem(0);
                final ItemMeta bookMeta = book.getItemMeta();
                bookMeta.setLore(finalLore);
                book.setItemMeta(bookMeta);
                inventory.setItem(0, book);
            }, 0);
        }

        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(
            this.plugin,
            () -> Zenchantment.updateEnchantmentGlowForItemStack(item, !addedEnchants.isEmpty(), worldConfiguration),
            0
        );
    }

    @EventHandler
    public void onInventoryClick(final @NotNull InventoryClickEvent event) {
        if (event.getSlotType() != SlotType.ARMOR) {
            return;
        }

        final ItemStack item = event.getCurrentItem();

        if (item == null) {
            return;
        }

        if (!item.hasItemMeta()) {
            return;
        }

        if (!item.getItemMeta().hasLore()) {
            return;
        }

        final Player player = (Player) event.getWhoClicked();
        final WorldConfiguration config = this.plugin.getWorldConfigurationProvider().getConfigurationForWorld(player.getWorld());
        final Set<Zenchantment> zenchantments = Zenchantment.getZenchantmentsOnItemStack(
            event.getCurrentItem(),
            config
        ).keySet();

        for (final Zenchantment zenchantment : zenchantments) {
            if (zenchantment.getClass() == Jump.class || zenchantment.getClass() == Meador.class) {
                player.removePotionEffect(PotionEffectType.JUMP);
            }

            if (zenchantment.getClass() == NightVision.class) {
                player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            }

            if (zenchantment.getClass() == Weight.class) {
                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
            }
        }
    }

    @EventHandler
    public void onEat(final @NotNull PlayerInteractEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand().getType().isEdible()
            && (event.getAction() == RIGHT_CLICK_AIR || event.getAction() == RIGHT_CLICK_BLOCK)
            && Toxic.HUNGER_PLAYERS.containsKey(event.getPlayer())
        ) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onArrowPickup(final @NotNull PlayerPickupArrowEvent event) {
        final Item item = event.getItem();
        if (item.hasMetadata("ze.arrow")) {
            item.remove();
            event.setCancelled(true);
        }
    }
}
