package zedly.zenchantments.player;

import org.bukkit.entity.Player;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataProvider implements zedly.zenchantments.api.player.PlayerDataProvider {
    private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();
    private final ZenchantmentsPlugin   plugin;

    public PlayerDataProvider(ZenchantmentsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public PlayerData getDataForPlayer(Player player) {
        PlayerData playerData = this.playerDataMap.get(player.getUniqueId());

        if (playerData == null) {
            playerData = new PlayerData(this.plugin, player);
            this.playerDataMap.put(player.getUniqueId(), playerData);
        } else {
            // The PlayerData instance should always be updated with a fresh Player instance.
            // For example, if a Player was to log off and log back on again,
            // the internal 'player' field of the PlayerData instance would still point to the old Player.
            // Even though this is technically the same human player,
            // any methods that access the 'player' field will break.
            playerData.setPlayer(player);
        }

        return playerData;
    }

    @Override
    public void resetDataForPlayer(Player player) {
        this.playerDataMap.put(player.getUniqueId(), new PlayerData(this.plugin, player));
    }
}