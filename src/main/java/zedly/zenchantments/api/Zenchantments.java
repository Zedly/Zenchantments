package zedly.zenchantments.api;

import org.bukkit.World;
import zedly.zenchantments.api.player.PlayerDataProvider;

import java.util.Set;

public interface Zenchantments {
    PlayerDataProvider getPlayerDataProvider();

    Set<Zenchantment> getZenchantmentsForWorld(World world);
}