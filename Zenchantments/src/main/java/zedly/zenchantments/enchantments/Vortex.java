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
import zedly.zenchantments.arrows.EnchantedArrow;
import zedly.zenchantments.arrows.enchanted.VortexArrow;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import java.util.HashMap;
import java.util.Map;

import static zedly.zenchantments.enums.Tool.BOW;
import static zedly.zenchantments.enums.Tool.SWORD;

public class Vortex extends CustomEnchantment {

    // Locations where Vortex has been used on a block and are waiting for the Watcher to handle their teleportation
    public static final Map<Block, Player> vortexLocs = new HashMap<>();
    public static final int ID = 66;

    @Override
    public Builder<Vortex> defaults() {
        return new Builder<>(Vortex::new, ID)
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
        final Block deathBlock = evt.getEntity().getLocation().getBlock();
        vortexLocs.put(deathBlock, evt.getEntity().getKiller());
        
        int i = evt.getDroppedExp();
        evt.setDroppedExp(0);
        Storage.COMPATIBILITY_ADAPTER.collectXP(evt.getEntity().getKiller(), i);
        
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
            vortexLocs.remove(deathBlock);
        }, 3);
        return true;
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        VortexArrow arrow = new VortexArrow((Arrow) evt.getProjectile());
        EnchantedArrow.putArrow((Arrow) evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }

}
