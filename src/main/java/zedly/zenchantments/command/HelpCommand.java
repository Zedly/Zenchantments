package zedly.zenchantments.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.Collections;
import java.util.List;

public class HelpCommand extends ZenchantmentsCommand {
    public HelpCommand(@NotNull ZenchantmentsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        sender.sendMessage(ZenchantmentsCommand.MESSAGE_PREFIX);
        sender.sendMessage(
            ChatColor.DARK_AQUA
                + "- ench info <?enchantment>: "
                + ChatColor.AQUA
                + "Returns information about custom enchantments."
        );
        sender.sendMessage(
            ChatColor.DARK_AQUA
                + "- ench list: "
                + ChatColor.AQUA
                + "Returns a list of enchantments for the tool in hand."
        );
        sender.sendMessage(
            ChatColor.DARK_AQUA
                + "- ench give <Player> <Material> <enchantment> <?level> ... "
                + ChatColor.AQUA
                + "Gives the target a specified enchanted item."
        );
        sender.sendMessage(
            ChatColor.DARK_AQUA
                + "- ench <enchantment> <?level>: "
                + ChatColor.AQUA
                + "Enchants the item in hand with the given enchantment and level"
        );
        sender.sendMessage(
            ChatColor.DARK_AQUA
                + "- ench disable <enchantment/all>: "
                + ChatColor.AQUA
                + "Disables selected enchantment for the user"
        );
        sender.sendMessage(
            ChatColor.DARK_AQUA
                + "- ench enable <enchantment/all>: "
                + ChatColor.AQUA
                + "Enables selected enchantment for the user"
        );
    }

    @Override
    @Nullable
    public List<String> getTabCompleteOptions(@NotNull CommandSender sender, @NotNull String[] args) {
        return Collections.emptyList();
    }
}