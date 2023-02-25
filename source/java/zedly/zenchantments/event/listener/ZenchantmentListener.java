package zedly.zenchantments.event.listener;

import org.bukkit.Bukkit;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.enchantments.Bind;
import zedly.zenchantments.event.BlockShredEvent;
import zedly.zenchantments.player.PlayerDataProvider;
import zedly.zenchantments.task.EffectTask;
import zedly.zenchantments.task.Frequency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.bukkit.Material.AIR;
import static org.bukkit.entity.EntityType.*;
import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.PROJECTILE;
import static org.bukkit.inventory.EquipmentSlot.*;
import static org.bukkit.potion.PotionEffectType.FAST_DIGGING;
import static zedly.zenchantments.Tool.*;

public final class ZenchantmentListener implements Listener {
    private static final EquipmentSlot[] ARMOR_AND_HELD_SLOTS = new EquipmentSlot[]{HEAD, EquipmentSlot.CHEST, LEGS, FEET, HAND, OFF_HAND};
    private static final HighFrequencyRunnableCache CACHE = new HighFrequencyRunnableCache(
        ZenchantmentsPlugin.getInstance(),
        ZenchantmentListener::feedEnchCache,
        5
    );

    private static final EntityType[] ENTITY_INTERACT_BAD_ENTITIES = {HORSE, ARMOR_STAND, ITEM_FRAME, VILLAGER};

    private final ZenchantmentsPlugin plugin;

    public ZenchantmentListener(final @NotNull ZenchantmentsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onBlockBreak(final @NotNull BlockBreakEvent event) {
        if (event.isCancelled() || event instanceof BlockShredEvent || event.getBlock().getType() == AIR) {
            return;
        }

        final Player player = event.getPlayer();

        this.applyZenchantmentForTool(
            player,
            EquipmentSlot.HAND,
            (ench, level, slot) -> ench.onBlockBreak(event, level, slot)
        );
    }

    @EventHandler
    public void onBlockPlace(final @NotNull BlockPlaceEvent event) {
        if (event.isCancelled() || event.getBlock().getType() == AIR) {
            return;
        }

        final Player player = event.getPlayer();

        this.applyZenchantmentForTool(
            player,
            event.getHand(),
            (ench, level, slot) -> ench.onBlockPlace(event, level, slot)
        );
        this.applyZenchantmentForTool(
            player,
            event.getHand() == HAND ? OFF_HAND : HAND,
            (ench, level, slot) -> ench.onBlockPlaceOtherHand(event, level, slot)
        );
    }


    @EventHandler
    private void onBlockInteract(final @NotNull PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL
            && (event.getClickedBlock() == null
            || !MaterialList.INTERACTABLE_BLOCKS.contains(event.getClickedBlock().getType()))
            && event.getHand() == HAND
        ) {
            final Player player = event.getPlayer();
            applyZenchantmentForArmorAndHeldItems(player, (ench, level, slot) -> ench.onBlockInteract(event, level, slot));
        }
    }

    @EventHandler
    private void onBlockInteractInteractable(final @NotNull PlayerInteractEvent event) {
        if (event.getClickedBlock() == null
            || MaterialList.INTERACTABLE_BLOCKS.contains(event.getClickedBlock().getType())
            && event.getHand() == HAND
        ) {
            final Player player = event.getPlayer();
            applyZenchantmentForArmorAndHeldItems(player, (ench, level, slot) -> ench.onBlockInteractInteractable(event, level, slot));
        }
    }

    @EventHandler
    private void onEntityInteract(final @NotNull PlayerInteractEntityEvent event) {
        if (event.getHand() == OFF_HAND || ArrayUtils.contains(ENTITY_INTERACT_BAD_ENTITIES, event.getRightClicked().getType())) {
            return;
        }

        final Player player = event.getPlayer();

        this.applyZenchantmentForTool(
            player,
            HAND,
            (ench, level, slot) -> ench.onEntityInteract(event, level, slot)
        );
    }

    @EventHandler
    private void onEntityKill(final @NotNull EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) {
            return;
        }

        final Player player = event.getEntity().getKiller();
        final PlayerInventory inventory = player.getInventory();
        final EquipmentSlot usedHand = event.getEntity().getLastDamageCause().getCause() == PROJECTILE
            && Tool.fromItemStack(inventory.getItemInOffHand()) == BOW
            && Tool.fromItemStack(inventory.getItemInMainHand()) != BOW
            ? EquipmentSlot.OFF_HAND
            : EquipmentSlot.HAND;

        this.applyZenchantmentForTool(
            player,
            usedHand,
            (ench, level, slot) -> ench.onEntityKill(event, level, slot)
        );
    }

    @EventHandler
    private void onEntityHit(final @NotNull EntityDamageByEntityEvent event) {
        if (event.getDamage() <= 0) {
            return;
        }

        if (event.getDamager() instanceof Player) {
            final Player player = (Player) event.getDamager();

            if (event.getEntity() instanceof LivingEntity) {
                applyZenchantmentForArmorAndHeldItems(player, (ench, level, slot) -> ench.onEntityHit(event, level, slot));
            }
        }

        if (event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();
            // Only check main hand for some reason.
            applyZenchantmentForArmorAndHeldItems(player, (ench, level, slot) -> ench.onBeingHit(event, level, slot));
        }
    }

    @EventHandler
    private void onEntityDamage(final @NotNull EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity();
        applyZenchantmentForArmorAndHeldItems(player, (ench, level, slot) -> ench.onEntityDamage(event, level, slot));
    }

    @EventHandler
    private void onPlayerFish(final @NotNull PlayerFishEvent event) {
        final Player player = event.getPlayer();
        final PlayerInventory inventory = player.getInventory();
        final Tool main = Tool.fromItemStack(inventory.getItemInMainHand());
        final Tool off = Tool.fromItemStack(inventory.getItemInOffHand());
        final EquipmentSlot usedHand = main != Tool.ROD && off == Tool.ROD ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND;

        this.applyZenchantmentForTool(
            player,
            usedHand,
            (ench, level, slot) -> ench.onPlayerFish(event, level, slot)
        );
    }

    @EventHandler
    private void onHungerChange(final @NotNull FoodLevelChangeEvent event) {
        if (event.isCancelled() || !(event.getEntity() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity();
        applyZenchantmentForArmorAndHeldItems(player, (ench, level, slot) -> ench.onHungerChange(event, level, slot));
    }

    @EventHandler
    private void onPlayerRespawn(PlayerRespawnEvent event) {
        Player p = event.getPlayer();
        if (p.hasMetadata("ze.force-inv-reload")) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(ZenchantmentsPlugin.getInstance(), () -> p.updateInventory(), 1);
            p.removeMetadata("ze.force-inv-reload", ZenchantmentsPlugin.getInstance());
        }
    }

    @EventHandler
    private void onShear(final @NotNull PlayerShearEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();
        final PlayerInventory inventory = player.getInventory();
        final Tool main = Tool.fromItemStack(inventory.getItemInMainHand());
        final Tool off = Tool.fromItemStack(inventory.getItemInOffHand());
        final EquipmentSlot usedHand = main != Tool.SHEAR && off == Tool.SHEAR ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND;

        this.applyZenchantmentForTool(
            player,
            usedHand,
            (ench, level, slot) -> ench.onShear(event, level, slot)
        );
    }

    @EventHandler
    private void onEntityShootBow(final @NotNull EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity();
        final PlayerInventory inventory = player.getInventory();
        final Tool main = Tool.fromItemStack(inventory.getItemInMainHand());
        final Tool off = Tool.fromItemStack(inventory.getItemInOffHand());
        final EquipmentSlot usedHand = main != BOW && off == BOW ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND;

        this.applyZenchantmentForTool(
            player,
            usedHand,
            (ench, level, slot) -> ench.onEntityShootBow(event, level, slot)
        );
    }

    @EventHandler
    private void onPotionSplash(final @NotNull PotionSplashEvent event) {
        for (final LivingEntity entity : event.getAffectedEntities()) {
            if (!(entity instanceof Player)) {
                continue;
            }

            final Player player = (Player) entity;
            final AtomicBoolean apply = new AtomicBoolean(true);

            applyZenchantmentForArmorAndHeldItems(player, (ench, level, slot) -> apply.get() && apply.compareAndSet(ench.onPotionSplash(event, level, slot), false));
        }
    }

    @EventHandler
    private void onProjectileLaunch(final @NotNull ProjectileLaunchEvent event) {
        final Entity shooter = (Entity) event.getEntity().getShooter();

        if (shooter == null || !(shooter instanceof Player)) {
            return;
        }

        final Player player = (Player) shooter;
        final PlayerInventory inventory = player.getInventory();
        final Tool main = Tool.fromItemStack(inventory.getItemInMainHand());
        final Tool off = Tool.fromItemStack(inventory.getItemInOffHand());
        final EquipmentSlot usedHand = main != BOW && main != Tool.ROD && (off == BOW || off == Tool.ROD) ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND;

        this.applyZenchantmentForTool(
            player,
            usedHand,
            (ench, level, slot) -> ench.onProjectileLaunch(event, level, slot)
        );
    }

    @EventHandler
    private void onDeath(final @NotNull PlayerDeathEvent event) {
        if (event.getKeepInventory()) {
            return;
        }
        final Player player = event.getEntity();
        final ItemStack[] contents = player.getInventory().getContents();
        // Contents retains position of Bind items in player's inventory

        final List<ItemStack> removed = new ArrayList<>();

        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null &&
                Zenchantment.getZenchantmentsOnItemStack(
                    contents[i],
                    ZenchantmentsPlugin.getInstance().getWorldConfigurationProvider().getConfigurationForWorld(player.getWorld())
                ).keySet().stream().anyMatch(e -> e instanceof Bind) // TODO: Make this search more efficient
            ) {
                removed.add(contents[i]);
                event.getDrops().remove(contents[i]);
            } else {
                contents[i] = null;
            }
        }

        ZenchantmentsPlugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(ZenchantmentsPlugin.getInstance(), () -> {
            player.getInventory().setContents(contents);
            player.setMetadata("ze.force-inv-reload", new FixedMetadataValue(plugin, "Yes"));
        }, 1);
    }

    @EventHandler
    private void onCombust(final @NotNull EntityCombustByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity();
        applyZenchantmentForArmorAndHeldItems(player, (ench, level, slot) -> ench.onCombust(event, level, slot));
    }

    private void applyZenchantmentForArmorAndHeldItems(Player player, final @NotNull EnchantmentFunction action) {
        final PlayerInventory inventory = player.getInventory();
        for (EquipmentSlot usedSlot : ARMOR_AND_HELD_SLOTS) {
            this.applyZenchantmentForTool(
                player,
                usedSlot,
                action
            );
        }
    }

    private void applyZenchantmentForTool(
        final @NotNull Player player,
        final @NotNull EquipmentSlot slot,
        final @NotNull EnchantmentFunction action
    ) {
        Zenchantment.applyForTool(
            player,
            this.plugin.getWorldConfigurationProvider(),
            slot,
            action
        );
    }

    // Fast Scan of Player's Armor and their hand to register enchantments
    @EffectTask(Frequency.HIGH)
    public static void scanPlayers() {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            PlayerDataProvider.getDataForPlayer(player).tick();
        }

        // Sweeping scan over the player list for armor enchants
        CACHE.run();
    }

    // Implicitly scheduled MEDIUM_HIGH due to being called by HighFrequencyEnchCache with interval 5
    private static void feedEnchCache(
        final @NotNull ZenchantmentsPlugin plugin,
        final @NotNull Player player,
        final @NotNull Consumer<Supplier<Boolean>> consumer
    ) {
        final PlayerInventory inventory = player.getInventory();
        for (final EquipmentSlot usedHand : ARMOR_AND_HELD_SLOTS) {
            ItemStack itemStack = inventory.getItem(usedHand);
            // Rather than being an ItemStack of air, armor contents are null if empty.
            // We technically don't need to test this for the hand/off hand items,
            // but a NullPointerException would occur here otherwise.
            if (itemStack == null) {
                continue;
            }

            Zenchantment.applyForTool(
                player,
                plugin.getWorldConfigurationProvider(),
                usedHand,
                (ench, level, slot) -> {
                    consumer.accept(() -> {
                        if (!player.isOnline()) {
                            return false;
                        }

                        if (ench.onFastScan(player, level, slot)) {
                            PlayerDataProvider.getDataForPlayer(player)
                                .setCooldown(ench.getKey(), ench.getCooldown());
                        }

                        return true;
                    });

                    return ench.onScan(player, level, slot);
                }
            );
        }

        final long currentTime = System.currentTimeMillis();
        if (player.hasMetadata("ze.speed") && (player.getMetadata("ze.speed").get(0).asLong() < currentTime - 1000)) {
            player.removeMetadata("ze.speed", plugin);
            player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
            player.setFlySpeed(0.1F);
            player.setWalkSpeed(0.2F);
        }

        if (player.hasMetadata("ze.haste") && (player.getMetadata("ze.haste").get(0).asLong() < currentTime - 1000)) {
            player.removePotionEffect(FAST_DIGGING);
            player.removeMetadata("ze.haste", plugin);
        }
    }
}
