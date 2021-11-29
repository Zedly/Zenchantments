package zedly.zenchantments.command;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static zedly.zenchantments.I18n.translateString;

public class ReloadCommand extends ZenchantmentsCommand {
    public ReloadCommand(final @NotNull ZenchantmentsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(final @NotNull CommandSender sender, final @NotNull String[] args) {
        if (!sender.hasPermission("zenchantments.command.reload")) {
            sender.sendMessage(translateString("message.no_permission"));
            return;
        }

        try {
            this.plugin.getGlobalConfiguration().loadGlobalConfiguration();
            this.plugin.getWorldConfigurationProvider().loadWorldConfigurations();
            sender.sendMessage(translateString("message.plugin_reloaded"));
        } catch (final IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            sender.sendMessage(translateString("message.plugin_reloaded_with_errors"));
        }
    }

    @Override
    @Nullable
    public List<String> getTabCompleteOptions(final @NotNull CommandSender sender, final @NotNull String[] args) {
        return Collections.emptyList();
    }
}
