package zedly.zenchantments.enchantments;

import org.bukkit.entity.Entity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import static zedly.zenchantments.enums.Tool.SHEAR;

public class Mow extends CustomEnchantment {

    public Mow() {
        super(37);
        maxLevel = 3;
        loreName = "Mow";
        probability = 0;
        enchantable = new Tool[]{SHEAR};
        conflicting = new Class[]{};
        description = "Shears all nearby sheep";
        cooldown = 0;
        power = 1.0;
        handUse = Hand.RIGHT;
    }

    private boolean shear(PlayerEvent evt, int level, boolean usedHand) {
        boolean hasSheep = false;
        int radius = (int) Math.round(level * power + 2);
        Player player = evt.getPlayer();
        for(Entity ent : evt.getPlayer().getNearbyEntities(radius, radius, radius)) {
            if(ent instanceof Sheep) {
                Sheep sheep = (Sheep) ent;
                if(sheep.isAdult()) {
                    ADAPTER.shearEntityNMS(sheep, player, usedHand);
                }
            } else if(ent instanceof MushroomCow) {
                MushroomCow mCow = (MushroomCow) ent;
                if(mCow.isAdult()) {
                    ADAPTER.shearEntityNMS(mCow, player, usedHand);
                }
            }
        }
        return hasSheep;
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        if(evt.getAction() == RIGHT_CLICK_AIR || evt.getAction() == RIGHT_CLICK_BLOCK) {
            shear(evt, level, usedHand);
        }
        return false;
    }

    @Override
    public boolean onShear(PlayerShearEntityEvent evt, int level, boolean usedHand) {
        shear(evt, level, usedHand);
        return false;
    }
}
