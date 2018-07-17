package zedly.zenchantments.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static zedly.zenchantments.enums.Tool.SWORD;

public class Conversion extends CustomEnchantment {

    public Conversion() {
        super(10);
        maxLevel = 4;
        loreName = "Conversion";
        probability = 0;
        enchantable = new Tool[]{SWORD};
        conflicting = new Class[]{};
        description = "Converts XP to health when right clicking and sneaking";
        cooldown = 0;
        power = 1.0;
        handUse = Hand.RIGHT;
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        if(evt.getAction() == Action.RIGHT_CLICK_AIR || evt.getAction() == Action.RIGHT_CLICK_BLOCK) {
            final Player player = (Player) evt.getPlayer();
            if(player.isSneaking()) {
                if(player.getLevel() > 1) {
                    if(player.getHealth() < 20) {
                        player.setLevel((int) (player.getLevel() - 1));
                        player.setHealth(Math.min(20, player.getHealth() + 2 * power * level));
                        for(int i = 0; i < 3; i++) {
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
                                Utilities
                                        .display(Utilities.getCenter(player.getLocation()), Particle.HEART, 10, .1f,
                                                 .5f, .5f, .5f);
                            }, ((i * 5) + 1));
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
