package zedly.zenchantments;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static org.bukkit.Material.*;

// This class handles all commands used by this plugin
public class CommandProcessor {

    // Adds or removes the given enchantment of the given level to the item stack
    public static ItemStack addEnchantments(World world, Player player, CustomEnchantment enchantment, ItemStack stack,
            String level, boolean isHeld) {
        Config config = Config.get(world);
        if (stack.getType() == AIR) {
            if (player != null) {
                player.sendMessage(Storage.logo + "You need to be holding an item!");
            }
            return stack;
        }
        if (!enchantment.validMaterial(stack) && stack.getType() != BOOK && stack.getType() != ENCHANTED_BOOK) {
            if (player != null) {
                player.sendMessage(Storage.logo + "The enchantment " + ChatColor.DARK_AQUA + enchantment.loreName
                        + ChatColor.AQUA + " cannot be added to this item.");
            }
            return stack;
        }
        try {
            level = Utilities.getRomanString(Integer.parseInt(level), enchantment.maxLevel);
        } catch (NumberFormatException e) {
            level = "I";
        }
        if (stack.getType() == BOOK) {
            stack.setType(ENCHANTED_BOOK);
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) stack.getItemMeta();
            meta.addStoredEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 1, true);
            stack.setItemMeta(meta);
        }
        String finalEnch = ChatColor.GRAY + enchantment.loreName + " " + level;
        List<String> lore = new ArrayList<>();
        if (stack.getItemMeta().hasLore()) {
            lore = stack.getItemMeta().getLore();
        }
        if (config.getEnchants(stack, true).containsKey(enchantment)) {
            Iterator it = lore.iterator();
            while (it.hasNext()) {
                String rawEnchant = (String) it.next();
                if (rawEnchant.contains(enchantment.loreName)) {
                    it.remove();
                }
            }
        }
        if (!level.equals("-")) {
            lore.add(finalEnch);
            if (isHeld) {
                if (player != null) {
                    player.sendMessage(Storage.logo + "The enchantment " + ChatColor.DARK_AQUA + enchantment.loreName
                            + ChatColor.AQUA + " has been added.");
                }
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
        ItemMeta meta = stack.getItemMeta();
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return config.descriptionLore() ? config.addDescriptions(stack, enchantment) : stack;
    }

    // Reloads the Zenchantments plugin
    public static boolean reload(EnchantPlayer player) {
        if (!player.hasPermission("zenchantments.command.reload")) {
            player.sendMessage(Storage.logo + "You do not have permission to do this!");
            return true;
        }
        player.sendMessage(Storage.logo + "Reloaded Zenchantments.");
        Storage.zenchantments.loadConfigs();
        return true;
    }

    // Gives the given player an item with certain enchantments determined by the arguments
    public static boolean give(EnchantPlayer player, String[] args, Config config) {
        if (!player.hasPermission("zenchantments.command.give")) {
            player.sendMessage(Storage.logo + "You do not have permission to do this!");
            return true;
        }
        if (args.length >= 4) {
            Scanner s = new Scanner(Arrays.toString(args).replace("[", "").replace("]", "").replace(",", ""));
            s.next();
            String playerName = s.next();
            Player toAdd = null;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().equalsIgnoreCase(playerName)) {
                    toAdd = p;
                }
            }
            if (toAdd == null) {
                player.sendMessage(Storage.logo + "The player " + ChatColor.DARK_AQUA + playerName
                        + ChatColor.AQUA + " is not online or does not exist.");
                return true;
            }
            Material mat;
            if (s.hasNextInt()) {
                mat = Material.getMaterial(s.nextInt());
            } else {
                mat = Material.matchMaterial(s.next());
            }
            if (mat == null) {
                player.sendMessage(Storage.logo + "The material " + ChatColor.DARK_AQUA
                        + args[4].toUpperCase() + ChatColor.AQUA + " is not valid.");
                return true;
            }
            Map<CustomEnchantment, Integer> enchantments = new HashMap<>();
            while (s.hasNext()) {
                String name = s.next();
                int level = 1;
                if (s.hasNextInt()) {
                    level = Math.max(1, s.nextInt());
                }
                boolean contains = false;
                for (String str : config.getEnchants().keySet()) {
                    if (name.toLowerCase().replace("_", "").equals(ChatColor.stripColor(str))) {
                        contains = true;
                        name = str;
                    }
                }
                if (contains) {
                    CustomEnchantment ench = config.getEnchants().get(name);
                    if (ench.validMaterial(mat) || mat == Material.BOOK || mat == Material.ENCHANTED_BOOK) {
                        enchantments.put(ench, level);
                    } else {
                        player.sendMessage(Storage.logo + "The enchantment " + ChatColor.DARK_AQUA
                                + ench.loreName + ChatColor.AQUA + " cannot be given with this item.");
                    }
                } else {
                    player.sendMessage(Storage.logo + "The enchantment " + ChatColor.DARK_AQUA
                            + name + ChatColor.AQUA + " does not exist!");
                }
            }

            ItemStack stk = new ItemStack(mat);
            String message = Storage.logo + "Gave " + ChatColor.DARK_AQUA + toAdd.getName()
                    + ChatColor.AQUA + " the enchantments ";
            for (CustomEnchantment e : enchantments.keySet()) {
                addEnchantments(player.getPlayer().getWorld(), player.getPlayer(), e, stk,
                        enchantments.get(e) + "", false);
                message += e.loreName + ", ";
            }
            if (!enchantments.isEmpty()) {
                toAdd.getInventory().addItem(stk);
                player.sendMessage(message.substring(0, message.length() - 2) + ".");
            }
        } else {
            player.sendMessage(Storage.logo + "/ench give <Player> <Material> <enchantment> <?level> ...");
        }
        return true;
    }

    // Lists the Custom Enchantments applicable to the held tool
    public static boolean listEnchantment(EnchantPlayer player, Config config, ItemStack stack) {
        if (!player.hasPermission("zenchantments.command.list")) {
            player.sendMessage(Storage.logo + "You do not have permission to do this!");
            return true;
        }
        player.sendMessage(Storage.logo + "Enchantment Types:");
        for (String str : config.getEnchants().keySet()) {
            if (config.getEnchants().get(str.replace(" ", "").toLowerCase()).validMaterial(stack)) {
                player.sendMessage(ChatColor.DARK_AQUA + "- "
                        + ChatColor.AQUA + config.getEnchants().get(str.replace(" ", "").toLowerCase()).loreName);
            }
        }
        return true;
    }

    // Gives information on each enchantment on the given tool or on the enchantment named in the parameter
    public static boolean infoEnchantment(EnchantPlayer player, Config config, String[] args) {
        if (!player.hasPermission("zenchantments.command.info")) {
            player.sendMessage(Storage.logo + "You do not have permission to do this!");
            return true;
        }
        if (args.length > 1) {
            String enchant = WordUtils.capitalize(args[1].toLowerCase().replace("_", " "));
            if (config.getEnchants().containsKey(enchant.replace(" ", "").toLowerCase())) {
                CustomEnchantment ench = config.getEnchants().get(enchant.replace(" ", "").toLowerCase());
                String e = "";
                if (player.isDisabled(ench.getId())) {
                    e = ChatColor.RED + "**Disabled** ";
                }
                player.sendMessage(Storage.logo + (ench.loreName + ": " + e + ChatColor.AQUA
                        + ench.description).replace(ChatColor.GRAY + "", ""));
            }
        } else {
            player.sendMessage(Storage.logo + "Enchantment Info:");
            for (CustomEnchantment ench : config.getEnchants(player.getPlayer().getItemInHand(), true).keySet()) {
                String s = "";
                if (player.isDisabled(ench.getId())) {
                    s = ChatColor.RED + "**Disabled** ";
                }
                player.sendMessage((ChatColor.DARK_AQUA + ench.loreName + ": " + s + ChatColor.AQUA
                        + ench.description).replace(ChatColor.GRAY + "", ""));
            }
        }
        return true;
    }

    // Disables the given enchantment for the player
    public static boolean disable(EnchantPlayer player, Config config, String[] args) {
        if (!player.hasPermission("zenchantments.command.onoff")) {
            player.sendMessage(Storage.logo + "You do not have permission to do this!");
            return true;
        }
        if (args.length > 1) {
            String toDisable = args[1].toLowerCase();
            if (config.getEnchants().containsKey(toDisable)) {
                CustomEnchantment ench = config.getEnchants().get(toDisable);
                player.disable(ench.getId());
                player.sendMessage(Storage.logo + "The enchantment " + ChatColor.DARK_AQUA
                        + ench.loreName + ChatColor.AQUA + " has been " + ChatColor.RED + "disabled.");
            } else if (toDisable.equals("all")) {
                player.disableAll();
                player.sendMessage(Storage.logo + ChatColor.DARK_AQUA + "All " + ChatColor.AQUA
                        + "enchantments have been " + ChatColor.RED + "disabled.");
            } else {
                player.sendMessage(Storage.logo + "That enchantment does not exist!");
            }
        }
        return true;
    }

    // Enables the given enchantment for the player
    public static boolean enable(EnchantPlayer player, Config config, String[] args) {
        if (!player.hasPermission("zenchantments.command.onoff")) {
            player.sendMessage(Storage.logo + "You do not have permission to do this!");
            return true;
        }
        if (args.length > 1) {
            String toDisable = args[1].toLowerCase();
            if (config.getEnchants().containsKey(toDisable)) {
                CustomEnchantment ench = config.getEnchants().get(toDisable);
                player.enable(ench.getId());
                player.sendMessage(Storage.logo + "The enchantment " + ChatColor.DARK_AQUA
                        + ench.loreName + ChatColor.AQUA + " has been" + ChatColor.GREEN + " enabled.");
            } else if (toDisable.equals("all")) {
                player.enableAll();
                player.sendMessage(Storage.logo + ChatColor.DARK_AQUA + "All " + ChatColor.AQUA
                        + "enchantments have been enabled.");
            } else {
                player.sendMessage(Storage.logo + "That enchantment does not exist!");
            }
        }
        return true;
    }

    // Lists all the commands associated with Custom Enchantments
    public static boolean helpEnchantment(EnchantPlayer player, String label) {
        if (label.isEmpty() || label.equals("help")) {
            player.sendMessage(Storage.logo);
            player.sendMessage(ChatColor.DARK_AQUA + "- " + "ench info <?enchantment>: " + ChatColor.AQUA + "Returns information about custom enchantments.");
            player.sendMessage(ChatColor.DARK_AQUA + "- " + "ench list: " + ChatColor.AQUA + "Returns a list of enchantments for the tool in hand.");
            player.sendMessage(ChatColor.DARK_AQUA + "- " + "ench give <Player> <Material> <enchantment> <?level> ... " + ChatColor.AQUA + "Gives the target a specified enchanted item.");
            player.sendMessage(ChatColor.DARK_AQUA + "- " + "ench <enchantment> <?level>: " + ChatColor.AQUA + "Enchants the item in hand with the given enchantment and level");
            player.sendMessage(ChatColor.DARK_AQUA + "- " + "ench disable <enchantment/all>: " + ChatColor.AQUA + "Disables selected enchantment for the user");
            player.sendMessage(ChatColor.DARK_AQUA + "- " + "ench enable <enchantment/all>: " + ChatColor.AQUA + "Enables selected enchantment for the user");
            return true;
        }
        return false;
    }

    // Enchants the held item with enchantments determined by the parameters
    public static boolean enchant(EnchantPlayer player, Config config, String[] args, String label, ItemStack stack) {
        if (!player.hasPermission("zenchantments.command.enchant")) {
            player.sendMessage(Storage.logo + "You do not have permission to do this!");
            return true;
        }
        boolean contains = false;
        String enchantName = "";
        for (String s : config.getEnchants().keySet()) {
            if (label.toLowerCase().replace("_", "").equals(ChatColor.stripColor(s))) {
                contains = true;
                enchantName = s;
            }
        }
        if (contains) {
            CustomEnchantment ench = config.getEnchants().get(enchantName);
            if (args.length >= 2) {
                player.getPlayer().setItemInHand(addEnchantments(player.getPlayer().getWorld(), player.getPlayer(), ench, stack, args[1], true));
            } else {
                player.getPlayer().setItemInHand(addEnchantments(player.getPlayer().getWorld(), player.getPlayer(), ench, stack, "1", true));
            }
        } else {
            player.sendMessage(Storage.logo + "That enchantment does not exist!");
        }
        return true;
    }

    // Control flow for the command processor
    public static boolean onCommand(CommandSender sender, Command command, String commandlabel, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        EnchantPlayer player = EnchantPlayer.matchPlayer((Player) sender);
        Config config = Config.get(player.getPlayer().getWorld());
        ItemStack stack = player.getPlayer().getItemInHand();
        String label = "";
        if (!(args.length == 0)) {
            label = args[0].toLowerCase().replace("_", "");
        }
        String cmd = commandlabel.toLowerCase();
        switch (cmd) {
            case "ench":
                switch (label) {
                    case "reload":
                        return reload(player);
                    case "give":
                        return give(player, args, config);
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
                        return helpEnchantment(player, label) ? true : enchant(player, config, args, label, stack);
                }
        }
        return true;
    }
}
