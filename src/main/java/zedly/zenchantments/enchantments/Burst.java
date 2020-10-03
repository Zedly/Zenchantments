package zedly.zenchantments.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.arrows.enchanted.MultiArrow;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import static zedly.zenchantments.Tool.BOW;

public class Burst extends Zenchantment {

    public static final int ID = 8;

    @Override
    public Builder<Burst> defaults() {
        return new Builder<>(Burst::new, ID)
                .maxLevel(3)
                .name("Burst")
                .probability(0)
                .enchantable(new Tool[]{BOW})
                .conflicting(new Class[]{Spread.class})
                .description("Rapidly fires arrows in series")
                .cooldown(0)
                .power(1.0)
                .handUse(Hand.RIGHT);
    }

    @Override
    public boolean onBlockInteract(final PlayerInteractEvent event, int level, boolean usedHand) {
        final Player player = event.getPlayer();
        final ItemStack hand = Utilities.usedStack(player, usedHand);
        boolean result = false;
        if (event.getAction().equals(RIGHT_CLICK_AIR) || event.getAction().equals(RIGHT_CLICK_BLOCK)) {
            for (int i = 0; i <= (int) Math.round((power * level) + 1); i++) {
                if ((hand.containsEnchantment(Enchantment.ARROW_INFINITE) && Utilities.hasItem(player, Material.ARROW, 1))
                        || Utilities.removeItem(player, Material.ARROW, 1)) {
                    result = true;
                    Utilities.setHand(player, hand, usedHand);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
                        Arrow arrow = player.getWorld().spawnArrow(player.getEyeLocation(),
                                player.getLocation().getDirection(), 1, 0);
                        arrow.setShooter(player);
                        if (hand.containsEnchantment(Enchantment.ARROW_FIRE)) {
                            arrow.setFireTicks(Integer.MAX_VALUE);
                        }
                        arrow.setVelocity(player.getLocation().getDirection().normalize().multiply(1.7));
                        EntityShootBowEvent shootEvent = new EntityShootBowEvent(player, hand, arrow, 1f);
                        ProjectileLaunchEvent launchEvent = new ProjectileLaunchEvent(arrow);
                        Bukkit.getPluginManager().callEvent(shootEvent);
                        Bukkit.getPluginManager().callEvent(launchEvent);
                        if (shootEvent.isCancelled() || launchEvent.isCancelled()) {
                            arrow.remove();
                        } else {
                            arrow.setMetadata("ze.arrow", new FixedMetadataValue(Storage.zenchantments, null));
                            arrow.setCritical(true);
                            EnchantedArrow.putArrow(arrow, new MultiArrow(arrow), player);
                            Utilities.damageTool(player, 1, usedHand);
                        }

                    }, i * 2);
                }
            }
        }
        return result;
    }

}
