package zedly.zenchantments.enchantments;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

@AZenchantment(runInSlots = Slots.ARMOR , conflicting = {})
public class Caffeine extends Zenchantment {
    @Override
    public boolean onScan(final @NotNull Player player, final int level, final EquipmentSlot slot) {
        int insomniaTicks = player.getStatistic(Statistic.TIME_SINCE_REST);
        if(insomniaTicks > 5) {
            player.setStatistic(Statistic.TIME_SINCE_REST, insomniaTicks - level - 1);
        }
        return true;
    }
}
