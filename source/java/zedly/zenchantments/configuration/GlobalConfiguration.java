package zedly.zenchantments.configuration;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.api.Zenchantments;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Set;

public class GlobalConfiguration {

    private static WorldConfiguration defaultWorldConfiguration = null;

    private GlobalConfiguration() {
    }

    @NotNull
    public static WorldConfiguration getDefaultWorldConfiguration() {
        if(defaultWorldConfiguration == null) {
            try {
                loadGlobalConfiguration();
            } catch (Exception e) {
                System.err.println("Zenchantments was unable to load the default configuration. This can only mean the plugin JAR is broken!\n" +
                    "Please try updating Zenchantments from https://dev.bukkit.org/projects/zenchantments");
                e.printStackTrace();
                Bukkit.getServer().getPluginManager().disablePlugin(ZenchantmentsPlugin.getInstance());
                throw new RuntimeException("Zenchantments was unable to load the default world configuration. This can really only mean your jar file is corrupted!");
            }
        }
        return defaultWorldConfiguration;
    }

    @NotNull
    public static Set<Zenchantment> getConfiguredZenchantments() {
        return Collections.emptySet();
    }

    private static void loadGlobalConfiguration() throws IOException, InvalidConfigurationException {
        // Create default config for this world if it doesn't exist
        InputStream stream = ZenchantmentsPlugin.class.getResourceAsStream("/config.yml");
        // Load the config for this config
        YamlConfiguration yamlConfig = new YamlConfiguration();
        yamlConfig.load(new InputStreamReader(stream));
        defaultWorldConfiguration = WorldConfiguration.fromYamlConfiguration(yamlConfig);
    }
}
