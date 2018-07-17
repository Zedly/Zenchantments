package zedly.zenchantments.enchantments;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.Config;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import java.util.Map;

import static zedly.zenchantments.enums.Tool.*;

public class PotionResistance extends CustomEnchantment {

    public PotionResistance() {
        super(45);
        maxLevel = 4;
        loreName = "Potion Resistance";
        probability = 0;
        enchantable = new Tool[]{HELMET, CHESTPLATE, LEGGINGS, BOOTS};
        conflicting = new Class[]{};
        description = "Lessens the effects of all potions on players";
        cooldown = 0;
        power = 1.0;
        handUse = Hand.NONE;
    }

    @Override
    public boolean onPotionSplash(PotionSplashEvent evt, int level, boolean usedHand) {
        for(LivingEntity ent : evt.getAffectedEntities()) {
            if(ent instanceof Player) {
                int effect = 0;
                for(ItemStack stk : ((Player) ent).getInventory().getArmorContents()) {
                    Map<CustomEnchantment, Integer> map = Config.get(ent.getWorld()).getEnchants(stk);
                    for(CustomEnchantment e : map.keySet()) {
                        if(e.equals(this)) {
                            effect += map.get(e);
                        }
                    }
                }
                evt.setIntensity(ent, evt.getIntensity(ent) / ((effect * power + 1.3) / 2));
            }
        }
        return true;
    }
}
