package zedly.zenchantments.event;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ZenEntityShootBowEvent extends EntityShootBowEvent {
    public ZenEntityShootBowEvent(@NotNull LivingEntity shooter, @Nullable ItemStack bow, @Nullable ItemStack consumable, @NotNull Entity projectile, @NotNull EquipmentSlot hand, float force, boolean consumeItem) {
        super(shooter, bow, consumable, projectile, hand, force, consumeItem);
    }
}
