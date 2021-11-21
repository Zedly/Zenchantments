package zedly.zenchantments.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.configuration.WorldConfiguration;

import java.util.Collections;
import java.util.List;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.DARK_AQUA;
import static org.bukkit.Material.*;

public class EnchantCommand extends ZenchantmentsCommand {
    public EnchantCommand(final @NotNull ZenchantmentsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(final @NotNull CommandSender sender, final @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MESSAGE_PREFIX + "You must be a player to do this!");
            return;
        }

        final Player player = (Player) sender;

        if (!player.hasPermission("zenchantments.command.enchant")) {
            player.sendMessage(MESSAGE_PREFIX + "You do not have permission to do this!");
            return;
        }

        final WorldConfiguration worldConfiguration = this.plugin
            .getWorldConfigurationProvider()
            .getConfigurationForWorld(player.getWorld());

        final Zenchantment zenchantment = worldConfiguration.getZenchantmentFromName(args[0]);

        if (zenchantment == null) {
            player.sendMessage(MESSAGE_PREFIX + "Enchantment " + args[0] + " does not exist!");
            return;
        }

        player.getInventory().setItemInMainHand(
            this.addEnchantments(
                worldConfiguration,
                player,
                zenchantment,
                player.getInventory().getItemInMainHand(),
                args.length >= 2 ? args[1] : "1"
            )
        );
    }

    @Override
    @Nullable
    public List<String> getTabCompleteOptions(final @NotNull CommandSender sender, final @NotNull String[] args) {
        return Collections.emptyList();
    }

    @NotNull
    @Contract(value = "_, _, _, _, _ -> param4", mutates = "param4")
    private ItemStack addEnchantments(
        final @NotNull WorldConfiguration worldConfiguration,
        final @NotNull Player player,
        final @NotNull Zenchantment enchantment,
        final @NotNull ItemStack itemStack,
        final @NotNull String levelString
    ) {
        // Check if the player is holding an item.
        if (itemStack.getType() == AIR) {
            player.sendMessage(MESSAGE_PREFIX + "You need to be holding an item!");
            return itemStack;
        }

        // Check if the item can be enchanted.
        if (!enchantment.isValidMaterial(itemStack)
            && itemStack.getType() != BOOK
            && itemStack.getType() != ENCHANTED_BOOK
        ) {
            player.sendMessage(
                MESSAGE_PREFIX + "The enchantment " + DARK_AQUA + enchantment.getName() + AQUA + " cannot be added to this item."
            );
            return itemStack;
        }

        // Get the level.
        int level;
        try {
            level = Math.min(Integer.parseInt(levelString), enchantment.getMaxLevel());
        } catch (NumberFormatException ex) {
            level = 1;
        }

        enchantment.setForItemStack(itemStack, level, worldConfiguration);

        if (level != 0) {
            player.sendMessage(
                MESSAGE_PREFIX + "The enchantment " + DARK_AQUA + enchantment.getName() + AQUA + " has been added."
            );
        } else {
            player.sendMessage(
                MESSAGE_PREFIX + "The enchantment " + DARK_AQUA + enchantment.getName() + AQUA + " has been removed."
            );
        }

        return itemStack;
    }
}
