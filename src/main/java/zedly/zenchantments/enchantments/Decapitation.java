package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.ZenchantmentsPlugin;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Material.*;
import static org.bukkit.entity.EntityType.*;

public class Decapitation extends Zenchantment {
    public static final String KEY = "decapitation";

    private static final String                             NAME        = "Decapitation";
    private static final String                             DESCRIPTION = "Increases the chance for dropping the enemy's head on death";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.LEFT;

    private static final int          BASE_PLAYER_DROP_CHANCE = 150;
    private static final int          BASE_MOB_DROP_CHANCE    = 150;
    private static final EntityType[] APPLICABLE_ENTITIES     = new EntityType[] {
        PLAYER,
        SKELETON,
        WITHER_SKULL,
        ZOMBIE,
        CREEPER
    };
    private static final Material[]   HEAD_MATERIALS          = new Material[] {
        PLAYER_HEAD,
        SKELETON_SKULL,
        WITHER_SKELETON_SKULL,
        ZOMBIE_HEAD,
        CREEPER_HEAD
    };

    private final NamespacedKey key;

    public Decapitation(
        @NotNull ZenchantmentsPlugin plugin,
        @NotNull Set<Tool> enchantable,
        int maxLevel,
        int cooldown,
        double power,
        float probability
    ) {
        super(plugin, enchantable, maxLevel, cooldown, power, probability);
        this.key = new NamespacedKey(plugin, Decapitation.KEY);
    }

    @Override
    @NotNull
    public NamespacedKey getKey() {
        return this.key;
    }

    @Override
    @NotNull
    public String getName() {
        return Decapitation.NAME;
    }

    @Override
    @NotNull
    public String getDescription() {
        return Decapitation.DESCRIPTION;
    }

    @Override
    @NotNull
    public Set<Class<? extends Zenchantment>> getConflicting() {
        return Decapitation.CONFLICTING;
    }

    @Override
    @NotNull
    public Hand getHandUse() {
        return Decapitation.HAND_USE;
    }

    @Override
    public boolean onEntityKill(@NotNull EntityDeathEvent event, int level, boolean usedHand) {
        int id = ArrayUtils.indexOf(APPLICABLE_ENTITIES, event.getEntityType());
        if (id == -1) {
            return false;
        }

        ItemStack itemStack = new ItemStack(HEAD_MATERIALS[id], 1);

        if (id == 0) {
            int bound = Math.max((int) Math.round(BASE_PLAYER_DROP_CHANCE / (level * this.getPower())), 1);
            if (ThreadLocalRandom.current().nextInt(bound) == 0) {
                SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
                meta.setOwningPlayer(this.getPlugin().getServer().getOfflinePlayer(event.getEntity().getUniqueId()));
                itemStack.setItemMeta(meta);
                event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), itemStack);
                return true;
            }
        } else {
            int bound = Math.max((int) Math.round(BASE_MOB_DROP_CHANCE / (level * this.getPower())), 1);
            if (ThreadLocalRandom.current().nextInt(bound) == 0) {
                event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), itemStack);
                return true;
            }
        }

        return false;
    }
}