package zedly.zenchantments;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import static org.bukkit.Material.*;

public class ArchDrop {

    private static ItemStack generate(Material material, int data, String name, String lore) {
        ItemStack stack = new ItemStack(material, 1);
        stack.setDurability((short) data);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        ArrayList<String> lore_ = new ArrayList<>();
        lore_.add(lore);
        meta.setLore(lore_);
        stack.setItemMeta(meta);
        return stack;
    }

    private static ItemStack generate(Material material, short data, String name, String lore) {
        ItemStack stack = new ItemStack(material, 1, data);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        ArrayList<String> lore_ = new ArrayList<>();
        lore_.add(lore);
        meta.setLore(lore_);
        stack.setItemMeta(meta);
        return stack;
    }

    public static void Drop(Block block) {
        int locY = block.getLocation().getBlockY();
        int durabilityI = Storage.rnd.nextInt(240);
        int durabilityG = Storage.rnd.nextInt(30);
        int durabilityS = Storage.rnd.nextInt(60);
        String eraA = "Iron Age";
        String eraB = "Bronze Age";
        String eraC = "Stone Age";
        String eraD = "Prehistoric";
        ItemStack[] ironItems = new ItemStack[]{generate(IRON_AXE, durabilityI, ChatColor.RED + "Iron Battle Axe", eraA),
            generate(IRON_SPADE, durabilityI, ChatColor.AQUA + "Iron Work Shovel", eraA),
            generate(IRON_PICKAXE, durabilityI, ChatColor.YELLOW + "Iron Work Pickaxe", eraA),
            generate(IRON_HOE, durabilityI, ChatColor.GREEN + "Farmer's Hoe", eraA),
            generate(IRON_SWORD, durabilityI, ChatColor.YELLOW + "Barbarian Sword", eraA),
            generate(SHEARS, durabilityI, ChatColor.LIGHT_PURPLE + "Sheep Shears", eraA),
            generate(BUCKET, (short) 0, ChatColor.BLUE + "Milk Bucket", eraA),
            generate(BOOK, (short) 0, ChatColor.GOLD + "The Torah", eraA),
            generate(PAPER, (short) 0, ChatColor.YELLOW + "Roman Message", eraA),
            generate(BOOK_AND_QUILL, (short) 0, ChatColor.DARK_PURPLE + "Priest's Workbook", eraA),
            generate(FURNACE, (short) 0, ChatColor.DARK_GRAY + "Peasant's Stove", eraA),
            generate(BOW, durabilityI, ChatColor.GREEN + "Advanced Long Bow", eraA),
            generate(BOW, durabilityI, ChatColor.DARK_GREEN + "Advanced Short Bow", eraA),
            generate(CHAINMAIL_HELMET, durabilityI, ChatColor.DARK_BLUE + "Knight's Helmet", eraA),
            generate(FIREBALL, (short) 0, ChatColor.YELLOW + "The Holy Hand Grenade of Antioch", eraA),
            generate(PAINTING, (short) 0, ChatColor.BLUE + "Early Painting", eraA),
            generate(PAPER, (short) 0, ChatColor.RED + "Modernized Calendar", eraA),
            generate(BOOK, (short) 0, ChatColor.AQUA + "Basic Arithmetic", eraA),
            generate(PAPER, (short) 0, ChatColor.YELLOW + "Star Map", eraA),
            generate(COMPASS, (short) 0, ChatColor.GOLD + "Early Compass", eraA)};
        ItemStack[] bronzeItems = new ItemStack[]{generate(GOLD_SWORD, (short) durabilityG, ChatColor.YELLOW + "Bronze Sword", eraB),
            generate(FLOWER_POT_ITEM, (short) 0, ChatColor.AQUA + "Water Jug", eraB),
            generate(GOLD_CHESTPLATE, durabilityG, ChatColor.GOLD + "Bronze Chestplate", eraB),
            generate(GOLD_AXE, durabilityG, ChatColor.YELLOW + "Bronze Axe", eraB),
            generate(PAPER, (short) 0, ChatColor.RED + "Early Letter", eraB),
            generate(BOOK, (short) 0, ChatColor.GREEN + "Egyptian Manuscript", eraB),
            generate(BOAT, (short) 0, ChatColor.LIGHT_PURPLE + "Early Wood Ship", eraB),
            generate(BREWING_STAND_ITEM, (short) 0, ChatColor.YELLOW + "Alchemist's Table", eraB),
            generate(SUGAR_CANE, (short) 0, ChatColor.DARK_GREEN + "Egyptian Reeds", eraB),
            generate(BOWL, (short) 0, ChatColor.RED + "Early Fruit Bowl", eraB),
            generate(GOLD_NUGGET, (short) 0, ChatColor.YELLOW + "Gold Coin", eraB),
            generate(MUSHROOM_SOUP, (short) 0, ChatColor.GREEN + "Farmers' Meal", eraB),
            generate(BONE, (short) 0, ChatColor.DARK_RED + "Pharoh's Bone", eraB),
            generate(BONE, (short) 0, ChatColor.YELLOW + "Mammoth Bone", eraB),
            generate(BRICK, (short) 0, ChatColor.RED + "Early Dried Brick", eraB),
            generate(WORKBENCH, (short) 0, ChatColor.DARK_GRAY + "Mason's Table", eraB),
            generate(GLASS_BOTTLE, (short) 0, ChatColor.YELLOW + "Cracked Glass Bottle", eraB),
            generate(FISHING_ROD, (short) 0, ChatColor.BLUE + "Early Fisherman's Rod", eraB),
            generate(PAPER, (short) 0, ChatColor.YELLOW + "Inaccurate Calendar", eraB),
            generate(WATCH, (short) 0, ChatColor.YELLOW + "Early Sun Dial", eraB)};
        ItemStack[] stoneItems = new ItemStack[]{generate(STONE_AXE, (short) durabilityS, ChatColor.DARK_GRAY + "Slate Axe", eraC),
            generate(STONE_SWORD, durabilityS, ChatColor.AQUA + "Brittle Stone Sword", eraC),
            generate(BOW, durabilityI, ChatColor.LIGHT_PURPLE + "Early Long Bow", eraC),
            generate(BOW, durabilityI, ChatColor.YELLOW + "Experimental Bow", eraC),
            generate(WOOD_HOE, durabilityG, ChatColor.GOLD + "Poor Farmer's Hoe", eraC),
            generate(STONE_HOE, durabilityS, ChatColor.DARK_PURPLE + "Advanced Stone Hoe", eraC),
            generate(FLOWER_POT_ITEM, (short) 0, ChatColor.BLUE + "Early Pottery", eraC),
            generate(BOWL, (short) 0, ChatColor.DARK_AQUA + "Rain Bowl", eraC),
            generate(SEEDS, (short) 0, ChatColor.GREEN + "Early Wheat Seeds", eraC),
            generate(FLINT_AND_STEEL, durabilityG, ChatColor.RED + "Fire Starter", eraC),
            generate(TORCH, (short) 0, ChatColor.YELLOW + "Stone Miner's Torch", eraC),
            generate(LEVER, (short) 0, ChatColor.GOLD + "Early Experimental Lever", eraC),
            generate(LEATHER_CHESTPLATE, durabilityS, ChatColor.GREEN + "Cow Hide Jacket", eraC),
            generate(WATCH, (short) 0, ChatColor.YELLOW + "Early Sun Dial", eraC),
            generate(SADDLE, (short) 0, ChatColor.GOLD + "Leather Saddle", eraC),
            generate(LEASH, (short) 0, ChatColor.YELLOW + "Ancient Rope", eraC),
            generate(PAPER, (short) 0, ChatColor.RED + "Illegible Gibberish", eraC),
            generate(MAP, (short) 0, ChatColor.DARK_GREEN + "Very Inaccurate Map", eraC),
            generate(PAINTING, (short) 0, ChatColor.AQUA + "Symbols on Cow Hide", eraC),
            generate(RAW_BEEF, (short) 0, ChatColor.DARK_RED + "Mammoth Meat", eraC)};
        ItemStack[] prehistoricItems = new ItemStack[]{generate(INK_SACK, (short) 15, ChatColor.RED + "Crushed Dinosaur Bone", eraD),
            generate(PUMPKIN_SEEDS, (short) 0, ChatColor.DARK_PURPLE + "Cretaceous Period Seeds", eraD),
            generate(MELON_SEEDS, (short) 0, ChatColor.GOLD + "Triassic Period Seeds", eraD),
            generate(SAPLING, (short) 2, ChatColor.DARK_GRAY + "Mesozoic Sapling", eraD),//
            generate(SAPLING, (short) 1, ChatColor.BLUE + "Paleozoic Sapling", eraD),//
            generate(VINE, (short) 0, ChatColor.DARK_PURPLE + "Precambrian Plant", eraD),
            generate(INK_SACK, (short) 0, ChatColor.LIGHT_PURPLE + "Cretaceous Squid Remains", eraD),
            generate(GHAST_TEAR, (short) 0, ChatColor.DARK_BLUE + "Megalodon Tooth", eraD),
            generate(EYE_OF_ENDER, (short) 0, ChatColor.RED + "Dinosaur Eye", eraD),
            generate(RED_MUSHROOM, (short) 0, ChatColor.GOLD + "Prototaxite", eraD),
            generate(SKULL_ITEM, (short) 2, ChatColor.GREEN + "Ancient Alien Skull", eraD),
            generate(SKULL_ITEM, (short) 0, ChatColor.YELLOW + "Dinosaur Skull", eraD),
            generate(SLIME_BALL, (short) 0, ChatColor.DARK_AQUA + "Corallinaceae", eraD),
            generate(EGG, (short) 0, ChatColor.YELLOW + "Unhatched Dinosaur Egg", eraD),
            generate(BONE, (short) 0, ChatColor.BLUE + "Camptosaurus Fossil", eraD),
            generate(BONE, (short) 0, ChatColor.AQUA + "Kentrosaurus Fossil", eraD),
            generate(POISONOUS_POTATO, (short) 0, ChatColor.YELLOW + "Fossilized Herb", eraD),
            generate(BONE, (short) 0, ChatColor.DARK_AQUA + "Stegosaurus Fossil", eraD),
            generate(LONG_GRASS, (short) 0, ChatColor.DARK_GREEN + "Fossilized Grass", eraD),
            generate(LONG_GRASS, (short) 2, ChatColor.DARK_GREEN + "Fossilized Fern", eraD)};
        if (locY >= 40) {
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(ironItems[Storage.rnd.nextInt(20)]));
        } else if (locY >= 30) {
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(bronzeItems[Storage.rnd.nextInt(20)]));
        } else if (locY >= 20) {
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(stoneItems[Storage.rnd.nextInt(20)]));
        } else if (locY >= 0) {
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(prehistoricItems[Storage.rnd.nextInt(20)]));
        }
    }
}
