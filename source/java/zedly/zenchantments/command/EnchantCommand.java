package zedly.zenchantments.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.configuration.GlobalConfiguration;
import zedly.zenchantments.configuration.WorldConfiguration;
import zedly.zenchantments.configuration.WorldConfigurationProvider;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.bukkit.Material.*;
import static zedly.zenchantments.I18n.translateString;

public class EnchantCommand extends ZenchantmentsCommand {

    public EnchantCommand(final @NotNull ZenchantmentsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(final @NotNull CommandSender sender, final @NotNull String[] args) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(translateString("message.must_be_player"));
            return;
        }

        if (!player.hasPermission("zenchantments.command.enchant")) {
            player.sendMessage(translateString("message.no_permission"));
            return;
        }

        final var commandString = String.join(" ", args);
        final var matcher = ENCHANT_COMMAND_PATTERN.matcher(commandString);
        if (!matcher.find()) {
            return;
        }

        final var worldConfiguration = WorldConfigurationProvider.getInstance()
            .getConfigurationForWorld(player.getWorld());

        final var zenchantmentName = matcher.group(1);
        final var zenchantment = worldConfiguration.getZenchantmentFromNameOrKey(zenchantmentName);
        final var levelString = matcher.group(2) != null ? matcher.group(2) : "1";

        if (zenchantment == null) {
            player.sendMessage(translateString("message.zenchantment_not_found", zenchantmentName));
            return;
        }

        player.getInventory().setItemInMainHand(
            this.addEnchantments(
                worldConfiguration,
                player,
                zenchantment,
                player.getInventory().getItemInMainHand(),
                levelString
            )
        );
    }

    @Override
    @Nullable
    public List<String> getTabCompleteOptions(final @NotNull CommandSender sender, final @NotNull String[] args) {
        if (!(sender instanceof final Player player)) {
            return Collections.emptyList();
        }

        if (!player.hasPermission("zenchantments.command.enchant")) {
            return Collections.emptyList();
        }

        final var commandString = String.join(" ", args);
        if (commandString.equals("")) {
            return fallbackToEnchantmentSuggestions("");
        }

        final var matcher = ENCHANT_COMMAND_PATTERN.matcher(commandString);
        if (!matcher.find()) {
            return Collections.emptyList();
        }

        final var worldConfiguration = WorldConfigurationProvider.getInstance()
            .getConfigurationForWorld(player.getWorld());

        final var zenchantmentName = matcher.group(1);
        final var zenchantment = worldConfiguration.getZenchantmentFromNameOrKey(zenchantmentName);

        if (zenchantment == null) {
            return this.fallbackToEnchantmentSuggestions(zenchantmentName);
        }

        return IntStream.rangeClosed(0, zenchantment.getMaxLevel()).mapToObj(String::valueOf).toList();
    }

    @NotNull
    private List<String> fallbackToEnchantmentSuggestions(final @NotNull String enchantString) {
        return GlobalConfiguration
            .getDefaultWorldConfiguration()
            .getEnchantNames()
            .stream()
            .filter(s -> s.startsWith(enchantString))
            .toList();
    }

    @NotNull
    @Contract(value = "_, _, _, _, _ -> param4", mutates = "param4")
    private ItemStack addEnchantments(
        final @NotNull WorldConfiguration worldConfiguration,
        final @NotNull Player player,
        final @NotNull Zenchantment zenchantment,
        final @NotNull ItemStack itemStack,
        final @NotNull String levelString
    ) {
        // Check if the player is holding an item.
        if (itemStack.getType() == AIR) {
            player.sendMessage(translateString("message.need_held_item"));
            return itemStack;
        }

        // Check if the item can be enchanted.
        if (!zenchantment.isValidMaterial(itemStack) && itemStack.getType() != BOOK && itemStack.getType() != ENCHANTED_BOOK) {
            player.sendMessage(
                translateString(
                    "message.zenchantment_illegal_for_item",
                    translateString("zenchantment." + zenchantment.getI18nKey() + ".name")
                )
            );
            return itemStack;
        }

        // Get the level.
        int level;
        try {
            level = Math.min(Integer.parseInt(levelString), zenchantment.getMaxLevel());
        } catch (NumberFormatException ex) {
            level = 1;
        }

        zenchantment.setForItemStack(itemStack, level, worldConfiguration);

        if (level != 0) {
            player.sendMessage(
                translateString(
                    "message.zenchantment_added",
                    translateString("zenchantment." + zenchantment.getI18nKey() + ".name")
                )
            );
        } else {
            player.sendMessage(
                translateString(
                    "message.zenchantment_removed",
                    translateString("zenchantment." + zenchantment.getI18nKey() + ".name")
                )
            );
        }

        return itemStack;
    }
}
