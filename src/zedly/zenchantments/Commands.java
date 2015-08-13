package zedly.zenchantments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import static org.bukkit.Material.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class Commands {

    private static void theLore(Player player, Enchantment enchantment, ItemStack stack, String level) {
        if (player.getItemInHand().getType() == AIR) {
            player.sendMessage(Storage.logo + "You need to be holding an item!");
            return;
        }
        if (!(ArrayUtils.contains(enchantment.enchantable, stack.getType())) && stack.getType() != BOOK && stack.getType() != ENCHANTED_BOOK) {
            player.sendMessage(Storage.logo + "The enchantment " + ChatColor.DARK_AQUA + enchantment.loreName + ChatColor.AQUA + " cannot be added to this tool.");
            return;
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
        List<String> loreEnch = new ArrayList<>();
        List<String> lore = new ArrayList<>();
        if (stack.getItemMeta().hasLore()) {
            lore = stack.getItemMeta().getLore();
        }
        for (String rawEnchant : lore) {
            int index1 = rawEnchant.lastIndexOf(" ");
            if (index1 == -1) {
                continue;
            }
            if (rawEnchant.length() >= index1 + 1) {
                String enchant = rawEnchant.substring(2, index1).toLowerCase();
                if (Storage.enchantClass.containsKey(enchant.replace(" ", "").toLowerCase())) {
                    Enchantment ench = (Enchantment) Storage.enchantClass.get(enchant.replace(" ", "").toLowerCase());
                    loreEnch.add(ench.loreName);
                }
            }
        }
        if (loreEnch.contains(enchantment.loreName)) {
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
            player.sendMessage(Storage.logo + "The enchantment " + ChatColor.DARK_AQUA + enchantment.loreName + ChatColor.AQUA + " has been added.");

        } else {
            player.sendMessage(Storage.logo + "The enchantment " + ChatColor.DARK_AQUA + enchantment.loreName + ChatColor.AQUA + " has been removed.");
        }
        ItemMeta meta = stack.getItemMeta();
        meta.setLore(lore);
        stack.setItemMeta(meta);
        player.setItemInHand(stack);
    }

    public static boolean onCommand(CommandSender sender, Command command, String commandlabel, String[] args) {
        Player player = (Player) sender;
        ItemStack stack = player.getItemInHand();
        String lArgs = "";
        if (!(args.length == 0)) {
            lArgs = args[0].toLowerCase().replace("_", "");
        }
        String cmd = commandlabel.toLowerCase();
        switch (cmd) {
            case "ench":
                switch (lArgs) {
                    case "list":
                        if (!sender.hasPermission("zenchantments.command.list")) {
                            player.sendMessage(Storage.logo + "You do not have permission to do this!");
                            return true;
                        }
                        player.sendMessage(Storage.logo + "Enchantment Types:");
                        for (String str : Storage.enchantClass.keySet()) {
                            if (ArrayUtils.contains(Storage.enchantClass.get(str.replace(" ", "").toLowerCase()).enchantable, stack.getType())) {
                                player.sendMessage(ChatColor.DARK_AQUA + "- " + ChatColor.AQUA + Storage.enchantClass.get(str.replace(" ", "").toLowerCase()).loreName);
                            }
                        }
                        break;
                    case "info":
                        if (!sender.hasPermission("zenchantments.command.info")) {
                            player.sendMessage(Storage.logo + "You do not have permission to do this!");
                            return true;
                        }
                        if (args.length > 1) {
                            String enchant = WordUtils.capitalize(args[1].toLowerCase().replace("_", " "));
                            if (Storage.enchantClassU.containsKey(enchant.replace(" ", "").toLowerCase())) {
                                Enchantment ench = (Enchantment) Storage.enchantClassU.get(enchant.replace(" ", "").toLowerCase());
                                String e = "";
                                if (Storage.playerSettings.containsKey(player.getUniqueId())) {
                                    if (Storage.playerSettings.get(player.getUniqueId()).contains(ench)) {
                                        e = ChatColor.RED + "**Disabled** ";
                                    }
                                }
                                player.sendMessage(Storage.logo + (ench.loreName + ": " + e + ChatColor.AQUA + ench.description).replace(ChatColor.GRAY + "", ""));
                            }
                        } else {
                            player.sendMessage(Storage.logo + "Enchantment Info:");
                            for (Enchantment e : Utilities.getEnchant(player.getItemInHand()).keySet()) {
                                String s = "";
                                if (Storage.playerSettings.containsKey(player.getUniqueId())) {
                                    if (Storage.playerSettings.get(player.getUniqueId()).contains(e)) {
                                        s = ChatColor.RED + "**Disabled** ";
                                    }
                                }
                                player.sendMessage((ChatColor.DARK_AQUA + e.loreName + ": " + s + ChatColor.AQUA + e.description).replace(ChatColor.GRAY + "", ""));
                            }
                        }
                        break;
                    case "disable":
                        if (!sender.hasPermission("zenchantments.command.onoff")) {
                            player.sendMessage(Storage.logo + "You do not have permission to do this!");
                            return true;
                        }
                        if (args.length > 1) {
                            String enchant = WordUtils.capitalize(args[1].toLowerCase().replace("_", " "));
                            if (Storage.enchantClassU.containsKey(enchant.replace(" ", "").toLowerCase())) {
                                Enchantment ench = (Enchantment) Storage.enchantClassU.get(enchant.replace(" ", "").toLowerCase());
                                HashSet<Enchantment> enchs = new HashSet<>();
                                if (Storage.playerSettings.containsKey(player.getUniqueId())) {
                                    enchs = Storage.playerSettings.get(player.getUniqueId());
                                }
                                enchs.add(ench);
                                Storage.playerSettings.put(player.getUniqueId(), enchs);
                                PlayerConfig.saveConfigs();
                                player.sendMessage(Storage.logo + "The enchantment " + ChatColor.DARK_AQUA + ench.loreName + ChatColor.AQUA + " has been disabled.");
                            } else if (args[1].toLowerCase().equals("all")) {
                                HashSet<Enchantment> enchs = new HashSet<>();
                                for (Enchantment e : Storage.enchantClassU.values()) {
                                    enchs.add(e);
                                }
                                Storage.playerSettings.put(player.getUniqueId(), enchs);
                                PlayerConfig.saveConfigs();
                                player.sendMessage(Storage.logo + ChatColor.DARK_AQUA + "All " + ChatColor.AQUA + "enchantments have been disabled.");
                            }
                        }
                        break;
                    case "enable":
                        if (!sender.hasPermission("zenchantments.command.onoff")) {
                            player.sendMessage(Storage.logo + "You do not have permission to do this!");
                            return true;
                        }
                        if (args.length > 1) {
                            String enchant = WordUtils.capitalize(args[1].toLowerCase().replace("_", " "));
                            if (Storage.enchantClassU.containsKey(enchant.replace(" ", "").toLowerCase())) {
                                Enchantment ench = (Enchantment) Storage.enchantClassU.get(enchant.replace(" ", "").toLowerCase());
                                if (Storage.playerSettings.containsKey(player.getUniqueId())) {
                                    Storage.playerSettings.get(player.getUniqueId()).remove(ench);
                                    if (Storage.playerSettings.get(player.getUniqueId()).isEmpty()) {
                                        Storage.playerSettings.remove(player.getUniqueId());
                                    }
                                    PlayerConfig.saveConfigs();
                                }
                                player.sendMessage(Storage.logo + "The enchantment " + ChatColor.DARK_AQUA + ench.loreName + ChatColor.AQUA + " has been enabled.");
                            } else if (args[1].toLowerCase().equals("all")) {
                                Storage.playerSettings.remove(player.getUniqueId());
                                PlayerConfig.saveConfigs();
                                player.sendMessage(Storage.logo + ChatColor.DARK_AQUA + "All " + ChatColor.AQUA + "enchantments have been enabled.");
                            }
                        }
                        break;
                    case "help":
                        player.sendMessage(Storage.logo);
                        player.sendMessage(ChatColor.DARK_AQUA + "- " + "ench info <?enchantment>: " + ChatColor.AQUA + "Returns information about custom enchantments.");
                        player.sendMessage(ChatColor.DARK_AQUA + "- " + "ench list: " + ChatColor.AQUA + "Returns a list of enchantments for the tool in hand.");
                        player.sendMessage(ChatColor.DARK_AQUA + "- " + "ench <enchantment> <?level>: " + ChatColor.AQUA + "Enchants the item in hand with the given enchantment and level");
                        player.sendMessage(ChatColor.DARK_AQUA + "- " + "ench disable <enchantment/all>: " + ChatColor.AQUA + "Disables selected enchantment for the user");
                        player.sendMessage(ChatColor.DARK_AQUA + "- " + "ench enable <enchantment/all>: " + ChatColor.AQUA + "Enables selected enchantment for the user");
                    default:
                        if (!sender.hasPermission("zenchantments.command.enchant")) {
                            player.sendMessage(Storage.logo + "You do not have permission to do this!");
                            return true;
                        }
                        if (Storage.enchantClass.containsKey(lArgs.toLowerCase().replace("_", ""))) {
                            Enchantment ench = Storage.enchantClass.get(lArgs.toLowerCase().replace("_", "").toLowerCase());
                            if (args.length >= 2) {
                                theLore(player, ench, stack, args[1]);
                            } else {
                                theLore(player, ench, stack, "1");
                            }
                        }else{
                            player.sendMessage(Storage.logo + "That enchantment does not exist!");
                        }
                }
                break;
            case "arrow":
                switch (lArgs) {
                    case "list":
                        if (!sender.hasPermission("zenchantments.command.list")) {
                            player.sendMessage(Storage.logo + "You do not have permission to do this!");
                            return true;
                        }
                        player.sendMessage(Storage.logo + "Arrow Types:");
                        for (String str : Storage.projectileTable.keySet()) {
                            player.sendMessage(ChatColor.DARK_AQUA + "- " + ChatColor.AQUA + str);
                        }
                        break;
                    case "info":
                        if (!sender.hasPermission("zenchantments.command.info")) {
                            player.sendMessage(Storage.logo + "You do not have permission to do this!");
                            return true;
                        }
                        if (args.length >= 2) {
                            for (Arrow ar : Storage.arrowClass.values()) {
                                if (ar.getName().toLowerCase().startsWith(args[1])) {
                                    player.sendMessage(Storage.logo + "Arrow Info:");
                                    player.sendMessage(ChatColor.DARK_AQUA + "- " + ar.getName() + ": " + ChatColor.AQUA + ar.getDescription());
                                    return true;
                                }
                            }
                            player.sendMessage(Storage.logo + "Could not find the type of Arrow you're looking for!");
                        } else {
                            if (stack.getType() == ARROW) {
                                if (stack.getItemMeta().hasLore()) {
                                    String str = stack.getItemMeta().getLore().get(0);
                                    str = ChatColor.stripColor(str);
                                    for (String string : Storage.arrowClass.keySet()) {
                                        if (string.equals(str)) {
                                            player.sendMessage(Storage.logo + "Arrow Info:");
                                            player.sendMessage(ChatColor.DARK_AQUA + "- " + str + ": " + ChatColor.AQUA + Storage.arrowClass.get(str).getDescription());
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case "help":
                        player.sendMessage(Storage.logo);
                        player.sendMessage(ChatColor.DARK_AQUA + "- " + "arrow info: " + ChatColor.AQUA + "Returns information about custom arrows.");
                        player.sendMessage(ChatColor.DARK_AQUA + "- " + "arrow list: " + ChatColor.AQUA + "Returns a list of custom arrows");
                        player.sendMessage(ChatColor.DARK_AQUA + "- " + "arrow <arrow type> <?arguments> <?arguments>: " + ChatColor.AQUA + "Adds the desired arrow effect to the arrow in hand.");
                        break;
                    default:
                        if (lArgs.equals("")) {
                            player.sendMessage(ChatColor.BLUE + "[" + ChatColor.DARK_AQUA + "Zenchantments" + ChatColor.BLUE + "] ");
                            player.sendMessage(ChatColor.DARK_AQUA + "- " + "arrow info: " + ChatColor.AQUA + "Returns information about custom arrows.");
                            player.sendMessage(ChatColor.DARK_AQUA + "- " + "arrow list: " + ChatColor.AQUA + "Returns a list of custom arrows");
                            player.sendMessage(ChatColor.DARK_AQUA + "- " + "arrow <arrow type> <?arguments> <?arguments>: " + ChatColor.AQUA + "Adds the desired arrow effect to the arrow in hand.");
                            break;
                        }
                        if (!sender.hasPermission("zenchantments.command.arrow")) {
                            player.sendMessage(Storage.logo + "You do not have permission to do this!");
                            return true;
                        }
                        if (player.getItemInHand() == null || player.getItemInHand().getType() != ARROW) {
                            player.sendMessage(Storage.logo + "You need to be holding arrows for this command!");
                            return true;
                        }
                        for (Arrow ar : Storage.arrowClass.values()) {
                            if (ar.getName().toLowerCase().startsWith(lArgs)) {
                                List<String> lore = ar.constructArrow(Arrays.copyOfRange(args, 1, args.length));
                                if (lore == null) {
                                    player.sendMessage(Storage.logo + ar.getCommand());
                                    return true;
                                }
                                ItemMeta soMeta = stack.getItemMeta();
                                soMeta.setLore(lore);
                                stack.setItemMeta(soMeta);
                                player.setItemInHand(stack);
                                player.sendMessage(Storage.logo + "Created " + ar.getName() + "s!");
                                return true;
                            }
                        }
                        player.sendMessage(Storage.logo + "That arrow does not exist!");
                }
                break;
        }
        return true;
    }
}
