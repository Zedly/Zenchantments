package zedly.zenchantments.enchantments;

import org.bukkit.Particle;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.Material.*;
import static org.bukkit.entity.EntityType.EXPERIENCE_ORB;
import static zedly.zenchantments.enums.Tool.PICKAXE;

public class Extraction extends CustomEnchantment {

    public Extraction() {
        super(12);
        maxLevel = 3;
        loreName = "Extraction";
        probability = 0;
        enchantable = new Tool[]{PICKAXE};
        conflicting = new Class[]{Switch.class};
        description = "Smelts and yields more product from ores";
        cooldown = 0;
        power = 1.0;
        handUse = Hand.LEFT;
    }

    @Override
    public boolean onBlockBreak(BlockBreakEvent evt, final int level, boolean usedHand) {
        if(evt.getBlock().getType() == GOLD_ORE || evt.getBlock().getType() == IRON_ORE) {
            Utilities.damageTool(evt.getPlayer(), 1, usedHand);
            for(int x = 0; x < Storage.rnd.nextInt((int) Math.round(power * level + 1)) + 1; x++) {
                evt.getBlock().getWorld().dropItemNaturally(Utilities.getCenter(evt.getBlock()),
                                                            new ItemStack(evt.getBlock().getType() == GOLD_ORE ?
                                                                          GOLD_INGOT : IRON_INGOT));
            }
            ExperienceOrb o = (ExperienceOrb) evt.getBlock().getWorld()
                                                 .spawnEntity(Utilities.getCenter(evt.getBlock()), EXPERIENCE_ORB);
            o.setExperience(
                    evt.getBlock().getType() == IRON_ORE ? Storage.rnd.nextInt(5) + 1 : Storage.rnd.nextInt(5) + 3);
            evt.getBlock().setType(AIR);
            Utilities.display(Utilities.getCenter(evt.getBlock()), Particle.FLAME, 10, .1f, .5f, .5f, .5f);
            return true;
        }
        return false;
    }
}
