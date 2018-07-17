package zedly.zenchantments;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

// This manages any changes to the config of the plugin. Beyond 1.4.1, there should be mininal changes.
public class UpdateConfig {

    // Determines which updates to perform and then changes the config version to the current plugin version
    public static void update(YamlConfiguration config, int[] version) {
        switch (version[1]) {
            case 3:
                oneFourZero(config);
                break;
            case 4:
                switch (version[2]) {
                    case 0:
                    case 1:
                        oneFourOne(config);
                        break;
                }
                break;
        }
        config.set("ZenchantmentsConfigVersion", Storage.version);
    }

    // Removes power and max level if they are not needed by an enchantment and adds any enchantments not in the config
    public static void oneFourOne(YamlConfiguration config) {
        List<Map<String, LinkedHashMap<String, Object>>> list;
        if (config.get("enchantments") != null) {
            list = (List<Map<String, LinkedHashMap<String, Object>>>) config.get("enchantments");
        } else {
            list = new ArrayList<Map<String, LinkedHashMap<String, Object>>>();
        }
        Map<String, CustomEnchantment> createdEnchants = new HashMap<>();
        for (Class cl : CustomEnchantment.class.getClasses()) {
            try {
                CustomEnchantment e = (CustomEnchantment) cl.newInstance();
                createdEnchants.put(e.loreName, e);
            } catch (InstantiationException | IllegalAccessException e) {
            }
        }
        Set<String> names = new HashSet<>();
        for (Map<String, LinkedHashMap<String, Object>> tmp : list) {
            for (String enchantmentName : tmp.keySet()) {
                names.add(enchantmentName);
            }
        }
        for (String s : createdEnchants.keySet()) {
            if (!names.contains(s)) {
                Map<String, LinkedHashMap<String, Object>> ench = new HashMap<>();
                LinkedHashMap<String, Object> values = new LinkedHashMap<>();
                CustomEnchantment e = createdEnchants.get(s);
                values.put("Probability", 0.0);
                String tools = e.enchantable[0].getID();
                for (int i = 1; i < e.enchantable.length; i++) {
                    tools += ", " + e.enchantable[i].getID();
                }
                values.put("Tools", tools);
                values.put("Name", e.loreName);
                values.put("Max Level", e.maxLevel);
                values.put("Cooldown", e.cooldown);
                values.put("Power", e.power);
                ench.put(e.loreName, values);
                list.add(ench);
            }
        }

        for (Map<String, LinkedHashMap<String, Object>> tmp : list) {
            for (String enchantmentName : tmp.keySet()) {
                if (createdEnchants.get(enchantmentName).power < 0.0) {
                    tmp.get(enchantmentName).remove("Power");
                }
                if (createdEnchants.get(enchantmentName).maxLevel < 2) {
                    tmp.get(enchantmentName).remove("Max Level");
                }
            }
        }
        //Sort Enchants
        config.set("enchantments", list);
    }

    // Adds cooldown, power, and changes probability to a double. This also adds the Haste enchantment
    public static void oneFourZero(YamlConfiguration config) {
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

        oneFourOne(config);
    }

}
