package zedly.zenchantments.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.List;
import java.util.regex.Pattern;

public abstract class ZenchantmentsCommand {
    protected static final String MESSAGE_PREFIX =
        ChatColor.BLUE
            + "["
            + ChatColor.DARK_AQUA
            + "Zenchantments"
            + ChatColor.BLUE
            + "] "
            + ChatColor.AQUA;
    static final Pattern ENCHANT_COMMAND_PATTERN = Pattern.compile("^([^\\d]*[^\\d\\s])(?: (\\d+$))?");

    protected final ZenchantmentsPlugin plugin;

    public ZenchantmentsCommand(final @NotNull ZenchantmentsPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract void execute(final @NotNull CommandSender sender, final @NotNull String[] args);

    @Nullable
    public abstract List<String> getTabCompleteOptions(final @NotNull CommandSender sender, final @NotNull String[] args);
}
