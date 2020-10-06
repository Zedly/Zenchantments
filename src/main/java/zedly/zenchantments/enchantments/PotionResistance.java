package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.Map;
import java.util.Set;

public class PotionResistance extends Zenchantment {
    public static final String KEY = "potion_resistance";

    private static final String                             NAME        = "Potion Resistance";
    private static final String                             DESCRIPTION = "Lessens the effects of all potions on players";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.NONE;

    private final NamespacedKey key;

    public PotionResistance(
        @NotNull ZenchantmentsPlugin plugin,
        @NotNull Set<Tool> enchantable,
        int maxLevel,
        int cooldown,
        double power,
        float probability
    ) {
        super(plugin, enchantable, maxLevel, cooldown, power, probability);
        this.key = new NamespacedKey(plugin, KEY);
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
    @NotNull
    public Hand getHandUse() {
        return HAND_USE;
    }

    @Override
    public boolean onPotionSplash(@NotNull PotionSplashEvent event, int level, boolean usedHand) {
        for (LivingEntity entity : event.getAffectedEntities()) {
            if (!(entity instanceof Player)) {
                continue;
            }

            int effect = 0;

            for (ItemStack stack : ((Player) entity).getInventory().getArmorContents()) {
                Map<Zenchantment, Integer> map = Zenchantment.getEnchants(stack, entity.getWorld());
                for (Zenchantment zenchantment : map.keySet()) {
                    if (zenchantment.equals(this)) {
                        effect += map.get(zenchantment);
                    }
                }
            }

            event.setIntensity(entity, event.getIntensity(entity) / ((effect * this.getPower() + 1.3) / 2));
        }

        return true;
    }
}