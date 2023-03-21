package zedly.zenchantments.enchantments;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import static org.bukkit.Material.AIR;

@AZenchantment(runInSlots = Slots.ARMOR, conflicting = {})
public class HelpingHand extends Zenchantment {
    @Override
    public boolean onBlockInteract(final @NotNull PlayerInteractEvent event, final int level, final EquipmentSlot slot) {
        if(event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return false;
        }
        Material clickedMaterial = event.getClickedBlock().getType();

        if(Tag.MINEABLE_PICKAXE.isTagged(clickedMaterial)) {
            selectTool(event.getPlayer(), Tool.PICKAXE);
        } else if(Tag.MINEABLE_AXE.isTagged(clickedMaterial)) {
            selectTool(event.getPlayer(), Tool.AXE);
        } else if(Tag.MINEABLE_SHOVEL.isTagged(clickedMaterial)) {
            selectTool(event.getPlayer(), Tool.SHOVEL);
        } else if(Tag.MINEABLE_HOE.isTagged(clickedMaterial)) {
            selectTool(event.getPlayer(), Tool.HOE);
        } else {
            return false;
        }

        return true;
    }

    private void selectTool(Player player, Tool tool) {
        PlayerInventory inv = player.getInventory();
        Material[] preferredTool = tool.getMaterials();
        Material[] hotbarMats = new Material[9];
        for(int i = 0; i < 9; i++) {ItemStack is = inv.getItem(i);
            hotbarMats[i] = is == null ? AIR : is.getType();
        }

        for(int j = preferredTool.length -1; j >= 0; j--) {
            for(int i = 0; i < 9; i++) {
                if(hotbarMats[i] == preferredTool[j]) {
                    inv.setHeldItemSlot(i);
                    return;
                }
            }
        }

    }
}
