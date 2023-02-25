package zedly.zenchantments;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.craftbukkit.v1_19_R2.util.CraftChatMessage;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.player.PlayerData;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

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

    public static final int[][] DEFAULT_SEARCH_FACES = new int[27][3];

    static {
        int i = 0;
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    DEFAULT_SEARCH_FACES[i++] = new int[]{x, y, z};
                }
            }
        }
    }

    private Utilities() {
        throw new IllegalStateException();
    }

    @NotNull
    public static <T extends Enum<?>> T randomOfEnum(Class<T> clazz) {
        final var i = ThreadLocalRandom.current().nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[i];
    }

    /*
    @NotNull
    public static List<ItemStack> getArmorAndHandItems(final @NotNull Player player, final boolean mainHand) {
        requireNonNull(player);

        final var stacks = new ArrayList<ItemStack>(5); // Full armor + one hand = max 5 ItemStacks.
        final var inventory = player.getInventory();

        stacks.addAll(Arrays.asList(inventory.getArmorContents()));
        stacks.add(mainHand ? inventory.getItemInMainHand() : inventory.getItemInOffHand());
        stacks.removeIf(itemStack -> itemStack == null || itemStack.getType() == Material.AIR);

        return stacks;
    }
    */

    public static int getUnbreakingLevel(ItemStack is) {
        return is.getEnchantmentLevel(Enchantment.DURABILITY);
    }

    public static boolean decideRandomlyIfDamageToolRespectUnbreaking(int unbreakingLevel) {
        return ThreadLocalRandom.current().nextInt(100) <= (100 / (unbreakingLevel + 1));
    }

    public static int getUsesRemainingOnTool(ItemStack is) {
        if(is == null || !is.hasItemMeta()) {
            return Integer.MAX_VALUE;
        }

        if (is.getItemMeta() instanceof final Damageable damageable) {
            return is.getType().getMaxDurability() - damageable.getDamage();
        }
        return Integer.MAX_VALUE;
    }

    public static void damageItemStackRespectUnbreaking(final @NotNull Player player, final int damage, final EquipmentSlot slot) {
        requireNonNull(player);

        final PlayerInventory inventory = player.getInventory();
        final ItemStack heldItem = inventory.getItem(slot);
        final int unbreakingLevel = getUnbreakingLevel(heldItem);
        int totalDamageApplied = 0;

        for (var i = 0; i < damage; i++) {
            if (decideRandomlyIfDamageToolRespectUnbreaking(unbreakingLevel)) {
                totalDamageApplied++;
            }
        }

        damageItemStackIgnoreUnbreaking(player, totalDamageApplied, slot);
    }

    public static void damageItemStackIgnoreUnbreaking(final @NotNull Player player, final int damage, final EquipmentSlot slot) {
        requireNonNull(player);

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        final var inventory = player.getInventory();
        final ItemStack heldItem = inventory.getItem(slot);
        int newDamage = getItemStackDamage(heldItem) + damage;
        if(heldItem.getType() == Material.ELYTRA && newDamage >= Material.ELYTRA.getMaxDurability()) {
            newDamage = Material.ELYTRA.getMaxDurability() - 1;
        }
        setItemStackDamage(heldItem, newDamage);

        final var maxDurability = heldItem.getType().getMaxDurability();
        final var item = getItemStackDamage(heldItem) > maxDurability ? new ItemStack(Material.AIR) : heldItem;

        inventory.setItem(slot, item);
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

    public static void setItemStackDamage(final @NotNull ItemStack itemStack, final int damage) {
        requireNonNull(itemStack);

        if (itemStack.getItemMeta() instanceof final Damageable damageable) {
            damageable.setDamage(damage);
            itemStack.setItemMeta(damageable);
        }
    }

    public static int getItemStackDamage(final @NotNull ItemStack itemStack) {
        requireNonNull(itemStack);

        if (itemStack.getItemMeta() instanceof final Damageable damageable) {
            return damageable.getDamage();
        }

        return 0;
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

        final var inventory = player.getInventory();

        if (!playerHasMaterial(player, material, amount)) {
            return false;
        }

        for (var i = 0; i < inventory.getSize(); i++) {
            final var item = inventory.getItem(i);

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

        final var inventory = player.getInventory();

        for (var i = 0; i < inventory.getSize(); i++) {
            final var item = inventory.getItem(i);

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

        final var sectionSize = 32 / (maxLevel - 1);
        final var position = levels / sectionSize;
        final var mod = levels - position * sectionSize;

        if (ThreadLocalRandom.current().nextInt(2 * sectionSize) >= mod) {
            return position + 1;
        } else {
            return position + 2;
        }
    }

    public static int convertNumeralToInt(final @NotNull String numeral) {
        requireNonNull(numeral);

        return switch (numeral.toUpperCase()) {
            case "-" -> 0;
            case "II" -> 2;
            case "III" -> 3;
            case "IV" -> 4;
            case "V" -> 5;
            case "VI" -> 6;
            case "VII" -> 7;
            case "VIII" -> 8;
            case "IX" -> 9;
            case "X" -> 10;
            default -> 1;
        };
    }

    @NotNull
    public static String convertIntToNumeral(final int number) {
        return switch (number) {
            case 0 -> "-";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> "I";
        };
    }

    @NotNull
    public static Location getCenter(final @NotNull Location location) {
        return getCenter(location, false);
    }

    @NotNull
    public static Location getCenter(final @NotNull Location location, final boolean centerVertical) {
        requireNonNull(location);

        final var centered = location.clone();
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

        final var i = (yaw + 8) / 18;
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
        for (final var effect : entity.getActivePotionEffects()) {
            if (effect.getType() != effectType) {
                continue;
            }

            if (effect.getAmplifier() >= intensity || effect.getDuration() > length) {
                return;
            }

            entity.removePotionEffect(effectType);
        }

        entity.addPotionEffect(new PotionEffect(effectType, length, intensity));
    }

    @NotNull
    public static String reproduceCorruptedInvisibleSequence(final @NotNull String original) {
        requireNonNull(original);
        return CraftChatMessage.fromJSONComponent(CraftChatMessage.fromStringToJSON(original, false));
    }

    @NotNull
    public static String makeStringInvisible(@NotNull String string) {
        requireNonNull(string);

        string = "\\<" + string + "\\>" + COLOR_CHAR + 'F';

        final var builder = new StringBuilder();

        for (final var c : string.toCharArray()) {
            builder.append(COLOR_CHAR);
            builder.append(c);
        }

        return builder.toString();
    }

    @NotNull
    public static Map<String, Boolean> fromInvisibleString(final @NotNull String string) {
        requireNonNull(string);

        final var strings = new HashMap<String, Boolean>();

        // Reassigned in the loop below when a section is completed.
        var builder = new StringBuilder();

        var state = 0;

        for (final var c : string.toCharArray()) {
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

    public static int countItems(final @NotNull Iterable<ItemStack> stacks, final @NotNull Predicate<ItemStack> predicate) {
        requireNonNull(stacks);
        requireNonNull(predicate);

        var count = 0;

        for (final var stack : stacks) {
            if (predicate.test(stack)) {
                count+=stack.getAmount();
            }
        }

        return count;
    }

    public static int countItemStacks(final @NotNull Iterable<ItemStack> stacks, final @NotNull Predicate<ItemStack> predicate) {
        requireNonNull(stacks);
        requireNonNull(predicate);

        var count = 0;

        for (final var stack : stacks) {
            if (predicate.test(stack)) {
                count++;
            }
        }

        return count;
    }

    // Returns a list of blocks found using the BFS algorithm given the passed search parameters.
    //
    // startBlock: The starting position of the BFS algorithm
    // maxBlocks: The max number of blocks to found (will return empty list if strict is true)
    // maxDistFromOrigin: The max distance the center of a found block can be from the center of startBlock to be a valid find
    // returnEmptyIfMaxExceeded: true -> return nothing if maxBlocks num is exceeded; false -> return current find if maxBlock num is exceeded
    // searchFaces: The block faces to search
    // validFind: valid materials for a found block
    // validSearch: valid materials for a searched block; Will return empty list if not one of these
    // strictValidSearch: true -> return nothing if blacklist block is found; false -> return current find if blacklist block is found
    // flipValidSearch: true -> validSearch is a blacklist; false -> validSearch is a whitelist
    @NotNull
    public static List<Block> bfs(
        final @NotNull Block startBlock,
        final int maxBlocks,
        final boolean returnEmptyIfMaxExceeded,
        final float maxDistFromOrigin,
        final int[][] searchFaces,
        final @NotNull MaterialList validFind,
        final @NotNull MaterialList validSearch,
        final boolean strictValidSearch,
        final boolean flipValidSearch
    ) {
        requireNonNull(startBlock);
        requireNonNull(validFind);
        requireNonNull(validSearch);

        // BFS through the trunk, cancel if forbidden blocks are adjacent or search body becomes too large.

        final var searchedBlocks = new LinkedHashSet<Block>();
        final var foundBlocks = new ArrayList<Block>();
        final var toSearch = new ArrayList<Block>();

        // Add the origin block.
        searchedBlocks.add(startBlock);
        toSearch.add(startBlock);

        // Keep searching as long as there's more blocks to search.
        while (!toSearch.isEmpty()) {
            final var searchBlock = toSearch.remove(0);

            // If block is in the search list, add adjacent blocks to search perimeter.
            if (validFind.contains(searchBlock.getType())) {
                foundBlocks.add(searchBlock);

                for (final var blockFace : searchFaces) {
                    // Add the adjacent block.
                    final var nextBlock = searchBlock.getRelative(blockFace[0], blockFace[1], blockFace[2]);

                    // Check if it's already been searched.
                    if (searchedBlocks.contains(nextBlock)) {
                        continue;
                    }

                    // Determine if the block is in the whitelist and flip the condition if flipValidSearch == true.
                    var check = validSearch.contains(nextBlock.getType());
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

            if (foundBlocks.size() >= maxBlocks) {
                // Allowed size exceeded.
                if (returnEmptyIfMaxExceeded) {
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
