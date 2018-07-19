package zedly.zenchantments.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.arrows.enchanted.VortexArrow;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import java.util.HashMap;
import java.util.Map;

import static zedly.zenchantments.enums.Tool.BOW;
import static zedly.zenchantments.enums.Tool.SWORD;

public class Vortex extends CustomEnchantment {

	// Locations where Vortex has been used on a block and are waiting for the Watcher to handle their teleportation
	public static final Map<Block, Location> vortexLocs = new HashMap<>();

    @Override
    public Builder<Vortex> defaults() {
        return new Builder<>(Vortex::new, 66)
            .maxLevel(1)
            .loreName("Vortex")
            .probability(0)
            .enchantable(new Tool[]{BOW, SWORD})
            .conflicting(new Class[]{})
            .description("Teleports mob loot and XP directly to the player")
            .cooldown(0)
            .power(-1.0)
            .handUse(Hand.BOTH);
    }

    @Override
    public boolean onEntityKill(final EntityDeathEvent evt, int level, boolean usedHand) {
        vortexLocs.put(evt.getEntity().getLocation().getBlock(), evt.getEntity().getKiller().getLocation());
        int i = evt.getDroppedExp();
        evt.setDroppedExp(0);
        evt.getEntity().getKiller().giveExp(i);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
            vortexLocs.remove(evt.getEntity().getLocation().getBlock());
        }, 3);
        return true;
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        VortexArrow arrow = new VortexArrow((Arrow) evt.getProjectile());
        Utilities.putArrow((Arrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }

}
