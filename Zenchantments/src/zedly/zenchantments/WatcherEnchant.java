package zedly.zenchantments;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
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
import zedly.zenchantments.annotations.EffectTask;
import zedly.zenchantments.enchantments.Haste;
import zedly.zenchantments.enums.Frequency;
import zedly.zenchantments.enums.Tool;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.bukkit.Material.AIR;
import static org.bukkit.entity.EntityType.HORSE;
import static org.bukkit.entity.EntityType.VILLAGER;
import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.PROJECTILE;
import static org.bukkit.inventory.EquipmentSlot.HAND;
import static org.bukkit.potion.PotionEffectType.FAST_DIGGING;
import static zedly.zenchantments.enums.Tool.BOW;

// This is the watcher used by the CustomEnchantment class. Each method checks the enchantments on relevant items,
//      ensures that the item is not an enchantment book, and calls each enchantment's method if the player can
//      perform a certain action and the cooldown time is 0. It will add the given enchantment's cooldown to the player
//      if the action performed is successful, determined by each enchantment in their respective classes.
public class WatcherEnchant implements Listener {

    private static final WatcherEnchant INSTANCE = new WatcherEnchant();

    public static WatcherEnchant instance() {
        return INSTANCE;
    }

    private WatcherEnchant() {
    }

    @EventHandler(ignoreCancelled = false)
    public void onBlockBreak(BlockBreakEvent evt) {
        if (!evt.isCancelled() && !(evt instanceof BlockShredEvent) && evt.getBlock().getType() != AIR) {
            Player player = evt.getPlayer();
            boolean usedHand = Utilities.isMainHand(HAND);
            ItemStack usedStack = Utilities.usedStack(player, usedHand);
            CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                return ench.onBlockBreak(evt, level, usedHand);
            });
        }
    }
    
    public void onBlockShred(BlockShredEvent evt) {
        if (!evt.isCancelled() && evt.getBlock().getType() != AIR) {
            Player player = evt.getPlayer();
            boolean usedHand = Utilities.isMainHand(HAND);
            ItemStack usedStack = Utilities.usedStack(player, usedHand);
            CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                return ench.onBlockBreak(evt, level, usedHand);
            });
        }
    }

    @EventHandler(ignoreCancelled = false)
    public void onBlockInteract(PlayerInteractEvent evt) {
        if (evt.getClickedBlock() == null || !ArrayUtils.contains(Storage.INTERACTABLE_BLOCKS, evt.getClickedBlock().getType())) {
            Player player = evt.getPlayer();
            boolean isMainHand = Utilities.isMainHand(evt.getHand());
            for (ItemStack usedStack : Utilities.getArmorandMainHandItems(player, isMainHand)) {
                CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                    return ench.onBlockInteract(evt, level, isMainHand);
                });
            }
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent evt) {
        final EntityType[] badEnts = new EntityType[]{HORSE, EntityType.ARMOR_STAND, EntityType.ITEM_FRAME, VILLAGER};
        Player player = evt.getPlayer();
        if (!ArrayUtils.contains(badEnts, evt.getRightClicked().getType())) {
            boolean usedHand = Utilities.isMainHand(HAND);
            ItemStack usedStack = Utilities.usedStack(player, usedHand);
            CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                return ench.onEntityInteract(evt, level, usedHand);
            });
        }
    }
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent evt) {
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent evt) {
        if (evt.getEntity().getKiller() != null) {
            if (evt.getEntity().getKiller() instanceof Player) {
                Player player = evt.getEntity().getKiller();
                EquipmentSlot slot = evt.getEntity().getLastDamageCause().getCause() == PROJECTILE
                        && Tool.fromItemStack(player.getInventory().getItemInOffHand()) == BOW
                        && Tool.fromItemStack(player.getInventory().getItemInMainHand()) != BOW ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND;
                boolean usedHand = Utilities.isMainHand(slot);
                ItemStack usedStack = Utilities.usedStack(player, usedHand);
                CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                    return ench.onEntityKill(evt, level, usedHand);
                });
            }
        }
    }

    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent evt) {
        if (evt.getDamage() <= 0) {
            return;
        }
        if (evt.getDamager() instanceof Player) {
            Player player = (Player) evt.getDamager();
            boolean usedHand = Utilities.isMainHand(HAND);
            if (evt.getEntity() instanceof LivingEntity) {
                for (ItemStack usedStack : Utilities.getArmorandMainHandItems(player, usedHand)) {
                    CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                        return ench.onEntityHit(evt, level, usedHand);
                    });
                }
            }
        }
        if (evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            for (ItemStack usedStack : Utilities.getArmorandMainHandItems(player, true)) { // Only check main hand for some reason
                CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                    return ench.onBeingHit(evt, level, true);
                });
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent evt) {
        if (evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            for (ItemStack usedStack : Utilities.getArmorandMainHandItems(player, false)) {
                CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                    return ench.onEntityDamage(evt, level, false);
                });
            }
        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent evt) {
        Player player = evt.getPlayer();
        Tool main = Tool.fromItemStack(player.getInventory().getItemInMainHand());
        Tool off = Tool.fromItemStack(player.getInventory().getItemInOffHand());
        boolean usedHand = Utilities.isMainHand(main != Tool.ROD && off == Tool.ROD ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND);
        ItemStack usedStack = Utilities.usedStack(player, usedHand);
        CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
            return ench.onPlayerFish(evt, level, true);
        });
    }

    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent evt) {
        if (!evt.isCancelled() && evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            for (ItemStack usedStack : Utilities.getArmorandMainHandItems(player, true)) {
                CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                    return ench.onHungerChange(evt, level, true);
                });
            }
        }
    }

    @EventHandler
    public void onShear(PlayerShearEntityEvent evt) {
        Player player = evt.getPlayer();
        Tool main = Tool.fromItemStack(player.getInventory().getItemInMainHand());
        Tool off = Tool.fromItemStack(player.getInventory().getItemInOffHand());
        boolean usedHand = Utilities.isMainHand(main != Tool.SHEAR && off == Tool.SHEAR ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND);
        ItemStack usedStack = Utilities.usedStack(player, usedHand);
        if (!evt.isCancelled()) {
            CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                return ench.onShear(evt, level, true);
            });
        }
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent evt) {
        if (evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            Tool main = Tool.fromItemStack(player.getInventory().getItemInMainHand());
            Tool off = Tool.fromItemStack(player.getInventory().getItemInOffHand());
            boolean usedHand = Utilities.isMainHand(main != BOW && off == BOW ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND);
            ItemStack usedStack = Utilities.usedStack(player, usedHand);
            LinkedHashMap<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(evt.getBow());
            CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                return ench.onEntityShootBow(evt, level, true);
            });
        }
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent evt) {
        Collection<LivingEntity> affected = evt.getAffectedEntities();
        for (LivingEntity entity : affected) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                AtomicBoolean apply = new AtomicBoolean(true);
                for (ItemStack usedStack : Utilities.getArmorandMainHandItems(player, true)) {
                    CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                        // Only apply one enchantment, which in practice is Potion Resistance.
                        // This will always skip execution of the Lambda and return false after a Lambda returned true once
                        // Yes, I am bored
                        return apply.get() && apply.compareAndSet(ench.onPotionSplash(evt, level, false), false);
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
            boolean usedHand = Utilities.isMainHand(main != BOW && main != Tool.ROD && (off == BOW || off == Tool.ROD) ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND);
            ItemStack usedStack = Utilities.usedStack(player, usedHand);
            CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                return ench.onProjectileLaunch(evt, level, usedHand);
            });
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent evt) {
        Player player = (Player) evt.getEntity();
        for (ItemStack usedStack : ArrayUtils.addAll(player.getInventory().getArmorContents(), player.getInventory().getContents())) {
            CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                return ench.onPlayerDeath(evt, level, true);
            });
        }
    }

    @EventHandler
    public void onCombust(EntityCombustByEntityEvent evt) {
        if (evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            for (ItemStack usedStack : ArrayUtils.addAll(player.getInventory().getArmorContents(), player.getInventory().getContents())) {
                CustomEnchantment.applyForTool(player, usedStack, (ench, level) -> {
                    return ench.onCombust(evt, level, true);
                });
            }
        }
    }

	@EffectTask(Frequency.MEDIUM)
	// TODO: rename
	// Scan of Player's Armor and their hand to register enchantments & make enchantment descriptions
	public static void scanPlayers2() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.hasMetadata("ze.haste")) {
				boolean has = false;
				for (CustomEnchantment e : Config.get(player.getWorld()).getEnchants(
						player.getInventory().getItemInMainHand()).keySet()) {
					if (e.getClass().equals(Haste.class)) {
						has = true;
					}
				}
				if (!has) {
					player.removePotionEffect(FAST_DIGGING);
					player.removeMetadata("ze.haste", Storage.zenchantments);
				}
			}


			// Dude this runs four times every second!? xD
			Config config = Config.get(player.getWorld());
			for (ItemStack stk : (ItemStack[]) org.apache.commons.lang.ArrayUtils.addAll(
					player.getInventory().getArmorContents(), player.getInventory().getContents())) {
				if (config.descriptionLore()) {
					config.addDescriptions(stk, null);
				} else {
					config.removeDescriptions(stk, null);
				}
			}


			EnchantPlayer.matchPlayer(player).tick();
			for (ItemStack stk : player.getInventory().getArmorContents()) {
				CustomEnchantment.applyForTool(player, stk, (ench, level) -> {
					return ench.onScan(player, level, true);
				});
			}
			ItemStack stk = player.getInventory().getItemInMainHand();
			CustomEnchantment.applyForTool(player, stk, (ench, level) -> {
				return ench.onScanHands(player, level, true);
			});
			stk = player.getInventory().getItemInOffHand();
			CustomEnchantment.applyForTool(player, stk, (ench, level) -> {
				return ench.onScanHands(player, level, false);
			});
		}
	}

	@EffectTask(Frequency.HIGH)
	// Fast Scan of Player's Armor and their hand to register enchantments
	public static void scanPlayers() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			EnchantPlayer.matchPlayer(player).tick();
			for (ItemStack stk : player.getInventory().getArmorContents()) {
				CustomEnchantment.applyForTool(player, stk, (ench, level) -> {
					return ench.onFastScan(player, level, true);
				});
			}
			ItemStack stk = player.getInventory().getItemInMainHand();
			CustomEnchantment.applyForTool(player, stk, (ench, level) -> {
				return ench.onFastScanHands(player, level, true);
			});
			stk = player.getInventory().getItemInOffHand();
			CustomEnchantment.applyForTool(player, stk, (ench, level) -> {
				return ench.onFastScanHands(player, level, false);
			});
		}
	}
}
