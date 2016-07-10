package zedly.zenchantments;

import java.io.*;
import java.util.*;
import org.bukkit.entity.*;

// This is used to manage players on the server. It allows for easy access in enabling/disabling enchantments
//      and for adding cooldowns for different enchantments as they are used
public class EnchantPlayer {

    public static final Set<EnchantPlayer> PLAYERS = new HashSet<>();   // Collection of all players on the server

    private final Player player;                          // Reference to the actual player object
    private final Map<String, Integer> enchantCooldown;   // Enchantment names mapped to their remaining cooldown
    public final Set<String> disabled;                    // Collection of disabled enchantments for the player

    // Creates a new enchant player objects and reads the player config file for their information
    public EnchantPlayer(Player player) {
        this.player = player;
        enchantCooldown = new HashMap<>();
        disabled = load();
        update();
        PLAYERS.add(this);
    }

    // Decrements the players cooldowns by one tick
    public void tick() {
        for (String ench : enchantCooldown.keySet()) {
            enchantCooldown.put(ench, Math.max(enchantCooldown.get(ench) - 1, 0));
        }
    }

    // Returns true if the given enchantment name is disabled for the player, otherwise false
    public boolean isDisabled(String enchantment) {
        for (String e : disabled) {
            if (e.equalsIgnoreCase(enchantment)) {
                return true;
            }
        }
        return false;
    }

    // Returns the cooldown remaining for the given enchantment name in ticks
    public int getCooldown(String enchantment) {
        for (String e : enchantCooldown.keySet()) {
            if (e.equalsIgnoreCase(enchantment)) {
                return enchantCooldown.get(e);
            }
        }
        return 0;
    }

    // Sets the given enchantment cooldown to the given amount of ticks
    public void setCooldown(String enchantment, int ticks) {
        enchantCooldown.put(enchantment.toLowerCase(), ticks);
    }

    // Disables the given enchantment for the player
    public void disable(String enchantment) {
        disabled.add(enchantment.toLowerCase());
        update();
    }

    // Enables the given enchantment for the player
    public void enable(String enchantment) {
        disabled.remove(enchantment.toLowerCase());
        update();
    }

    // Disables all enchantments for the player
    public void disableAll() {
        for (String enchant : Config.get(player.getWorld()).getEnchants().keySet()) {
            disabled.add(enchant.toLowerCase());
        }
        update();
    }

    // Enables all enchantments for the player
    public void enableAll() {
        disabled.clear();
        update();
    }

    // Returns the Player object associated with the EnchantPlayer
    public Player getPlayer() {
        return player;
    }

    // Returns the EnchantPlayer object associated with the given Player
    public static EnchantPlayer matchPlayer(Player player) {
        for (EnchantPlayer p : PLAYERS) {
            if (p.player.equals(player)) {
                return p;
            }
        }
        return new EnchantPlayer(player);
    }

    // Loads the config information for players
    private Set<String> load() {
        try {
            File f = new File("plugins/Zenchantments/PlayerSettings.txt");
            if (!f.exists()) {
                f.createNewFile();
            }
            Scanner input = new Scanner(f);
            while (input.hasNextLine()) {
                String[] playerData = input.nextLine().split(";");
                if (playerData[0].equals(player.getUniqueId().toString())) {
                    Set<String> enchantments = new HashSet<>();
                    for (int i = 1; i < playerData.length; i++) {
                        for (Config config : Config.CONFIGS) {
                            String enchantName = playerData[i].replace(" ", "").toLowerCase();
                            if (config.getEnchants().containsKey(enchantName)) {
                                enchantments.add(enchantName.toLowerCase());
                                break;
                            }
                        }
                    }
                    return enchantments;
                }
            }
        } catch (IOException ex) {
        }
        return new HashSet<>();
    }

    // Saves the config information for players
    private void update() {
        try {
            File f = new File("plugins/Zenchantments/PlayerSettings.txt");
            if (!f.exists()) {
                f.createNewFile();
            }
            Scanner input = new Scanner(f);
            String output = "";
            while (input.hasNextLine()) {
                String raw = input.nextLine();
                String[] playerData = raw.split(";");
                if (!playerData[0].equals(player.getUniqueId().toString())) {
                    output += raw + "\n";
                }
            }
            output += player.getUniqueId();
            for (String e : disabled) {
                output += ";" + e.toLowerCase();
            }
            output += "\n";
            PrintStream out = new PrintStream(f);
            out.print(output);
        } catch (IOException ex) {
        }
    }

    // Sends the EnchantPlayer the given message
    public void sendMessage(String message) {
        player.sendMessage(message);
    }

    // Returns true if the EnchantPlayer has the given permission, otherwise false
    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }

}
