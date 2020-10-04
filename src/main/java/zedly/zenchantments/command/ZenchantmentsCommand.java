package zedly.zenchantments.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

    public ZenchantmentsCommand(@NotNull ZenchantmentsPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract void execute(@NotNull CommandSender sender, @NotNull String[] args);

    @Nullable
    public abstract List<String> getTabCompleteOptions(@NotNull CommandSender sender, @NotNull String[] args);
}