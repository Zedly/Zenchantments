package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.arrows.ZenchantedArrow;
import zedly.zenchantments.arrows.MultiArrow;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public final class Spread extends Zenchantment {
    public static final String KEY = "spread";

    private static final String                             NAME        = "Spread";
    private static final String                             DESCRIPTION = "Fires an array of arrows simultaneously";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of(Burst.class);
    private static final Hand                               HAND_USE    = Hand.RIGHT;

    private final NamespacedKey key;

    public Spread(
        @NotNull ZenchantmentsPlugin plugin,
        @NotNull Set<Tool> enchantable,
        int maxLevel,
        int cooldown,
        double power,
        float probability
    ) {
        super(plugin, enchantable, maxLevel, cooldown, power, probability);
        this.key = new NamespacedKey(plugin, KEY);
    }

    @Override
    @NotNull
    public NamespacedKey getKey() {
        return this.key;
    }

    @Override
    @NotNull
    public String getName() {
        return NAME;
    }

    @Override
    @NotNull
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    @NotNull
    public Set<Class<? extends Zenchantment>> getConflicting() {
        return CONFLICTING;
    }

    @Override
    @NotNull
    public Hand getHandUse() {
        return HAND_USE;
    }

    @Override
    public boolean onProjectileLaunch(@NotNull ProjectileLaunchEvent event, int level, boolean usedHand) {
        Arrow originalArrow = (Arrow) event.getEntity();
        Player player = (Player) originalArrow.getShooter();
        ItemStack hand = Utilities.getUsedItemStack(player, usedHand);

        MultiArrow multiArrow = new MultiArrow(this.getPlugin(), originalArrow);
        ZenchantedArrow.putArrow(originalArrow, multiArrow, player);

        this.getPlugin().getServer().getPluginManager().callEvent(
            new EntityShootBowEvent(player, hand, originalArrow, (float) originalArrow.getVelocity().length())
        );

        Utilities.damageItemStack(player, (int) Math.round(level / 2.0 + 1), usedHand);

        for (int i = 0; i < (int) Math.round(this.getPower() * level * 4); i++) {
            Vector vector = originalArrow.getVelocity();

            vector.setX(vector.getX() + Math.max(Math.min(ThreadLocalRandom.current().nextGaussian() / 8, 0.75), -0.75));
            vector.setZ(vector.getZ() + Math.max(Math.min(ThreadLocalRandom.current().nextGaussian() / 8, 0.75), -0.75));

            Arrow arrow = player.getWorld().spawnArrow(player.getEyeLocation().add(player.getLocation().getDirection().multiply(1.0)), vector, 1, 0);
            arrow.setShooter(player);
            arrow.setVelocity(vector.normalize().multiply(originalArrow.getVelocity().length()));
            arrow.setFireTicks(originalArrow.getFireTicks());
            arrow.setKnockbackStrength(originalArrow.getKnockbackStrength());

            EntityShootBowEvent entityShootBowEvent = new EntityShootBowEvent(player, hand, arrow, (float) originalArrow.getVelocity().length());

            this.getPlugin().getServer().getPluginManager().callEvent(entityShootBowEvent);

            if (entityShootBowEvent.isCancelled()) {
                arrow.remove();
                return false;
            }

            arrow.setMetadata("ze.arrow", new FixedMetadataValue(this.getPlugin(), null));
            arrow.setCritical(originalArrow.isCritical());

            ZenchantedArrow.putArrow(originalArrow, new MultiArrow(this.getPlugin(), originalArrow), player);
        }

        return true;
    }
}