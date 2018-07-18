package zedly.zenchantments.enchantments;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.entity.EntityType.*;
import static zedly.zenchantments.enums.Tool.SWORD;

public class Decapitation extends CustomEnchantment {

    private static final int BASE_PLAYER_DROP_CHANCE = 150;
    private static final int BASE_MOB_DROP_CHANCE    = 150;

    public Decapitation() {
        super(11);
        maxLevel = 4;
        loreName = "Decapitation";
        probability = 0;
        enchantable = new Tool[]{SWORD};
        conflicting = new Class[]{};
        description = "Increases the chance for dropping the enemies head on death";
        cooldown = 0;
        power = 1.0;
        handUse = Hand.LEFT;
    }

    @Override
    public boolean onEntityKill(EntityDeathEvent evt, int level, boolean usedHand) {
        EntityType[] t = new EntityType[]{SKELETON, WITHER_SKULL, ZOMBIE, PLAYER, CREEPER};
        short id = (short) ArrayUtils.indexOf(t, evt.getEntityType());
        if(id == -1) {
            return false;
        }
        if(id == 3) {
            if(Storage.rnd.nextInt((int) Math.round(BASE_PLAYER_DROP_CHANCE / (level * power))) == 0) {
                ItemStack stk = new ItemStack(Material.SKULL_ITEM, 1, id);
                SkullMeta meta = (SkullMeta) stk.getItemMeta();
                meta.setOwner(evt.getEntity().getName());
                stk.setItemMeta(meta);
                evt.getEntity().getWorld().dropItemNaturally(evt.getEntity().getLocation(), stk);
            }
        } else if(Storage.rnd.nextInt((int) Math.round(BASE_MOB_DROP_CHANCE / level * power)) == 0) {
            ItemStack stk = new ItemStack(Material.SKULL_ITEM, 1, id);
            evt.getEntity().getWorld().dropItemNaturally(evt.getEntity().getLocation(), stk);
        }
        return false;
    }
}
