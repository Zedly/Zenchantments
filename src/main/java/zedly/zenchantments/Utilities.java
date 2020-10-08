package zedly.zenchantments;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.compatibility.EnumStorage;
import zedly.zenchantments.player.PlayerData;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.ChatColor.COLOR_CHAR;
import static org.bukkit.GameMode.CREATIVE;
import static org.bukkit.Material.AIR;
import static org.bukkit.inventory.EquipmentSlot.HAND;

public class Utilities {
    private static final int[][] DEFAULT_SEARCH_FACES = new int[27][3];

    static {
        int i = 0;
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    DEFAULT_SEARCH_FACES[i++] = new int[] {x, y, z};
                }
            }
        }
    }

    // Returns true for main hand slots, false otherwise
    public static boolean isMainHand(@NotNull EquipmentSlot preferred) {
        return preferred == HAND;
    }

    // Returns an ArrayList of ItemStacks of the player's held item and armor
    @NotNull
    public static List<ItemStack> getArmorAndHandItems(@NotNull Player player, boolean mainHand) {
        PlayerInventory inventory = player.getInventory();
        List<ItemStack> stack = Arrays.asList(inventory.getArmorContents());
        stack.add(mainHand ? inventory.getItemInMainHand() : inventory.getItemInOffHand());
        stack.removeIf(itemStack -> itemStack == null || itemStack.getType() == Material.AIR);
        return stack;
    }

    // Removes the given ItemStack's durability by the given 'damage' and then sets the item direction the given
    // players hand.
    //      This also takes into account the unbreaking enchantment
    public static void damageTool(@NotNull Player player, int damage, boolean handUsed) {
        if (!player.getGameMode().equals(CREATIVE)) {
            ItemStack heldItem = handUsed
                ? player.getInventory().getItemInMainHand()
                : player.getInventory().getItemInOffHand();
            for (int i = 0; i < damage; i++) {
                if (ThreadLocalRandom.current().nextInt(100) <= (100 / (heldItem.getEnchantmentLevel(Enchantment.DURABILITY) + 1))) {
                    Utilities.setDamage(heldItem, Utilities.getDamage(heldItem) + 1);
                }
            }
            if (handUsed) {
                player.getInventory().setItemInMainHand(
                    Utilities.getDamage(heldItem) > heldItem.getType().getMaxDurability() ? new ItemStack(AIR) : heldItem
                );
            } else {
                player.getInventory().setItemInOffHand(
                    Utilities.getDamage(heldItem) > heldItem.getType().getMaxDurability() ? new ItemStack(AIR) : heldItem
                );
            }
        }
    }

    // Displays a particle with the given data
    public static void displayParticle(
        @NotNull Location location,
        @NotNull Particle particle,
        int amount,
        double speed,
        double xOffset,
        double yOffset,
        double zOffset
    ) {
        location.getWorld().spawnParticle(
            particle,
            location.getX(),
            location.getY(),
            location.getZ(),
            amount,
            (float) xOffset,
            (float) yOffset,
            (float) zOffset,
            (float) speed
        );
    }

    // Removes the given ItemStack's durability by the given 'damage'
    //      This also takes into account the unbreaking enchantment
    public static void addUnbreaking(@NotNull Player player, @NotNull ItemStack itemStack, int damage) {
        if (!player.getGameMode().equals(CREATIVE)) {
            for (int i = 0; i < damage; i++) {
                if (ThreadLocalRandom.current().nextInt(100) <= (100 / (itemStack.getEnchantmentLevel(Enchantment.DURABILITY) + 1))) {
                    Utilities.setDamage(itemStack, getDamage(itemStack) + 1);
                }
            }
        }
    }

    public static void setDamage(@NotNull ItemStack itemStack, int damage) {
        if (itemStack.getItemMeta() instanceof Damageable) {
            Damageable damageable = (Damageable) itemStack.getItemMeta();
            damageable.setDamage(damage);
            itemStack.setItemMeta((ItemMeta) damageable);
        }
    }

    public static int getDamage(@NotNull ItemStack itemStack) {
        if (itemStack.getItemMeta() instanceof Damageable) {
            Damageable damageable = (Damageable) itemStack.getItemMeta();
            return damageable.getDamage();
        }

        return 0;
    }

    // Returns the item stack direction the player's main or off hand, determined by 'handUsed'
    @NotNull
    public static ItemStack usedStack(@NotNull Player player, boolean handUsed) {
        return handUsed ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand();
    }

    // Sets the hand the player to the given item stack, determined by 'handUsed'
    public static void setHand(@NotNull Player player, @NotNull ItemStack itemStack, boolean handUsed) {
        if (handUsed) {
            player.getInventory().setItemInMainHand(itemStack);
        } else {
            player.getInventory().setItemInOffHand(itemStack);
        }
    }

    // Removes an item stack of the given description from the players inventory
    public static boolean removeItem(@NotNull Player player, @NotNull Material material) {
        return Utilities.removeItem(player, material, 1);
    }

    // Removes an item stack of the given description from the players inventory
    public static boolean removeItem(@NotNull Player player, @NotNull ItemStack itemStack) {
        return Utilities.removeItem(player, itemStack.getType(), itemStack.getAmount());
    }

    // Removes a certain number of an item stack of the given description from the players inventory and returns true
    //      if the item stack was direction their inventory
    public static boolean removeItem(@NotNull Player player, @NotNull Material material, int amount) {
        if (player.getGameMode() == CREATIVE) {
            return true;
        }

        Inventory inventory = player.getInventory();

        if (!Utilities.hasItem(player, material, amount)) {
            return false;
        }

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);

            if (item == null || item.getType() != material) {
                continue;
            }

            if (item.getAmount() > amount) {
                item.setAmount(item.getAmount() - amount);
                inventory.setItem(i, item);
                return true;
            } else {
                amount -= item.getAmount();
                inventory.setItem(i, null);
            }
        }

        return true;
    }

    // Removes a certain number of an item stack of the given description from the players inventory and returns true
    //      if the item stack was direction their inventory
    public static boolean hasItem(@NotNull Player player, @NotNull Material material, int amount) {
        if (player.getGameMode() == CREATIVE) {
            return true;
        }

        Inventory inventory = player.getInventory();

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && item.getType() == material) {
                if (item.getAmount() >= amount) {
                    amount = 0;
                } else {
                    amount -= item.getAmount();
                }
            }
        }

        return amount == 0;
    }

    // Returns a level for the enchant event given the XP level and the enchantments max level
    public static int getEnchantLevel(int maxLevel, int levels) {
        if (maxLevel == 1) {
            return 1;
        }
        int sectionSize = 32 / (maxLevel - 1);
        int position = levels / sectionSize;
        int mod = levels - position * sectionSize;
        if (ThreadLocalRandom.current().nextInt(2 * sectionSize) >= mod) {
            return position + 1;
        } else {
            return position + 2;
        }
    }

    // Returns the english number representation of the given roman number string
    public static int getNumber(@NotNull String numeral) {
        switch (numeral.toUpperCase()) {
            case "-":
                return 0;
            case "II":
                return 2;
            case "III":
                return 3;
            case "IV":
                return 4;
            case "V":
                return 5;
            case "VI":
                return 6;
            case "VII":
                return 7;
            case "VIII":
                return 8;
            case "IX":
                return 9;
            case "X":
                return 10;
            case "I":
            default:
                return 1;
        }
    }

    // Returns the Roman number string representation of the given English number
    @NotNull
    public static String getRomanString(int number) {
        switch (number) {
            case 0:
                return "-";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            case 6:
                return "VI";
            case 7:
                return "VII";
            case 8:
                return "VIII";
            case 9:
                return "IX";
            case 10:
                return "X";
            case 1:
            default:
                return "I";
        }
    }

    // Returns the Roman number string representation of the given English number, capped at the int 'limit'
    @NotNull
    public static String getRomanString(int number, int limit) {
        if (number > limit) {
            return Utilities.getRomanString(limit);
        } else {
            return Utilities.getRomanString(number);
        }
    }

    // Returns the exact center of a block of a given location
    @NotNull
    public static Location getCenter(@NotNull Location location) {
        return Utilities.getCenter(location, false);
    }

    // Returns the exact center of a block of a given location
    @NotNull
    public static Location getCenter(@NotNull Location location, boolean centerVertical) {
        Location centered = location.clone();
        centered.setX(location.getX() + 0.5);
        centered.setY(centerVertical ? location.getY() + 0.5 : location.getY());
        centered.setZ(location.getZ() + 0.5);
        return centered;
    }

    // Returns the exact center of a block of a given block
    @NotNull
    public static Location getCenter(@NotNull Block block) {
        return Utilities.getCenter(block.getLocation());
    }

    // Returns the exact center of a block of a given block
    @NotNull
    public static Location getCenter(@NotNull Block block, boolean centerVertical) {
        return Utilities.getCenter(block.getLocation(), centerVertical);
    }

    // Returns the nearby entities at any location within the given range
    // Returns a direction integer, 0-8, for the given player's pitch and yaw
    @NotNull
    public static BlockFace getDirection(@NotNull Player player) {
        float yaw = player.getLocation().getYaw();

        if (yaw < 0) {
            yaw += 360;
        }

        yaw %= 360;
        double i = (yaw + 8) / 18;
        BlockFace direction = BlockFace.SELF;

        if (i >= 19 || i < 1) {
            direction = BlockFace.SOUTH;
        } else if (i < 3) {
            direction = BlockFace.SOUTH_WEST;
        } else if (i < 6) {
            direction = BlockFace.WEST;
        } else if (i < 8) {
            direction = BlockFace.NORTH_WEST;
        } else if (i < 11) {
            direction = BlockFace.NORTH;
        } else if (i < 13) {
            direction = BlockFace.NORTH_EAST;
        } else if (i < 16) {
            direction = BlockFace.EAST;
        } else if (i < 18) {
            direction = BlockFace.SOUTH_EAST;
        }

        return direction;
    }

    // Returns a more simple direction integer, 0-6, for the given player's pitch and yaw
    public static BlockFace getCardinalDirection(float yaw, float pitch) {
        if (yaw < 0) {
            yaw += 360;
        }

        yaw %= 360;
        double i = (yaw + 8) / 18;
        BlockFace direction;

        if (i >= 18 || i < 3) {
            direction = BlockFace.SOUTH;
        } else if (i < 8) {
            direction = BlockFace.WEST;
        } else if (i < 13) {
            direction = BlockFace.NORTH;
        } else {
            direction = BlockFace.EAST;
        }

        if (pitch < -50) {
            direction = BlockFace.UP;
        } else if (pitch > 50) {
            direction = BlockFace.DOWN;
        }

        return direction;
    }

    // Returns true if a player can use a certain enchantment at a certain time (permissions and cooldowns),
    //      otherwise false
    public static boolean canUse(
        @NotNull Player player,
        @NotNull PlayerData playerData,
        @NotNull NamespacedKey zenchantmentKey
    ) {
        if (!player.hasPermission("zenchantments.enchant.use")) {
            return false;
        }

        if (playerData.getCooldownForZenchantment(zenchantmentKey) != 0) {
            return false;
        }

        return !playerData.isDisabled(zenchantmentKey);
    }

    // Adds a potion effect of given length and intensity to the given entity.
    public static void addPotion(@NotNull LivingEntity entity, @NotNull PotionEffectType type, int length, int intensity) {
        for (PotionEffect eff : entity.getActivePotionEffects()) {
            if (eff.getType().equals(type)) {
                if (eff.getAmplifier() > intensity) {
                    return;
                } else if (eff.getDuration() > length) {
                    return;
                } else {
                    entity.removePotionEffect(type);
                }
            }
        }

        entity.addPotionEffect(new PotionEffect(type, length, intensity));
    }

    // Encodes a given string to be invisible to players surrounded by the escape sequence "\< \>"
    @NotNull
    public static String toInvisibleString(@NotNull String string) {
        string = "\\<" + string + "\\>" + COLOR_CHAR + 'F';
        StringBuilder builder = new StringBuilder();
        for (char c : string.toCharArray()) {
            builder.append(COLOR_CHAR);
            builder.append(c);
        }
        return builder.toString();
    }

    // Returns a map of strings to booleans, where the boolean represents visibility
    @NotNull
    public static Map<String, Boolean> fromInvisibleString(@NotNull String string) {
        Map<String, Boolean> strings = new LinkedHashMap<>();
        int state = 0; // 0 = close, 1 = waiting for next to open, 2 = open, 3 = waiting for next to close
        StringBuilder builder = new StringBuilder();
        for (char c : string.toCharArray()) {
            switch (state) {
                case 0: // Visible, waiting for '§'
                    if (c == COLOR_CHAR) {
                        state = 1;
                    } else {
                        builder.append(c);
                    }
                    break;
                case 1: // Got a '§', waiting for '\'
                    if (c == '\\') {
                        state = 2;
                    } else if (c == COLOR_CHAR) {
                        builder.append(COLOR_CHAR);
                    } else {
                        builder.append(COLOR_CHAR);
                        builder.append(c);
                        state = 0;
                    }
                    break;
                case 2: // Got a '\', waiting for '§'
                    if (c == COLOR_CHAR) {
                        state = 3;
                    } else {
                        builder.append(COLOR_CHAR);
                        builder.append('\\');
                        builder.append(c);
                        state = 0;
                    }
                    break;
                case 3: // Got a '§', waiting for '<'
                    if (c == '<') {
                        state = 4;
                        if (builder.length() != 0) {
                            strings.put(builder.toString(), true);
                            builder = new StringBuilder();
                        }
                    } else if (c == COLOR_CHAR) {
                        builder.append(COLOR_CHAR);
                        builder.append('\\');
                        state = 1;
                    } else {
                        builder.append(COLOR_CHAR);
                        builder.append('\\');
                        builder.append(COLOR_CHAR);
                        builder.append(c);
                        state = 0;
                    }
                    break;
                case 4: // Invisible, ignore '§'
                    state = 5;
                    break;
                case 5: // Invisible, waiting for '\'
                    if (c == '\\') {
                        state = 6;
                    } else {
                        builder.append(c);
                        state = 4;
                    }
                    break;
                case 6: // Got '\', waiting for '§'
                    if (c == COLOR_CHAR) {
                        state = 7;
                    } else {
                        builder.append('\\');
                        state = 5;
                    }
                    break;
                case 7: // Got '§', waiting for '>'
                    if (c == '>') {
                        state = 0;
                        if (builder.length() != 0) {
                            strings.put(builder.toString(), false);
                            builder = new StringBuilder();
                        }
                    } else {
                        builder.append('\\');
                        builder.append(c);
                        state = 4;
                    }
                    break;
            }
        }

        if (builder.length() != 0) {
            strings.put(builder.toString(), true);
        }

        return strings;
    }

    public static void selfRemovingArea(
        @NotNull Material fill,
        @NotNull Material check,
        int radius,
        @NotNull Block center,
        @NotNull Player player,
        @NotNull Map<Location, Long> placed
    ) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block possiblePlatformBlock = center.getRelative(x, -1, z);
                Location possiblePlatformLocation = possiblePlatformBlock.getLocation();

                if (!(possiblePlatformLocation.distanceSquared(center.getLocation()) < radius * radius - 2)) {
                    continue;
                }

                if (placed.containsKey(possiblePlatformLocation)) {
                    placed.put(possiblePlatformLocation, System.nanoTime());
                } else if (possiblePlatformBlock.getType() == check
                    && Storage.COMPATIBILITY_ADAPTER.Airs().contains(possiblePlatformBlock.getRelative(0, 1, 0).getType())
                ) {
                    if (possiblePlatformBlock.getBlockData() instanceof Levelled
                        && ((Levelled) possiblePlatformBlock.getBlockData()).getLevel() != 0
                    ) {
                        continue;
                    }

                    if (Storage.COMPATIBILITY_ADAPTER.formBlock(possiblePlatformBlock, fill, player)) {
                        placed.put(possiblePlatformLocation, System.nanoTime());
                    }
                }
            }
        }
    }

    // Returns a list of blocks found using the BFS algorithm given the passed search parameters
    //
    // startBlock: The starting position of the BFS algorithm
    // maxBlocks: The max number of blocks to found (will return empty list if strict is true)
    // maxDistFromOrigin: The max distance the center of a found block can be from the center of startBlock to be a valid find
    // strictMax: true -> return nothing if maxBlocks num is exceeded; false -> return current find if maxBlock num is exceeded
    // searchFaces: The block faces to search
    // validFind: valid materials for a found block
    // validSearch: valid materials for a searched block; Will return empty list if not one of these
    // strictValidSearch: true -> return nothing if blacklist block is found; false -> return current find if blacklist block is found
    // flipValidSearch: true -> validSearch is a blacklist; false -> validSearch is a whitelist
    @NotNull
    public static List<Block> bfs(
        @NotNull Block startBlock,
        int maxBlocks,
        boolean strictMax,
        float maxDistFromOrigin,
        int[][] searchFaces,
        @NotNull EnumStorage<Material> validFind,
        @NotNull EnumStorage<Material> validSearch,
        boolean strictValidSearch,
        boolean flipValidSearch
    ) {
        // Ensure the search list is in the whitelist
        if (!flipValidSearch) {
            validSearch = new EnumStorage<>(new Material[] {}, validSearch, validFind);
        }

        // BFS through the trunk, cancel if forbidden blocks are adjacent or search body becomes too large

        // Searched blocks
        Set<Block> searchedBlocks = new LinkedHashSet<>();

        // Searched blocks that match the whitelist
        List<Block> foundBlocks = new ArrayList<>();

        // Blocks that still need to be searched
        List<Block> toSearch = new ArrayList<>();

        // Add the origin block
        searchedBlocks.add(startBlock);
        toSearch.add(startBlock);

        // Keep searching as long as there's more blocks to search
        while (!toSearch.isEmpty()) {
            // Get the next block to search
            Block searchBlock = toSearch.remove(0);

            // If block is in the search list, add adjacent blocks to search perimeter
            if (validFind.contains(searchBlock.getType())) {
                foundBlocks.add(searchBlock);

                for (int[] blockFace : searchFaces) {
                    // Add the adjacent block
                    Block nextBlock = searchBlock.getRelative(blockFace[0], blockFace[1], blockFace[2]);

                    // See if its been searched before
                    if (searchedBlocks.contains(nextBlock)) {
                        continue;
                    }

                    // Determine if the block is in the whitelist and flip the condition if flipValidSearch
                    boolean check = validSearch.contains(nextBlock.getType());
                    if (flipValidSearch) {
                        check = !check;
                    }

                    // Add to search body if it meets the condition, else return
                    if (check) {
                        if (nextBlock.getLocation().distance(startBlock.getLocation()) > maxDistFromOrigin) {
                            continue;
                        }

                        toSearch.add(nextBlock);
                        searchedBlocks.add(nextBlock);
                    } else {
                        // Adjacent to a forbidden block. Nothing more to do
                        if (strictValidSearch) {
                            return new ArrayList<>();
                        } else {
                            return foundBlocks;
                        }
                    }
                }
            }

            if (foundBlocks.size() > maxBlocks) {
                // Allowed size exceeded.
                if (strictMax) {
                    return new ArrayList<>();
                } else {
                    return foundBlocks;
                }
            }
        }

        return foundBlocks;
    }

    @NotNull
    public static List<Block> bfs(
        @NotNull Block startBlock,
        int maxBlocks,
        boolean strictMax,
        float maxDistFromOrigin,
        @NotNull EnumStorage<Material> validFind,
        @NotNull EnumStorage<Material> validSearch,
        boolean strictValidSearch,
        boolean flipValidSearch
    ) {
        return Utilities.bfs(
            startBlock,
            maxBlocks,
            strictMax,
            maxDistFromOrigin,
            DEFAULT_SEARCH_FACES,
            validFind,
            validSearch,
            strictValidSearch,
            flipValidSearch
        );
    }
}