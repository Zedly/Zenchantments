package zedly.zenchantments.enchantments;

import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.Material.AIR;
import org.bukkit.entity.EntityType;
import static zedly.zenchantments.enums.Tool.SWORD;

public class Transformation extends CustomEnchantment {

    public static final int ID = 64;

    @Override
    public Builder<Transformation> defaults() {
        return new Builder<>(Transformation::new, ID)
                .maxLevel(3)
                .loreName("Transformation")
                .probability(0)
                .enchantable(new Tool[]{SWORD})
                .conflicting(new Class[]{})
                .description("Occasionally causes the attacked mob to be transformed into its similar cousin")
                .cooldown(0)
                .power(1.0)
                .handUse(Hand.LEFT);
    }

    @Override
    public boolean onEntityHit(EntityDamageByEntityEvent evt, int level, boolean usedHand) {
        if (evt.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return false;
        }
        if (evt.getEntity() instanceof Tameable) {
            if (((Tameable) evt.getEntity()).isTamed()) {
                return false;
            }
        }
        if (!(evt.getEntity() instanceof LivingEntity)) {
            return true;
        }

        LivingEntity le = (LivingEntity) evt.getEntity();
        if (hasValuableItems(le)) {
            return true;
        }

        if (ADAPTER.attackEntity(le, (Player) evt.getDamager(), 0)) {
            if (Storage.rnd.nextInt(100) < (level * power * 8)) {
                LivingEntity newEnt = Storage.COMPATIBILITY_ADAPTER.TransformationCycle(le,
                        Storage.rnd);
                if (newEnt != null) {
                    if (evt.getDamage() > (le).getHealth()) {
                        evt.setCancelled(true);
                    }
                    Utilities.display(Utilities.getCenter(evt.getEntity().getLocation()), Particle.HEART, 70, .1f,
                            .5f, 2, .5f);

                    double originalHealth = (le).getHealth();
                    newEnt.setHealth(Math.max(1,
                            Math.min(originalHealth, newEnt.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())));
                    evt.getEntity().remove();
                }
            }
        }
        return true;
    }

    private boolean hasValuableItems(LivingEntity le) {
        if (le.getEquipment() != null) {
            for (ItemStack stk : le.getEquipment().getArmorContents()) {
                if (stk.hasItemMeta() && stk.getItemMeta().hasEnchants()) {
                    return true;
                }
                switch (stk.getType()) {
                    case AIR:
                        continue;
                    case GOLDEN_SWORD:
                        if (le.getType() != EntityType.ZOMBIFIED_PIGLIN) {
                            return true;
                        }
                        break;
                    case BOW:
                        if (le.getType() != EntityType.SKELETON) {
                            return true;
                        }
                        break;
                    case STONE_SWORD:
                        if (le.getType() != EntityType.WITHER_SKELETON) {
                            return true;
                        }
                        break;
                    default:
                        return true;
                }
            }
        }
        return false;
    }
}
