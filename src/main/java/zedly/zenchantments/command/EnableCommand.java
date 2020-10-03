package zedly.zenchantments.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import zedly.zenchantments.Config;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.player.PlayerData;

import java.util.Collections;
import java.util.List;

public class EnableCommand extends ZenchantmentsCommand {
    public EnableCommand(ZenchantmentsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ZenchantmentsCommand.MESSAGE_PREFIX + "You must be a player to do this!");
            return;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("zenchantments.command.onoff")) {
            player.sendMessage(ZenchantmentsCommand.MESSAGE_PREFIX + "You do not have permission to do this!");
            return;
        }

        if (args.length == 0) {
            player.sendMessage(
                ZenchantmentsCommand.MESSAGE_PREFIX
                    + ChatColor.DARK_AQUA
                    + "Usage: "
                    + ChatColor.AQUA
                    + "/ench enable <enchantment/all>"
            );
            return;
        }

        PlayerData playerData = this.plugin.getPlayerDataProvider().getDataForPlayer(player);
        Zenchantment zenchantment = Config.get(player.getWorld()).enchantFromString(args[0]);

        if (zenchantment != null) {
            playerData.enableZenchantment(zenchantment.getId());
            player.sendMessage(
                ZenchantmentsCommand.MESSAGE_PREFIX
                    + "The zenchantment "
                    + ChatColor.DARK_AQUA
                    + zenchantment.getName()
                    + ChatColor.AQUA
                    + " has been"
                    + ChatColor.GREEN
                    + " enabled."
            );
        } else if (args[0].equalsIgnoreCase("all")) {
            playerData.enableAllZenchantments();
            player.sendMessage(
                ZenchantmentsCommand.MESSAGE_PREFIX
                    + ChatColor.DARK_AQUA
                    + "All "
                    + ChatColor.AQUA
                    + "zenchantments have been enabled."
            );
        } else {
            player.sendMessage(ZenchantmentsCommand.MESSAGE_PREFIX + "That zenchantment does not exist!");
        }
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}