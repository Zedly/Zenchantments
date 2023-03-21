package zedly.zenchantments;

import com.google.common.collect.Sets;
import org.bukkit.inventory.EquipmentSlot;

import java.util.*;

import static org.bukkit.inventory.EquipmentSlot.*;

public enum Slots {
    ALL(HAND, OFF_HAND, HEAD, CHEST, LEGS, FEET),
    ARMOR(HEAD, CHEST, LEGS, FEET),
    HANDS(HAND, OFF_HAND),
    MAIN_HAND(HAND),
    NONE();

    private final Collection<EquipmentSlot> slots;

    public static Collection<EquipmentSlot> of(EquipmentSlot... slots) {
        return Sets.newHashSet(slots);
    }

    Slots(EquipmentSlot... slots) {
        this.slots = Sets.newHashSet(slots);
    }

    public boolean contains(EquipmentSlot slot) {
        return slots.contains(slot);
    }
}
