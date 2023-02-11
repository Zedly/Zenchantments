package zedly.zenchantments.arrows;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.ZenchantmentsPlugin;

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
        final FireworkEffect.Builder builder = FireworkEffect.builder()
            .withColor(Color.LIME)
            .withColor(Color.RED)
            .withColor(Color.BLUE)
            .withColor(Color.YELLOW)
            .withColor(Color.fromRGB(0xFF00FF))
            .withColor(Color.ORANGE)
            .withColor(Color.fromRGB(0x3E89FF))
            .trail(true)
            .with(type[(Math.min(this.getLevel(), 4)) - 1]);

        final Location location = this.getArrow().getLocation();
        final Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);

        final FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.setPower(0);
        fireworkMeta.addEffect(builder.build());

        firework.setFireworkMeta(fireworkMeta);

        Bukkit.getScheduler().scheduleSyncDelayedTask(ZenchantmentsPlugin.getInstance(), firework::detonate, 1);

        this.die();
    }
}
