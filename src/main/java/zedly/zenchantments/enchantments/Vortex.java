package zedly.zenchantments.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.arrows.enchanted.VortexArrow;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import java.util.HashMap;
import java.util.Map;

import static zedly.zenchantments.Tool.BOW;
import static zedly.zenchantments.Tool.SWORD;

public class Vortex extends Zenchantment {

    // Locations where Vortex has been used on a block and are waiting for the Watcher to handle their teleportation
    public static final Map<Block, Player> vortexLocs = new HashMap<>();
    public static final int ID = 66;

    @Override
    public Builder<Vortex> defaults() {
        return new Builder<>(Vortex::new, ID)
                .maxLevel(1)
                .name("Vortex")
                .probability(0)
                .enchantable(new Tool[]{BOW, SWORD})
                .conflicting(new Class[]{})
                .description("Teleports mob loot and XP directly to the player")
                .cooldown(0)
                .power(-1.0)
                .handUse(Hand.BOTH);
    }

    @Override
    public boolean onEntityKill(final EntityDeathEvent event, int level, boolean usedHand) {
        final Block deathBlock = event.getEntity().getLocation().getBlock();
        vortexLocs.put(deathBlock, event.getEntity().getKiller());
        
        int i = event.getDroppedExp();
        event.setDroppedExp(0);
        Storage.COMPATIBILITY_ADAPTER.collectXP(event.getEntity().getKiller(), i);
        
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
            vortexLocs.remove(deathBlock);
        }, 3);
        return true;
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent event, int level, boolean usedHand) {
        VortexArrow arrow = new VortexArrow((Arrow) event.getProjectile());
        EnchantedArrow.putArrow((Arrow) event.getProjectile(), arrow, (Player) event.getEntity());
        return true;
    }

}
