package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Collection;
import java.util.Set;

import static org.bukkit.potion.PotionEffectType.JUMP;

public final class Meador extends Zenchantment {
    public static final String KEY = "meador";

    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of(Weight.class, Speed.class, Jump.class);

    public Meador(
        final @NotNull Set<Tool> enchantable,
        final int maxLevel,
        final int cooldown,
        final double probability,
        final float power
    ) {
        super(enchantable, maxLevel, cooldown, probability, power, CONFLICTING, KEY);
    }

    @Override
    public Collection<EquipmentSlot> getApplyToSlots() {
        return Slots.ARMOR;
    }

    @Override
    public boolean onScan(final @NotNull Player player, final int level, final EquipmentSlot slot) {
        //final float speed = (float) Math.min(0.5f + level * this.getPower() * 0.05f, 1);
        final float speed = (float) Math.min((0.2f * this.getPower()) + 0.2f, 1);

        player.setWalkSpeed(speed);
        player.setFlySpeed(speed);

        player.setMetadata("ze.speed", new FixedMetadataValue(ZenchantmentsPlugin.getInstance(), System.currentTimeMillis()));

        Utilities.addPotionEffect(player, JUMP, 610, (int) Math.round(this.getPower() * level + 2));

        return true;
    }
}
