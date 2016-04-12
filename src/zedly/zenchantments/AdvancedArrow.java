package zedly.zenchantments;

import java.util.List;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public interface AdvancedArrow {

    public void tick();

    public int getTick();

    public Projectile getArrow();

    public void onLaunch(LivingEntity player, List<String> lore);

    public void onFlight();

    public void onImpact();

    public void onKill(EntityDeathEvent evt);

    public boolean onImpact(EntityDamageByEntityEvent evt);

}
