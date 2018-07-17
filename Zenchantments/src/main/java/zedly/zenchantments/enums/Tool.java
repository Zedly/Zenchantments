package zedly.zenchantments.enums;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.Material.*;

// Enum for Tool sets within the game, used by enchantment classes to easily define what tools can be used on each
//      enchantment. Each enum has a String representation used in the config, and an array of materials it represents
public enum Tool {
    AXE("Axe", new Material[]{WOOD_AXE, STONE_AXE, GOLD_AXE, IRON_AXE, DIAMOND_AXE}, false),
    SHOVEL("Shovel", new Material[]{WOOD_SPADE, STONE_SPADE, GOLD_SPADE, IRON_SPADE, DIAMOND_SPADE}, false),
    SWORD("Sword", new Material[]{WOOD_SWORD, STONE_SWORD, GOLD_SWORD, IRON_SWORD, DIAMOND_SWORD}, false),
    PICKAXE("Pickaxe", new Material[]{WOOD_PICKAXE, STONE_PICKAXE, GOLD_PICKAXE, IRON_PICKAXE, DIAMOND_PICKAXE}, false),
    ROD("Rod", new Material[]{FISHING_ROD}, true),
    SHEAR("Shears", new Material[]{SHEARS}, true),
    BOW("Bow", new Material[]{Material.BOW}, true),
    LIGHTER("Lighter", new Material[]{FLINT_AND_STEEL}, true),
    HOE("Hoe", new Material[]{WOOD_HOE, STONE_HOE, GOLD_HOE, IRON_HOE, DIAMOND_HOE}, true),
    HELMET("Helmet", new Material[]{DIAMOND_HELMET, IRON_HELMET, GOLD_HELMET, CHAINMAIL_HELMET, LEATHER_HELMET}, false),
    CHESTPLATE("Chestplate", new Material[]{DIAMOND_CHESTPLATE, IRON_CHESTPLATE, GOLD_CHESTPLATE, CHAINMAIL_CHESTPLATE, LEATHER_CHESTPLATE, ELYTRA}, false),
    LEGGINGS("Leggings", new Material[]{DIAMOND_LEGGINGS, IRON_LEGGINGS, GOLD_LEGGINGS, CHAINMAIL_LEGGINGS, LEATHER_LEGGINGS}, false),
    BOOTS("Boots", new Material[]{DIAMOND_BOOTS, IRON_BOOTS, GOLD_BOOTS, CHAINMAIL_BOOTS, LEATHER_BOOTS}, false),
    WINGS("Elytra", new Material[]{ELYTRA}, false),
    ALL("All", Material.values(), false);

    private final String id;              // The identification String of the enum
    private final Material[] materials;   // The array of materials the enum represents
    private final boolean rightClickAction; // If the tool has a right click action

    // Constructs a new enum of given materials with the given ID
    Tool(String id, Material[] materials, boolean rightClickAction) {
        this.id = id;
        this.materials = materials;
        this.rightClickAction = rightClickAction;
    }

    // Returns the String ID of the enum
    public String getID() {
        return id;
    }

    // Returns the array of materials that the enum represents
    public Material[] getMaterials() {
        return materials;
    }

    // Returns whether or not the tool type has a right click action
    public boolean canRightClickAction() {
        return rightClickAction;
    }

    // Returns true if the given material is contained within the enum, otherwise false
    public boolean contains(Material mat) {
        return ArrayUtils.contains(materials, mat);
    }

    // Returns true if the given item stack's material is contained within the enum, otherwise false
    public boolean contains(ItemStack stk) {
        return contains(stk.getType());
    }

    // Returns an enum using the given String, matching this with the enum's ID String
    public static Tool fromString(String text) {
        if (text != null) {
            for (Tool t : Tool.values()) {
                if (text.equalsIgnoreCase(t.id)) {
                    return t;
                }
            }
        }
        return null;
    }

    // Returns an enum using a given Material
    public static Tool fromMaterial(Material mat) {
        for (Tool t : Tool.values()) {
            if (t.contains(mat)) {
                return t;
            }
        }
        return null;
    }

    // Returns an enum using a given ItemStack
    public static Tool fromItemStack(ItemStack stk) {
        return stk == null ? null : fromMaterial(stk.getType());
    }

}