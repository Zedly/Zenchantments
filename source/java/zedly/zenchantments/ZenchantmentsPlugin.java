package zedly.zenchantments;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.api.Zenchantments;
import zedly.zenchantments.command.ZenchantmentsCommandHandler;
import zedly.zenchantments.configuration.GlobalConfiguration;
import zedly.zenchantments.configuration.WorldConfigurationProvider;
import zedly.zenchantments.enchantments.Anthropomorphism;
import zedly.zenchantments.enchantments.NetherStep;
import zedly.zenchantments.event.listener.ArrowListener;
import zedly.zenchantments.event.listener.GeneralListener;
import zedly.zenchantments.event.listener.ZenchantmentListener;
import zedly.zenchantments.event.listener.merge.AnvilMergeListener;
import zedly.zenchantments.event.listener.merge.GrindstoneMergeListener;
import zedly.zenchantments.player.PlayerDataProvider;
import zedly.zenchantments.task.Frequency;
import zedly.zenchantments.task.TaskRunner;

import java.util.Locale;
import java.util.Objects;

import static org.bukkit.Material.LAVA;
import static org.bukkit.Material.WATER;
import static org.bukkit.potion.PotionEffectType.FAST_DIGGING;

public class ZenchantmentsPlugin extends JavaPlugin implements Zenchantments {
    private static ZenchantmentsPlugin instance;

    private final GlobalConfiguration        globalConfiguration        = new GlobalConfiguration(this);
    private final WorldConfigurationProvider worldConfigurationProvider = WorldConfigurationProvider.getInstance();
    private final PlayerDataProvider         playerDataProvider         = new PlayerDataProvider(this);
    private final ZenchantmentFactory        zenchantmentFactory        = new ZenchantmentFactory(this);
    private final I18n                       i18n                       = new I18n(this);

    @NotNull
    public static ZenchantmentsPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        ZenchantmentsPlugin.instance = this;

        this.i18n.updateLocale(Locale.getDefault());

        try {
            this.globalConfiguration.loadGlobalConfiguration();
        } catch (Exception e) {
            System.err.println("Zenchantments was unable to load the default configuration. This can only mean the plugin JAR is broken!\n" +
                "Please try updating Zenchantments from https://dev.bukkit.org/projects/zenchantments");
            e.printStackTrace();
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.worldConfigurationProvider.loadWorldConfigurations();

        ZenchantmentsCommandHandler commandHandler = new ZenchantmentsCommandHandler(this);
        PluginCommand enchCommand = Objects.requireNonNull(this.getCommand("ench"));
        enchCommand.setExecutor(commandHandler);
        enchCommand.setTabCompleter(commandHandler);

        this.getServer().getPluginManager().registerEvents(new ArrowListener(), this);
        this.getServer().getPluginManager().registerEvents(new GeneralListener(this), this);
        this.getServer().getPluginManager().registerEvents(new ZenchantmentListener(this), this);
        this.getServer().getPluginManager().registerEvents(new AnvilMergeListener(this), this);
        this.getServer().getPluginManager().registerEvents(new GrindstoneMergeListener(this), this);

        for (Frequency frequency : Frequency.values()) {
            this.getServer().getScheduler().scheduleSyncRepeatingTask(
                this,
                new TaskRunner(this, frequency),
                1,
                frequency.getPeriod()
            );
        }
    }

    @Override
    public void onDisable() {
        for (Location location : NetherStep.NETHERSTEP_LOCATIONS.keySet()) {
            location.getBlock().setType(LAVA);
        }

        for (Entity entity : Anthropomorphism.IDLE_BLOCKS.keySet()) {
            entity.remove();
        }

        for (Player player : this.getServer().getOnlinePlayers()) {
            if (player.hasMetadata("ze.speed")) {
                player.removeMetadata("ze.speed", this);
                player.setFlySpeed(0.1f);
                player.setWalkSpeed(0.2f);
            }

            if (player.hasMetadata("ze.haste")) {
                player.removePotionEffect(FAST_DIGGING);
                player.removeMetadata("ze.haste", this);
            }
        }
    }

    @Override
    @NotNull
    @Deprecated
    public GlobalConfiguration getGlobalConfiguration() {
        return this.globalConfiguration;
    }

    @Override
    @Deprecated
    @NotNull
    public WorldConfigurationProvider getWorldConfigurationProvider() {
        return this.worldConfigurationProvider;
    }

    @Override
    @Deprecated
    @NotNull
    public PlayerDataProvider getPlayerDataProvider() {
        return this.playerDataProvider;
    }

    @NotNull
    @Deprecated
    public ZenchantmentFactory getZenchantmentFactory() {
        return this.zenchantmentFactory;
    }

    @NotNull
    @Deprecated
    public CompatibilityAdapter getCompatibilityAdapter() {
        return CompatibilityAdapter.instance();
    }

    @NotNull
    public I18n getI18n() {
        return this.i18n;
    }
}
