package zedly.zenchantments.api.player;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface PlayerDataProvider {
    @NotNull
    @Contract(mutates = "this")
    PlayerData getDataForPlayer(@NotNull Player player);

    @Contract(mutates = "this")
    void resetDataForPlayer(@NotNull Player player);
}