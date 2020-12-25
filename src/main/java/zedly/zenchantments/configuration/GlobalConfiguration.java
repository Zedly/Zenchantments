package zedly.zenchantments.configuration;

import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;

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

    public void loadGlobalConfiguration() {

    }
}
