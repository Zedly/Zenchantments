package zedly.zenchantments.player;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import zedly.zenchantments.Config;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Zenchantment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// This is used to manage players on the server. It allows for easy access in enabling/disabling enchantments
//      and for adding cooldowns for different enchantments as they are used
public class PlayerData {
    public static final Set<PlayerData> PLAYERS = new HashSet<>();   // Collection of all players on the server

    private final Player                player;          // Reference to the actual player object
    private final Map<Integer, Integer> enchantCooldown; // Enchantment names mapped to their remaining cooldown

    // Creates a new enchant player objects and reads the player config file for their information
    public PlayerData(Player player) {
        this.player = player;
        this.enchantCooldown = new HashMap<>();
        PLAYERS.add(this);
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

    // Returns the cooldown remaining for the given enchantment name in ticks
    public int getCooldown(int enchantmentId) {
        return this.enchantCooldown.getOrDefault(enchantmentId, 0);
    }

    // Sets the given enchantment cooldown to the given amount of ticks
    public void setCooldown(int enchantmentId, int ticks) {
        this.enchantCooldown.put(enchantmentId, ticks);
    }

    // Disables the given enchantment for the player
    public void disable(int enchantmentId) {
        this.player.setMetadata("ze." + enchantmentId, new FixedMetadataValue(Storage.zenchantments, true));
    }

    // Enables the given enchantment for the player
    public void enable(int enchantmentId) {
        this.player.setMetadata("ze." + enchantmentId, new FixedMetadataValue(Storage.zenchantments, false));
    }

    // Disables all enchantments for the player
    public void disableAll() {
        for (Zenchantment enchant : Config.get(player.getWorld()).getEnchants()) {
            this.player.setMetadata("ze." + enchant.getId(), new FixedMetadataValue(Storage.zenchantments, true));
        }
    }

    // Enables all enchantments for the player
    public void enableAll() {
        for (Zenchantment enchant : Config.get(this.player.getWorld()).getEnchants()) {
            this.player.setMetadata("ze." + enchant.getId(), new FixedMetadataValue(Storage.zenchantments, false));
        }
    }

    // Returns the EnchantPlayer object associated with the given Player
    public static PlayerData matchPlayer(Player player) {
        for (PlayerData players : PLAYERS) {
            if (players.player.equals(player)) {
                return players;
            }
        }

        return new PlayerData(player);
    }
}