package zedly.zenchantments.event.listener;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.event.BlockShredEvent;
import zedly.zenchantments.player.PlayerData;
import zedly.zenchantments.task.EffectTask;
import zedly.zenchantments.task.Frequency;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.bukkit.Material.AIR;
import static org.bukkit.entity.EntityType.*;
import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.PROJECTILE;
import static org.bukkit.inventory.EquipmentSlot.HAND;
import static org.bukkit.potion.PotionEffectType.FAST_DIGGING;
import static zedly.zenchantments.Tool.BOW;

public final class ZenchantmentListener implements Listener {
    private static final HighFrequencyRunnableCache CACHE = new HighFrequencyRunnableCache(ZenchantmentListener::feedEnchCache, 5);

    private static final EntityType[] ENTITY_INTERACT_BAD_ENTITIES = { HORSE, ARMOR_STAND, ITEM_FRAME, VILLAGER };

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
        final boolean usedHand = Utilities.isMainHand(HAND);

        this.applyZenchantmentForTool(
            player,
            Utilities.getUsedItemStack(player, usedHand),
            (ench, level) -> ench.onBlockBreak(event, level, usedHand)
        );
    }


    @EventHandler
    private void onBlockInteract(final @NotNull PlayerInteractEvent event) {
        if (event.getClickedBlock() == null
            || !MaterialList.INTERACTABLE_BLOCKS.contains(event.getClickedBlock().getType())
        ) {
            final Player player = event.getPlayer();
            final boolean isMainHand = Utilities.isMainHand(event.getHand());

            for (final ItemStack usedStack : Utilities.getArmorAndHandItems(player, isMainHand)) {
                this.applyZenchantmentForTool(
                    player,
                    usedStack,
                    (ench, level) -> ench.onBlockInteract(event, level, isMainHand)
                );
            }
        }
    }

    @EventHandler
    private void onBlockInteractInteractable(final @NotNull PlayerInteractEvent event) {
        if (event.getClickedBlock() == null
            || MaterialList.INTERACTABLE_BLOCKS.contains(event.getClickedBlock().getType())
        ) {
            final Player player = event.getPlayer();
            final boolean isMainHand = Utilities.isMainHand(event.getHand());

            for (final ItemStack usedStack : Utilities.getArmorAndHandItems(player, isMainHand)) {
                this.applyZenchantmentForTool(
                    player,
                    usedStack,
                    (ench, level) -> ench.onBlockInteractInteractable(event, level, isMainHand)
                );
            }
        }
    }

    @EventHandler
    private void onEntityInteract(final @NotNull PlayerInteractEntityEvent event) {
        if (ArrayUtils.contains(ENTITY_INTERACT_BAD_ENTITIES, event.getRightClicked().getType())) {
            return;
        }

        final Player player = event.getPlayer();
        final boolean usedHand = Utilities.isMainHand(HAND);

        this.applyZenchantmentForTool(
            player,
            Utilities.getUsedItemStack(player, usedHand),
            (ench, level) -> ench.onEntityInteract(event, level, usedHand)
        );
    }

    @EventHandler
    private void onEntityKill(final @NotNull EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) {
            return;
        }

        final Player player = event.getEntity().getKiller();
        final PlayerInventory inventory = player.getInventory();
        final EquipmentSlot slot = event.getEntity().getLastDamageCause().getCause() == PROJECTILE
            && Tool.fromItemStack(inventory.getItemInOffHand()) == BOW
            && Tool.fromItemStack(inventory.getItemInMainHand()) != BOW
            ? EquipmentSlot.OFF_HAND
            : EquipmentSlot.HAND;
        final boolean usedHand = Utilities.isMainHand(slot);

        this.applyZenchantmentForTool(
            player,
            Utilities.getUsedItemStack(player, usedHand),
            (ench, level) -> ench.onEntityKill(event, level, usedHand)
        );
    }

    @EventHandler
    private void onEntityHit(final @NotNull EntityDamageByEntityEvent event) {
        if (event.getDamage() <= 0) {
            return;
        }

        if (event.getDamager() instanceof Player) {
            final Player player = (Player) event.getDamager();
            final boolean usedHand = Utilities.isMainHand(HAND);
            if (event.getEntity() instanceof LivingEntity) {
                for (final ItemStack usedStack : Utilities.getArmorAndHandItems(player, usedHand)) {
                    this.applyZenchantmentForTool(
                        player,
                        usedStack,
                        (ench, level) -> ench.onEntityHit(event, level, usedHand)
                    );
                }
            }
        }

        if (event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();
            // Only check main hand for some reason.
            for (final ItemStack usedStack : Utilities.getArmorAndHandItems(player, true)) {
                this.applyZenchantmentForTool(
                    player,
                    usedStack,
                    (ench, level) -> ench.onBeingHit(event, level, true)
                );
            }
        }
    }

    @EventHandler
    private void onEntityDamage(final @NotNull EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity();
        for (final ItemStack usedStack : Utilities.getArmorAndHandItems(player, false)) {
            this.applyZenchantmentForTool(
                player,
                usedStack,
                (ench, level) -> ench.onEntityDamage(event, level, false)
            );
        }
    }

    @EventHandler
    private void onPlayerFish(final @NotNull PlayerFishEvent event) {
        final Player player = event.getPlayer();
        final PlayerInventory inventory = player.getInventory();
        final Tool main = Tool.fromItemStack(inventory.getItemInMainHand());
        final Tool off = Tool.fromItemStack(inventory.getItemInOffHand());
        final boolean usedHand = Utilities.isMainHand(
            main != Tool.ROD && off == Tool.ROD ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND
        );

        this.applyZenchantmentForTool(
            player,
            Utilities.getUsedItemStack(player, usedHand),
            (ench, level) -> ench.onPlayerFish(event, level, true)
        );
    }

    @EventHandler
    private void onHungerChange(final @NotNull FoodLevelChangeEvent event) {
        if (event.isCancelled() || !(event.getEntity() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity();
        for (final ItemStack usedStack : Utilities.getArmorAndHandItems(player, true)) {
            this.applyZenchantmentForTool(
                player,
                usedStack,
                (ench, level) -> ench.onHungerChange(event, level, true)
            );
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
        final boolean usedHand = Utilities.isMainHand(
            main != Tool.SHEAR && off == Tool.SHEAR ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND
        );

        this.applyZenchantmentForTool(
            player,
            Utilities.getUsedItemStack(player, usedHand),
            (ench, level) -> ench.onShear(event, level, true)
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
        boolean usedHand = Utilities.isMainHand(
            main != BOW && off == BOW ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND
        );

        this.applyZenchantmentForTool(
            player,
            Utilities.getUsedItemStack(player, usedHand),
            (ench, level) -> ench.onEntityShootBow(event, level, true)
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

            for (final ItemStack usedStack : Utilities.getArmorAndHandItems(player, true)) {
                // Only apply one enchantment, which in practice is Potion Resistance.
                // This will always skip execution of the Lambda and return false after a Lambda returned true once
                // Yes, I am bored
                this.applyZenchantmentForTool(
                    player,
                    usedStack,
                    (ench, level) -> apply.get() && apply.compareAndSet(ench.onPotionSplash(event, level, false), false)
                );
            }
        }
    }

    @EventHandler
    private void onProjectileLaunch(final @NotNull ProjectileLaunchEvent event) {
        final Entity shooter = (Entity) event.getEntity().getShooter();

        if (!(shooter instanceof Player)) {
            return;
        }

        final Player player = (Player) shooter;
        final PlayerInventory inventory = player.getInventory();
        final Tool main = Tool.fromItemStack(inventory.getItemInMainHand());
        final Tool off = Tool.fromItemStack(inventory.getItemInOffHand());
        final boolean usedHand = Utilities.isMainHand(
            main != BOW && main != Tool.ROD && (off == BOW || off == Tool.ROD) ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND
        );

        this.applyZenchantmentForTool(
            player,
            Utilities.getUsedItemStack(player, usedHand),
            (ench, level) -> ench.onProjectileLaunch(event, level, usedHand)
        );
    }

    @EventHandler
    private void onDeath(final @NotNull PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final PlayerInventory inventory = player.getInventory();

        for (final ItemStack usedStack : ArrayUtils.addAll(inventory.getArmorContents(), inventory.getContents())) {
            this.applyZenchantmentForTool(
                player,
                usedStack,
                (ench, level) -> ench.onPlayerDeath(event, level, true)
            );
        }
    }

    @EventHandler
    private void onCombust(final @NotNull EntityCombustByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity();
        final PlayerInventory inventory = player.getInventory();

        for (final ItemStack usedStack : ArrayUtils.addAll(inventory.getArmorContents(), inventory.getContents())) {
            this.applyZenchantmentForTool(
                player,
                usedStack,
                (ench, level) -> ench.onCombust(event, level, true)
            );
        }
    }

    private void applyZenchantmentForTool(
        final @NotNull Player player,
        final @NotNull ItemStack tool,
        final @NotNull BiPredicate<Zenchantment, Integer> action
    ) {
        Zenchantment.applyForTool(
            player,
            this.plugin.getPlayerDataProvider(),
            this.plugin.getGlobalConfiguration(),
            this.plugin.getWorldConfigurationProvider(),
            tool,
            action
        );
    }

    // Fast Scan of Player's Armor and their hand to register enchantments
    @EffectTask(Frequency.HIGH)
    public static void scanPlayers() {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            // TODO: Replace this.
            PlayerData.matchPlayer(player).tick();
        }

        // Sweeping scan over the player list for armor enchants
        CACHE.run();
    }

    // Implicitly scheduled MEDIUM_HIGH due to being called by HighFrequencyEnchCache with interval 5
    private static void feedEnchCache(final @NotNull Player player, final @NotNull Consumer<Supplier<Boolean>> consumer) {
        final PlayerInventory inventory = player.getInventory();
        for (final ItemStack itemStack : inventory.getArmorContents()) {
            Zenchantment.applyForTool(
                player,
                null, // TODO: Pass an instance in.
                itemStack,
                (ench, level) -> {
                    consumer.accept(() -> {
                        if (!player.isOnline()) {
                            return false;
                        }

                        if (ench.onFastScan(player, level, true)) {
                            PlayerData.matchPlayer(player).setCooldown(ench.getKey(), ench.getCooldown());
                        }

                        return true;
                    });

                    return ench.onScan(player, level, true);
                }
            );
        }

        Zenchantment.applyForTool(
            player,
            null, // TODO: Pass an instance in.
            inventory.getItemInMainHand(),
            (ench, level) -> {
                consumer.accept(() -> {
                    if (!player.isOnline()) {
                        return false;
                    }

                    if (ench.onFastScanHands(player, level, true)) {
                        PlayerData.matchPlayer(player).setCooldown(ench.getKey(), ench.getCooldown());
                    }

                    return true;
                });

                return ench.onScanHands(player, level, true);
            }
        );

        Zenchantment.applyForTool(
            player,
            null, // TODO: Pass an instance in.
            inventory.getItemInOffHand(),
            (ench, level) -> {
                consumer.accept(() -> {
                    if (!player.isOnline()) {
                        return false;
                    }

                    if (ench.onFastScanHands(player, level, false)) {
                        PlayerData.matchPlayer(player).setCooldown(ench.getKey(), ench.getCooldown());
                    }

                    return true;
                });

                return ench.onScanHands(player, level, false);
            }
        );

        final long currentTime = System.currentTimeMillis();
        if (player.hasMetadata("ze.speed") && (player.getMetadata("ze.speed").get(0).asLong() < currentTime - 1000)) {
            player.removeMetadata("ze.speed", Storage.zenchantments);
            player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
            player.setFlySpeed(0.1F);
            player.setWalkSpeed(0.2F);
        }

        if (player.hasMetadata("ze.haste") && (player.getMetadata("ze.haste").get(0).asLong() < currentTime - 1000)) {
            player.removePotionEffect(FAST_DIGGING);
            player.removeMetadata("ze.haste", Storage.zenchantments);
        }
    }
}
