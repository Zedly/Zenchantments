package zedly.zenchantments.api;

import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

public interface Zenchantment {
    int getId();

    String getLoreName();

    String getDescription();

    int getMaxLevel();

    int getCooldown();

    double getPower();

    float getProbability();

    Tool[] getEnchantable();

    Class<?>[] getConflicting();

    Hand getHandUse();
}