package zedly.zenchantments.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import zedly.zenchantments.*;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.potion.PotionEffectType.CONFUSION;
import static org.bukkit.potion.PotionEffectType.HUNGER;
import static zedly.zenchantments.enums.Tool.BOW_;
import static zedly.zenchantments.enums.Tool.SWORD;

public class Toxic extends CustomEnchantment {

    public Toxic() {
        maxLevel = 4;
        loreName = "Toxic";
        probability = 0;
        enchantable = new Tool[]{BOW_, SWORD};
        conflicting = new Class[]{};
        description = "Sickens the target, making them nauseous and unable to eat";
        cooldown = 0;
        power = 1.0;
        handUse = 3;
    }

    public int getEnchantmentId() {
        return 62;
    }

    @Override
    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        EnchantArrow.ArrowEnchantToxic arrow =
                new EnchantArrow.ArrowEnchantToxic((Projectile) evt.getProjectile(), level, power);
        Utilities.putArrow(evt.getProjectile(), arrow, (Player) evt.getEntity());
        return true;
    }

    @Override
    public boolean onEntityHit(final EntityDamageByEntityEvent evt, int level, boolean usedHand) {
        if(!(evt.getEntity() instanceof LivingEntity) ||
           !ADAPTER.attackEntity((LivingEntity) evt.getEntity(), (Player) evt.getDamager(), 0)) {
            final int value = (int) Math.round(level * power);
            Utilities.addPotion((LivingEntity) evt.getEntity(), CONFUSION, 80 + 60 * value, 4);
            Utilities.addPotion((LivingEntity) evt.getEntity(), HUNGER, 40 + 60 * value, 4);
            if(evt.getEntity() instanceof Player) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
                    ((LivingEntity) evt.getEntity()).removePotionEffect(HUNGER);
                    Utilities.addPotion((LivingEntity) evt.getEntity(), HUNGER, 60 + 40 * value, 0);
                }, 20 + 60 * value);
                Storage.hungerPlayers.put((Player) evt.getEntity(), (1 + value) * 100);
            }
        }
        return true;
    }

}
