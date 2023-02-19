package zedly.zenchantments.arrows;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.FireworkEffect.Type.*;

public final class FireworkArrow extends ZenchantedArrow {
    public FireworkArrow(
        final @NotNull AbstractArrow entity,
        final int level
    ) {
        super(entity, level);
    }

    @Override
    public void onImpact() {
        final FireworkEffect.Type[] type = { BALL, BURST, STAR, BALL_LARGE };
        final FireworkEffect.Builder builder = FireworkEffect.builder();
        if(ThreadLocalRandom.current().nextBoolean()) {
            builder.withColor(Color.LIME);
        }
        if(ThreadLocalRandom.current().nextBoolean()) {
            builder.withColor(Color.RED);
        }
        if(ThreadLocalRandom.current().nextBoolean()) {
            builder.withColor(Color.BLUE);
        }
        if(ThreadLocalRandom.current().nextBoolean()) {
            builder.withColor(Color.ORANGE);
        }
        if(ThreadLocalRandom.current().nextBoolean()) {
            builder.withColor(Color.YELLOW);
        }
        if(ThreadLocalRandom.current().nextBoolean()) {
            builder.withColor(Color.fromRGB(0xFF00FF));
        }
        if(ThreadLocalRandom.current().nextBoolean()) {
            builder.withColor(Color.fromRGB(0x3E89FF));
        }
        builder.trail(ThreadLocalRandom.current().nextBoolean())
            .flicker(ThreadLocalRandom.current().nextBoolean())
            .with(type[(ThreadLocalRandom.current().nextInt(Math.min(this.getLevel(), 4)))]);

        final Location location = this.getArrow().getLocation();
        final Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        firework.setSilent(true);
        final FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.setPower(0);
        fireworkMeta.addEffect(builder.build());
        firework.setFireworkMeta(fireworkMeta);
        Bukkit.getScheduler().scheduleSyncDelayedTask(ZenchantmentsPlugin.getInstance(), firework::detonate, 0);

        this.die();
    }
}
