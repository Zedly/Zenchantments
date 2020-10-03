package zedly.zenchantments.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.configuration.WorldConfiguration;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.Collections;
import java.util.List;

import static org.bukkit.Material.*;

public class EnchantCommand extends ZenchantmentsCommand {
    public EnchantCommand(ZenchantmentsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ZenchantmentsCommand.MESSAGE_PREFIX + "You must be a player to do this!");
            return;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("zenchantments.command.enchant")) {
            player.sendMessage(ZenchantmentsCommand.MESSAGE_PREFIX + "You do not have permission to do this!");
            return;
        }

        WorldConfiguration config = WorldConfiguration.get(player.getWorld());
        Zenchantment zenchantment = config.enchantFromString(args[0]);

        if (zenchantment == null) {
            player.sendMessage(ZenchantmentsCommand.MESSAGE_PREFIX + "That enchantment does not exist!");
            return;
        }

        player.getInventory().setItemInMainHand(
            this.addEnchantments(
                config,
                player,
                zenchantment,
                player.getInventory().getItemInMainHand(),
                args.length >= 2 ? args[1] : "1"
            )
        );
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    private ItemStack addEnchantments(
        WorldConfiguration config,
        Player player,
        Zenchantment enchantment,
        ItemStack itemStack,
        String levelString
    ) {
        if (config == null) {
            return itemStack;
        }

        // Check if the player is holding an item
        if (itemStack.getType() == AIR) {
            if (player != null) {
                player.sendMessage(ZenchantmentsCommand.MESSAGE_PREFIX + "You need to be holding an item!");
            }
            return itemStack;
        }

        // Check if the item can be enchanted
        if (!enchantment.validMaterial(itemStack)
            && itemStack.getType() != BOOK
            && itemStack.getType() != ENCHANTED_BOOK
        ) {
            if (player != null) {
                player.sendMessage(
                    ZenchantmentsCommand.MESSAGE_PREFIX
                        + "The enchantment "
                        + ChatColor.DARK_AQUA
                        + enchantment.getName()
                        + ChatColor.AQUA +
                        " cannot be added to this item."
                );
            }
            return itemStack;
        }

        // Get the level
        int level;
        try {
            level = Math.min(Integer.parseInt(levelString), enchantment.getMaxLevel());
        } catch (NumberFormatException ex) {
            level = 1;
        }

        enchantment.setEnchantment(itemStack, level, config.getWorld());

        if (level != 0) {
            if (player != null) {
                player.sendMessage(
                    ZenchantmentsCommand.MESSAGE_PREFIX
                        + "The enchantment "
                        + ChatColor.DARK_AQUA
                        + enchantment.getName()
                        + ChatColor.AQUA +
                        " has been added."
                );
            }
        } else {
            if (player != null) {
                player.sendMessage(
                    ZenchantmentsCommand.MESSAGE_PREFIX
                        + "The enchantment "
                        + ChatColor.DARK_AQUA
                        + enchantment.getName()
                        + ChatColor.AQUA
                        + " has been removed."
                );
            }
        }

        return itemStack;
    }
}