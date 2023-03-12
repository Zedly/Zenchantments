package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
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

import java.util.Collection;
import java.util.Set;

public final class QuickShot extends Zenchantment {
    public static final String KEY = "quick_shot";

    private static final String NAME = "Quick Shot";
    private static final String DESCRIPTION = "Shoots arrows at full speed, instantly";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand HAND_USE = Hand.RIGHT;

    private final NamespacedKey key;

    public QuickShot(
        final @NotNull Set<Tool> enchantable,
        final int maxLevel,
        final int cooldown,
        final double probability,
        final float power
    ) {
        super(enchantable, maxLevel, cooldown, probability, power);
        this.key = new NamespacedKey(ZenchantmentsPlugin.getInstance(), KEY);
    }

    @Override
    @NotNull
    public NamespacedKey getKey() {
        return this.key;
    }

    @Override
    @NotNull
    public String getName() {
        return NAME;
    }

    @Override
    @NotNull
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    @NotNull
    public Set<Class<? extends Zenchantment>> getConflicting() {
        return CONFLICTING;
    }

    @Override
    public Collection<EquipmentSlot> getApplyToSlots() {
        return Slots.HANDS;
    }

    @Override
    public boolean onEntityShootBow(final @NotNull EntityShootBowEvent event, final int level, final EquipmentSlot slot) {
        Player player = (Player) event.getEntity();
        PlayerInventory inv = player.getInventory();
        ItemStack bow = inv.getItem(slot);

        final QuickArrow arrow = new QuickArrow((AbstractArrow) event.getProjectile());
        ZenchantedArrow.putArrow((AbstractArrow) event.getProjectile(), arrow, (Player) event.getEntity());

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
