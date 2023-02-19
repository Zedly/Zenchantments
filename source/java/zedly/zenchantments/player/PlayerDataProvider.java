package zedly.zenchantments.player;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataProvider {
    private static final Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    private PlayerDataProvider() {
    }

    @NotNull
    @Contract(mutates = "this")
    public static PlayerData getDataForPlayer(@NotNull Player player) {
        PlayerData playerData = playerDataMap.get(player.getUniqueId());

        if (playerData == null) {
            playerData = new PlayerData(ZenchantmentsPlugin.getInstance(), player);
            playerDataMap.put(player.getUniqueId(), playerData);
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

    @Contract(mutates = "this")
    public static void resetDataForPlayer(@NotNull Player player) {
        playerDataMap.put(player.getUniqueId(), new PlayerData(ZenchantmentsPlugin.getInstance(), player));
    }
}
