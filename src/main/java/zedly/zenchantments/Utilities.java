package zedly.zenchantments;

import org.bukkit.*;
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
import zedly.zenchantments.player.PlayerData;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Objects.requireNonNull;
import static org.bukkit.ChatColor.COLOR_CHAR;

public final class Utilities {
    public static final BlockFace[] CARDINAL_BLOCK_FACES = {
        BlockFace.UP,
        BlockFace.DOWN,
        BlockFace.NORTH,
        BlockFace.EAST,
        BlockFace.SOUTH,
        BlockFace.WEST
    };

    private static final int[][] DEFAULT_SEARCH_FACES = new int[27][3];

    private static ZenchantmentsPlugin plugin;

    static {
        int i = 0;
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    DEFAULT_SEARCH_FACES[i++] = new int[] { x, y, z };
                }
            }
        }
    }

    private Utilities() {
        throw new IllegalStateException();
    }

    public static void setPlugin(final @NotNull ZenchantmentsPlugin plugin) {
        if (Utilities.plugin != null) {
            throw new IllegalStateException();
        }

        Utilities.plugin = plugin;
    }

    // Maybe inline usages of this?
    public static boolean isMainHand(final @NotNull EquipmentSlot preferred) {
        return preferred == EquipmentSlot.HAND;
    }

    @NotNull
    public static List<ItemStack> getArmorAndHandItems(final @NotNull Player player, final boolean mainHand) {
        requireNonNull(player);

        final PlayerInventory inventory = player.getInventory();
        final List<ItemStack> stack = Arrays.asList(inventory.getArmorContents());

        stack.add(mainHand ? inventory.getItemInMainHand() : inventory.getItemInOffHand());
        stack.removeIf(itemStack -> itemStack == null || itemStack.getType() == Material.AIR);

        return stack;
    }

    public static void damageItemStack(final @NotNull Player player, final int damage, final boolean handUsed) {
        requireNonNull(player);

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        final PlayerInventory inventory = player.getInventory();
        final ItemStack heldItem = handUsed
            ? inventory.getItemInMainHand()
            : inventory.getItemInOffHand();

        for (int i = 0; i < damage; i++) {
            if (ThreadLocalRandom.current().nextInt(100) <= (100 / (heldItem.getEnchantmentLevel(Enchantment.DURABILITY) + 1))) {
                setItemStackDamage(heldItem, getItemStackDamage(heldItem) + 1);
            }
        }

        final int maxDurability = heldItem.getType().getMaxDurability();
        final ItemStack item = getItemStackDamage(heldItem) > maxDurability ? new ItemStack(Material.AIR) : heldItem;

        if (handUsed) {
            inventory.setItemInMainHand(item);
        } else {
            inventory.setItemInOffHand(item);
        }
    }

    public static void displayParticle(
        final @NotNull Location location,
        final @NotNull Particle particle,
        final int amount,
        final double speed,
        final double xOffset,
        final double yOffset,
        final double zOffset
    ) {
        requireNonNull(location);
        requireNonNull(location.getWorld());
        requireNonNull(particle);

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

    public static void addUnbreaking(
        final @NotNull Player player,
        final @NotNull ItemStack itemStack,
        final int damage
    ) {
        requireNonNull(player);
        requireNonNull(itemStack);

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        for (int i = 0; i < damage; i++) {
            if (ThreadLocalRandom.current().nextInt(100) <= (100 / (itemStack.getEnchantmentLevel(Enchantment.DURABILITY) + 1))) {
                setItemStackDamage(itemStack, getItemStackDamage(itemStack) + 1);
            }
        }
    }

    public static void setItemStackDamage(final @NotNull ItemStack itemStack, final int damage) {
        requireNonNull(itemStack);

        if (itemStack.getItemMeta() instanceof Damageable) {
            final Damageable damageable = (Damageable) itemStack.getItemMeta();
            damageable.setDamage(damage);
            itemStack.setItemMeta((ItemMeta) damageable);
        }
    }

    public static int getItemStackDamage(final @NotNull ItemStack itemStack) {
        requireNonNull(itemStack);

        if (itemStack.getItemMeta() instanceof Damageable) {
            final Damageable damageable = (Damageable) itemStack.getItemMeta();
            return damageable.getDamage();
        }

        return 0;
    }

    @NotNull
    public static ItemStack getUsedItemStack(final @NotNull Player player, final boolean handUsed) {
        requireNonNull(player);

        return handUsed
            ? player.getInventory().getItemInMainHand()
            : player.getInventory().getItemInOffHand();
    }

    public static void setItemStackInHand(
        final @NotNull Player player,
        final @NotNull ItemStack itemStack,
        final boolean handUsed
    ) {
        requireNonNull(player);
        requireNonNull(itemStack);

        if (handUsed) {
            player.getInventory().setItemInMainHand(itemStack);
        } else {
            player.getInventory().setItemInOffHand(itemStack);
        }
    }

    public static boolean removeMaterialsFromPlayer(
        final @NotNull Player player,
        final @NotNull Material material,
        int amount
    ) {
        requireNonNull(player);
        requireNonNull(material);

        if (player.getGameMode() == GameMode.CREATIVE) {
            return true;
        }

        final Inventory inventory = player.getInventory();

        if (!playerHasMaterial(player, material, amount)) {
            return false;
        }

        for (int i = 0; i < inventory.getSize(); i++) {
            final ItemStack item = inventory.getItem(i);

            if (item == null || item.getType() != material) {
                continue;
            }

            if (item.getAmount() > amount) {
                item.setAmount(item.getAmount() - amount);
                inventory.setItem(i, item);
                return true;
            }

            amount -= item.getAmount();
            inventory.setItem(i, null);
        }

        return true;
    }

    public static boolean playerHasMaterial(
        final @NotNull Player player,
        final @NotNull Material material,
        int amount
    ) {
        requireNonNull(player);
        requireNonNull(material);

        if (player.getGameMode() == GameMode.CREATIVE) {
            return true;
        }

        final Inventory inventory = player.getInventory();

        for (int i = 0; i < inventory.getSize(); i++) {
            final ItemStack item = inventory.getItem(i);

            if (item == null || item.getType() != material) {
                continue;
            }

            if (item.getAmount() >= amount) {
                amount = 0;
            } else {
                amount -= item.getAmount();
            }
        }

        return amount == 0;
    }

    public static int getEnchantmentLevel(final int maxLevel, final int levels) {
        if (maxLevel == 1) {
            return 1;
        }

        final int sectionSize = 32 / (maxLevel - 1);
        final int position = levels / sectionSize;
        final int mod = levels - position * sectionSize;

        if (ThreadLocalRandom.current().nextInt(2 * sectionSize) >= mod) {
            return position + 1;
        } else {
            return position + 2;
        }
    }

    public static int convertNumeralToInt(final @NotNull String numeral) {
        requireNonNull(numeral);

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

    @NotNull
    public static String convertIntToNumeral(final int number) {
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

    @NotNull
    public static Location getCenter(final @NotNull Location location) {
        return getCenter(location, false);
    }

    @NotNull
    public static Location getCenter(final @NotNull Location location, final boolean centerVertical) {
        requireNonNull(location);

        final Location centered = location.clone();
        centered.setX(location.getX() + 0.5);
        centered.setY(centerVertical ? location.getY() + 0.5 : location.getY());
        centered.setZ(location.getZ() + 0.5);

        return centered;
    }

    @NotNull
    public static Location getCenter(final @NotNull Block block) {
        return getCenter(block, false);
    }

    @NotNull
    public static Location getCenter(final @NotNull Block block, final boolean centerVertical) {
        requireNonNull(block);

        return getCenter(block.getLocation(), centerVertical);
    }

    public static BlockFace getCardinalDirection(float yaw, final float pitch) {
        if (yaw < 0) {
            yaw += 360;
        }

        yaw %= 360;

        final double i = (yaw + 8) / 18;
        final BlockFace direction;

        if (pitch < -50) {
            direction = BlockFace.UP;
        } else if (pitch > 50) {
            direction = BlockFace.DOWN;
        } else if (i >= 18 || i < 3) {
            direction = BlockFace.SOUTH;
        } else if (i < 8) {
            direction = BlockFace.WEST;
        } else if (i < 13) {
            direction = BlockFace.NORTH;
        } else {
            direction = BlockFace.EAST;
        }

        return direction;
    }

    public static boolean playerCanUseZenchantment(
        final @NotNull Player player,
        final @NotNull PlayerData playerData,
        final @NotNull NamespacedKey zenchantmentKey
    ) {
        requireNonNull(player);
        requireNonNull(playerData);
        requireNonNull(zenchantmentKey);

        if (!player.hasPermission("zenchantments.enchant.use")) {
            return false;
        }

        if (playerData.getCooldownForZenchantment(zenchantmentKey) != 0) {
            return false;
        }

        return !playerData.isDisabled(zenchantmentKey);
    }

    public static void addPotionEffect(
        final @NotNull LivingEntity entity,
        final @NotNull PotionEffectType effectType,
        final int length,
        final int intensity
    ) {
        requireNonNull(entity);
        requireNonNull(effectType);

        // Examine existing potion effects to see what operations need to be performed.
        for (final PotionEffect effect : entity.getActivePotionEffects()) {
            if (effect.getType() != effectType) {
                continue;
            }

            if (effect.getAmplifier() > intensity || effect.getDuration() > length) {
                return;
            }

            entity.removePotionEffect(effectType);
        }

        entity.addPotionEffect(new PotionEffect(effectType, length, intensity));
    }

    @NotNull
    public static String makeStringInvisible(@NotNull String string) {
        requireNonNull(string);

        string = "\\<" + string + "\\>" + COLOR_CHAR + 'F';

        final StringBuilder builder = new StringBuilder();

        for (final char c : string.toCharArray()) {
            builder.append(COLOR_CHAR);
            builder.append(c);
        }

        return builder.toString();
    }

    @NotNull
    public static Map<String, Boolean> fromInvisibleString(final @NotNull String string) {
        requireNonNull(string);

        final Map<String, Boolean> strings = new HashMap<>();

        // Reassigned in the loop below when a section is completed.
        StringBuilder builder = new StringBuilder();

        int state = 0;

        for (final char c : string.toCharArray()) {
            switch (state) {
                // Visible, waiting for '§'.
                case 0:
                    if (c == COLOR_CHAR) {
                        state = 1;
                    } else {
                        builder.append(c);
                    }
                    break;
                // Got a '§', waiting for '\'.
                case 1:
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
                // Got a '\', waiting for '§'.
                case 2:
                    if (c == COLOR_CHAR) {
                        state = 3;
                    } else {
                        builder.append(COLOR_CHAR);
                        builder.append('\\');
                        builder.append(c);
                        state = 0;
                    }
                    break;
                // Got a '§', waiting for '<'.
                case 3:
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
                // Invisible, ignore '§'.
                case 4:
                    state = 5;
                    break;
                // Invisible, waiting for '\'.
                case 5:
                    if (c == '\\') {
                        state = 6;
                    } else {
                        builder.append(c);
                        state = 4;
                    }
                    break;
                // Got '\', waiting for '§'.
                case 6:
                    if (c == COLOR_CHAR) {
                        state = 7;
                    } else {
                        builder.append('\\');
                        state = 5;
                    }
                    break;
                // Got '§', waiting for '>'.
                case 7:
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
        final @NotNull Material fill,
        final @NotNull Material check,
        final int radius,
        final @NotNull Block center,
        final @NotNull Player player,
        final @NotNull Map<Location, Long> placed
    ) {
        requireNonNull(fill);
        requireNonNull(check);
        requireNonNull(center);
        requireNonNull(player);
        requireNonNull(placed);

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                final Block possiblePlatformBlock = center.getRelative(x, -1, z);
                final Location possiblePlatformLocation = possiblePlatformBlock.getLocation();

                if (!(possiblePlatformLocation.distanceSquared(center.getLocation()) < radius * radius - 2)) {
                    continue;
                }

                if (placed.containsKey(possiblePlatformLocation)) {
                    placed.put(possiblePlatformLocation, System.nanoTime());
                } else if (possiblePlatformBlock.getType() == check
                    && MaterialList.AIR.contains(possiblePlatformBlock.getRelative(0, 1, 0).getType())
                ) {
                    if (possiblePlatformBlock.getBlockData() instanceof Levelled
                        && ((Levelled) possiblePlatformBlock.getBlockData()).getLevel() != 0
                    ) {
                        continue;
                    }

                    if (plugin.getCompatibilityAdapter().formBlock(possiblePlatformBlock, fill, player)) {
                        placed.put(possiblePlatformLocation, System.nanoTime());
                    }
                }
            }
        }
    }

    // Returns a list of blocks found using the BFS algorithm given the passed search parameters.
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
        final @NotNull Block startBlock,
        final int maxBlocks,
        final boolean strictMax,
        final float maxDistFromOrigin,
        final int[][] searchFaces,
        final @NotNull MaterialList validFind,
        @NotNull MaterialList validSearch,
        final boolean strictValidSearch,
        final boolean flipValidSearch
    ) {
        requireNonNull(startBlock);
        requireNonNull(validFind);
        requireNonNull(validSearch);

        // Ensure the search list is in the whitelist.
        if (!flipValidSearch) {
            validSearch.addAll(validFind);
        }

        // BFS through the trunk, cancel if forbidden blocks are adjacent or search body becomes too large.

        final Set<Block> searchedBlocks = new LinkedHashSet<>();
        final List<Block> foundBlocks = new ArrayList<>();
        final List<Block> toSearch = new ArrayList<>();

        // Add the origin block.
        searchedBlocks.add(startBlock);
        toSearch.add(startBlock);

        // Keep searching as long as there's more blocks to search.
        while (!toSearch.isEmpty()) {
            final Block searchBlock = toSearch.remove(0);

            // If block is in the search list, add adjacent blocks to search perimeter.
            if (validFind.contains(searchBlock.getType())) {
                foundBlocks.add(searchBlock);

                for (final int[] blockFace : searchFaces) {
                    // Add the adjacent block.
                    final Block nextBlock = searchBlock.getRelative(blockFace[0], blockFace[1], blockFace[2]);

                    // Check if it's already been searched.
                    if (searchedBlocks.contains(nextBlock)) {
                        continue;
                    }

                    // Determine if the block is in the whitelist and flip the condition if flipValidSearch == true.
                    boolean check = validSearch.contains(nextBlock.getType());
                    if (flipValidSearch) {
                        check = !check;
                    }

                    // Add to search body if it meets the condition, else return.
                    if (check) {
                        if (nextBlock.getLocation().distance(startBlock.getLocation()) > maxDistFromOrigin) {
                            continue;
                        }

                        toSearch.add(nextBlock);
                        searchedBlocks.add(nextBlock);
                    } else {
                        // Adjacent to a forbidden block. Nothing more to do.
                        if (strictValidSearch) {
                            return Collections.emptyList();
                        } else {
                            return foundBlocks;
                        }
                    }
                }
            }

            if (foundBlocks.size() > maxBlocks) {
                // Allowed size exceeded.
                if (strictMax) {
                    return Collections.emptyList();
                } else {
                    return foundBlocks;
                }
            }
        }

        return foundBlocks;
    }

    @NotNull
    public static List<Block> bfs(
        final @NotNull Block startBlock,
        final int maxBlocks,
        final boolean strictMax,
        final float maxDistFromOrigin,
        final @NotNull MaterialList validFind,
        final @NotNull MaterialList validSearch,
        final boolean strictValidSearch,
        final boolean flipValidSearch
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
