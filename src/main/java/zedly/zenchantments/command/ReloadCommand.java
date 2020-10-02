package zedly.zenchantments.command;

import org.bukkit.command.CommandSender;
import zedly.zenchantments.Storage;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.Collections;
import java.util.List;

public class ReloadCommand extends ZenchantmentsCommand {
    public ReloadCommand(ZenchantmentsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("zenchantments.command.reload")) {
            sender.sendMessage(Storage.logo + "You do not have permission to do this!");
            return;
        }

        this.plugin.loadConfigs();
        sender.sendMessage(Storage.logo + "Reloaded Zenchantments.");
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}