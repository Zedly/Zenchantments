package zedly.zenchantments.arrows;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public final class PotionArrow extends ZenchantedArrow {
    public PotionArrow(@NotNull ZenchantmentsPlugin plugin, @NotNull Arrow entity, int level, double power) {
        super(plugin, entity, level, power);
    }

    @Override
    public boolean onImpact(@NotNull EntityDamageByEntityEvent event) {
        if (ThreadLocalRandom.current().nextInt((int) Math.round(10 / (this.getLevel() * this.getPower() + 1))) == 1) {
            Utilities.addPotion(
                (LivingEntity) Objects.requireNonNull(this.getArrow().getShooter()),
                Storage.COMPATIBILITY_ADAPTER.PotionPotions().get(ThreadLocalRandom.current().nextInt(12)),
                150 + (int) Math.round(this.getLevel() * this.getPower() * 50),
                (int) Math.round(this.getLevel() * this.getPower())
            );
        }

        this.die();
        return true;
    }
}