package zedly.zenchantments.command;

import org.bukkit.ChatColor;
import org.bukkit.World;
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

import static org.bukkit.Material.*;

public class EnchantCommand extends ZenchantmentsCommand {
    public EnchantCommand(@NotNull ZenchantmentsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ZenchantmentsCommand.MESSAGE_PREFIX + "You must be a player to do this!");
            return;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("zenchantments.command.enchant")) {
            player.sendMessage(ZenchantmentsCommand.MESSAGE_PREFIX + "You do not have permission to do this!");
            return;
        }

        final WorldConfiguration worldConfiguration = this.plugin
            .getWorldConfigurationProvider()
            .getConfigurationForWorld(player.getWorld());

        final Zenchantment zenchantment = worldConfiguration.getZenchantmentFromName(args[0]);

        if (zenchantment == null) {
            player.sendMessage(ZenchantmentsCommand.MESSAGE_PREFIX + "That enchantment does not exist!");
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
    public List<String> getTabCompleteOptions(@NotNull CommandSender sender, @NotNull String[] args) {
        return Collections.emptyList();
    }

    @NotNull
    @Contract(value = "_, _, _, _, _ -> param4", mutates = "param4")
    private ItemStack addEnchantments(
        @NotNull WorldConfiguration worldConfiguration,
        @NotNull Player player,
        @NotNull Zenchantment enchantment,
        @NotNull ItemStack itemStack,
        @NotNull String levelString
    ) {
        // Check if the player is holding an item
        if (itemStack.getType() == AIR) {
            player.sendMessage(ZenchantmentsCommand.MESSAGE_PREFIX + "You need to be holding an item!");
            return itemStack;
        }

        // Check if the item can be enchanted
        if (!enchantment.isValidMaterial(itemStack)
            && itemStack.getType() != BOOK
            && itemStack.getType() != ENCHANTED_BOOK
        ) {
            player.sendMessage(
                ZenchantmentsCommand.MESSAGE_PREFIX
                    + "The enchantment "
                    + ChatColor.DARK_AQUA
                    + enchantment.getName()
                    + ChatColor.AQUA +
                    " cannot be added to this item."
            );
            return itemStack;
        }

        // Get the level
        int level;
        try {
            level = Math.min(Integer.parseInt(levelString), enchantment.getMaxLevel());
        } catch (NumberFormatException ex) {
            level = 1;
        }

        enchantment.setForItemStack(itemStack, level, worldConfiguration);

        if (level != 0) {
            player.sendMessage(
                ZenchantmentsCommand.MESSAGE_PREFIX
                    + "The enchantment "
                    + ChatColor.DARK_AQUA
                    + enchantment.getName()
                    + ChatColor.AQUA +
                    " has been added."
            );
        } else {
            player.sendMessage(
                ZenchantmentsCommand.MESSAGE_PREFIX
                    + "The enchantment "
                    + ChatColor.DARK_AQUA
                    + enchantment.getName()
                    + ChatColor.AQUA
                    + " has been removed."
            );
        }

        return itemStack;
    }
}