package zedly.zenchantments.player;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.configuration.WorldConfiguration;

import java.util.HashMap;
import java.util.Map;

public class PlayerData implements zedly.zenchantments.api.player.PlayerData {
    private final Map<Integer, Integer> enchantCooldown = new HashMap<>();
    private final ZenchantmentsPlugin   plugin;

    private Player player;

    public PlayerData(ZenchantmentsPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void enableZenchantment(int zenchantmentId) {
        this.player.setMetadata("ze." + zenchantmentId, new FixedMetadataValue(this.plugin, false));
    }

    @Override
    public void enableAllZenchantments() {
        World world = this.player.getWorld();
        WorldConfiguration config = this.plugin.getWorldConfigurationProvider().getConfigurationForWorld(world);

        for (Zenchantment zenchantment : config.getEnchants()) {
            this.player.setMetadata("ze." + zenchantment.getId(), new FixedMetadataValue(this.plugin, false));
        }
    }

    @Override
    public void disableZenchantment(int zenchantmentId) {
        this.player.setMetadata("ze." + zenchantmentId, new FixedMetadataValue(this.plugin, true));
    }

    @Override
    public void disableAllZenchantments() {
        World world = this.player.getWorld();
        WorldConfiguration config = this.plugin.getWorldConfigurationProvider().getConfigurationForWorld(world);

        for (Zenchantment zenchantment : config.getEnchants()) {
            this.player.setMetadata("ze." + zenchantment.getId(), new FixedMetadataValue(this.plugin, true));
        }
    }

    @Override
    public int getCooldownForZenchantment(int zenchantmentId) {
        return this.enchantCooldown.getOrDefault(zenchantmentId, 0);
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void tick() {
        this.enchantCooldown.replaceAll((e, v) -> Math.max(enchantCooldown.get(e) - 1, 0));
    }

    public void setCooldown(int zenchantmentId, int ticks) {
        this.enchantCooldown.put(zenchantmentId, ticks);
    }

    public boolean isDisabled(int zenchantmentId) {
        if (this.player.hasMetadata("ze." + zenchantmentId)) {
            return this.player.getMetadata("ze." + zenchantmentId).get(0).asBoolean();
        }

        this.player.setMetadata("ze." + zenchantmentId, new FixedMetadataValue(this.plugin, false));
        return false;
    }

    @Deprecated
    public static PlayerData matchPlayer(Player player) {
        return null;
    }
}