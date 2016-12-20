package zedly.zenchantments;

import java.util.*;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Material;
import static org.bukkit.Material.*;
import org.bukkit.entity.*;
import static org.bukkit.entity.EntityType.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.PROJECTILE;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import static org.bukkit.inventory.EquipmentSlot.HAND;
import org.bukkit.inventory.ItemStack;
import static zedly.zenchantments.Tool.BOW_;

// This is the watcher used by the CustomEnchantment class. Each method checks the enchantments on relevant items,
//      ensures that the item is not an enchantment book, and calls each enchantment's method if the player can
//      perform a certain action and the cooldown time is 0. It will add the given enchantment's cooldown to the player
//      if the action performed is successful, determined by each enchantment in their respective classes.
public class WatcherEnchant implements Listener {

    private static final WatcherEnchant instance = new WatcherEnchant();

    public static WatcherEnchant instance() {
        return instance;
    }

    private WatcherEnchant() {
    }

    @EventHandler(ignoreCancelled = false)
    public void onBlockBreak(BlockBreakEvent evt) {
        if (!evt.isCancelled() && evt.getBlock().getType() != AIR) {
            Player player = evt.getPlayer();
            boolean usedHand = Utilities.usedHand(HAND);
            ItemStack usedStack = Utilities.usedStack(player, usedHand);
            CustomEnchantment.applyForTool(player.getWorld(), usedStack, (ench, level) -> {
                if (ench.onBlockBreak(evt, level, usedHand)) {
                    EnchantPlayer.matchPlayer(player).setCooldown(ench.enchantmentID, ench.cooldown);
                    evt.setCancelled(true);
                }
            });
        }
    }

    @EventHandler(ignoreCancelled = false)
    public void onBlockInteract(PlayerInteractEvent evt) {
        final Material[] badMats = new Material[]{CHEST, TRAPPED_CHEST, FURNACE, ANVIL, WORKBENCH, ENCHANTMENT_TABLE, DROPPER,
            DISPENSER, BEACON, STONE_BUTTON, WOOD_BUTTON, LEVER, BURNING_FURNACE, JUKEBOX, DIODE_BLOCK_ON, DIODE_BLOCK_OFF,
            TRAP_DOOR, BREWING_STAND, ENDER_CHEST, COMMAND, REDSTONE_COMPARATOR_OFF, REDSTONE_COMPARATOR_ON, HOPPER, DAYLIGHT_DETECTOR,
            DAYLIGHT_DETECTOR_INVERTED, IRON_TRAPDOOR, FENCE_GATE, ACACIA_FENCE_GATE, SPRUCE_FENCE_GATE, DARK_OAK_FENCE_GATE,
            BIRCH_FENCE_GATE, JUNGLE_FENCE_GATE, WOODEN_DOOR, SPRUCE_DOOR, BIRCH_DOOR, JUNGLE_DOOR, DARK_OAK_DOOR, ACACIA_DOOR};
        if (evt.getClickedBlock() == null || !ArrayUtils.contains(badMats, evt.getClickedBlock().getType())) {
            Player player = evt.getPlayer();
            boolean usedHand = Utilities.usedHand(evt.getHand());
            for (ItemStack stk : Utilities.getArmorandMainHandItems(player, usedHand)) {
                CustomEnchantment.applyForTool(player.getWorld(), stk, (ench, level) -> {
                    if (ench.onBlockInteract(evt, level, usedHand)) {
                        EnchantPlayer.matchPlayer(player).setCooldown(ench.enchantmentID, ench.cooldown);
                        evt.setCancelled(true);
                    }
                });
            }
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent evt) {
        final EntityType[] badEnts = new EntityType[]{HORSE, EntityType.ARMOR_STAND, EntityType.ITEM_FRAME, VILLAGER};
        Player player = evt.getPlayer();
        if (!ArrayUtils.contains(badEnts, evt.getRightClicked().getType())) {
            boolean usedHand = Utilities.usedHand(HAND);
            ItemStack usedStack = Utilities.usedStack(player, usedHand);
            CustomEnchantment.applyForTool(player.getWorld(), usedStack, (ench, level) -> {
                if (ench.onEntityInteract(evt, level, usedHand)) {
                    EnchantPlayer.matchPlayer(player).setCooldown(ench.enchantmentID, ench.cooldown);
                    evt.setCancelled(true);
                }
            });
        }
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent evt) {
        if (evt.getEntity().getKiller() != null) {
            if (evt.getEntity().getKiller() instanceof Player) {
                Player player = evt.getEntity().getKiller();
                EquipmentSlot slot = evt.getEntity().getLastDamageCause().getCause() == PROJECTILE
                        && Tool.fromItemStack(player.getInventory().getItemInOffHand()) == BOW_
                        && Tool.fromItemStack(player.getInventory().getItemInMainHand()) != BOW_ ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND;
                boolean usedHand = Utilities.usedHand(slot);
                ItemStack usedStack = Utilities.usedStack(player, usedHand);
                CustomEnchantment.applyForTool(player.getWorld(), usedStack, (ench, level) -> {
                    if (ench.onEntityKill(evt, level, usedHand)) {
                        EnchantPlayer.matchPlayer(player).setCooldown(ench.enchantmentID, ench.cooldown);
                    }
                });
            }
        }
    }

    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent evt) {
        if (evt.getDamager() instanceof Player) {
            Player player = (Player) evt.getDamager();
            boolean usedHand = Utilities.usedHand(HAND);
            if (evt.getEntity() instanceof LivingEntity) {
                for (ItemStack stk : Utilities.getArmorandMainHandItems(player, usedHand)) {
                    CustomEnchantment.applyForTool(player.getWorld(), stk, (ench, level) -> {
                        if (ench.onEntityHit(evt, level, usedHand)) {
                            EnchantPlayer.matchPlayer(player).setCooldown(ench.enchantmentID, ench.cooldown);
                        }
                    });
                }
            }
        }
        if (evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            for (ItemStack stk : Utilities.getArmorandMainHandItems(player, true)) { // Only check main hand for some reason
                CustomEnchantment.applyForTool(player.getWorld(), stk, (ench, level) -> {
                    if (ench.onBeingHit(evt, level, true)) {
                        EnchantPlayer.matchPlayer(player).setCooldown(ench.enchantmentID, ench.cooldown);
                    }
                });
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent evt) {
        if (evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            for (ItemStack stk : Utilities.getArmorandMainHandItems(player, false)) {
                CustomEnchantment.applyForTool(player.getWorld(), stk, (ench, level) -> {
                    if (ench.onEntityDamage(evt, level, false)) { // Check off hand for enchanted shields
                        EnchantPlayer.matchPlayer(player).setCooldown(ench.enchantmentID, ench.cooldown);
                    }
                });
            }
        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent evt) {
        Player player = evt.getPlayer();
        Tool main = Tool.fromItemStack(player.getInventory().getItemInMainHand());
        Tool off = Tool.fromItemStack(player.getInventory().getItemInOffHand());
        boolean usedHand = Utilities.usedHand(main != Tool.ROD && off == Tool.ROD ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND);
        ItemStack usedStack = Utilities.usedStack(player, usedHand);
        CustomEnchantment.applyForTool(player.getWorld(), usedStack, (ench, level) -> {
            if (ench.onPlayerFish(evt, level, usedHand)) {
                EnchantPlayer.matchPlayer(player).setCooldown(ench.enchantmentID, ench.cooldown);
            }
        });
    }

    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent evt) {
        if (!evt.isCancelled() && evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            for (ItemStack stk : Utilities.getArmorandMainHandItems(player, true)) {
                CustomEnchantment.applyForTool(player.getWorld(), stk, (ench, level) -> {
                    if (ench.onHungerChange(evt, level, true)) {
                        EnchantPlayer.matchPlayer(player).setCooldown(ench.enchantmentID, ench.cooldown);
                    }
                });
            }
        }
    }

    @EventHandler
    public void onShear(PlayerShearEntityEvent evt) {
        Player player = evt.getPlayer();
        Tool main = Tool.fromItemStack(player.getInventory().getItemInMainHand());
        Tool off = Tool.fromItemStack(player.getInventory().getItemInOffHand());
        boolean usedHand = Utilities.usedHand(main != Tool.SHEAR && off == Tool.SHEAR ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND);
        ItemStack usedStack = Utilities.usedStack(player, usedHand);
        if (!evt.isCancelled()) {
            CustomEnchantment.applyForTool(player.getWorld(), usedStack, (ench, level) -> {
                if (ench.onShear(evt, level, usedHand)) {
                    EnchantPlayer.matchPlayer(player).setCooldown(ench.enchantmentID, ench.cooldown);
                }
            });
        }
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent evt) {
        if (evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            Tool main = Tool.fromItemStack(player.getInventory().getItemInMainHand());
            Tool off = Tool.fromItemStack(player.getInventory().getItemInOffHand());
            boolean usedHand = Utilities.usedHand(main != BOW_ && off == BOW_ ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND);
            ItemStack usedStack = Utilities.usedStack(player, usedHand);
            LinkedHashMap<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(evt.getBow());
            CustomEnchantment.applyForTool(player.getWorld(), usedStack, (ench, level) -> {
                if (ench.onEntityShootBow(evt, level, usedHand)) {
                    EnchantPlayer.matchPlayer(player).setCooldown(ench.enchantmentID, ench.cooldown);
                }
            });
        }
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent evt) {
        Collection<LivingEntity> affected = evt.getAffectedEntities();
        for (LivingEntity entity : affected) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                boolean applied = false; // Only apply one enchantment, which in practice is Potion Resistance
                for (ItemStack stk : Utilities.getArmorandMainHandItems(player, true)) {
                    CustomEnchantment.applyForTool(player.getWorld(), stk, (ench, level) -> {
                        if (!applied) {
                            if (ench.onPotionSplash(evt, level, true)) {
                                EnchantPlayer.matchPlayer(player).setCooldown(ench.enchantmentID, ench.cooldown);
                            }
                        }
                    });
                }
            }
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent evt) {
        if (evt.getEntity().getShooter() != null && evt.getEntity().getShooter() instanceof Player) {
            Player player = (Player) evt.getEntity().getShooter();
            Tool main = Tool.fromItemStack(player.getInventory().getItemInMainHand());
            Tool off = Tool.fromItemStack(player.getInventory().getItemInOffHand());
            boolean usedHand = Utilities.usedHand(main != BOW_ && main != Tool.ROD && (off == BOW_ || off == Tool.ROD) ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND);
            ItemStack usedStack = Utilities.usedStack(player, usedHand);
            CustomEnchantment.applyForTool(player.getWorld(), usedStack, (ench, level) -> {
                if (ench.onProjectileLaunch(evt, level, usedHand)) {
                    EnchantPlayer.matchPlayer(player).setCooldown(ench.enchantmentID, ench.cooldown);
                }
            });
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent evt) {
        Player player = (Player) evt.getEntity();
        for (ItemStack stk : ArrayUtils.addAll(player.getInventory().getArmorContents(), player.getInventory().getContents())) {
            if (stk != null && stk.getType() != Material.AIR) {
                CustomEnchantment.applyForTool(player.getWorld(), stk, (ench, level) -> {
                    if (ench.onPlayerDeath(evt, level, true)) {
                        EnchantPlayer.matchPlayer(player).setCooldown(ench.enchantmentID, ench.cooldown);
                    }
                });
            }
        }
    }
    
    @EventHandler
    public void onCombust(EntityCombustByEntityEvent evt) {
        Player player = (Player) evt.getEntity();
        for (ItemStack stk : ArrayUtils.addAll(player.getInventory().getArmorContents(), player.getInventory().getContents())) {
            if (stk != null && stk.getType() != Material.AIR) {
                CustomEnchantment.applyForTool(player.getWorld(), stk, (ench, level) -> {
                    if (ench.onCombust(evt, level, true)) {
                        EnchantPlayer.matchPlayer(player).setCooldown(ench.enchantmentID, ench.cooldown);
                    }
                });
            }
        }
    }
}
