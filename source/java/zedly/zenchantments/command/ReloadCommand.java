package zedly.zenchantments.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.Collections;
import java.util.List;

public class ReloadCommand extends ZenchantmentsCommand {
    public ReloadCommand(final @NotNull ZenchantmentsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(final @NotNull CommandSender sender, final @NotNull String[] args) {
        if (!sender.hasPermission("zenchantments.command.reload")) {
            sender.sendMessage(MESSAGE_PREFIX + "You do not have permission to do this!");
            return;
        }

        this.plugin.getGlobalConfiguration().loadGlobalConfiguration();
        this.plugin.getWorldConfigurationProvider().loadWorldConfigurations();

        sender.sendMessage(MESSAGE_PREFIX + "Reloaded Zenchantments.");
    }

    @Override
    @Nullable
    public List<String> getTabCompleteOptions(final @NotNull CommandSender sender, final @NotNull String[] args) {
        return Collections.emptyList();
    }
}