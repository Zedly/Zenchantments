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

    @EventHandler(ignoreCancelled = false)
    public void onBlockBreak(BlockBreakEvent evt) {
        if (!evt.isCancelled() && evt.getBlock().getType() != AIR) {
            Player player = evt.getPlayer();
            boolean usedHand = Utilities.usedHand(HAND);
            LinkedHashMap<CustomEnchantment, Integer> map = Config.get(evt.getBlock().getWorld()).getEnchants(Utilities.usedStack(player, usedHand));
            for (CustomEnchantment ench : map.keySet()) {
                if (Utilities.canUse(player, ench.enchantmentID)) {
                    if (ench.onBlockBreak(evt, map.get(ench), usedHand)) {
                        EnchantPlayer.matchPlayer(player).setCooldown(ench.enchantmentID, ench.cooldown);
                    }
                }

            }
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
            for (ItemStack stk : Utilities.getRelevant(player, usedHand)) {
                LinkedHashMap<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(stk);
                for (CustomEnchantment ench : map.keySet()) {
                    if (Utilities.canUse(player, ench.enchantmentID)) {
                        if (ench.onBlockInteract(evt, map.get(ench), usedHand)) {
                            EnchantPlayer.matchPlayer(player).setCooldown(ench.enchantmentID, ench.cooldown);
                            evt.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent evt) {
        final EntityType[] badEnts = new EntityType[]{HORSE, EntityType.ARMOR_STAND, EntityType.ITEM_FRAME, VILLAGER};
        Player player = evt.getPlayer();
        boolean usedHand = Utilities.usedHand(evt.getHand());
        LinkedHashMap<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(Utilities.usedStack(player, usedHand));
        if (!ArrayUtils.contains(badEnts, evt.getRightClicked().getType())) {
            for (CustomEnchantment ench : map.keySet()) {
                if (Utilities.canUse(player, ench.enchantmentID)) {
                    if (ench.onEntityInteract(evt, map.get(ench), usedHand)) {
                        EnchantPlayer.matchPlayer(player).setCooldown(ench.enchantmentID, ench.cooldown);
                    }
                }
            }
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
                LinkedHashMap<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(Utilities.usedStack(player, usedHand));
                for (CustomEnchantment ench : map.keySet()) {
                    if (Utilities.canUse(player, ench.enchantmentID)) {
                        if (ench.onEntityKill(evt, map.get(ench), usedHand)) {
                            EnchantPlayer.matchPlayer(player).setCooldown(ench.enchantmentID, ench.cooldown);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent evt) {
        if (evt.getDamager() instanceof Player) {
            Player player = (Player) evt.getDamager();
            boolean usedHand = Utilities.usedHand(HAND);
            if (evt.getEntity() instanceof LivingEntity) {
                for (ItemStack stk : Utilities.getRelevant(player, usedHand)) {
                    LinkedHashMap<CustomEnchantment, Integer> map = Config.get(evt.getEntity().getWorld()).getEnchants(stk);
                    for (CustomEnchantment ench : map.keySet()) {
                        if (Utilities.canUse(player, ench.enchantmentID)) {
                            if (ench.onHitting(evt, map.get(ench), usedHand)) {
                                EnchantPlayer.matchPlayer(player).setCooldown(ench.enchantmentID, ench.cooldown);
                            }
                        }
                    }
                }
            }
        }
        if (evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            for (ItemStack stk : Utilities.getRelevant(player, true)) { // I specifically use 'true' here and after
                LinkedHashMap<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(stk);
                for (CustomEnchantment ench : map.keySet()) {
                    if (Utilities.canUse(player, ench.enchantmentID)) {
                        if (ench.onBeingHit(evt, map.get(ench), true)) {
                            EnchantPlayer.matchPlayer(player).setCooldown(ench.enchantmentID, ench.cooldown);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent evt) {
        if (evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            for (ItemStack stk : Utilities.getRelevant(player, false)) {
                LinkedHashMap<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(stk);
                for (CustomEnchantment ench : map.keySet()) {
                    if (Utilities.canUse(player, ench.enchantmentID)) {
                        if (ench.onEntityDamage(evt, map.get(ench), false)) {
                            EnchantPlayer.matchPlayer(player).setCooldown(ench.enchantmentID, ench.cooldown);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent evt) {
        Player player = evt.getPlayer();
        Tool main = Tool.fromItemStack(player.getInventory().getItemInMainHand());
        Tool off = Tool.fromItemStack(player.getInventory().getItemInOffHand());
        boolean usedHand = Utilities.usedHand(main != Tool.ROD && off == Tool.ROD ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND);
        LinkedHashMap<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(Utilities.usedStack(player, usedHand));
        for (CustomEnchantment ench : map.keySet()) {
            if (Utilities.canUse(player, ench.enchantmentID)) {
                if (ench.onPlayerFish(evt, map.get(ench), usedHand)) {
                    EnchantPlayer.matchPlayer(player).setCooldown(ench.enchantmentID, ench.cooldown);
                }
            }
        }
    }

    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent evt) {
        if (evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            for (ItemStack stk : Utilities.getRelevant(player, true)) {
                LinkedHashMap<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(stk);
                for (CustomEnchantment ench : map.keySet()) {
                    if (Utilities.canUse(player, ench.enchantmentID)) {
                        if (ench.onHungerChange(evt, map.get(ench), true)) {
                            EnchantPlayer.matchPlayer(player).setCooldown(ench.enchantmentID, ench.cooldown);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onShear(PlayerShearEntityEvent evt) {
        Player player = evt.getPlayer();
        Tool main = Tool.fromItemStack(player.getInventory().getItemInMainHand());
        Tool off = Tool.fromItemStack(player.getInventory().getItemInOffHand());
        boolean usedHand = Utilities.usedHand(main != Tool.SHEAR && off == Tool.SHEAR ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND);
        if (!evt.isCancelled()) {
            LinkedHashMap<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(Utilities.usedStack(player, usedHand));
            for (CustomEnchantment ench : map.keySet()) {
                if (Utilities.canUse(player, ench.enchantmentID)) {
                    if (ench.onShear(evt, map.get(ench), usedHand)) {
                        EnchantPlayer.matchPlayer(player).setCooldown(ench.enchantmentID, ench.cooldown);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent evt) {
        if (evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            Tool main = Tool.fromItemStack(player.getInventory().getItemInMainHand());
            Tool off = Tool.fromItemStack(player.getInventory().getItemInOffHand());
            boolean usedHand = Utilities.usedHand(main != BOW_ && off == BOW_ ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND);
            LinkedHashMap<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(evt.getBow());
            for (CustomEnchantment ench : map.keySet()) {
                if (Utilities.canUse(player, ench.enchantmentID)) {
                    if (ench.onEntityShootBow(evt, map.get(ench), usedHand)) {
                        EnchantPlayer.matchPlayer(player).setCooldown(ench.enchantmentID, ench.cooldown);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent evt) {
        Collection<LivingEntity> affected = evt.getAffectedEntities();
        for (LivingEntity entity : affected) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                int test = 0;
                for (ItemStack stk : Utilities.getRelevant(player, true)) {
                    if (test == 0) {
                        LinkedHashMap<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(stk);
                        for (CustomEnchantment ench : map.keySet()) {
                            if (Utilities.canUse(player, ench.enchantmentID)) {
                                if (ench.onPotionSplash(evt, map.get(ench), true)) {
                                    test++;
                                }
                                EnchantPlayer.matchPlayer(player).setCooldown(ench.enchantmentID, ench.cooldown);
                            }
                        }
                    }
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
            LinkedHashMap<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(Utilities.usedStack(player, usedHand));
            for (CustomEnchantment ench : map.keySet()) {
                if (Utilities.canUse(player, ench.enchantmentID)) {
                    if (ench.onProjectileLaunch(evt, map.get(ench), usedHand)) {
                        EnchantPlayer.matchPlayer(player).setCooldown(ench.enchantmentID, ench.cooldown);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent evt) {
        Player player = (Player) evt.getEntity();
        for (ItemStack stk : ArrayUtils.addAll(player.getInventory().getArmorContents(), player.getInventory().getContents())) {
            LinkedHashMap<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(stk);
            if (stk != null) {
                for (CustomEnchantment ench : map.keySet()) {
                    if (Utilities.canUse(player, ench.enchantmentID)) {
                        if (ench.onPlayerDeath(evt, map.get(ench), true)) {
                            EnchantPlayer.matchPlayer(player).setCooldown(ench.enchantmentID, ench.cooldown);
                        }
                    }
                }
            }
        }
    }

}
