package zedly.zenchantments.configuration;

import zedly.zenchantments.ZenchantmentsPlugin;

public class GlobalConfiguration implements zedly.zenchantments.api.configuration.GlobalConfiguration {
    private final ZenchantmentsPlugin plugin;

    public GlobalConfiguration(ZenchantmentsPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadGlobalConfiguration() {

    }
}