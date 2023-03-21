package zedly.zenchantments.enchantments;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.configuration.WorldConfigurationProvider;

import java.util.Map;

@AZenchantment(runInSlots = Slots.ARMOR, conflicting = {})
public final class PotionResistance extends Zenchantment {
    @Override
    public boolean onPotionSplash(final @NotNull PotionSplashEvent event, final int level, final EquipmentSlot slot) {
        for (final LivingEntity entity : event.getAffectedEntities()) {
            if (!(entity instanceof Player)) {
                continue;
            }

            int effect = 0;

            for (final ItemStack stack : ((Player) entity).getInventory().getArmorContents()) {
                final Map<Zenchantment, Integer> map = Zenchantment.getZenchantmentsOnItemStack(
                    stack,
                    WorldConfigurationProvider.getInstance().getConfigurationForWorld(entity.getWorld())
                );

                for (final Zenchantment zenchantment : map.keySet()) {
                    if (zenchantment.equals(this)) {
                        effect += map.get(zenchantment);
                    }
                }
            }

            event.setIntensity(entity, event.getIntensity(entity) / ((effect * this.getPower() + 1.3) / 2));
        }

        return true;
    }
}
