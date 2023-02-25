package zedly.zenchantments.command;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.configuration.WorldConfigurationProvider;

import java.util.*;

import static zedly.zenchantments.I18n.translateString;

public class GiveCommand extends ZenchantmentsCommand {
    public GiveCommand(final @NotNull ZenchantmentsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(final @NotNull CommandSender sender, final @NotNull String[] args) {
        if (!sender.hasPermission("zenchantments.command.give")) {
            sender.sendMessage(translateString("message.no_permission"));
            return;
        }

        if (args.length < 3) {
            sender.sendMessage(translateString("message.command_usage", "/ench give " + translateString("command.give.usage")));
            return;
        }

        final var scanner = new Scanner(
            Arrays.toString(args)
                .replace("[", "")
                .replace("]", "")
                .replace(",", " ")
        );

        final var playerName = scanner.next();
        Player recipient = null;

        for (final var player : this.plugin.getServer().getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(playerName)) {
                recipient = player;
            }
        }

        if (recipient == null) {
            sender.sendMessage(translateString("message.player_not_found", playerName));
            return;
        }

        Material material = null;
        if (!scanner.hasNextInt()) {
            material = Material.matchMaterial(scanner.next());
        }

        final var world = recipient.getWorld();
        final var worldConfiguration = WorldConfigurationProvider.getInstance()
            .getConfigurationForWorld(world);

        if (material == null) {
            sender.sendMessage(translateString("message.invalid_material", args[1]));
            return;
        }

        final var zenchantmentsToAdd = new HashMap<Zenchantment, Integer>();
        final var enchantmentsToAdd = new HashMap<Enchantment, Integer>();
        final var itemStack = new ItemStack(material);

        while (scanner.hasNext()) {
            final var enchantName = scanner.next();
            var level = 1;
            if (scanner.hasNextInt()) {
                level = Math.max(1, scanner.nextInt());
            }

            final var zenchantment = worldConfiguration.getZenchantmentFromNameOrKey(enchantName);
            final var enchantment = Enchantment.getByName(enchantName.toUpperCase(Locale.ROOT));

            if (zenchantment != null) {
                if (zenchantment.isValidMaterial(material) || material == Material.ENCHANTED_BOOK) {
                    zenchantmentsToAdd.put(zenchantment, level);
                } else {
                    sender.sendMessage(
                        translateString(
                            "message.zenchantment_illegal_with_item",
                            translateString("zenchantment." + zenchantment.getKey().getKey() + ".name")
                        )
                    );
                }
            } else if (enchantment != null) {
                if (enchantment.canEnchantItem(itemStack) && level <= enchantment.getMaxLevel()) {
                    enchantmentsToAdd.put(enchantment, level);
                } else {
                    sender.sendMessage(
                        translateString(
                            "message.enchantment_illegal_for_config",
                            enchantment.getName()
                        )
                    );
                }
            } else {
                sender.sendMessage(translateString("message.zenchantment_not_found", enchantName));
            }
        }

        final var enchantmentList = new StringBuilder();

        for (final var zenchantment : zenchantmentsToAdd.entrySet()) {
            zenchantment.getKey().setForItemStack(itemStack, zenchantment.getValue(), worldConfiguration);
            enchantmentList
                .append(translateString("zenchantment." + zenchantment.getKey().getKey().getKey() + ".name"))
                .append(", ");
        }

        for (final var enchantment : enchantmentsToAdd.entrySet()) {
            itemStack.addEnchantment(enchantment.getKey(), enchantment.getValue());
            enchantmentList
                .append(enchantment.getKey().getName())
                .append(", ");
        }

        if (!zenchantmentsToAdd.isEmpty() || !enchantmentsToAdd.isEmpty()) {
            recipient.getInventory().addItem(itemStack);
        }

        sender.sendMessage(translateString("message.given_enchantments", recipient.getName(), enchantmentList));
    }

    @Override
    @Nullable
    public List<String> getTabCompleteOptions(final @NotNull CommandSender sender, final @NotNull String[] args) {
        return Collections.emptyList();
    }
}
