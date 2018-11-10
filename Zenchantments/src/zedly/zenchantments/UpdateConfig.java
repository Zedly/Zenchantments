package zedly.zenchantments;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
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
            case 5:
                switch(version[2]) {
                    case 2:
                        break;
                    default:
                        oneFiveTwo(config);
                }
                break;
        }
        config.set("ZenchantmentsConfigVersion", Storage.version);
    }


    private static void oneFiveTwo(YamlConfiguration config) {
        config.set("enchantment_glow", false);
        config.set("enchantment_color", 7);
        config.set("curse_color", 'c');

        genericUpdate(config);

    }

    // Removes power and max level if they are not needed by an enchantment and adds any enchantments not in the config
    private static void genericUpdate(YamlConfiguration config) {
    	// Get the existing config data from the file
        List<Map<String, LinkedHashMap<String, Object>>> configData;
        if (config.get("enchantments") != null) {
            configData = (List<Map<String, LinkedHashMap<String, Object>>>) config.get("enchantments");
        } else {
            configData = new ArrayList<>();
        }


        Map<String, CustomEnchantment> enchantments = new HashMap<>();
        List<Class<? extends CustomEnchantment>> customEnchantments = new ArrayList<>();

        new FastClasspathScanner(CustomEnchantment.class.getPackage().getName())
            .matchSubclassesOf(CustomEnchantment.class, customEnchantments::add)
            .scan();

	    for (Class<? extends CustomEnchantment> cl : customEnchantments) {
		    try {
			    CustomEnchantment.Builder<? extends CustomEnchantment> ench = cl.newInstance().defaults();
			    enchantments.put(ench.loreName(), ench.build());
		    } catch (IllegalAccessException | ClassCastException | InstantiationException ex) {
			    System.err.println("Error parsing config for enchantment " + cl.getName() + ", skipping");
		    }

	    }

        Set<String> names = new HashSet<>();
        for (Map<String, LinkedHashMap<String, Object>> tmp : configData) {
            names.addAll(tmp.keySet());
        }

        for (String s : enchantments.keySet()) {
            if (!names.contains(s)) {
                Map<String, LinkedHashMap<String, Object>> ench = new HashMap<>();
                LinkedHashMap<String, Object> values = new LinkedHashMap<>();
                CustomEnchantment e = enchantments.get(s);

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
                configData.add(ench);
            }
        }
        for (Map<String, LinkedHashMap<String, Object>> tmp : configData) {
            for (String enchantmentName : tmp.keySet()) {
                if (enchantments.get(enchantmentName).power < 0.0) {
                    tmp.get(enchantmentName).remove("Power");
                }
                if (enchantments.get(enchantmentName).maxLevel < 2) {
                    tmp.get(enchantmentName).remove("Max Level");
                }
            }
        }
        // Sort Enchants
        config.set("enchantments", configData);
    }

    // Adds cooldown, power, and changes probability to a double. This also adds the Haste enchantment
    private static void oneFourZero(YamlConfiguration config) {
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

        genericUpdate(config);
    }

}
