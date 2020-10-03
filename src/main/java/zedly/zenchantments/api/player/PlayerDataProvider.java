package zedly.zenchantments.api.player;

import org.bukkit.entity.Player;

public interface PlayerDataProvider {
    PlayerData getDataForPlayer(Player player);

    void resetDataForPlayer(Player player);
}