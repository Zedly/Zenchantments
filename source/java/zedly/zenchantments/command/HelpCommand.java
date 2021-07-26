package zedly.zenchantments.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.Collections;
import java.util.List;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.DARK_AQUA;

public class HelpCommand extends ZenchantmentsCommand {
    public HelpCommand(final @NotNull ZenchantmentsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(final @NotNull CommandSender sender, final @NotNull String[] args) {
        sender.sendMessage(MESSAGE_PREFIX);
        sender.sendMessage(DARK_AQUA + "- ench info <?enchantment>: " + AQUA + "Returns information about custom enchantments.");
        sender.sendMessage(DARK_AQUA + "- ench list: " + AQUA + "Returns a list of enchantments for the tool in hand.");
        sender.sendMessage(DARK_AQUA + "- ench give <Player> <Material> <enchantment> <?level> ... " + AQUA + "Gives the target a specified enchanted item.");
        sender.sendMessage(DARK_AQUA + "- ench <enchantment> <?level>: " + AQUA + "Enchants the item in hand with the given enchantment and level");
        sender.sendMessage(DARK_AQUA + "- ench disable <enchantment/all>: " + AQUA + "Disables selected enchantment for the user");
        sender.sendMessage(DARK_AQUA + "- ench enable <enchantment/all>: " + AQUA + "Enables selected enchantment for the user");
    }

    @Override
    @Nullable
    public List<String> getTabCompleteOptions(final @NotNull CommandSender sender, final @NotNull String[] args) {
        return Collections.emptyList();
    }
}