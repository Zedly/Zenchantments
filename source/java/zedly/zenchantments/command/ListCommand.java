package zedly.zenchantments.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.Collections;
import java.util.List;

import static zedly.zenchantments.I18n.translateString;

public class ListCommand extends ZenchantmentsCommand {
    public ListCommand(final @NotNull ZenchantmentsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(final @NotNull CommandSender sender, final @NotNull String[] args) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(translateString("message.must_be_player"));
            return;
        }

        if (!player.hasPermission("zenchantments.command.list")) {
            sender.sendMessage(translateString("message.no_permission"));
            return;
        }

        player.sendMessage(translateString("message.zenchantment_list_header"));

        this.plugin
            .getWorldConfigurationProvider()
            .getConfigurationForWorld(player.getWorld())
            .getZenchantments()
            .stream()
            .filter(zenchantment -> zenchantment.isValidMaterial(player.getInventory().getItemInMainHand()))
            .map(zenchantment -> translateString("zenchantment." + zenchantment.getI18nKey() + ".name"))
            .sorted()
            .forEach(name -> player.sendMessage("- " + name));
    }

    @Override
    @Nullable
    public List<String> getTabCompleteOptions(final @NotNull CommandSender sender, final @NotNull String[] args) {
        return Collections.emptyList();
    }
}
