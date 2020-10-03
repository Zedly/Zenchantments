package zedly.zenchantments.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.List;

public abstract class ZenchantmentsCommand {
    protected static final String MESSAGE_PREFIX =
        ChatColor.BLUE
            + "["
            + ChatColor.DARK_AQUA
            + "Zenchantments"
            + ChatColor.BLUE
            + "] "
            + ChatColor.AQUA;

    protected final ZenchantmentsPlugin plugin;

    public ZenchantmentsCommand(ZenchantmentsPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract void execute(CommandSender sender, String[] args);

    public abstract List<String> getTabCompleteOptions(CommandSender sender, String[] args);
}