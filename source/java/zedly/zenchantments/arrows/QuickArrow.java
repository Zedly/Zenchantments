package zedly.zenchantments.arrows;

import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class QuickArrow extends ZenchantedArrow {
    public QuickArrow(final @NotNull AbstractArrow entity) {
        super(entity);
    }

    @Override
    public void onLaunch(final @NotNull LivingEntity player, final @Nullable List<String> lore) {
        this.getArrow().setVelocity(this.getArrow().getVelocity().normalize().multiply(3.5f));
    }
}
