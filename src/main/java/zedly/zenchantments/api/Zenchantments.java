package zedly.zenchantments.api;

import org.bukkit.World;

import java.util.Set;

public interface Zenchantments {
    Set<Zenchantment> getZenchantmentsForWorld(World world);
}