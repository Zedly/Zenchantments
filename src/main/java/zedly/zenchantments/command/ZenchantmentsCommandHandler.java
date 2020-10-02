package zedly.zenchantments.command;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ZenchantmentsCommandHandler implements CommandExecutor, TabCompleter {
    private final ZenchantmentsPlugin               plugin;
    private final Map<String, ZenchantmentsCommand> commandMap;

    public ZenchantmentsCommandHandler(ZenchantmentsPlugin plugin) {
        this.plugin = plugin;
        this.commandMap = ImmutableMap.of(
            "disable", new DisableCommand(plugin),
            "help", new HelpCommand(plugin),
            "list", new ListCommand(plugin),
            "info", new InfoCommand(plugin),
            "reload", new ReloadCommand(plugin)
        );
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String subcommand = "help";
        if (args.length > 0) {
            subcommand = args[0].toLowerCase();
        }

        ZenchantmentsCommand zenchantmentsCommand = this.commandMap.get(subcommand);

        if (zenchantmentsCommand == null) {
            sender.sendMessage("Unknown command! Type /" + label + " help for assistance.");
        } else {
            zenchantmentsCommand.execute(sender, ArrayUtils.subarray(args, 1, args.length));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return Collections.emptyList();
        }

        ZenchantmentsCommand zenchantmentsCommand = this.commandMap.get(args[0].toLowerCase());

        if (zenchantmentsCommand == null) {
            return Collections.emptyList();
        }

        return zenchantmentsCommand.getTabCompleteOptions(sender, ArrayUtils.subarray(args, 1, args.length));
    }
}