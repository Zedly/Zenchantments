package zedly.zenchantments.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.player.PlayerData;

import java.util.Collections;
import java.util.List;

public class DisableCommand extends ZenchantmentsCommand {
    public DisableCommand(final @NotNull ZenchantmentsPlugin plugin) {
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
            player.sendMessage(
                MESSAGE_PREFIX + ChatColor.DARK_AQUA + "Usage: " + ChatColor.AQUA + "/ench disable <enchantment/all>"
            );
            return;
        }

        final PlayerData playerData = this.plugin.getPlayerDataProvider().getDataForPlayer(player);
        final Zenchantment zenchantment = this.plugin
            .getWorldConfigurationProvider()
            .getConfigurationForWorld(player.getWorld())
            .getZenchantmentFromName(args[0]);

        if (zenchantment != null) {
            playerData.disableZenchantment(zenchantment.getKey());
            player.sendMessage(
                MESSAGE_PREFIX
                    + "The zenchantment "
                    + ChatColor.DARK_AQUA
                    + zenchantment.getName()
                    + ChatColor.AQUA
                    + " has been "
                    + ChatColor.RED
                    + "disabled."
            );
        } else if (args[0].equalsIgnoreCase("all")) {
            playerData.disableAllZenchantments();
            player.sendMessage(
                MESSAGE_PREFIX
                    + ChatColor.DARK_AQUA
                    + "All "
                    + ChatColor.AQUA
                    + "zenchantments have been "
                    + ChatColor.RED
                    + "disabled."
            );
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