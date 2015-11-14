package zedly.zenchantments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.UUID;

public class PlayerConfig {

    public static void loadConfigs() {
        File file = new File("plugins/Zenchantments/PlayerSettings.txt");
        if (!file.exists()) {
            System.err.println("PlayerSettings file does not exist!");
        } else {
            byte[] rawData = new byte[(int) file.length()];
            try {
                try (FileInputStream fis = new FileInputStream(file)) {
                    fis.read(rawData);
                }
                String rawText = new String(rawData);
                String[] players = rawText.split("\n");
                for (String player : players) {
                    String[] enchantments = player.split(";");
                    UUID playerID = UUID.fromString(enchantments[0]);
                    HashSet<CustomEnchantment> finalEnchants = new HashSet<>();
                    for (int i = 1; i < enchantments.length; i++) {
                        for (Config config : Storage.worldConfigs) {
                            if (config.getEnchants().containsKey(enchantments[i].replace(" ", "").toLowerCase())) {
                                CustomEnchantment ench = config.getEnchants().get(enchantments[i].replace(" ", "").toLowerCase());
                                finalEnchants.add(ench);
                            }
                        }
                    }
                    Storage.playerSettings.put(playerID, finalEnchants);
                }
            } catch (IOException ex) {
            }
        }
    }

    public static void saveConfigs() {
        String rawText = "";
        for (UUID p : Storage.playerSettings.keySet()) {
            rawText += p.toString();
            for (CustomEnchantment e : Storage.playerSettings.get(p)) {
                rawText += ";" + e.loreName;
            }
        }
        rawText += "\n";
        byte[] b = rawText.getBytes();
        File file = new File("plugins/Zenchantments/PlayerSettings.txt");
        try {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(b, 0, b.length);
                fos.flush();
            }
        } catch (IOException ex) {
            System.err.println("Could not save Player Configs!");
        }
    }
}
