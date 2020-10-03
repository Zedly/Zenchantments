package zedly.zenchantments.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import zedly.zenchantments.Config;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

public class ListCommand extends ZenchantmentsCommand {
    public ListCommand(ZenchantmentsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Storage.logo + "You must be a player to do this!");
            return;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("zenchantments.command.list")) {
            player.sendMessage(Storage.logo + "You do not have permission to do this!");
            return;
        }

        player.sendMessage(Storage.logo + "Enchantment Types:");

        // TODO: Find a more efficient way of displaying the enchantments in alphabetical order.
        for (Zenchantment zenchantment : new TreeSet<>(Config.get(player.getWorld()).getEnchants())) {
            if (zenchantment.validMaterial(player.getInventory().getItemInMainHand())) {
                player.sendMessage(ChatColor.DARK_AQUA + "- " + ChatColor.AQUA + zenchantment.getName());
            }
        }
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}