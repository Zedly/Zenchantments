package zedly.zenchantments;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;
import static org.bukkit.Material.*;

public enum Tool {
    AXE(
        "Axe",
        new Material[] { WOODEN_AXE, STONE_AXE, GOLDEN_AXE, IRON_AXE, DIAMOND_AXE, NETHERITE_AXE },
        false
    ),
    SHOVEL(
        "Shovel",
        new Material[] { WOODEN_SHOVEL, STONE_SHOVEL, GOLDEN_SHOVEL, IRON_SHOVEL, DIAMOND_SHOVEL, NETHERITE_SHOVEL },
        false
    ),
    SWORD(
        "Sword",
        new Material[] { WOODEN_SWORD, STONE_SWORD, GOLDEN_SWORD, IRON_SWORD, DIAMOND_SWORD, NETHERITE_SWORD },
        false
    ),
    PICKAXE(
        "Pickaxe",
        new Material[] { WOODEN_PICKAXE, STONE_PICKAXE, GOLDEN_PICKAXE, IRON_PICKAXE, DIAMOND_PICKAXE, NETHERITE_PICKAXE },
        false
    ),
    ROD(
        "Rod",
        new Material[] { FISHING_ROD },
        true
    ),
    SHEAR(
        "Shears",
        new Material[] { SHEARS },
        true
    ),
    BOW(
        "Bow",
        new Material[] { Material.BOW, CROSSBOW },
        true
    ),
    LIGHTER(
        "Lighter",
        new Material[] { FLINT_AND_STEEL },
        true
    ),
    HOE(
        "Hoe",
        new Material[] { WOODEN_HOE, STONE_HOE, GOLDEN_HOE, IRON_HOE, DIAMOND_HOE, NETHERITE_HOE },
        true
    ),
    HELMET(
        "Helmet",
        new Material[] { NETHERITE_HELMET, DIAMOND_HELMET, IRON_HELMET, GOLDEN_HELMET, CHAINMAIL_HELMET, LEATHER_HELMET, TURTLE_HELMET },
        false
    ),
    CHESTPLATE(
        "Chestplate",
        new Material[] {
            NETHERITE_CHESTPLATE,
            DIAMOND_CHESTPLATE,
            IRON_CHESTPLATE,
            GOLDEN_CHESTPLATE,
            CHAINMAIL_CHESTPLATE,
            LEATHER_CHESTPLATE,
            ELYTRA
        },
        false
    ),
    LEGGINGS(
        "Leggings",
        new Material[] {
            NETHERITE_LEGGINGS,
            DIAMOND_LEGGINGS,
            IRON_LEGGINGS,
            GOLDEN_LEGGINGS,
            CHAINMAIL_LEGGINGS,
            LEATHER_LEGGINGS
        },
        false
    ),
    BOOTS(
        "Boots",
        new Material[] { NETHERITE_BOOTS, DIAMOND_BOOTS, IRON_BOOTS, GOLDEN_BOOTS, CHAINMAIL_BOOTS, LEATHER_BOOTS },
        false
    ),
    WINGS(
        "Elytra",
        new Material[] { ELYTRA },
        false
    ),
    ALL(
        "All",
        new Material[] {
            WOODEN_AXE,
            STONE_AXE,
            GOLDEN_AXE,
            IRON_AXE,
            DIAMOND_AXE,
            NETHERITE_AXE,
            WOODEN_SHOVEL,
            STONE_SHOVEL,
            GOLDEN_SHOVEL,
            IRON_SHOVEL,
            DIAMOND_SHOVEL,
            NETHERITE_SHOVEL,
            WOODEN_SWORD,
            STONE_SWORD,
            GOLDEN_SWORD,
            IRON_SWORD,
            DIAMOND_SWORD,
            NETHERITE_SWORD,
            WOODEN_PICKAXE,
            STONE_PICKAXE,
            GOLDEN_PICKAXE,
            IRON_PICKAXE,
            DIAMOND_PICKAXE,
            NETHERITE_PICKAXE,
            FISHING_ROD,
            SHEARS,
            Material.BOW,
            CROSSBOW,
            FLINT_AND_STEEL,
            ELYTRA,
            WOODEN_HOE,
            STONE_HOE,
            GOLDEN_HOE,
            IRON_HOE,
            DIAMOND_HOE,
            NETHERITE_HOE,
            NETHERITE_HELMET,
            DIAMOND_HELMET,
            IRON_HELMET,
            GOLDEN_HELMET,
            CHAINMAIL_HELMET,
            LEATHER_HELMET,
            NETHERITE_CHESTPLATE,
            DIAMOND_CHESTPLATE,
            IRON_CHESTPLATE,
            GOLDEN_CHESTPLATE,
            CHAINMAIL_CHESTPLATE,
            LEATHER_CHESTPLATE,
            NETHERITE_LEGGINGS,
            DIAMOND_LEGGINGS,
            IRON_LEGGINGS,
            GOLDEN_LEGGINGS,
            CHAINMAIL_LEGGINGS,
            LEATHER_LEGGINGS,
            NETHERITE_BOOTS,
            DIAMOND_BOOTS,
            IRON_BOOTS,
            GOLDEN_BOOTS,
            CHAINMAIL_BOOTS,
            LEATHER_BOOTS
        },
        false
    );

    private final String     id;
    private final Material[] materials;
    private final boolean    rightClickAction;

    Tool(final @NotNull String id, final @NotNull Material[] materials, final boolean rightClickAction) {
        this.id = id;
        this.materials = materials;
        this.rightClickAction = rightClickAction;
    }

    @Nullable
    public static Tool fromString(final @NotNull String text) {
        for (Tool tool : values()) {
            if (text.equalsIgnoreCase(tool.id)) {
                return tool;
            }
        }

        return null;
    }

    @Nullable
    public static Tool fromMaterial(final @NotNull Material material) {
        requireNonNull(material);

        for (Tool tool : values()) {
            if (tool.contains(material)) {
                return tool;
            }
        }

        return null;
    }

    public static Tool fromItemStack(final @Nullable ItemStack stack) {
        return stack == null ? null : fromMaterial(stack.getType());
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    @NotNull
    public Material[] getMaterials() {
        return this.materials;
    }

    public boolean hasRightClickAction() {
        return this.rightClickAction;
    }

    public boolean contains(final @NotNull Material material) {
        requireNonNull(material);

        return ArrayUtils.contains(this.materials, material);
    }
}
