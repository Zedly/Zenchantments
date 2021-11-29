package zedly.zenchantments.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.Collections;
import java.util.List;

import static zedly.zenchantments.I18n.translateString;

public class HelpCommand extends ZenchantmentsCommand {
    public HelpCommand(final @NotNull ZenchantmentsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(final @NotNull CommandSender sender, final @NotNull String[] args) {
        sender.sendMessage("- ench info " + translateString("command.info.usage") + ": " + translateString("command.info.description"));
        sender.sendMessage("- ench list: " + translateString("command.list.description"));
        sender.sendMessage("- ench give " + translateString("command.give.usage") + ": " + translateString("command.give.description"));
        sender.sendMessage("- ench enable " + translateString("command.enable.usage") + ": " + translateString("command.enable.description"));
        sender.sendMessage("- ench disable " + translateString("command.disable.usage") + ": " + translateString("command.disable.description"));
        sender.sendMessage("- ench reload: " + translateString("command.reload.description"));
        sender.sendMessage("- ench " + translateString("command.enchant.usage") + ": " + translateString("command.enchant.description"));
    }

    @Override
    @Nullable
    public List<String> getTabCompleteOptions(final @NotNull CommandSender sender, final @NotNull String[] args) {
        return Collections.emptyList();
    }
}
