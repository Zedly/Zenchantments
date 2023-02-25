package zedly.zenchantments;

import com.google.common.collect.Sets;
import org.bukkit.inventory.EquipmentSlot;

import java.util.*;

import static org.bukkit.inventory.EquipmentSlot.*;

public class Slots {

    public static final Collection<EquipmentSlot> ALL = of(HAND, OFF_HAND, HEAD, CHEST, LEGS, FEET);
    public static final Collection<EquipmentSlot> ARMOR = of(HEAD, CHEST, LEGS, FEET);
    public static final Collection<EquipmentSlot> HANDS = of(HAND, OFF_HAND);
    public static final Collection<EquipmentSlot> MAIN_HAND = of(HAND);

    public static Collection<EquipmentSlot> of(EquipmentSlot... slots) {
        return Sets.newHashSet(slots);
    }

}
