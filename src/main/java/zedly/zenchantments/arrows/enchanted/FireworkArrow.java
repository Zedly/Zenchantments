package zedly.zenchantments.arrows.enchanted;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.arrows.ZenchantedArrow;

import static org.bukkit.FireworkEffect.Type.*;

public class FireworkArrow extends ZenchantedArrow {
    public FireworkArrow(@NotNull ZenchantmentsPlugin plugin, @NotNull Arrow entity, int level) {
        super(plugin, entity, level);
    }

    @Override
    public void onImpact() {
        FireworkEffect.Type[] type = {BALL, BURST, STAR, BALL_LARGE};
        FireworkEffect.Builder builder = FireworkEffect.builder()
            .withColor(Color.LIME)
            .withColor(Color.RED)
            .withColor(Color.BLUE)
            .withColor(Color.YELLOW)
            .withColor(Color.fromRGB(0xFF00FF))
            .withColor(Color.ORANGE)
            .withColor(Color.fromRGB(0x3E89FF))
            .trail(true)
            .with(type[(Math.min(this.getLevel(), 4)) - 1]);

        Location location = this.getArrow().getLocation();
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);

        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.setPower(0);
        fireworkMeta.addEffect(builder.build());

        firework.setFireworkMeta(fireworkMeta);

        this.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(this.getPlugin(), firework::detonate, 1);

        this.die();
    }
}