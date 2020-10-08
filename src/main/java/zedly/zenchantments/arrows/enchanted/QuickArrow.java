package zedly.zenchantments.arrows.enchanted;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zedly.zenchantments.arrows.EnchantedArrow;

import java.util.List;

public class QuickArrow extends EnchantedArrow {
    public QuickArrow(@NotNull Arrow entity) {
        super(entity);
    }

    @Override
    public void onLaunch(@NotNull LivingEntity player, @Nullable List<String> lore) {
        this.getArrow().setVelocity(this.getArrow().getVelocity().normalize().multiply(3.5f));
    }
}