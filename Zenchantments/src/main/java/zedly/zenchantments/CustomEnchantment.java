package zedly.zenchantments;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.compatibility.CompatibilityAdapter;
import zedly.zenchantments.enums.Tool;

import java.util.function.*;

// CustomEnchantment is the defualt structure for any enchantment. Each enchantment below it will extend this class
//      and will override any methods as neccecary in its behavior
public class CustomEnchantment {

    protected static final CompatibilityAdapter ADAPTER = Storage.COMPATIBILITY_ADAPTER;

    public static void applyForTool(Player player, ItemStack tool, BiPredicate<CustomEnchantment, Integer> action) {
        Config.get(player.getWorld()).getEnchants(tool).forEach((CustomEnchantment ench, Integer level) -> {
            if (!ench.used && Utilities.canUse(player, ench.getEnchantmentId())) {
                try {
                    ench.used = true;
                    if (action.test(ench, level)) {
                        EnchantPlayer.matchPlayer(player).setCooldown(ench.getEnchantmentId(), ench.cooldown);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                ench.used = false;
            }
        });
    }
    protected int     maxLevel;         // Max level the given enchant can naturally obtain
    protected String  loreName;      // Name the given enchantment will appear as; with &7 (Gray) color
    protected float   probability;    // Relative probability of obtaining the given enchantment
    protected Tool[]  enchantable;   // Enums that represent tools that can receive and work with given enchantment
    protected Class[] conflicting;  // Classes of enchantments that don't work with given enchantment
    protected String  description;   // Description of what the enchantment does
    protected int     cooldown;         // Cooldown for given enchantment given in ticks; Default is 0
    protected double  power;         // Power multiplier for the enchantment's effects; Default is 0; -1 means no effect
    protected int handUse;          // Which hands an enchantment has actiosn for; 0 = none, 1 = left, 2 = right, 3 = both
    private boolean used;       // Indicates that an enchantment has already been applied to an event, avoiding infinite regress

    public int getEnchantmentId() {
        return -1;
    }

    public void loadData() {

    }

    // Returns true if the given material (tool) is compatible with the enchantment, otherwise false
    public boolean validMaterial(Material m) {
        for (Tool t : enchantable) {
            if (t.contains(m)) {
                return true;
            }
        }
        return false;
    }

    // Returns true if the given item stack (tool) is compatible with the enchantment, otherwise false
    public boolean validMaterial(ItemStack m) {
        return validMaterial(m.getType());
    }

    //Empty Methods for Events and Scanning Tasks: These are empty by default so that the WatcherEnchant can call these 
    //      for any enchantment without performing any checks. Each enchantment will override them as neccecary
    public boolean onBlockBreak(BlockBreakEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onEntityInteract(PlayerInteractEntityEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onEntityKill(EntityDeathEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onEntityHit(EntityDamageByEntityEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onBeingHit(EntityDamageByEntityEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onEntityDamage(EntityDamageEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onPlayerFish(PlayerFishEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onHungerChange(FoodLevelChangeEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onShear(PlayerShearEntityEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onEntityShootBow(EntityShootBowEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onPotionSplash(PotionSplashEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onProjectileLaunch(ProjectileLaunchEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onPlayerDeath(PlayerDeathEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onScan(Player player, int level, boolean usedHand) {
        return false;
    }

    public boolean onScanHands(Player player, int level, boolean usedHand) {
        return false;
    }

    public boolean onCombust(EntityCombustByEntityEvent evt, int level, boolean usedHand) {
        return false;
    }

    public boolean onFastScan(Player player, int level, boolean usedHand) {
        return false;
    }

    public boolean onFastScanHands(Player player, int level, boolean usedHand) {
        return false;
    }
}
