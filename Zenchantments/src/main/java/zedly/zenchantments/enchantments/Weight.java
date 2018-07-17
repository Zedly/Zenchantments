package zedly.zenchantments.enchantments;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.potion.PotionEffectType.INCREASE_DAMAGE;
import static zedly.zenchantments.enums.Tool.BOOTS;

public class Weight extends CustomEnchantment {

    public Weight() {
        super(67);
        maxLevel = 4;
        loreName = "Weight";
        probability = 0;
        enchantable = new Tool[]{BOOTS};
        conflicting = new Class[]{Meador.class, Speed.class};
        description = "Slows the player down but makes them stronger and more resistant to knockback";
        cooldown = 0;
        power = 1.0;
        handUse = Hand.NONE;
    }

    @Override
    public boolean onBeingHit(EntityDamageByEntityEvent evt, int level, boolean usedHand) {
        if(!(evt.getEntity() instanceof LivingEntity) ||
           !ADAPTER.attackEntity((LivingEntity) evt.getEntity(), (Player) evt.getDamager(), 0)) {
            return true;
        }

        if(evt.getEntity() instanceof Player) {
            Player player = (Player) evt.getEntity();
            if(evt.getDamage() < player.getHealth()) {
                evt.setCancelled(true);
                player.damage(evt.getDamage());
                player.setVelocity(player.getLocation().subtract(evt.getDamager().getLocation()).toVector()
                                         .multiply((float) (1 / (level * power + 1.5))));
                ItemStack[] s = player.getInventory().getArmorContents();
                for(int i = 0; i < 4; i++) {
                    if(s[i] != null) {
                        Utilities.addUnbreaking(player, s[i], 1);
                        if(s[i].getDurability() > s[i].getType().getMaxDurability()) {
                            s[i] = null;
                        }
                    }
                }
                player.getInventory().setArmorContents(s);
            }
        }

        return true;
    }

    @Override
    public boolean onScan(Player player, int level, boolean usedHand
    ) {
        player.setWalkSpeed((float) (.164f - level * power * .014f));
        Utilities.addPotion(player, INCREASE_DAMAGE, 610, (int) Math.round(power * level));
        player.setMetadata("ze.speed", new FixedMetadataValue(Storage.zenchantments, true));
        return true;
    }
}
