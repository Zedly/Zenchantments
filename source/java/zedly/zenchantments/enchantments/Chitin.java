package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Collection;
import java.util.Set;

public class Chitin extends Zenchantment {
    public static final String KEY = "chitin";

    private static final String                             NAME        = "Chitin";
    private static final String                             DESCRIPTION = "Hardens Elytra to protect the wearer against damage";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand HAND_USE    = Hand.NONE;

    private final NamespacedKey key;

    public Chitin(
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
        return Slots.ARMOR;
    }

    @Override
    public boolean onEntityDamage(final @NotNull EntityDamageEvent event, final int level, final EquipmentSlot slot) {
        switch(event.getCause()) {
            case STARVATION:
            case VOID:
            case SONIC_BOOM:
                return false;
        }

        double protectionLevelSum = 0;
        ItemStack[] armorProtection = ((Player)event.getEntity()).getInventory().getArmorContents();
        for(ItemStack is : armorProtection) {
            if (is == null) {
                continue;
            }
            protectionLevelSum += is.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
        }

        // The Protection enchantment adds up all armor levels, then each level reduces the damage by 4 percentage points.
        // This expression reduces damage as if Chitin was counted as Protection
        event.setDamage(event.getDamage() * (1 - 0.04 * protectionLevelSum - 0.04 * level) / (1 - 0.04 * protectionLevelSum));

        return true;
    }
}
