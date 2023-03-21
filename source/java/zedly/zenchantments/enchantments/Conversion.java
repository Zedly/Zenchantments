package zedly.zenchantments.enchantments;

import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

@AZenchantment(runInSlots = Slots.MAIN_HAND, conflicting = {})
public final class Conversion extends Zenchantment {
    @Override
    public boolean onBlockInteract(final @NotNull PlayerInteractEvent event, final int level, final EquipmentSlot slot) {
        if (event.getAction() != RIGHT_CLICK_AIR && event.getAction() != RIGHT_CLICK_BLOCK) {
            return false;
        }

        final Player player = event.getPlayer();
        if (!player.isSneaking()) {
            return false;
        }

        if (player.getLevel() <= 1) {
            return false;
        }

        if (!(player.getHealth() < 20)) {
            return false;
        }

        player.setLevel((player.getLevel() - 1));
        player.setHealth(Math.min(20, player.getHealth() + 2 * this.getPower() * level));

        for (int i = 0; i < 3; i++) {
            ZenchantmentsPlugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(
                ZenchantmentsPlugin.getInstance(),
                () -> Utilities.displayParticle(
                    Utilities.getCenter(player.getLocation()),
                    Particle.HEART,
                    10,
                    0.1f,
                    0.5f,
                    0.5f,
                    0.5f
                ),
                i * 5 + 1
            );
        }

        return true;
    }
}
