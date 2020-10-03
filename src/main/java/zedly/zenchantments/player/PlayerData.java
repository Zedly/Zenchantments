package zedly.zenchantments.player;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import zedly.zenchantments.Config;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.HashMap;
import java.util.Map;

// This is used to manage players on the server. It allows for easy access in enabling/disabling enchantments
//      and for adding cooldowns for different enchantments as they are used
public class PlayerData implements zedly.zenchantments.api.player.PlayerData {
    private final Map<Integer, Integer> enchantCooldown = new HashMap<>(); // Enchantment names mapped to their remaining cooldown
    private final ZenchantmentsPlugin   plugin;

    private Player player;          // Reference to the actual player object

    // Creates a new enchant player objects and reads the player config file for their information
    public PlayerData(ZenchantmentsPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void enableZenchantment(int zenchantmentId) {
        this.player.setMetadata("ze." + zenchantmentId, new FixedMetadataValue(this.plugin, false));
    }

    @Override
    public void enableAllZenchantments() {
        for (Zenchantment zenchantment : Config.get(this.player.getWorld()).getEnchants()) {
            this.player.setMetadata("ze." + zenchantment.getId(), new FixedMetadataValue(this.plugin, false));
        }
    }

    @Override
    public void disableZenchantment(int zenchantmentId) {
        this.player.setMetadata("ze." + zenchantmentId, new FixedMetadataValue(this.plugin, true));
    }

    @Override
    public void disableAllZenchantments() {
        for (Zenchantment zenchantment : Config.get(player.getWorld()).getEnchants()) {
            this.player.setMetadata("ze." + zenchantment.getId(), new FixedMetadataValue(this.plugin, true));
        }
    }

    @Override
    public int getCooldownForZenchantment(int zenchantmentId) {
        return this.enchantCooldown.getOrDefault(zenchantmentId, 0);
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    // Decrements the players cooldowns by one tick
    public void tick() {
        this.enchantCooldown.replaceAll((e, v) -> Math.max(enchantCooldown.get(e) - 1, 0));
    }

    // Returns true if the given enchantment name is disabled for the player, otherwise false
    public boolean isDisabled(int enchantmentId) {
        if (this.player.hasMetadata("ze." + enchantmentId)) {
            return this.player.getMetadata("ze." + enchantmentId).get(0).asBoolean();
        } else {
            this.player.setMetadata("ze." + enchantmentId, new FixedMetadataValue(Storage.zenchantments, false));
            return false;
        }
    }

    // Sets the given enchantment cooldown to the given amount of ticks
    public void setCooldown(int enchantmentId, int ticks) {
        this.enchantCooldown.put(enchantmentId, ticks);
    }

    // Returns the EnchantPlayer object associated with the given Player
    @Deprecated
    public static PlayerData matchPlayer(Player player) {
        return null;
    }
}