package zedly.zenchantments.command;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ZenchantmentsCommandHandler implements CommandExecutor, TabCompleter {
    private final EnchantCommand                    enchantCommand;
    private final Map<String, ZenchantmentsCommand> commandMap;

    public ZenchantmentsCommandHandler(final @NotNull ZenchantmentsPlugin plugin) {
        this.enchantCommand = new EnchantCommand(plugin);
        this.commandMap = new ImmutableMap.Builder<String, ZenchantmentsCommand>()
            .put("disable", new DisableCommand(plugin))
            .put("enable", new EnableCommand(plugin))
            .put("give", new GiveCommand(plugin))
            .put("help", new HelpCommand(plugin))
            .put("list", new ListCommand(plugin))
            .put("info", new InfoCommand(plugin))
            .put("reload", new ReloadCommand(plugin))
            .build();
    }

    @Override
    public boolean onCommand(
        final @NotNull CommandSender sender,
        final @NotNull Command command,
        final @NotNull String label,
        final @NotNull String[] args
    ) {
        String subcommand = "help";
        if (args.length > 0) {
            subcommand = args[0].toLowerCase();
        }

        if(this.commandMap.containsKey(subcommand)) {
            this.commandMap.get(subcommand).execute(sender, ArrayUtils.subarray(args, 1, args.length));
        } else {
            this.enchantCommand.execute(sender, args);
        }

        return true;
    }

    @Override
    @Nullable
    public List<String> onTabComplete(
        final @NotNull CommandSender sender,
        final @NotNull Command command,
        final @NotNull String label,
        final @NotNull String[] args
    ) {
        if (args.length == 0) {
            return Collections.emptyList();
        }

        final ZenchantmentsCommand zenchantmentsCommand = this.commandMap.get(args[0].toLowerCase());

        if (zenchantmentsCommand == null) {
            return Collections.emptyList();
        }

        return zenchantmentsCommand.getTabCompleteOptions(sender, ArrayUtils.subarray(args, 1, args.length));
    }
}
