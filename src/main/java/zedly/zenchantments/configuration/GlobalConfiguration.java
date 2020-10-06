package zedly.zenchantments.configuration;

import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.ZenchantmentsPlugin;

public class GlobalConfiguration implements zedly.zenchantments.api.configuration.GlobalConfiguration {
    private final ZenchantmentsPlugin plugin;

    private WorldConfiguration defaultWorldConfiguration;

    public GlobalConfiguration(@NotNull ZenchantmentsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public WorldConfiguration getDefaultWorldConfiguration() {
        return this.defaultWorldConfiguration;
    }

    public void loadGlobalConfiguration() {

    }
}