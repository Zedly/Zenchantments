package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import static org.bukkit.potion.PotionEffectType.JUMP;
import static zedly.zenchantments.Tool.BOOTS;

public class Meador extends Zenchantment {

	public static final int ID = 36;

	@Override
	public Builder<Meador> defaults() {
		return new Builder<>(Meador::new, ID)
			.maxLevel(1)
			.name("Meador")
			.probability(0)
			.enchantable(new Tool[]{BOOTS})
			.conflicting(new Class[]{Weight.class, Speed.class, Jump.class})
			.description("Gives the player both a speed and jump boost")
			.cooldown(0)
			.power(1.0)
			.handUse(Hand.NONE);
	}

	@Override
	public boolean onScan(Player player, int level, boolean usedHand) {
		player.setWalkSpeed((float) Math.min(.5f + level * power * .05f, 1));
		player.setFlySpeed((float) Math.min(.5f + level * power * .05f, 1));
		player.setMetadata("ze.speed", new FixedMetadataValue(Storage.zenchantments, System.currentTimeMillis()));
		Utilities.addPotion(player, JUMP, 610, (int) Math.round(power * level + 2));
		return true;
	}
}
