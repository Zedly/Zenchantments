package zedly.zenchantments.event.listener;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import zedly.zenchantments.Zenchantment;

public interface EnchantmentFunction {

    public boolean run(Zenchantment ench, int level, EquipmentSlot slot);

}
