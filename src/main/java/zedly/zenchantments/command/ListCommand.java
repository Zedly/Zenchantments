package zedly.zenchantments.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListCommand extends ZenchantmentsCommand {
    public ListCommand(@NotNull ZenchantmentsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ZenchantmentsCommand.MESSAGE_PREFIX + "You must be a player to do this!");
            return;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("zenchantments.command.list")) {
            player.sendMessage(ZenchantmentsCommand.MESSAGE_PREFIX + "You do not have permission to do this!");
            return;
        }

        player.sendMessage(ZenchantmentsCommand.MESSAGE_PREFIX + "Enchantment Types:");

        this.plugin
            .getWorldConfigurationProvider()
            .getConfigurationForWorld(player.getWorld())
            .getZenchantments()
            .stream()
            .filter(zenchantment -> zenchantment.isValidMaterial(player.getInventory().getItemInMainHand()))
            .sorted(Comparator.comparing(Zenchantment::getName))
            .forEachOrdered(zenchantment -> player.sendMessage(ChatColor.DARK_AQUA + "- " + ChatColor.AQUA + zenchantment.getName()));
    }

    @Override
    @Nullable
    public List<String> getTabCompleteOptions(@NotNull CommandSender sender, @NotNull String[] args) {
        return Collections.emptyList();
    }
}