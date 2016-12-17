package zedly.zenchantments;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import org.bukkit.*;
import static org.bukkit.GameMode.*;
import static org.bukkit.Material.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
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
    public static List<ItemStack> getArmorandMainHandItems(Player player, boolean usedHand) {
        List<ItemStack> stk = new ArrayList<>();
        stk.addAll(Arrays.asList(player.getInventory().getArmorContents()));
        stk.add(usedStack(player, usedHand));
        Iterator<ItemStack> it = stk.iterator();
        while (it.hasNext()) {
            ItemStack is = it.next();
            if (is == null || is.getType() == Material.AIR) {
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
                player.getInventory().setItemInMainHand(hand.getDurability() > hand.getType().getMaxDurability() ? new ItemStack(AIR) : hand);
            } else {
                player.getInventory().setItemInOffHand(hand.getDurability() > hand.getType().getMaxDurability() ? new ItemStack(AIR) : hand);
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
    public static Collection<Entity> getNearbyEntities(Location loc, double x, double y, double z) {
        Collection<Entity> out = loc.getWorld().getNearbyEntities(loc, x, y, z);
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
    public static boolean canUse(Player player, int enchantmentID) {
        if (!player.hasPermission("zenchantments.enchant.use")) {
            return false;
        }
        if (EnchantPlayer.matchPlayer(player).getCooldown(enchantmentID) != 0) {
            return false;
        }
        return !EnchantPlayer.matchPlayer(player).isDisabled(enchantmentID);
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

    // Returns an instance of an AdvancedArrow of the given class
    public static AdvancedArrow construct(Class cl, Projectile p) {
        try {
            Constructor ctor = cl.getDeclaredConstructor(Projectile.class
            );
            ctor.setAccessible(
                    true);
            return (ElementalArrow) ctor.newInstance(
                    (Object) p);
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
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

    public static boolean canEdit(Player player, Block block) {
        return true;
    }
}
