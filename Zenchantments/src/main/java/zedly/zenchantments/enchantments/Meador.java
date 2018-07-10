package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Tool;
import zedly.zenchantments.Utilities;

import static org.bukkit.potion.PotionEffectType.JUMP;
import static zedly.zenchantments.Tool.BOOTS;

public class Meador extends CustomEnchantment {

    public Meador() {
        maxLevel = 1;
        loreName = "Meador";
        probability = 0;
        enchantable = new Tool[]{BOOTS};
        conflicting = new Class[]{Weight.class, Speed.class, Jump.class};
        description = "Gives the player both a speed and jump boost";
        cooldown = 0;
        power = 1.0;
        handUse = 0;
    }

    public int getEnchantmentId() {
        return 36;
    }

    @Override
    public boolean onScan(Player player, int level, boolean usedHand) {
        player.setWalkSpeed((float) Math.min(.5f + level * power * .05f, 1));
        player.setFlySpeed((float) Math.min(.5f + level * power * .05f, 1));
        player.setMetadata("ze.speed", new FixedMetadataValue(Storage.zenchantments, true));
        Utilities.addPotion(player, JUMP, 610, (int) Math.round(power * level + 2));
        return true;
    }
}
