package zedly.zenchantments;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;

// Basic Structure for both EnchantmentArrows and ElementalArrows
public interface AdvancedArrow {

    // Advances the Arrow's tick integer by one
    public void tick();

    // Returns the Arrow's current tick integer
    public int getTick();

    // Returns the projectile associated with the particular instance of the AdvancedArrow
    public Projectile getArrow();

    // Called when a user shoots an arrow and creates a new instance of the given AdvancedArrow
    public void onLaunch(LivingEntity player, List<String> lore);

    // Called repeatedly as the arrow is in flight
    public void onFlight();

    // Called when the arrow hits the ground
    public void onImpact();

    // Called when the arrow kills a LivingEntity
    public void onKill(EntityDeathEvent evt);

    // Called when the arrow hits an Entity
    public boolean onImpact(EntityDamageByEntityEvent evt);

}
