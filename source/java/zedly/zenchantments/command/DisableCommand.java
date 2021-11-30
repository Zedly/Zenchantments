package zedly.zenchantments.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.List;

import static zedly.zenchantments.I18n.translateString;

public class DisableCommand extends ZenchantmentsCommand {
    public DisableCommand(final @NotNull ZenchantmentsPlugin plugin) {
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
            player.sendMessage(translateString("message.command_usage", "/ench disable " + translateString("command.disable.usage")));
            return;
        }

        final var playerData = this.plugin.getPlayerDataProvider().getDataForPlayer(player);
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
            playerData.disableZenchantment(zenchantment.getKey());
            player.sendMessage(
                translateString(
                    "message.zenchantment_disabled",
                    translateString("zenchantment." + zenchantment.getKey().getKey() + ".name")
                )
            );
        } else if (args[0].equalsIgnoreCase("all")) {
            playerData.disableAllZenchantments();
            player.sendMessage(translateString("message.all_zenchantments_disabled"));
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
