package zedly.zenchantments;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.*;
import static org.bukkit.GameMode.*;
import static org.bukkit.Material.*;
import org.bukkit.block.Block;
import static org.bukkit.enchantments.Enchantment.LOOT_BONUS_BLOCKS;
import static org.bukkit.enchantments.Enchantment.SILK_TOUCH;
import org.bukkit.entity.*;
import static org.bukkit.entity.EntityType.EXPERIENCE_ORB;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.*;
import static org.bukkit.inventory.EquipmentSlot.HAND;
import org.bukkit.potion.*;

public class Utilities {

    // Returns true for main hand slots, false otherwise
    public static boolean usedHand(EquipmentSlot preferred) {
        return preferred == HAND;
    }

    // Returns an ArrayList of ItemStacks of the player's held item and armor
    public static List<ItemStack> getRelevant(Player player, boolean usedHand) {
        List<ItemStack> stk = new ArrayList<>();
        stk.addAll(Arrays.asList(player.getInventory().getArmorContents()));
        stk.add(usedStack(player, usedHand));
        Iterator<ItemStack> it = stk.iterator();
        while (it.hasNext()) {
            if (it.next() == null) {
                it.remove();
            }
        }
        return stk;
    }

    // Removes the given ItemStack's durability by the given 'damage' and then sets the item in the given players hand.
    //      This also takes into account the unbreaking enchantment
    public static void addUnbreaking(Player player, int damage, boolean handUsed) {
        if (!player.getGameMode().equals(CREATIVE)) {
            ItemStack hand = handUsed ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand();
            for (int i = 0; i < damage; i++) {
                if (Storage.rnd.nextInt(100) <= (100 / (hand.getEnchantmentLevel(org.bukkit.enchantments.Enchantment.DURABILITY) + 1))) {
                    hand.setDurability((short) (hand.getDurability() + 1));
                }
            }
            if (handUsed) {
                player.getInventory().setItemInMainHand(hand.getDurability() > hand.getType().getMaxDurability() ? null : hand);
            } else {
                player.getInventory().setItemInOffHand(hand.getDurability() > hand.getType().getMaxDurability() ? null : hand);
            }
        }
    }

    // Displays a particle with the given data
    public static void display(Location loc, Particle particle, int amount, float speed, float xO, float yO, float zO) {
        loc.getWorld().spawnParticle(particle, loc.getX(), loc.getY(), loc.getZ(), amount, xO, yO, zO, speed);
    }

    // Removes the given ItemStack's durability by the given 'damage'
    //      This also takes into account the unbreaking enchantment
    public static void addUnbreaking(Player player, ItemStack is, int damage) {
        if (!player.getGameMode().equals(CREATIVE)) {
            for (int i = 0; i < damage; i++) {
                if (Storage.rnd.nextInt(100) <= (100 / (is.getEnchantmentLevel(org.bukkit.enchantments.Enchantment.DURABILITY) + 1))) {
                    is.setDurability((short) (is.getDurability() + 1));
                }
            }
        }
    }

    // Returns the item stack in the player's main or off hand, determinted by 'handUsed'
    public static ItemStack usedStack(Player player, boolean handUsed) {
        return handUsed ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand();
    }

    // Sets the hand the player to the given item stack, determined by 'handUsed'
    public static void setHand(Player player, ItemStack stk, boolean handUsed) {
        if (handUsed) {
            player.getInventory().setItemInMainHand(stk);
        } else {
            player.getInventory().setItemInOffHand(stk);
        }
    }

    // Removes a certain number of an item stack of the given description from the players inventory
    public static void removeItem(Player player, Material mat, short data, int amount) {
        if (!player.getGameMode().equals(CREATIVE)) {
            Inventory inv = player.getInventory();
            for (int i = 0; i < inv.getSize(); i++) {
                if (inv.getItem(i) != null && inv.getItem(i).getType() == mat && inv.getItem(i).getDurability() == data) {
                    if (inv.getItem(i).getAmount() > amount) {
                        int res = inv.getItem(i).getAmount() - amount;
                        ItemStack rest = inv.getItem(i);
                        rest.setAmount(res);
                        inv.setItem(i, rest);
                        return;
                    } else {
                        amount -= inv.getItem(i).getAmount();
                        inv.setItem(i, null);
                    }
                }
            }
        }
    }

    // Removes a certain number of an item stack of the given description from the players inventory
    public static void removeItem(Player player, Material mat, int amount) {
        removeItem(player, mat, (short) 0, amount);
    }

    // Removes a certain number of an item stack of the given description from the players inventory
    public static void removeItem(Player player, ItemStack is) {
        removeItem(player, is.getType(), is.getDurability(), is.getAmount());
    }

    // Removes a certain number of an item stack of the given description from the players inventory and returns true
    //      if the item stack was in their inventory
    public static boolean removeItemCheck(Player player, Material mat, short data, int amount) {
        if (player.getGameMode().equals(CREATIVE)) {
            return true;
        }
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) != null && inv.getItem(i).getType() == mat && inv.getItem(i).getDurability() == data) {
                if (inv.getItem(i).getAmount() > amount) {
                    int res = inv.getItem(i).getAmount() - amount;
                    ItemStack rest = inv.getItem(i);
                    rest.setAmount(res);
                    inv.setItem(i, rest);
                    return true;
                } else {
                    amount -= inv.getItem(i).getAmount();
                    inv.setItem(i, null);
                    return true;
                }
            }
        }
        return false;
    }

    // Returns a level for the enchant event given the XP level and the enchantments max level
    public static int getEnchantLevel(int maxlevel, int levels) {
        if (maxlevel == 1) {
            return 1;
        }
        int sectionsize = 32 / (maxlevel - 1);
        int position = levels / sectionsize;
        int mod = levels - position * sectionsize;
        if (Storage.rnd.nextInt(2 * sectionsize) >= mod) {
            return position + 1;
        } else {
            return position + 2;
        }
    }

    // Returns the english number representation of the given roman number string
    public static int getNumber(String numeral) {
        switch (numeral.toUpperCase()) {
            case "-":
                return 0;
            case "I":
                return 1;
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
            default:
                return 1;
        }
    }

    // Returns the roman number string representation of the given english number
    public static String getRomanString(int number) {
        switch (number) {
            case 0:
                return "-";
            case 1:
                return "I";
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
            default:
                return "I";
        }
    }

    // Returns the roman number string representation of the given english number, capped at the int 'limit'
    public static String getRomanString(int number, int limit) {
        if (number > limit) {
            return getRomanString(limit);
        } else {
            return getRomanString(number);
        }
    }

    // Returns a set of item stacks that would be dropped during a normal block break event with fortune
    public static ArrayList<ItemStack> getFortuneDrops(int level, Block blk) {
        Material mat = blk.getType();
        ArrayList<ItemStack> stacks = new ArrayList<>();
        int prob = Storage.rnd.nextInt(100);
        switch (mat) {
            case COAL_ORE:
            case DIAMOND_ORE:
            case EMERALD_ORE:
            case QUARTZ_ORE:
            case LAPIS_ORE:
                short n = 0;
                if (mat == LAPIS_ORE) {
                    n = 4;
                }
                Material m = AIR;
                int n0 = blk.getDrops().size();
                for (ItemStack s : blk.getDrops()) {
                    m = s.getType();
                }
                Random rnd = new Random();
                int c = 0;
                for (int i2 = 0; i2 < n0; i2++) {
                    double f = Math.pow(1.3, level);
                    f -= 1;
                    f *= 100;
                    int something = (int) f;
                    c++;
                    while (something > 0) {
                        if (rnd.nextInt(100) < something) {
                            c++;
                        }
                        something -= 100;
                    }
                }
                for (int i3 = 0; i3 < c; i3++) {
                    stacks.add(new ItemStack(m, 1, n));
                }
                break;
            case REDSTONE_ORE:
            case GLOWING_REDSTONE_ORE:
                int n1 = blk.getDrops().size() - 1;
                n1 += level;
                for (int i = 0; i < n1; i++) {
                    stacks.add(new ItemStack(REDSTONE, 1));
                }
                break;
            case SEA_LANTERN:
                int n2 = blk.getDrops().size() - 1;
                n2 += level;
                if (n2 > 5) {
                    n2 = 5;
                }
                for (int i = 0; i < n2; i++) {
                    stacks.add(new ItemStack(PRISMARINE_CRYSTALS, 1));
                }
                break;
            case GLOWSTONE:
                int n3 = blk.getDrops().size() - 1;
                n3 += level;
                if (n3 > 4) {
                    n3 = 4;
                }
                for (int i = 0; i < n3; i++) {
                    stacks.add(new ItemStack(GLOWSTONE_DUST, 1));
                }
                break;
            case GRAVEL:
                if (level == 1) {
                    if (prob < 15) {
                        ItemStack stk2 = new ItemStack(FLINT, 1);
                        stacks.add(stk2);
                    } else {
                        ItemStack stk2 = new ItemStack(GRAVEL, 1);
                        stacks.add(stk2);
                    }
                } else if (level == 2) {

                    if (prob < 25) {
                        ItemStack stk2 = new ItemStack(FLINT, 1);
                        stacks.add(stk2);
                    } else {
                        ItemStack stk2 = new ItemStack(GRAVEL, 1);
                        stacks.add(stk2);
                    }
                } else if (level >= 3) {
                    ItemStack stk2 = new ItemStack(FLINT, 1);
                    stacks.add(stk2);
                }
                break;
            default:
                stacks.addAll(blk.getDrops());
                break;
        }
        return stacks;
    }

    // Returns a set of item stacks that would be dropped during a normal block break event with silk touch
    public static ArrayList<ItemStack> getSilkTouchDrops(Block blk) {
        ArrayList<ItemStack> stacks = new ArrayList<>();
        Material mat = blk.getType();
        Material m;
        ItemStack stk = null;
        switch (mat) {
            case COAL_ORE:
            case DIAMOND_ORE:
            case EMERALD_ORE:
            case GRASS:
            case ICE:
            case PACKED_ICE:
            case LAPIS_ORE:
            case MYCEL:
            case QUARTZ_ORE:
            case GLASS:
            case SEA_LANTERN:
            case THIN_GLASS:
            case ENDER_CHEST:
            case MELON_BLOCK:
            case GLOWSTONE:
            case CLAY:
            case SNOW_BLOCK:
            case BOOKSHELF:
            case DEAD_BUSH:
            case GRAVEL:
            case WEB:
            case HUGE_MUSHROOM_1:
            case HUGE_MUSHROOM_2:
                m = mat;
                stk = new ItemStack(m, 1);
                break;
            case STAINED_GLASS_PANE:
            case STAINED_GLASS:
            case DIRT:
            case STONE:
            case LEAVES:
            case LEAVES_2:
                short s = (short) blk.getData();
                if (s >= 8 && (mat == LEAVES || mat == LEAVES_2)) {
                    s -= 8;
                }
                m = mat;
                stk = new ItemStack(m, 1, s);
                break;
            case GLOWING_REDSTONE_ORE:
            case REDSTONE_ORE:
                stk = new ItemStack(REDSTONE_ORE, 1);
                break;
            case MONSTER_EGGS:
                switch (blk.getData()) {
                    case 0:
                        stk = new ItemStack(STONE);
                        break;
                    case 1:
                        stk = new ItemStack(COBBLESTONE);
                        break;
                    default:
                        stk = new ItemStack(SMOOTH_BRICK, 1, (short) (blk.getData() - 2));
                }
                break;
            default:
                stacks.addAll(blk.getDrops());
                break;
        }
        if (stk != null) {
            stacks.add(stk);
        }
        return stacks;
    }

    // Returns the exact center of a block of a given location
    public static Location getCenter(Location loc) {
        double x = loc.getX();
        double z = loc.getZ();
        if (x >= 0) {
            x += .5;
        } else {
            x += .5;
        }
        if (z >= 0) {
            z += .5;
        } else {
            z += .5;
        }
        Location lo = loc.clone();
        lo.setX(x);
        lo.setZ(z);
        return lo;
    }

    // Returns the exact center of a block of a given block
    public static Location getCenter(Block blk) {
        return getCenter(blk.getLocation());
    }

    // Returns the nearby entities at any loction within the given range
    public static List<Entity> getNearbyEntities(Location loc, double x, double y, double z) {
        FallingBlock ent = loc.getWorld().spawnFallingBlock(loc, 132, (byte) 0);
        List<Entity> out = ent.getNearbyEntities(x, y, z);
        ent.remove();
        return out;
    }

    // Advances the growth cycle of the given block if it is a plant. It will return true if it changed anything,
    // otherwise false
    public static boolean grow(Block blk) {
        if (blk != null) {
            if (blk.getType() == COCOA) {
                if (blk.getData() < 8) {
                    blk.setData((byte) (blk.getData() + 4));
                    return true;
                } else {
                    return false;
                }
            } else if (blk.getType() == PUMPKIN_STEM || blk.getType() == MELON_STEM
                    || blk.getType() == CARROT || blk.getType() == CROPS || blk.getType() == POTATO) {
                if (blk.getData() < 7) {
                    blk.setData((byte) (blk.getData() + 1));
                    return true;
                } else {
                    return false;
                }
            } else if (blk.getType() == NETHER_WARTS || blk.getType() == BEETROOT_BLOCK) {
                if (blk.getData() < 3) {
                    blk.setData((byte) (blk.getData() + 1));
                    return true;
                } else {
                    return false;
                }
            } else if (blk.getType() == CACTUS) {
                return false;
            } else if (blk.getType() == SUGAR_CANE_BLOCK) {
                return false;
            }
        }
        return false;
    }

    // Returns a direction integer, 0-8, for the given player's pitch and yaw
    public static int getDirection(Player player) {
        float direction = player.getLocation().getYaw();
        int in = 0;
        if (direction < 0) {
            direction += 360;
        }
        direction %= 360;
        double i = (double) ((direction + 8) / 18);
        if (i >= 19 || i < 1) {//S
            in = 1;
        } else if (i < 3 && i >= 1) {//SW
            in = 2;
        } else if (i < 6 && i >= 3) {//W
            in = 3;
        } else if (i < 8 && i >= 6) {//NW
            in = 4;
        } else if (i < 11 && i >= 8) {//N
            in = 5;
        } else if (i < 13 && i >= 11) {//NE
            in = 6;
        } else if (i < 16 && i >= 13) {//E
            in = 7;
        } else if (i < 18 && i >= 16) {//SE
            in = 8;
        }
        return in;
    }

    // Returns a more simple direction integer, 0-6, for the given player's pitch and yaw
    public static int getSimpleDirection(float yaw, float pitch) {
        float direction = yaw;
        int in = 0;
        if (direction < 0) {
            direction += 360;
        }
        direction %= 360;
        double i = (double) ((direction + 8) / 18);
        if (i >= 18 || i < 3) {//S
            in = 1;
        } else if (i < 8 && i >= 3) {//W
            in = 2;
        } else if (i < 13 && i >= 8) {//N
            in = 3;
        } else if (i < 18 && i >= 13) {//E
            in = 4;
        }
        if (pitch < -50) {//U
            in = 5;
        } else if (pitch > 50) {//D
            in = 6;
        }
        return in;
    }

    // Adds an arrow entity into the arrow storage variable calls its launch method
    public static void putArrow(Entity e, EnchantArrow a, Player p) {
        Set<AdvancedArrow> ars;
        if (Storage.advancedProjectiles.containsKey(e)) {
            ars = Storage.advancedProjectiles.get(e);
        } else {
            ars = new HashSet<>();
        }
        ars.add(a);
        Storage.advancedProjectiles.put(e, ars);
        a.onLaunch(p, null);
    }

    // Returns true if a player can use a certain enchantment at a certain time (permissions and cooldowns),
    //      otherwise false
    public static boolean canUse(Player player, String ench) {
        if (!player.hasPermission("zenchantments.enchant.use")) {
            return false;
        }
        if (EnchantPlayer.matchPlayer(player).getCooldown(ench) != 0) {
            return false;
        }
        return !EnchantPlayer.matchPlayer(player).isDisabled(ench);
    }

    // Stores a player and an enchantments in a map to prevent infinite recursion of method calls from the WatcherEnchant
    //      Returns true of the player was already stored with the given enchantment
    public static boolean eventStart(Player player, String enchantment) {
        if (Storage.duringEvents.containsKey(player)) {
            if (Storage.duringEvents.get(player).contains(enchantment)) {
                return true;
            } else {
                Storage.duringEvents.get(player).add(enchantment);
                return false;
            }
        } else {
            HashSet<String> s = new HashSet<>();
            s.add(enchantment);
            Storage.duringEvents.put(player, s);
            return false;
        }
    }

    // Removes a player from the map that prevented them from being able to use the enchantment 
    public static void eventEnd(Player player, String enchantment) {
        Storage.duringEvents.get(player).remove(enchantment);
    }

    // Returns true if the first given entity can damage the second given entity, otherwise false
    public static boolean canDamage(Entity damager, Entity entity) {
        if (!Storage.damagingPlayer.contains(damager)) {
            Storage.damagingPlayer.add(damager);
            EntityDamageByEntityEvent evt = new EntityDamageByEntityEvent(damager, entity,
                    EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1);
            Bukkit.getPluginManager().callEvent(evt);
            Storage.damagingPlayer.remove(damager);
            if ((damager instanceof Player && !Config.get(damager.getWorld()).enchantPVP())
                    || !(entity instanceof LivingEntity)) {
                return false;
            }
            return !evt.isCancelled();
        }
        return false;
    }

    // Adds a potion effect of given length and intensity to the given entity. 
    public static void addPotion(LivingEntity ent, PotionEffectType type, int length, int intensity) {
        for (PotionEffect eff : ent.getActivePotionEffects()) {
            if (eff.getType().equals(type)) {
                if (eff.getAmplifier() > intensity) {
                    return;
                } else if (eff.getDuration() > length) {
                    return;
                } else {
                    ent.removePotionEffect(type);
                }
            }
        }
        ent.addPotionEffect(new PotionEffect(type, length, intensity));
    }

    // Returns true if a player can change a given block, otherwise false
    public static boolean canEdit(Player player, Block block) {
        if (Storage.worldGuard == null) {
            return true;
        } else {
            return Storage.worldGuard.canBuild(player, block);
        }
    }

    // Returns an instance of an AdvancedArrow of the given class
    public static AdvancedArrow construct(Class cl, Projectile p) {
        try {
            Constructor ctor = cl.getDeclaredConstructor(Projectile.class
            );
            ctor.setAccessible(
                    true);
            return (ElementalArrow) ctor.newInstance(
                    (Object) p);
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException |
                IllegalArgumentException | InvocationTargetException ex) {
        }
        return null;
    }

    // Returns the amount of XP dropped by a given material
    public static int getBlockXP(Material mat) {
        switch (mat) {
            case COAL_ORE:
                return Storage.rnd.nextInt(3);
            case DIAMOND_ORE:
            case EMERALD_ORE:
                return Storage.rnd.nextInt(5) + 3;
            case LAPIS_ORE:
            case QUARTZ_ORE:
                return Storage.rnd.nextInt(4) + 2;
            case REDSTONE_ORE:
            case GLOWING_REDSTONE_ORE:
                return Storage.rnd.nextInt(5) + 1;
            case MOB_SPAWNER:
                return Storage.rnd.nextInt(29) + 15;
            default:
                return 0;
        }
    }

    // A better implementation of Bukkit/Spigot's breakBlockNaturally. This method accounts for Silk Touch, Fortune, and XP
    public static void breakBlockNaturally(Block blk, Player player) {
        if (player.getGameMode().equals(CREATIVE)) {
            blk.setType(AIR);
        } else if (player.getGameMode().equals(SURVIVAL) && !ArrayUtils.contains(Storage.badBlocks, blk.getType().getId())) {
            if (player.getInventory().getItemInMainHand().getEnchantments().containsKey(SILK_TOUCH)) {
                for (ItemStack st : Utilities.getSilkTouchDrops(blk)) {
                    player.getWorld().dropItem(Utilities.getCenter(blk), st);
                }
                blk.setType(AIR);
            } else if (player.getInventory().getItemInMainHand().getEnchantments().containsKey(LOOT_BONUS_BLOCKS)) {
                for (ItemStack st : Utilities.getFortuneDrops(player.getInventory().getItemInMainHand().getEnchantments().get(LOOT_BONUS_BLOCKS), blk)) {
                    player.getWorld().dropItem(Utilities.getCenter(blk), st);
                }
                ExperienceOrb o = (ExperienceOrb) player.getWorld().spawnEntity(Utilities.getCenter(blk), EXPERIENCE_ORB);
                o.setExperience(getBlockXP(blk.getType()));
                blk.setType(AIR);
            } else {
                blk.breakNaturally();
            }
        }
    }

}
