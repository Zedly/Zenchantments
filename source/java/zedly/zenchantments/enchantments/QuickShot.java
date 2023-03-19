package zedly.zenchantments.enchantments;

import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.arrows.QuickArrow;
import zedly.zenchantments.arrows.ZenchantedArrow;

@AZenchantment(runInSlots = Slots.HANDS, conflicting = {})
public final class QuickShot extends Zenchantment {
    @Override
    public boolean onEntityShootBow(final @NotNull EntityShootBowEvent event, final int level, final EquipmentSlot slot) {
        Player player = (Player) event.getEntity();
        PlayerInventory inv = player.getInventory();
        ItemStack bow = inv.getItem(slot);

        final QuickArrow arrow = new QuickArrow((AbstractArrow) event.getProjectile());
        ZenchantedArrow.addZenchantedArrowToArrowEntity((AbstractArrow) event.getProjectile(), arrow, (Player) event.getEntity());

        if (bow.getType() == Material.CROSSBOW) {
            CrossbowMeta meta = (CrossbowMeta) bow.getItemMeta();
            ItemStack previousArrow = meta.getChargedProjectiles().get(0);
            if(previousArrow == null) {
                return false;
            }
            if (Utilities.removeMaterialsFromPlayer(player, (is) -> is != null && is.isSimilar(previousArrow), 1)){
                // An ostensible bug produces the desired behavior here:
                // Utilities changes the item's damage value, then writes the item back to the player inventory.
                // This written item overrides the state of the item after the projectile launch event has been passed.
                // This way, the crossbow is written back charged again, and with the correct amount of damage applied.
                Utilities.damageItemStackRespectUnbreaking(player, 1, slot);
            }
        }
        return true;
    }
}
