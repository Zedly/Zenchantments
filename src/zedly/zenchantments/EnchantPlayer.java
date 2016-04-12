package zedly.zenchantments;

import java.io.*;
import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;

public class EnchantPlayer {

    public static final Set<EnchantPlayer> PLAYERS = new HashSet<>();

    private final Player player;
    private final Map<String, Integer> enchantCooldown;
    public final Set<String> disabled;

    public EnchantPlayer(Player player) {
        this.player = player;
        enchantCooldown = new HashMap<>();
        disabled = load();
        update();
        PLAYERS.add(this);
    }

    public void tick() {
        for (String ench : enchantCooldown.keySet()) {
            enchantCooldown.put(ench, Math.max(enchantCooldown.get(ench) - 1, 0));
        }
    }

    public boolean isDisabled(String enchantment) {
        for (String e : disabled) {
            if (e.equalsIgnoreCase(enchantment)) {
                return true;
            }
        }
        return false;
    }

    public int getCooldown(String enchantment) {
        for (String e : enchantCooldown.keySet()) {
            if (e.equalsIgnoreCase(enchantment)) {
                return enchantCooldown.get(e);
            }
        }
        return 0;
    }

    public void setCooldown(String enchantment, int ticks) {
        enchantCooldown.put(enchantment.toLowerCase(), ticks);
    }

    public void disable(String enchantment) {
        disabled.add(enchantment.toLowerCase());
        update();
    }

    public void enable(String enchantment) {
        disabled.remove(enchantment.toLowerCase());
        update();
    }

    public void disableAll() {
        for (String enchant: Config.get(player.getWorld()).getEnchants().keySet()){
            disabled.add(enchant.toLowerCase());
        }
        update();
    }

    public void enableAll() {
        disabled.clear();
        update();
    }

    public Player getPlayer() {
        return player;
    }

    public static EnchantPlayer matchPlayer(Player player) {
        for (EnchantPlayer p : PLAYERS) {
            if (p.player.equals(player)) {
                return p;
            }
        }
        return new EnchantPlayer(player);
    }

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

    public void sendMessage(String message) {
        player.sendMessage(message);
    }

}
