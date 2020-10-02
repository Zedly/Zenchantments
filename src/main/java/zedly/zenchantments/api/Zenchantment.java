package zedly.zenchantments.api;

import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

public interface Zenchantment {
    int getId();

    String getLoreName();

    String getDescription();

    int getMaxLevel();

    float getProbability();

    int getCooldown();

    double getPower();

    Tool[] getEnchantable();

    Class<?>[] getConflicting();

    Hand getHandUse();
}