package zedly.zenchantments.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.player.PlayerDataProvider;

import java.util.List;

import static zedly.zenchantments.I18n.translateString;

public class EnableCommand extends ZenchantmentsCommand {
    public EnableCommand(final @NotNull ZenchantmentsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(final @NotNull CommandSender sender, final @NotNull String[] args) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(translateString("message.must_be_player"));
            return;
        }

        if (!player.hasPermission("zenchantments.command.onoff")) {
            player.sendMessage(translateString("message.no_permission"));
            return;
        }

        if (args.length == 0) {
            player.sendMessage(translateString("message.command_usage", "/ench enable " + translateString("command.enable.usage")));
            return;
        }

        final var playerData = PlayerDataProvider.getDataForPlayer(player);
        final var commandString = String.join(" ", args);
        final var matcher = ENCHANT_COMMAND_PATTERN.matcher(commandString);
        if (!matcher.find()) {
            return;
        }

        final var zenchantmentName = matcher.group(1);
        final var zenchantment = this.plugin
            .getWorldConfigurationProvider()
            .getConfigurationForWorld(player.getWorld())
            .getZenchantmentFromName(zenchantmentName);

        if (zenchantment != null) {
            playerData.enableZenchantment(zenchantment.getKey());
            player.sendMessage(
                translateString("message.zenchantment_enabled", translateString("zenchantment." + zenchantment.getKey().getKey() + ".name"))
            );
        } else if (args[0].equalsIgnoreCase("all")) {
            playerData.enableAllZenchantments();
            player.sendMessage(translateString("message.all_zenchantments_enabled"));
        } else {
            player.sendMessage(translateString("message.zenchantment_not_found", args[0]));
        }
    }

    @Override
    @Nullable
    public List<String> getTabCompleteOptions(final @NotNull CommandSender sender, final @NotNull String[] args) {
        return ZenchantmentsPlugin.getInstance().getGlobalConfiguration().getDefaultWorldConfiguration().getEnchantNames();
    }
}
