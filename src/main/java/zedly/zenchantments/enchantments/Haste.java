package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import static org.bukkit.potion.PotionEffectType.FAST_DIGGING;
import static zedly.zenchantments.Tool.*;

public class Haste extends Zenchantment {

	public static final int ID = 27;

	@Override
	public Builder<Haste> defaults() {
		return new Builder<>(Haste::new, ID)
			.maxLevel(4)
			.name("Haste")
			.probability(0)
			.enchantable(new Tool[]{PICKAXE, AXE, SHOVEL})
			.conflicting(new Class[]{})
			.description("Gives the player a mining boost")
			.cooldown(0)
			.power(1.0)
			.handUse(Hand.NONE);
	}

	@Override
	public boolean onScanHands(Player player, int level, boolean usedHand) {
		Utilities.addPotion(player, FAST_DIGGING, 610, (int) Math.round(level * power));
		player.setMetadata("ze.haste", new FixedMetadataValue(Storage.zenchantments, System.currentTimeMillis()));
		return false;
	}

}
