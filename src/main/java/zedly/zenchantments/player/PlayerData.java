package zedly.zenchantments.player;

import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.configuration.WorldConfiguration;

import java.util.HashMap;
import java.util.Map;

public class PlayerData implements zedly.zenchantments.api.player.PlayerData {
    private final Map<NamespacedKey, Integer> enchantCooldown = new HashMap<>();
    private final ZenchantmentsPlugin         plugin;

    private Player player;

    public PlayerData(@NotNull ZenchantmentsPlugin plugin, @NotNull Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void enableZenchantment(@NotNull NamespacedKey zenchantmentKey) {
        this.player.setMetadata("ze." + zenchantmentKey, new FixedMetadataValue(this.plugin, false));
    }

    @Override
    public void enableAllZenchantments() {
        World world = this.player.getWorld();
        WorldConfiguration config = this.plugin.getWorldConfigurationProvider().getConfigurationForWorld(world);

        for (Zenchantment zenchantment : config.getEnchants()) {
            this.player.setMetadata("ze." + zenchantment.getKey(), new FixedMetadataValue(this.plugin, false));
        }
    }

    @Override
    public void disableZenchantment(@NotNull NamespacedKey zenchantmentKey) {
        this.player.setMetadata("ze." + zenchantmentKey, new FixedMetadataValue(this.plugin, true));
    }

    @Override
    public void disableAllZenchantments() {
        World world = this.player.getWorld();
        WorldConfiguration config = this.plugin.getWorldConfigurationProvider().getConfigurationForWorld(world);

        for (Zenchantment zenchantment : config.getEnchants()) {
            this.player.setMetadata("ze." + zenchantment.getKey(), new FixedMetadataValue(this.plugin, true));
        }
    }

    @Override
    public int getCooldownForZenchantment(@NotNull NamespacedKey zenchantmentKey) {
        return this.enchantCooldown.getOrDefault(zenchantmentKey, 0);
    }

    @Contract(mutates = "this")
    public void setPlayer(@NotNull Player player) {
        this.player = player;
    }

    public void tick() {
        this.enchantCooldown.replaceAll((e, v) -> Math.max(enchantCooldown.get(e) - 1, 0));
    }

    public void setCooldown(NamespacedKey zenchantmentKey, int ticks) {
        this.enchantCooldown.put(zenchantmentKey, ticks);
    }

    public boolean isDisabled(NamespacedKey zenchantmentKey) {
        if (this.player.hasMetadata("ze." + zenchantmentKey)) {
            return this.player.getMetadata("ze." + zenchantmentKey).get(0).asBoolean();
        }

        this.player.setMetadata("ze." + zenchantmentKey, new FixedMetadataValue(this.plugin, false));
        return false;
    }

    @Deprecated
    public static PlayerData matchPlayer(Player player) {
        return null;
    }
}