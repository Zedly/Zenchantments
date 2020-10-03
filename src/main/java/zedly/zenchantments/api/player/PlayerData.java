package zedly.zenchantments.api.player;

public interface PlayerData {
    void enableZenchantment(int zenchantmentId);

    void enableAllZenchantments();

    void disableZenchantment(int zenchantmentId);

    void disableAllZenchantments();

    int getCooldownForZenchantment(int zenchantmentId);
}