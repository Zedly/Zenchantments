package zedly.zenchantments.arrows.enchanted;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import zedly.zenchantments.Storage;
import zedly.zenchantments.arrows.EnchantedArrow;

public class FireworkArrow extends EnchantedArrow {

	public FireworkArrow(Arrow entity, int level) {
		super(entity, level);
	}

	public void onImpact() {
		Location l = arrow.getLocation();
		FireworkEffect.Type[] type = {FireworkEffect.Type.BALL, FireworkEffect.Type.BURST, FireworkEffect.Type.STAR,
			FireworkEffect.Type.BALL_LARGE};
		FireworkEffect.Builder b = FireworkEffect.builder();
		b = b.withColor(Color.LIME).withColor(Color.RED).withColor(Color.BLUE).withColor(Color.YELLOW).withColor(
			Color.fromRGB(0xFF00FF)).withColor(Color.ORANGE).withColor(Color.fromRGB(0x3E89FF));
		b = b.trail(true);
		b = b.with(type[(getLevel() > 4 ? 4 : getLevel()) - 1]);
		final Firework f = (Firework) l.getWorld().spawnEntity(l, EntityType.FIREWORK);
		FireworkMeta d = f.getFireworkMeta();
		d.setPower(0);
		d.addEffect(b.build());
		f.setFireworkMeta(d);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
			f.detonate();
		}, 1);
		die();
	}
}
