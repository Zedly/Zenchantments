package zedly.zenchantments.command;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.configuration.WorldConfiguration;

import java.util.*;

import static org.bukkit.ChatColor.*;

public class GiveCommand extends ZenchantmentsCommand {
    public GiveCommand(final @NotNull ZenchantmentsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(final @NotNull CommandSender sender, final @NotNull String[] args) {
        if (!sender.hasPermission("zenchantments.command.give")) {
            sender.sendMessage(ZenchantmentsCommand.MESSAGE_PREFIX + "You do not have permission to do this!");
            return;
        }

        if (args.length < 3) {
            sender.sendMessage(MESSAGE_PREFIX + DARK_AQUA + "Usage: " + AQUA + "/ench give <Player> <Material> <enchantment> <?level> ...");
            return;
        }
        final Scanner scanner = new Scanner(
            Arrays.toString(args)
                .replace("[", "")
                .replace("]", "")
                .replace(",", " ")
        );

        final String playerName = scanner.next();
        Player recipient = null;

        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(playerName)) {
                recipient = player;
            }
        }

        if (recipient == null) {
            sender.sendMessage(MESSAGE_PREFIX + "The player " + DARK_AQUA + playerName + AQUA + " is not online or does not exist.");
            return;
        }

        Material material = null;
        if (!scanner.hasNextInt()) {
            material = Material.matchMaterial(scanner.next());
        }

        final World world = recipient.getWorld();
        final WorldConfiguration worldConfiguration = this.plugin
            .getWorldConfigurationProvider()
            .getConfigurationForWorld(world);

        if (material == null) {
            sender.sendMessage(MESSAGE_PREFIX + "The material " + DARK_AQUA + args[1].toUpperCase() + AQUA + " is not valid.");
            return;
        }

        final Map<Zenchantment, Integer> zenchantmentsToAdd = new HashMap<>();
        final Map<Enchantment, Integer> enchantmentsToAdd = new HashMap<>();
        final ItemStack itemStack = new ItemStack(material);

        while (scanner.hasNext()) {
            final String enchantName = scanner.next();
            int level = 1;
            if (scanner.hasNextInt()) {
                level = Math.max(1, scanner.nextInt());
            }

            final Zenchantment zenchantment = worldConfiguration.getZenchantmentFromName(enchantName);
            final Enchantment enchantment = Enchantment.getByName(enchantName.toUpperCase(Locale.ROOT));

            if (zenchantment != null) {
                if (zenchantment.isValidMaterial(material) || material == Material.ENCHANTED_BOOK) {
                    zenchantmentsToAdd.put(zenchantment, level);
                } else {
                    sender.sendMessage(
                        MESSAGE_PREFIX
                            + "The enchantment "
                            + DARK_AQUA
                            + zenchantment.getName()
                            + AQUA
                            + " cannot be given with this item."
                    );
                }
            } else if (enchantment != null) {
                if (enchantment.canEnchantItem(itemStack) && level <= enchantment.getMaxLevel()) {
                    enchantmentsToAdd.put(enchantment, level);
                } else {
                    sender.sendMessage(
                        ZenchantmentsCommand.MESSAGE_PREFIX
                            + "The enchantment "
                            + DARK_AQUA
                            + enchantment.getName()
                            + AQUA +
                            " cannot be given in this configuration."
                    );
                }
            } else {
                sender.sendMessage(MESSAGE_PREFIX + "The enchantment " + DARK_AQUA + enchantName + AQUA + " does not exist!");
            }
        }

        final StringBuilder message = new StringBuilder(MESSAGE_PREFIX + "Gave " + DARK_AQUA + recipient.getName() + AQUA + " the enchantments ");

        for (final Map.Entry<Zenchantment, Integer> zenchantment : zenchantmentsToAdd.entrySet()) {
            zenchantment.getKey().setForItemStack(itemStack, zenchantment.getValue(), worldConfiguration);
            message.append(stripColor(zenchantment.getKey().getName())).append(", ");
        }

        for (final Map.Entry<Enchantment, Integer> enchantment : enchantmentsToAdd.entrySet()) {
            itemStack.addEnchantment(enchantment.getKey(), enchantment.getValue());
            message.append(stripColor(enchantment.getKey().getName())).append(", ");
        }

        if (!zenchantmentsToAdd.isEmpty() || !enchantmentsToAdd.isEmpty()) {
            recipient.getInventory().addItem(itemStack);
            sender.sendMessage(message.substring(0, message.length() - 2) + ".");
        }
    }

    @Override
    @Nullable
    public List<String> getTabCompleteOptions(final @NotNull CommandSender sender, final @NotNull String[] args) {
        return Collections.emptyList();
    }
}
