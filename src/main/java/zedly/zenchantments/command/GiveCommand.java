package zedly.zenchantments.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.Config;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.*;

public class GiveCommand extends ZenchantmentsCommand {
    public GiveCommand(ZenchantmentsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("zenchantments.command.give")) {
            sender.sendMessage(Storage.logo + "You do not have permission to do this!");
            return;
        }

        if (args.length < 3) {
            sender.sendMessage(
                Storage.logo
                    + ChatColor.DARK_AQUA
                    + "Usage: "
                    + ChatColor.AQUA
                    + "/ench give <Player> <Material> <enchantment> <?level> ..."
            );
            return;
        }
        Scanner scanner = new Scanner(
            Arrays.toString(args)
                .replace("[", "")
                .replace("]", "")
                .replace(",", " ")
        );
        scanner.next();

        String playerName = scanner.next();
        Player recipient = null;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(playerName)) {
                recipient = player;
            }
        }

        if (recipient == null) {
            sender.sendMessage(
                Storage.logo
                    + "The player "
                    + ChatColor.DARK_AQUA
                    + playerName
                    + ChatColor.AQUA
                    + " is not online or does not exist."
            );
            return;
        }

        Material material = null;
        if (!scanner.hasNextInt()) {
            material = Material.matchMaterial(scanner.next());
        }

        Config config = Config.get(recipient.getWorld());

        if (material == null) {
            sender.sendMessage(
                Storage.logo
                    + "The material "
                    + ChatColor.DARK_AQUA
                    + args[1].toUpperCase()
                    + ChatColor.AQUA +
                    " is not valid."
            );
            return;
        }

        Map<Zenchantment, Integer> zenchantmentsToAdd = new HashMap<>();
        Map<Enchantment, Integer> enchantmentsToAdd = new HashMap<>();
        ItemStack itemStack = new ItemStack(material);

        while (scanner.hasNext()) {
            String enchantName = scanner.next();
            int level = 1;
            if (scanner.hasNextInt()) {
                level = Math.max(1, scanner.nextInt());
            }

            Zenchantment zenchantment = config.enchantFromString(enchantName);
            Enchantment enchantment = Enchantment.getByName(enchantName);

            if (zenchantment != null) {
                if (zenchantment.validMaterial(material) || material == Material.ENCHANTED_BOOK) {
                    zenchantmentsToAdd.put(zenchantment, level);
                } else {
                    sender.sendMessage(
                        Storage.logo
                            + "The enchantment "
                            + ChatColor.DARK_AQUA
                            + zenchantment.getLoreName()
                            + ChatColor.AQUA
                            + " cannot be given with this item."
                    );
                }
            } else if (enchantment != null) {
                if (enchantment.canEnchantItem(itemStack) && level <= enchantment.getMaxLevel()) {
                    enchantmentsToAdd.put(enchantment, level);
                } else {
                    sender.sendMessage(
                        Storage.logo
                            + "The enchantment "
                            + ChatColor.DARK_AQUA
                            + enchantment.getName()
                            + ChatColor.AQUA +
                            " cannot be given in this configuration."
                    );
                }
            } else {
                sender.sendMessage(
                    Storage.logo +
                        "The enchantment "
                        + ChatColor.DARK_AQUA
                        + enchantName +
                        ChatColor.AQUA +
                        " does not exist!"
                );
            }
        }

        StringBuilder message = new StringBuilder(
            Storage.logo
                + "Gave "
                + ChatColor.DARK_AQUA
                + recipient.getName()
                + ChatColor.AQUA
                + " the enchantments "
        );

        for (Map.Entry<Zenchantment, Integer> zenchantment : zenchantmentsToAdd.entrySet()) {
            zenchantment.getKey().setEnchantment(itemStack, zenchantment.getValue(), config.getWorld());
            message.append(ChatColor.stripColor(zenchantment.getKey().getLoreName())).append(", ");
        }

        for (Map.Entry<Enchantment, Integer> enchantment : enchantmentsToAdd.entrySet()) {
            itemStack.addEnchantment(enchantment.getKey(), enchantment.getValue());
            message.append(ChatColor.stripColor(enchantment.getKey().getName())).append(", ");
        }

        if (!zenchantmentsToAdd.isEmpty() && !enchantmentsToAdd.isEmpty()) {
            recipient.getInventory().addItem(itemStack);
            sender.sendMessage(message.substring(0, message.length() - 2) + ".");
        }
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}