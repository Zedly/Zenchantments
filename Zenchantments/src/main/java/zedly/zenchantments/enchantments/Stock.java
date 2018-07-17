package zedly.zenchantments.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.Material.AIR;
import static zedly.zenchantments.enums.Tool.CHESTPLATE;

public class Stock extends CustomEnchantment {

    public Stock() {
        super(59);
        maxLevel = 1;
        loreName = "Stock";
        probability = 0;
        enchantable = new Tool[]{CHESTPLATE};
        conflicting = new Class[]{};
        description = "Refills the player's item in hand when they run out";
        cooldown = -1;
        power = -1.0;
        handUse = Hand.NONE;
    }

    @Override
    public boolean onBlockInteract(final PlayerInteractEvent evt, int level, boolean usedHand) {
        final ItemStack stk = evt.getPlayer().getInventory().getItemInMainHand().clone();
        if(stk == null || stk.getType() == AIR) {
            return false;
        }
        final Player player = evt.getPlayer();
        Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
            int current = -1;
            ItemStack newHandItem = evt.getPlayer().getInventory().getItemInMainHand();
            if(newHandItem != null && newHandItem.getType() != AIR) {
                return;
            }
            for(int i = 0; i < evt.getPlayer().getInventory().getContents().length; i++) {
                ItemStack s = player.getInventory().getContents()[i];
                if(s != null && s.getType().equals(stk.getType())) {
                    if(s.getData().getData() == stk.getData().getData()) {
                        current = i;
                        break;
                    }
                    current = i;
                }
            }
            if(current != -1) {
                evt.getPlayer().getInventory()
                   .setItemInMainHand(evt.getPlayer().getInventory().getContents()[current]);
                evt.getPlayer().getInventory().setItem(current, new ItemStack(AIR));
                evt.getPlayer().updateInventory();
            }
        }, 1);
        return false;
    }
}
