package zedly.zenchantments.enchantments;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.entity.EntityType.DROPPED_ITEM;
import static zedly.zenchantments.enums.Tool.LEGGINGS;

public class Magnetism extends CustomEnchantment {

    public Magnetism() {
        super(35);
        maxLevel = 3;
        loreName = "Magnetism";
        probability = 0;
        enchantable = new Tool[]{LEGGINGS};
        conflicting = new Class[]{};
        description = "Slowly attracts nearby items to the players inventory";
        cooldown = 0;
        power = 1.0;
        handUse = Hand.NONE;
    }

    @Override
    public boolean onFastScan(Player player, int level, boolean usedHand) {
        int radius = (int) Math.round(power * level * 2 + 3);
        for(Entity e : player.getNearbyEntities(radius, radius, radius)) {
            if(e.getType().equals(DROPPED_ITEM) && e.getTicksLived() > 160) {
                e.setVelocity(player.getLocation().toVector().subtract(e.getLocation().toVector()).multiply(.05));
            }
        }
        return true;
    }
}
