package zedly.zenchantments.api.player;

import org.bukkit.NamespacedKey;

public interface PlayerData {
    void enableZenchantment(NamespacedKey zenchantmentKey);

    void enableAllZenchantments();

    void disableZenchantment(NamespacedKey zenchantmentKey);

    void disableAllZenchantments();

    int getCooldownForZenchantment(NamespacedKey zenchantmentKey);
}