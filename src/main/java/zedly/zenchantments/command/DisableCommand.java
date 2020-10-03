package zedly.zenchantments.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import zedly.zenchantments.Config;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.player.PlayerData;

import java.util.Collections;
import java.util.List;

public class DisableCommand extends ZenchantmentsCommand {
    public DisableCommand(ZenchantmentsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Storage.logo + "You must be a player to do this!");
            return;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("zenchantments.command.onoff")) {
            player.sendMessage(Storage.logo + "You do not have permission to do this!");
            return;
        }

        if (args.length == 0) {
            player.sendMessage(
                Storage.logo
                    + ChatColor.DARK_AQUA
                    + "Usage: "
                    + ChatColor.AQUA +
                    "/ench disable <enchantment/all>"
            );
            return;
        }

        PlayerData playerData = this.plugin.getPlayerDataProvider().getDataForPlayer(player);
        Zenchantment zenchantment = Config.get(player.getWorld()).enchantFromString(args[0]);

        if (zenchantment != null) {
            playerData.disableZenchantment(zenchantment.getId());
            player.sendMessage(
                Storage.logo
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
                Storage.logo
                    + ChatColor.DARK_AQUA
                    + "All "
                    + ChatColor.AQUA
                    + "zenchantments have been "
                    + ChatColor.RED
                    + "disabled."
            );
        } else {
            player.sendMessage(Storage.logo + "That zenchantment does not exist!");
        }
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}