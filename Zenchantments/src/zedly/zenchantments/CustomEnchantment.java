package zedly.zenchantments;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import zedly.zenchantments.compatibility.CompatibilityAdapter;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

// CustomEnchantment is the defualt structure for any enchantment. Each enchantment below it will extend this class
//      and will override any methods as neccecary in its behavior
public abstract class CustomEnchantment {
    protected static final CompatibilityAdapter ADAPTER = Storage.COMPATIBILITY_ADAPTER;

    protected int     maxLevel;         // Max level the given enchant can naturally obtain
    protected String  loreName;      // Name the given enchantment will appear as; with &7 (Gray) color
    protected float   probability;    // Relative probability of obtaining the given enchantment
    protected Tool[]  enchantable;   // Enums that represent tools that can receive and work with given enchantment
    protected Class[] conflicting;  // Classes of enchantments that don't work with given enchantment
    protected String  description;   // Description of what the enchantment does
    protected int     cooldown;         // Cooldown for given enchantment given in ticks; Default is 0
    protected double  power;         // Power multiplier for the enchantment's effects; Default is 0; -1 means no effect
    protected Hand    handUse;          // Which hands an enchantment has actiosn for; 0 = none, 1 = left, 2 = right, 3 = both
    private   boolean used;       // Indicates that an enchantment has already been applied to an event, avoiding infinite regress
    protected int     id;
    protected boolean isCursed;

    public abstract Builder<? extends CustomEnchantment> defaults();

    //region Enchanment Events

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
    
    //endregion

    //region Getters and Setters

    int getMaxLevel() {
        return maxLevel;
    }

    void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    String getLoreName() {
        return loreName;
    }

    void setLoreName(String loreName) {
        this.loreName = loreName;
    }

    float getProbability() {
        return probability;
    }

    void setProbability(float probability) {
        this.probability = probability;
    }

    Tool[] getEnchantable() {
        return enchantable;
    }

    void setEnchantable(Tool[] enchantable) {
        this.enchantable = enchantable;
    }

    Class[] getConflicting() {
        return conflicting;
    }

    void setConflicting(Class[] conflicting) {
        this.conflicting = conflicting;
    }

    String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    int getCooldown() {
        return cooldown;
    }

    void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    double getPower() {
        return power;
    }

    void setPower(double power) {
        this.power = power;
    }

    Hand getHandUse() {
        return handUse;
    }

    void setHandUse(Hand handUse) {
        this.handUse = handUse;
    }

    int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    //endregion

    public static void applyForTool(Player player, ItemStack tool, BiPredicate<CustomEnchantment, Integer> action) {
        Config.get(player.getWorld()).getEnchants(tool).forEach((CustomEnchantment ench, Integer level) -> {
            if (!ench.used && Utilities.canUse(player, ench.id)) {
                try {
                    ench.used = true;
                    if (action.test(ench, level)) {
                        EnchantPlayer.matchPlayer(player).setCooldown(ench.id, ench.cooldown);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                ench.used = false;
            }
        });
    }

    /**
     * Determines if the material provided is enchantable with this enchantment.
     *
     * @param m The material to test.
     * @return true iff the material can be enchanted with this enchantment.
     */
    // Returns true if the given material (tool) is compatible with the enchantment, otherwise false
    public boolean validMaterial(Material m) {
        for (Tool t : enchantable) {
            if (t.contains(m)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the stack of material provided is enchantable with this enchantment.
     *
     * @param m The stack of material to test.
     * @return true iff the stack of material can be enchanted with this enchantment.
     */
    public boolean validMaterial(ItemStack m) {
        return validMaterial(m.getType());
    }

    public void addEnchantment(ItemStack stk, int level) {
       ItemMeta meta = stk.getItemMeta();
       List<String> lore = new LinkedList<>();
       if (meta.hasLore()) {
           lore.addAll(meta.getLore());
       }
       String levelStr = Utilities.getRomanString(level);
       lore.add((isCursed ? ChatColor.RED : ChatColor.GRAY) + loreName + " " + (maxLevel == 1 ? "" : levelStr));
       //ChatColor.COLOR_CHAR
       meta.setLore(lore);
       stk.setItemMeta(meta);
    }

    protected static final class Builder<T extends CustomEnchantment> {
        private final T customEnchantment;

        public Builder(Supplier<T> sup, int id) {
            customEnchantment = sup.get();
            customEnchantment.setId(id);
        }

        public Builder<T> maxLevel(int maxLevel) {
            customEnchantment.setMaxLevel(maxLevel);
            return this;
        }

        public int maxLevel() {
            return customEnchantment.getMaxLevel();
        }

        public Builder<T> loreName(String loreName) {
            customEnchantment.setLoreName(loreName);
            return this;
        }

        public String loreName() {
            return customEnchantment.getLoreName();
        }

        public Builder<T> probability(float probability) {
            customEnchantment.setProbability(probability);
            return this;
        }

        public float probability() {
            return customEnchantment.getProbability();
        }

        public Builder<T> enchantable(Tool[] enchantable) {
            customEnchantment.setEnchantable(enchantable);
            return this;
        }

        public Tool[] enchantable() {
            return customEnchantment.getEnchantable();
        }

        public Builder<T> conflicting(Class[] conflicting) {
            customEnchantment.setConflicting(conflicting);
            return this;
        }

        public Class[] conflicting() {
            return customEnchantment.getConflicting();
        }

        public Builder<T> description(String description) {
            customEnchantment.setDescription(description);
            return this;
        }

        public String description() {
            return customEnchantment.getDescription();
        }

        public Builder<T> cooldown(int cooldown) {
            customEnchantment.setCooldown(cooldown);
            return this;
        }

        public int cooldown() {
            return customEnchantment.getCooldown();
        }

        public Builder<T> power(double power) {
            customEnchantment.setPower(power);
            return this;
        }

        public double power() {
            return customEnchantment.getPower();
        }

        public Builder<T> handUse(Hand handUse) {
            customEnchantment.setHandUse(handUse);
            return this;
        }

        public Hand handUse() {
            return customEnchantment.getHandUse();
        }

        public T build() {
            return customEnchantment;
        }
    }
}
