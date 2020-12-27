package zedly.zenchantments.command;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.configuration.WorldConfiguration;
import zedly.zenchantments.player.PlayerData;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.bukkit.ChatColor.*;

public class InfoCommand extends ZenchantmentsCommand {
    public InfoCommand(final @NotNull ZenchantmentsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(final @NotNull CommandSender sender, final @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MESSAGE_PREFIX + "You must be a player to do this!");
            return;
        }

        final Player player = (Player) sender;

        if (!player.hasPermission("zenchantments.command.info")) {
            player.sendMessage(MESSAGE_PREFIX + "You do not have permission to do this!");
            return;
        }

        final World world = player.getWorld();
        final WorldConfiguration config = this.plugin
            .getWorldConfigurationProvider()
            .getConfigurationForWorld(world);
        final PlayerData playerData = this.plugin
            .getPlayerDataProvider()
            .getDataForPlayer(player);

        if (args.length > 0) {
            final Zenchantment zenchantment = config.getZenchantmentFromName(args[0]);
            if (zenchantment != null) {
                player.sendMessage(
                    MESSAGE_PREFIX
                        + zenchantment.getName()
                        + ": "
                        + (playerData.isDisabled(zenchantment.getKey()) ? RED + "**Disabled** " : "")
                        + AQUA + zenchantment.getDescription()
                );
            }
            return;
        }

        final Set<Zenchantment> zenchantments = Zenchantment.getZenchantmentsOnItemStack(
            player.getInventory().getItemInMainHand(),
            true,
            this.plugin.getGlobalConfiguration(),
            this.plugin.getWorldConfigurationProvider().getConfigurationForWorld(world)
        ).keySet();

        if (zenchantments.isEmpty()) {
            player.sendMessage(MESSAGE_PREFIX + "There are no zenchantments on this tool!");
            return;
        }

        player.sendMessage(MESSAGE_PREFIX + "Enchantment Info:");
        for (final Zenchantment zenchantment : zenchantments) {
            player.sendMessage(
                DARK_AQUA
                    + zenchantment.getName()
                    + ": "
                    + (playerData.isDisabled(zenchantment.getKey()) ? RED + "**Disabled** " : "")
                    + AQUA
                    + zenchantment.getDescription()
            );
        }
    }

    @Override
    @Nullable
    public List<String> getTabCompleteOptions(final @NotNull CommandSender sender, final @NotNull String[] args) {
        return Collections.emptyList();
    }
}