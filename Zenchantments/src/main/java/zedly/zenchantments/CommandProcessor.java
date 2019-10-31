package zedly.zenchantments;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.enums.Tool;

import java.util.*;

import static org.bukkit.Material.*;

// This class handles all commands used by this plugin
public class CommandProcessor {

    public static class TabCompletion implements TabCompleter {

        @Override
        public List<String> onTabComplete(CommandSender sender, Command commandlabel, String alias, String[] args) {
            if (args.length == 0 || !(sender instanceof Player)) {
                return null;
            }

            EnchantPlayer player = EnchantPlayer.matchPlayer((Player) sender);
            Config config = Config.get(player.getPlayer().getWorld());
            ItemStack stack = player.getPlayer().getInventory().getItemInMainHand();
            String label = args[0].toLowerCase();
            List<String> results = new LinkedList<>();

            switch (label) {
                case "reload":
                case "list":
                case "help":
                    break;
                case "give":
                    if (args.length == 2) {
                        for (Player plyr : Bukkit.getOnlinePlayers()) {
                            if (plyr.getPlayerListName().toLowerCase().startsWith(args[1].toLowerCase())) {
                                results.add(plyr.getPlayerListName());
                            }
                        }
                    } else if (args.length == 3) {
                        for (Material mat : Tool.ALL.getMaterials()) {
                            if (mat.toString().toLowerCase().startsWith(args[2].toLowerCase())) {
                                results.add(mat.toString());
                            }
                        }
                        // TODO: Fix out of bounds error below
                    } else if (args.length > 1 && config.enchantFromString(args[args.length - 2]) != null) {
                        CustomEnchantment ench = config.enchantFromString(args[args.length - 2]);
                        for (int i = 1; i <= ench.getMaxLevel(); i++) {
                            results.add(i + "");
                        }
                    } else if (args.length != 1) {
                        for (Map.Entry<String, CustomEnchantment> ench : config.getSimpleMappings()) {
                            if (ench.getKey().startsWith(args[args.length - 1]) && (stack.getType() == BOOK
                                    || stack.getType() == ENCHANTED_BOOK
                                    || ench.getValue().validMaterial(Material.matchMaterial(args[2])))) {
                                results.add(ench.getKey());
                            }
                        }
                    }
                    break;
                case "disable":
                case "enable":
                    results.add("all");
                case "info":
                    results = config.getEnchantNames();
                    if (args.length > 1) {
                        results.removeIf(e -> !e.startsWith(args[1]));
                    }
                    break;
                default:

                    if (args.length == 1) {
                        for (Map.Entry<String, CustomEnchantment> ench : config.getSimpleMappings()) {
                            if (ench.getKey().startsWith(args[0]) && (stack.getType() == BOOK
                                    || stack.getType() == ENCHANTED_BOOK || ench.getValue().validMaterial(
                                    stack.getType())
                                    || stack.getType() == AIR)) {
                                results.add(ench.getKey());
                            }
                        }
                    } else if (args.length == 2) {
                        CustomEnchantment ench = config.enchantFromString(args[0]);
                        if (ench != null) {
                            for (int i = 1; i <= ench.getMaxLevel(); i++) {
                                results.add(i + "");
                            }
                        }

                    }
            }
            return results;
        }
    }

    // Adds or removes the given enchantment of the given level to the item stack
    static ItemStack addEnchantments(Config config, Player player, CustomEnchantment enchantment, ItemStack stack,
            String levelStr, boolean isHeld) {
        if (config == null) {
            return stack;
        }

        // Check if the player is holding an item
        if (stack.getType() == AIR) {
            if (player != null) {
                player.sendMessage(Storage.logo + "You need to be holding an item!");
            }
            return stack;
        }

        // Check if the item can be enchanted
        if (!enchantment.validMaterial(stack) && stack.getType() != BOOK && stack.getType() != ENCHANTED_BOOK) {
            if (player != null) {
                player.sendMessage(Storage.logo + "The enchantment " + ChatColor.DARK_AQUA + enchantment.loreName
                        + ChatColor.AQUA + " cannot be added to this item.");
            }
            return stack;
        }

        // Get the level
        int level;
        try {
            level = Math.min(Integer.parseInt(levelStr), enchantment.maxLevel);
        } catch (NumberFormatException e) {
            level = 1;
        }

        enchantment.setEnchantment(stack, level, config.getWorld());

        if (level != 0) {
            if (isHeld && player != null) {
                player.sendMessage(Storage.logo + "The enchantment " + ChatColor.DARK_AQUA + enchantment.loreName
                        + ChatColor.AQUA + " has been added.");
            }
        } else {
            if (!isHeld) {
                return null;
            }
            if (player != null) {
                player.sendMessage(Storage.logo + "The enchantment " + ChatColor.DARK_AQUA + enchantment.loreName
                        + ChatColor.AQUA + " has been removed.");
            }
        }

        return stack;
    }

    // Reloads the Zenchantments plugin
    private static boolean reload(CommandSender player) {
        if (!player.hasPermission("zenchantments.command.reload")) {
            player.sendMessage(Storage.logo + "You do not have permission to do this!");
            return true;
        }
        player.sendMessage(Storage.logo + "Reloaded Zenchantments.");
        Storage.zenchantments.loadConfigs();
        return true;
    }

    // Gives the given player an item with certain enchantments determined by the arguments
    private static boolean give(CommandSender sender, String[] args) {
        if (!sender.hasPermission("zenchantments.command.give")) {
            sender.sendMessage(Storage.logo + "You do not have permission to do this!");
            return true;
        }
        if (args.length >= 4) {

            Scanner scanner = new Scanner(Arrays.toString(args).replace("[", "").replace("]",
                    "").replace(",", " "));
            scanner.next();
            String playerName = scanner.next();
            Player recipient = null;
            for (Player plyr : Bukkit.getOnlinePlayers()) {
                if (plyr.getName().equalsIgnoreCase(playerName)) {
                    recipient = plyr;
                }
            }
            if (recipient == null) {
                sender.sendMessage(Storage.logo + "The player " + ChatColor.DARK_AQUA + playerName
                        + ChatColor.AQUA + " is not online or does not exist.");
                return true;
            }
            Material mat = null;
            if (scanner.hasNextInt()) {
                // TODO: ID MAPPINGS, mat = Material.getMaterial(s.nextInt());
            } else {
                mat = Material.matchMaterial(scanner.next());
            }

            Config config = Config.get(recipient.getWorld());

            if (mat == null) {
                sender.sendMessage(Storage.logo + "The material " + ChatColor.DARK_AQUA
                        + args[2].toUpperCase() + ChatColor.AQUA + " is not valid.");
                return true;
            }
            Map<CustomEnchantment, Integer> enchantsToAdd = new HashMap<>();
            while (scanner.hasNext()) {
                String enchantName = scanner.next();
                int level = 1;
                if (scanner.hasNextInt()) {
                    level = Math.max(1, scanner.nextInt());
                }

                CustomEnchantment ench = config.enchantFromString(enchantName);

                if (ench != null) {
                    if (ench.validMaterial(mat) || mat == Material.BOOK || mat == Material.ENCHANTED_BOOK) {
                        enchantsToAdd.put(ench, level);
                    } else {
                        sender.sendMessage(Storage.logo + "The enchantment " + ChatColor.DARK_AQUA
                                + ench.loreName + ChatColor.AQUA + " cannot be given with this item.");
                    }
                } else {
                    sender.sendMessage(Storage.logo + "The enchantment " + ChatColor.DARK_AQUA
                            + enchantName + ChatColor.AQUA + " does not exist!");
                }
            }

            ItemStack stk = new ItemStack(mat);
            StringBuilder msgBldr
                    = new StringBuilder(Storage.logo + "Gave " + ChatColor.DARK_AQUA + recipient.getName()
                            + ChatColor.AQUA + " the enchantments ");

            for (Map.Entry<CustomEnchantment, Integer> ench : enchantsToAdd.entrySet()) {
                ench.getKey().setEnchantment(stk, ench.getValue(), config.getWorld());
                msgBldr.append(ChatColor.stripColor(ench.getKey().getLoreName()));
                msgBldr.append(", ");
            }
            if (!enchantsToAdd.isEmpty()) {
                recipient.getInventory().addItem(stk);
                String message = msgBldr.toString();
                sender.sendMessage(message.substring(0, message.length() - 2) + ".");
            }

        } else {
            sender.sendMessage(Storage.logo + ChatColor.DARK_AQUA + "Usage: " + ChatColor.AQUA
                    + "/ench give <Player> <Material> <enchantment> <?level> ...");
        }
        return true;
    }

    // Lists the Custom Enchantments applicable to the held tool
    private static boolean listEnchantment(EnchantPlayer player, Config config, ItemStack stack) {
        if (!player.hasPermission("zenchantments.command.list")) {
            player.sendMessage(Storage.logo + "You do not have permission to do this!");
            return true;
        }
        player.sendMessage(Storage.logo + "Enchantment Types:");

        for (CustomEnchantment ench : new TreeSet<>(config.getEnchants())) {
            if (ench.validMaterial(stack)) {
                player.sendMessage(ChatColor.DARK_AQUA + "- " + ChatColor.AQUA + ench.getLoreName());
            }
        }
        return true;
    }

    // Gives information on each enchantment on the given tool or on the enchantment named in the parameter
    private static boolean infoEnchantment(EnchantPlayer player, Config config, String[] args) {
        if (!player.hasPermission("zenchantments.command.info")) {
            player.sendMessage(Storage.logo + "You do not have permission to do this!");
            return true;
        }
        if (args.length > 1) {
            CustomEnchantment ench = config.enchantFromString(args[1]);
            if (ench != null) {
                player.sendMessage(Storage.logo + ench.loreName + ": "
                        + (player.isDisabled(ench.getId()) ? ChatColor.RED + "**Disabled** " : "")
                        + ChatColor.AQUA + ench.description);
            }
        } else {
            Set<CustomEnchantment> enchs = CustomEnchantment.getEnchants(
                    player.getPlayer().getInventory().getItemInMainHand(), true, config.getWorld()).keySet();
            if (enchs.isEmpty()) {
                player.sendMessage(Storage.logo + "There are no custom enchantments on this tool!");
            } else {
                player.sendMessage(Storage.logo + "Enchantment Info:");
            }

            for (CustomEnchantment ench : enchs) {
                player.sendMessage(ChatColor.DARK_AQUA + ench.loreName + ": "
                        + (player.isDisabled(ench.getId()) ? ChatColor.RED + "**Disabled** " : "")
                        + ChatColor.AQUA + ench.description);
            }
        }
        return true;
    }

    // Disables the given enchantment for the player
    private static boolean disable(EnchantPlayer player, Config config, String[] args) {
        if (!player.hasPermission("zenchantments.command.onoff")) {
            player.sendMessage(Storage.logo + "You do not have permission to do this!");
            return true;
        }
        if (args.length > 1) {
            CustomEnchantment ench = config.enchantFromString(args[1]);
            if (ench != null) {
                player.disable(ench.getId());
                player.sendMessage(Storage.logo + "The enchantment " + ChatColor.DARK_AQUA
                        + ench.loreName + ChatColor.AQUA + " has been " + ChatColor.RED + "disabled.");
            } else if (args[1].equalsIgnoreCase("all")) {
                player.disableAll();
                player.sendMessage(Storage.logo + ChatColor.DARK_AQUA + "All " + ChatColor.AQUA
                        + "enchantments have been " + ChatColor.RED + "disabled.");
            } else {
                player.sendMessage(Storage.logo + "That enchantment does not exist!");
            }
        } else {
            player.sendMessage(
                    Storage.logo + ChatColor.DARK_AQUA + "Usage: " + ChatColor.AQUA + "/ench disable <enchantment/all>");
        }
        return true;
    }

    // Enables the given enchantment for the player
    private static boolean enable(EnchantPlayer player, Config config, String[] args) {
        if (!player.hasPermission("zenchantments.command.onoff")) {
            player.sendMessage(Storage.logo + "You do not have permission to do this!");
            return true;
        }
        if (args.length > 1) {
            CustomEnchantment ench = config.enchantFromString(args[1]);
            if (ench != null) {
                player.enable(ench.getId());
                player.sendMessage(Storage.logo + "The enchantment " + ChatColor.DARK_AQUA
                        + ench.loreName + ChatColor.AQUA + " has been" + ChatColor.GREEN + " enabled.");
            } else if (args[1].equalsIgnoreCase("all")) {
                player.enableAll();
                player.sendMessage(Storage.logo + ChatColor.DARK_AQUA + "All " + ChatColor.AQUA
                        + "enchantments have been enabled.");
            } else {
                player.sendMessage(Storage.logo + "That enchantment does not exist!");
            }
        } else {
            player.sendMessage(
                    Storage.logo + ChatColor.DARK_AQUA + "Usage: " + ChatColor.AQUA + "/ench enable <enchantment/all>");
        }
        return true;
    }

    // Enchants the held item with enchantments determined by the parameters
    private static boolean enchant(EnchantPlayer player, Config config, String[] args, String label, ItemStack stack) {
        if (!player.hasPermission("zenchantments.command.enchant")) {
            player.sendMessage(Storage.logo + "You do not have permission to do this!");
            return true;
        }

        CustomEnchantment ench = config.enchantFromString(label);
        if (ench != null) {
            player.getPlayer().getInventory().setItemInMainHand(
                    addEnchantments(Config.get(player.getPlayer().getWorld()),
                            player.getPlayer(),
                            ench,
                            stack,
                            args.length >= 2 ? args[1] : "1",
                            true)
            );
        } else {
            player.sendMessage(Storage.logo + "That enchantment does not exist!");
        }
        return true;
    }

    // Lists all the commands associated with Custom Enchantments
    private static boolean helpEnchantment(CommandSender player, String label) {
        if (label.isEmpty() || label.equals("help")) {
            player.sendMessage(Storage.logo);
            player.sendMessage(ChatColor.DARK_AQUA + "- " + "ench info <?enchantment>: " + ChatColor.AQUA
                    + "Returns information about custom enchantments.");
            player.sendMessage(ChatColor.DARK_AQUA + "- " + "ench list: " + ChatColor.AQUA
                    + "Returns a list of enchantments for the tool in hand.");
            player.sendMessage(ChatColor.DARK_AQUA + "- " + "ench give <Player> <Material> <enchantment> <?level> ... "
                    + ChatColor.AQUA + "Gives the target a specified enchanted item.");
            player.sendMessage(ChatColor.DARK_AQUA + "- " + "ench <enchantment> <?level>: " + ChatColor.AQUA
                    + "Enchants the item in hand with the given enchantment and level");
            player.sendMessage(ChatColor.DARK_AQUA + "- " + "ench disable <enchantment/all>: " + ChatColor.AQUA
                    + "Disables selected enchantment for the user");
            player.sendMessage(ChatColor.DARK_AQUA + "- " + "ench enable <enchantment/all>: " + ChatColor.AQUA
                    + "Enables selected enchantment for the user");
            return true;
        }
        return false;
    }

    // Control flow for the command processor
    static boolean onCommand(CommandSender sender, Command command, String commandlabel, String[] args) {
        if (commandlabel.equalsIgnoreCase("ench")) {
            String label = args.length == 0 ? "" : args[0].toLowerCase();
            switch (label) {
                case "reload":
                    return reload(sender);
                case "give":
                    return give(sender, args);
            }
            if (!(sender instanceof Player)) {
                return false;
            }
            EnchantPlayer player = EnchantPlayer.matchPlayer((Player) sender);
            Config config = Config.get(player.getPlayer().getWorld());
            ItemStack stack = player.getPlayer().getInventory().getItemInMainHand();
            switch (label) {
                case "list":
                    return listEnchantment(player, config, stack);
                case "info":
                    return infoEnchantment(player, config, args);
                case "disable":
                    return disable(player, config, args);
                case "enable":
                    return enable(player, config, args);
                case "help":
                default:
                    return helpEnchantment(sender, label) || enchant(player, config, args, label, stack);
            }
        }
        return true;
    }
}
