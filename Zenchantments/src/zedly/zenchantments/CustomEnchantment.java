package zedly.zenchantments;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import zedly.zenchantments.compatibility.CompatibilityAdapter;
import zedly.zenchantments.enchantments.*;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

import static org.bukkit.Material.BOOK;
import static org.bukkit.Material.ENCHANTED_BOOK;

// CustomEnchantment is the defualt structure for any enchantment. Each enchantment below it will extend this class
//      and will override any methods as neccecary in its behavior
public abstract class CustomEnchantment {
	protected static final CompatibilityAdapter ADAPTER = Storage.COMPATIBILITY_ADAPTER;
	protected              int                  id;

	protected int     maxLevel;         // Max level the given enchant can naturally obtain
	protected String  loreName;      // Name the given enchantment will appear as; with &7 (Gray) color
	protected float   probability;    // Relative probability of obtaining the given enchantment
	protected Tool[]  enchantable;   // Enums that represent tools that can receive and work with given enchantment
	protected Class[] conflicting;  // Classes of enchantments that don't work with given enchantment
	protected String  description;   // Description of what the enchantment does
	protected int     cooldown;         // Cooldown for given enchantment given in ticks; Default is 0
	protected double  power;         // Power multiplier for the enchantment's effects; Default is 0; -1 means no
	// effect
	protected Hand    handUse;
		// Which hands an enchantment has actiosn for; 0 = none, 1 = left, 2 = right, 3 = both
	private   boolean used;
		// Indicates that an enchantment has already been applied to an event, avoiding infinite regress
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
		getEnchants(tool, player.getWorld()).forEach((CustomEnchantment ench, Integer level) -> {
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

	// Updates lore enchantments and descriptions to new format. This will be removed eventually
	public static ItemStack updateToNewFormat(ItemStack stk, World world) {
		if (stk != null) {
			if (stk.hasItemMeta()) {
				if (stk.getItemMeta().hasLore()) {
					boolean hasEnch = false;
					ItemMeta meta = stk.getItemMeta();
					List<String> lore = new ArrayList<>();
					CustomEnchantment lastEnch = null;

					List<String> tempLore = new LinkedList<>();
					for (String str : meta.getLore()) {

						CustomEnchantment ench = null;
						int level = 0;
						if (str.startsWith(ChatColor.GRAY + "")) {
							String stripString = ChatColor.stripColor(str);

							int splitIndex = stripString.lastIndexOf(" ");
							if (splitIndex != -1) {
								if (stripString.length() > 2) {
									String enchant;
									level = Utilities.getNumber(stripString.substring(splitIndex + 1));
									try {
										enchant = stripString.substring(0, splitIndex);
									} catch (Exception e) {
										enchant = "";
									}
									ench = Config.get(world).enchantFromString(enchant);
								}
							}
						}

						if (ench != null) {
							lastEnch = ench;
							hasEnch = true;
							lore.add(ench.getShown(level, world));
							lore.addAll(tempLore);
							tempLore.clear();
							continue;
						}

						if (lastEnch != null) {
							tempLore.add(str);

							StringBuilder bldr = new StringBuilder();
							for (String ls : tempLore) {
								bldr.append(ChatColor.stripColor(ls));
							}
							if (lastEnch.description.equals(bldr.toString())) {
								lastEnch = null;
								tempLore.clear();
							}
						} else {
							lore.add(str);
						}
					}
					lore.addAll(tempLore);

					meta.setLore(lore);
					stk.setItemMeta(meta);
					if (hasEnch) {
						setGlow(stk, true, world);
					}
					return stk;
				}
			}
		}
		return stk;
	}

	// Returns a mapping of custom enchantments and their level on a given tool
	public static LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk, World world,
		List<String> outExtraLore) {
		return getEnchants(stk, false, world, outExtraLore);
	}

	// Returns a mapping of custom enchantments and their level on a given tool
	public static LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk, boolean acceptBooks,
		World world) {
		return getEnchants(stk, acceptBooks, world, null);
	}

	// Returns a mapping of custom enchantments and their level on a given tool
	public static LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk, World world) {
		return getEnchants(stk, false, world, null);
	}

	public static LinkedHashMap<CustomEnchantment, Integer> getEnchants(ItemStack stk, boolean acceptBooks,
		World world,
		List<String> outExtraLore) {
		ItemStack stack;
		Map<CustomEnchantment, Integer> map = new LinkedHashMap<>();
		if (stk != null && (acceptBooks || stk.getType() != Material.ENCHANTED_BOOK)) {
			if (stk.hasItemMeta()) {
				if (stk.getItemMeta().hasLore()) {
					List<String> lore = stk.getItemMeta().getLore();
					for (String raw : lore) {
						Map.Entry<CustomEnchantment, Integer> ench = getEnchant(raw, world);
						if (ench != null) {
							map.put(ench.getKey(), ench.getValue());
						} else {
							if (outExtraLore != null) {
								outExtraLore.add(raw);
							}
						}
					}
				}
			}
		}
		LinkedHashMap<CustomEnchantment, Integer> finalMap = new LinkedHashMap<>();
		for (int id : new int[]{Lumber.ID, Shred.ID, Mow.ID, Pierce.ID, Extraction.ID, Plough.ID}) {
			CustomEnchantment e = null;
			for (CustomEnchantment en : Config.allEnchants) {
				if (en.getId() == id) {
					e = en;
				}
			}
			if (map.containsKey(e)) {
				finalMap.put(e, map.get(e));
				map.remove(e);
			}
		}
		finalMap.putAll(map);
		return finalMap;
	}

	// Returns the custom enchantment from the lore name
	private static Map.Entry<CustomEnchantment, Integer> getEnchant(String raw, World world) {
		Map<String, Boolean> unescaped = Utilities.fromInvisibleString(raw);
		for (Map.Entry<String, Boolean> entry : unescaped.entrySet()) {
			if (!entry.getValue()) {
				String[] vals = entry.getKey().split("\\.");
				if (vals.length == 4 && vals[0].equals("ze") && vals[1].equals("ench")) {
					int enchID = Integer.parseInt(vals[2]);
					int enchLvl = Integer.parseInt(vals[3]);
					CustomEnchantment ench = Config.get(world).enchantFromID(enchID);
					if (ench == null) {
						continue;
					}
					return new AbstractMap.SimpleEntry<>(ench, enchLvl);
				}
			}
		}
		return null;
	}

	/**
	 * Determines if the material provided is enchantable with this enchantment.
	 *
	 * @param m The material to test.
	 *
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
	 *
	 * @return true iff the stack of material can be enchanted with this enchantment.
	 */
	public boolean validMaterial(ItemStack m) {
		return validMaterial(m.getType());
	}

	public String getShown(int level, World world) {
		String levelStr = Utilities.getRomanString(level);
		return Utilities.toInvisibleString("ze.ench." + getId() + '.' + level) +
			(isCursed ? Config.get(world).getCurseColor() : Config.get(world).getEnchantmentColor()) + loreName + " "
			+ (maxLevel == 1 ? "" : levelStr);
	}

	public List<String> getDescription(World world) {
		List<String> desc = new LinkedList<>();
		if (Config.get(world).descriptionLore()) {
			String strStart = Utilities.toInvisibleString("ze.desc." + getId())
				+ Config.get(world).getDescriptionColor() + "" + ChatColor.ITALIC + " ";
			StringBuilder bldr = new StringBuilder();

			int i = 0;
			for (char c : description.toCharArray()) {
				if (i < 30) {
					i++;
					bldr.append(c);
				} else {
					if (c == ' ') {
						desc.add(strStart + bldr.toString());
						bldr = new StringBuilder(" ");
						i = 1;
					} else {
						bldr.append(c);
					}
				}
			}
			if (i != 0) {
				desc.add(strStart + bldr.toString());
			}
		}
		return desc;
	}

	public static boolean isDescription(String str) {
		Map<String, Boolean> unescaped = Utilities.fromInvisibleString(str);
		for (Map.Entry<String, Boolean> entry : unescaped.entrySet()) {
			if (!entry.getValue()) {
				String[] vals = entry.getKey().split("\\.");
				if (vals.length == 3 && vals[0].equals("ze") && vals[1].equals("desc")) {
					return true;
				}
			}
		}
		return false;
	}

	public void setEnchantment(ItemStack stk, int level, World world) {
		setEnchantment(stk, this, level, world);
	}

	public static void setEnchantment(ItemStack stk, CustomEnchantment ench, int level, World world) {
		if (stk == null) {
			return;
		}
		ItemMeta meta = stk.getItemMeta();
		List<String> lore = new LinkedList<>();
		List<String> normalLore = new LinkedList<>();
		boolean customEnch = false;
		if (meta.hasLore()) {
			for (String loreStr : meta.getLore()) {
				Map.Entry<CustomEnchantment, Integer> enchEntry = getEnchant(loreStr, world);
				if (enchEntry == null && !isDescription(loreStr)) {
					normalLore.add(loreStr);
				} else if (enchEntry != null && enchEntry.getKey() != ench) {
					customEnch = true;
					lore.add(enchEntry.getKey().getShown(enchEntry.getValue(), world));
					lore.addAll(enchEntry.getKey().getDescription(world));
				}
			}
		}

		if (ench != null && level > 0 && level <= ench.maxLevel) {
			lore.add(ench.getShown(level, world));
			lore.addAll(ench.getDescription(world));
			customEnch = true;
		}

		lore.addAll(normalLore);
		meta.setLore(lore);
		stk.setItemMeta(meta);
		setGlow(stk, customEnch, world);
	}

	public static void setGlow(ItemStack stk, boolean customEnch, World world) {
		if (!Config.get(world).enchantGlow()) {
			return;
		}
		ItemMeta itemMeta = stk.getItemMeta();
		EnchantmentStorageMeta bookMeta = null;

		boolean isBook = stk.getType() == BOOK || stk.getType() == ENCHANTED_BOOK;

		boolean containsNormal = false;
		boolean containsHidden = false;
		int duraLevel = 0;
		Map<Enchantment, Integer> enchs;

		if (stk.getType() == ENCHANTED_BOOK) {
			bookMeta = (EnchantmentStorageMeta) stk.getItemMeta();
			enchs = bookMeta.getStoredEnchants();
		} else {
			enchs = itemMeta.getEnchants();

		}

		for (Map.Entry<Enchantment, Integer> set : enchs.entrySet()) {
			if (!(set.getKey().equals(Enchantment.DURABILITY) && (duraLevel = set.getValue()) == 0)) {
				containsNormal = true;
			} else {
				containsHidden = true;
			}
		}
		if (containsNormal || (!customEnch && containsHidden)) {
			if (stk.getType() == ENCHANTED_BOOK) {
				if (duraLevel == 0) {
					bookMeta.removeStoredEnchant(org.bukkit.enchantments.Enchantment.DURABILITY);
				}
				bookMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
			} else {
				if (duraLevel == 0) {
					itemMeta.removeEnchant(Enchantment.DURABILITY);
				}
				itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
			}
		} else if (customEnch) {
			if (stk.getType() == BOOK) {
				stk.setType(ENCHANTED_BOOK);
				bookMeta = (EnchantmentStorageMeta) stk.getItemMeta();
				bookMeta.addStoredEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 0, true);
				bookMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			} else {
				itemMeta.addEnchant(Enchantment.DURABILITY, 0, true);
				itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			}
		}

		stk.setItemMeta(isBook ? bookMeta : itemMeta);
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
