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
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.EnchantArrow;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import static zedly.zenchantments.enums.Tool.BOW;

public class Burst extends CustomEnchantment {

    public Burst() {
        super(8);
        maxLevel = 3;
        loreName = "Burst";
        probability = 0;
        enchantable = new Tool[]{BOW};
        conflicting = new Class[]{Spread.class};
        description = "Rapidly fires arrows in series";
        cooldown = 0;
        power = 1.0;
        handUse = Hand.RIGHT;
    }

    @Override
    public boolean onBlockInteract(final PlayerInteractEvent evt, int level, boolean usedHand) {
        final Player player = evt.getPlayer();
        final ItemStack hand = Utilities.usedStack(player, usedHand);
        if(evt.getAction().equals(RIGHT_CLICK_AIR) || evt.getAction().equals(RIGHT_CLICK_BLOCK)) {
            for(int i = 0; i <= (int) Math.round((power * level) + 1); i++) {
                if(hand.containsEnchantment(Enchantment.ARROW_INFINITE) ||
                   Utilities.removeItemCheck(player, Material.ARROW, (short) 0, 1)) {
                    Utilities.setHand(player, hand, usedHand);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
                        Arrow arrow = player.getWorld().spawnArrow(player.getEyeLocation(),
                                                                   player.getLocation().getDirection(), 1, 0);
                        arrow.setShooter(player);
                        arrow.setVelocity(player.getLocation().getDirection().normalize().multiply(1.7));
                        EntityShootBowEvent shootEvent = new EntityShootBowEvent(player, hand, arrow, 1f);
                        ProjectileLaunchEvent launchEvent = new ProjectileLaunchEvent(arrow);
                        Bukkit.getPluginManager().callEvent(shootEvent);
                        Bukkit.getPluginManager().callEvent(launchEvent);
                        if(shootEvent.isCancelled() || launchEvent.isCancelled()) {
                            arrow.remove();
                        } else {
                            arrow.setMetadata("ze.arrow", new FixedMetadataValue(Storage.zenchantments, null));
                            arrow.setCritical(true);
                            Utilities.putArrow(arrow, new EnchantArrow.ArrowGenericMulitple(arrow), player);
                            Utilities.damageTool(player, 1, usedHand);
                        }
                    }, i * 2);
                }
                return true;
            }
        }
        return false;
    }

}
