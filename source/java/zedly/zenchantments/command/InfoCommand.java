package zedly.zenchantments.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.player.PlayerDataProvider;

import java.util.List;

import static zedly.zenchantments.I18n.translateString;

public class InfoCommand extends ZenchantmentsCommand {
    public InfoCommand(final @NotNull ZenchantmentsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(final @NotNull CommandSender sender, final @NotNull String[] args) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(translateString("message.must_be_player"));
            return;
        }

        if (!player.hasPermission("zenchantments.command.info")) {
            sender.sendMessage(translateString("message.no_permission"));
            return;
        }

        final var world = player.getWorld();
        final var config = this.plugin.getWorldConfigurationProvider().getConfigurationForWorld(world);
        final var playerData = PlayerDataProvider.getDataForPlayer(player);

        if (args.length > 0) {
            final var zenchantment = config.getZenchantmentFromName(args[0]);
            if (zenchantment != null) {
                player.sendMessage(
                    translateString(
                        playerData.isDisabled(zenchantment.getKey()) ? "message.disabled_zenchantment_info" : "message.zenchantment_info",
                        translateString("zenchantment." + zenchantment.getKey().getKey() + ".name"),
                        translateString("zenchantment." + zenchantment.getKey().getKey() + ".description")
                    )
                );
            }
            return;
        }

        final var zenchantments = Zenchantment.getZenchantmentsOnItemStack(
            player.getInventory().getItemInMainHand(),
            true,
            this.plugin.getGlobalConfiguration(),
            this.plugin.getWorldConfigurationProvider().getConfigurationForWorld(world)
        ).keySet();

        if (zenchantments.isEmpty()) {
            player.sendMessage(translateString("message.no_zenchantments_on_item"));
            return;
        }

        player.sendMessage(translateString("message.zenchantment_info_header"));
        for (final var zenchantment : zenchantments) {
            player.sendMessage(
                translateString(
                    playerData.isDisabled(zenchantment.getKey()) ? "message.disabled_zenchantment_info" : "message.zenchantment_info",
                    translateString("zenchantment." + zenchantment.getKey().getKey() + ".name"),
                    translateString("zenchantment." + zenchantment.getKey().getKey() + ".description")
                )
            );
        }
    }

    @Override
    @Nullable
    public List<String> getTabCompleteOptions(final @NotNull CommandSender sender, final @NotNull String[] args) {
        return ZenchantmentsPlugin.getInstance().getGlobalConfiguration().getDefaultWorldConfiguration().getEnchantNames();
    }
}
