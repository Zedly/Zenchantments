package zedly.zenchantments.configuration;

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

public class GlobalConfiguration implements zedly.zenchantments.api.configuration.GlobalConfiguration {
    private final ZenchantmentsPlugin plugin;

    private WorldConfiguration defaultWorldConfiguration;

    public GlobalConfiguration(final @NotNull ZenchantmentsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public WorldConfiguration getDefaultWorldConfiguration() {
        return this.defaultWorldConfiguration;
    }

    @Override
    @NotNull
    public Set<Zenchantment> getConfiguredZenchantments() {
        return Collections.emptySet();
    }

    public void loadGlobalConfiguration() throws IOException, InvalidConfigurationException {
        // Create default config for this world if it doesn't exist
        InputStream stream = Zenchantments.class.getResourceAsStream("/config.yml");
        // Load the config for this config
        YamlConfiguration yamlConfig = new YamlConfiguration();
        yamlConfig.load(new InputStreamReader(stream));
        defaultWorldConfiguration = WorldConfiguration.fromYamlConfiguration(yamlConfig);
    }
}
