package zedly.zenchantments;

import java.util.*;
import org.bukkit.configuration.file.YamlConfiguration;

public class UpdateConfig {

    public static void update(int major, int minor) {
        if (major < 4) {

        }
    }

    public static void updateToCurrent(YamlConfiguration config) {
        List<Map<String, LinkedHashMap<String, Object>>> list = (List<Map<String, LinkedHashMap<String, Object>>>) config.get("enchantments");
        for (Map<String, LinkedHashMap<String, Object>> tmp : list) {
            for (String map : tmp.keySet()) {
                tmp.get(map).put("Cooldown", 0);
                tmp.get(map).put("Power", 1.0);
                tmp.get(map).put("Probability", (double) ((Integer) tmp.get(map).get("Probability")));
            }
        }
        double d = (double) ((Integer) config.get("enchant_rarity"));
        config.set("enchant_rarity", d);
        config.set("ZenchantmentsConfigVersion", Storage.version);
        config.set("enchantments", list);

        List<Map<String, Map<String, Object>>> list2 = (List<Map<String, Map<String, Object>>>) config.get("enchantments");
        Map<String, Map<String, Object>> m = new HashMap<String, Map<String, Object>>();
        Map<String, Object> values = new LinkedHashMap<String, Object>();
        values.put("Probability", 1.0);
        values.put("Tools", "Pickaxe, Shovel, Axe");
        values.put("Name", "Haste");
        values.put("Max Level", 4);
        values.put("Cooldown", 0);
        values.put("Power", 1.0);
        m.put("Haste", values);
        list2.add(m);
        config.set("enchantments", list2);
    }

}
