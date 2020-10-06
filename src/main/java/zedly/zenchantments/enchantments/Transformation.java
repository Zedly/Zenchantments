package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public final class Transformation extends Zenchantment {
    public static final String KEY = "transformation";

    private static final String                             NAME        = "Transformation";
    private static final String                             DESCRIPTION = "Occasionally causes the attacked mob to be transformed into its similar cousin";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.LEFT;

    private final NamespacedKey key;

    public Transformation(
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
    public boolean onEntityHit(@NotNull EntityDamageByEntityEvent event, int level, boolean usedHand) {
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return false;
        }
        if (event.getEntity() instanceof Tameable) {
            if (((Tameable) event.getEntity()).isTamed()) {
                return false;
            }
        }
        if (!(event.getEntity() instanceof LivingEntity)) {
            return true;
        }

        LivingEntity entity = (LivingEntity) event.getEntity();
        if (this.hasValuableItems(entity)) {
            return true;
        }

        if (!ADAPTER.attackEntity(entity, (Player) event.getDamager(), 0)) {
            return true;
        }

        if (!(ThreadLocalRandom.current().nextInt(100) < (level * this.getPower() * 8))) {
            return true;
        }

        LivingEntity newEntity = Storage.COMPATIBILITY_ADAPTER.TransformationCycle(entity, ThreadLocalRandom.current());

        if (newEntity == null) {
            return true;
        }

        if (event.getDamage() > (entity).getHealth()) {
            event.setCancelled(true);
        }

        Utilities.display(Utilities.getCenter(event.getEntity().getLocation()), Particle.HEART, 70, 0.1f, 0.5f, 2, 0.5f);

        double originalHealth = (entity).getHealth();
        newEntity.setHealth(Math.max(1, Math.min(originalHealth, newEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())));
        event.getEntity().remove();

        return true;
    }

    private boolean hasValuableItems(LivingEntity entity) {
        if (entity.getEquipment() == null) {
            return false;
        }

        for (ItemStack stack : entity.getEquipment().getArmorContents()) {
            if (stack.hasItemMeta() && stack.getItemMeta().hasEnchants()) {
                return true;
            }

            switch (stack.getType()) {
                case AIR:
                    continue;
                case GOLDEN_SWORD:
                    if (entity.getType() != EntityType.ZOMBIFIED_PIGLIN) {
                        return true;
                    }
                    break;
                case BOW:
                    if (entity.getType() != EntityType.SKELETON) {
                        return true;
                    }
                    break;
                case STONE_SWORD:
                    if (entity.getType() != EntityType.WITHER_SKELETON) {
                        return true;
                    }
                    break;
                default:
                    return true;
            }
        }
        return false;
    }
}