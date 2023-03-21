package zedly.zenchantments.enchantments;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.concurrent.ThreadLocalRandom;

import static java.util.Objects.requireNonNull;
import static org.bukkit.Material.*;
import static org.bukkit.entity.EntityType.*;

@AZenchantment(runInSlots = Slots.MAIN_HAND, conflicting = {})
public final class Decapitation extends Zenchantment {
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

    @Override
    public boolean onEntityKill(final @NotNull EntityDeathEvent event, final int level, final EquipmentSlot slot) {
        final int id = ArrayUtils.indexOf(APPLICABLE_ENTITIES, event.getEntityType());
        if (id == -1) {
            return false;
        }

        final ItemStack itemStack = new ItemStack(HEAD_MATERIALS[id], 1);

        if (id == 0) {
            final int bound = Math.max((int) Math.round(BASE_PLAYER_DROP_CHANCE / (level * this.getPower())), 1);
            if (ThreadLocalRandom.current().nextInt(bound) == 0) {
                SkullMeta meta = (SkullMeta) requireNonNull(itemStack.getItemMeta());
                meta.setOwningPlayer(ZenchantmentsPlugin.getInstance().getServer().getOfflinePlayer(event.getEntity().getUniqueId()));
                itemStack.setItemMeta(meta);
                event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), itemStack);
                return true;
            }
        } else {
            final int bound = Math.max((int) Math.round(BASE_MOB_DROP_CHANCE / (level * this.getPower())), 1);
            if (ThreadLocalRandom.current().nextInt(bound) == 0) {
                event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), itemStack);
                return true;
            }
        }

        return false;
    }
}
