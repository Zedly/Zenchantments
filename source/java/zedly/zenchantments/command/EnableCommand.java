package zedly.zenchantments.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.player.PlayerData;

import java.util.Collections;
import java.util.List;

import static org.bukkit.ChatColor.*;

public class EnableCommand extends ZenchantmentsCommand {
    public EnableCommand(final @NotNull ZenchantmentsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(final @NotNull CommandSender sender, final @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MESSAGE_PREFIX + "You must be a player to do this!");
            return;
        }

        final Player player = (Player) sender;

        if (!player.hasPermission("zenchantments.command.onoff")) {
            player.sendMessage(MESSAGE_PREFIX + "You do not have permission to do this!");
            return;
        }

        if (args.length == 0) {
            player.sendMessage(MESSAGE_PREFIX + DARK_AQUA + "Usage: " + AQUA + "/ench enable <enchantment/all>");
            return;
        }

        final PlayerData playerData = this.plugin.getPlayerDataProvider().getDataForPlayer(player);
        final Zenchantment zenchantment = this.plugin
            .getWorldConfigurationProvider()
            .getConfigurationForWorld(player.getWorld())
            .getZenchantmentFromName(args[0]);

        if (zenchantment != null) {
            playerData.enableZenchantment(zenchantment.getKey());
            player.sendMessage(
                MESSAGE_PREFIX + "The zenchantment " + DARK_AQUA + zenchantment.getName() + AQUA + " has been" + GREEN + " enabled."
            );
        } else if (args[0].equalsIgnoreCase("all")) {
            playerData.enableAllZenchantments();
            player.sendMessage(MESSAGE_PREFIX + DARK_AQUA + "All " + AQUA + "zenchantments have been enabled.");
        } else {
            player.sendMessage(MESSAGE_PREFIX + "That zenchantment does not exist!");
        }
    }

    @Override
    @Nullable
    public List<String> getTabCompleteOptions(final @NotNull CommandSender sender, final @NotNull String[] args) {
        return Collections.emptyList();
    }
}