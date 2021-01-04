package zedly.zenchantments.arrows;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.List;

public final class QuickArrow extends ZenchantedArrow {
    public QuickArrow(final @NotNull ZenchantmentsPlugin plugin, final @NotNull Arrow entity) {
        super(plugin, entity);
    }

    @Override
    public void onLaunch(final @NotNull LivingEntity player, final @Nullable List<String> lore) {
        this.getArrow().setVelocity(this.getArrow().getVelocity().normalize().multiply(3.5f));
    }
}
