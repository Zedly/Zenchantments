package zedly.zenchantments.enchantments;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static zedly.zenchantments.enums.Tool.BOOTS;

public class Speed extends CustomEnchantment {

    public Speed() {
        super(55);
        maxLevel = 4;
        loreName = "Speed";
        probability = 0;
        enchantable = new Tool[]{BOOTS};
        conflicting = new Class[]{Meador.class, Weight.class};
        description = "Gives the player a speed boost";
        cooldown = 0;
        power = 1.0;
        handUse = Hand.NONE;
    }

    @Override
    public boolean onScan(Player player, int level, boolean usedHand) {
        player.setWalkSpeed((float) Math.min((.05f * level * power) + .2f, 1));
        player.setFlySpeed((float) Math.min((.05f * level * power) + .2f, 1));
        player.setMetadata("ze.speed", new FixedMetadataValue(Storage.zenchantments, true));
        return true;
    }
}
