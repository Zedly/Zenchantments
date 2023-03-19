package zedly.zenchantments.api;

import org.bukkit.Keyed;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Slots;
import zedly.zenchantments.Tool;

import java.util.Collection;
import java.util.Set;

/**
 * Represents a zenchantment.
 */
public interface Zenchantment extends Keyed {
    /**
     * Gets the name of this {@link Zenchantment}.
     * <p>
     * This is also the text used in the lore of an item enchanted with it.
     *
     * @return A {@link String} representing the name of this {@link Zenchantment}.
     */
    @NotNull
    String getName();

    /**
     * Gets the description of this {@link Zenchantment}.
     * <p>
     * Depending on the configuration, this can appear in the lore of an item enchanted with it beneath the name/level.
     *
     * @return A {@link String} representing the description of this {@link Zenchantment}.
     */
    @NotNull
    String getDescription();

    /**
     * Gets the maximum possible level this {@link Zenchantment} can naturally obtain.
     *
     * @return An {@code int} representing the maximum possible level this {@link Zenchantment} can naturally obtain.
     */
    int getMaxLevel();

    /**
     * Gets the cooldown (in ticks) for this {@link Zenchantment}.
     * <p>
     * The default value is 0.
     *
     * @return An {@code int} representing the cooldown (in ticks) for this {@link Zenchantment}.
     */
    int getCooldown();

    /**
     * Gets the power multiplier for the effects of this {@link Zenchantment}.
     * <p>
     * The default value is 0. A value of -1 means there will be no effects.
     *
     * @return A {@code double} representing the power multiplier for the effects of this {@link Zenchantment}.
     */
    double getPower();

    /**
     * Gets the relative probability of obtaining this {@link Zenchantment}.
     *
     * @return A {@code float} representing the relative probability of obtaining this {@link Zenchantment}.
     */
    float getProbability();

    /**
     * Gets the {@link Tool Tools} that can receive and work with this {@link Zenchantment}.
     *
     * @return A {@link Set} of {@link Tool Tools} that can receive and work with this {@link Zenchantment}.
     */
    @NotNull
    Set<Tool> getEnchantable();

    /**
     * Gets the {@link Class Classes} of {@link Zenchantment Zenchantments} that conflict with this
     * {@link Zenchantment}.
     *
     * @return A {@link Set} of {@link Class Classes} of {@link Zenchantments} that conflict with this {@link Zenchantment}.
     */
    @NotNull
    Set<? extends Class<? extends Zenchantment>> getConflicting();

    /**
     * Gets the EquipmentSlots that this {@link Zenchantment} will be applied to.
     * Any event will only be observed by the Zenchantment if it is held in an EquipmentSlot listed in this Collection.
     *
     * @return A Collection<EquipmentSlot> representing the slots in which the Zenchantment will observe events.
     */
    Slots getApplyToSlots();
}
