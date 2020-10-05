package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Set;

import static org.bukkit.Material.*;
import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.FIRE;
import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.LAVA;
import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.*;

public class BlazesCurse extends Zenchantment {
    private static final float SUBMERGE_DAMAGE = 1.5f;
    private static final float RAIN_DAMAGE     = 0.5f;

    public static final String KEY = "blazes_curse";

    private static final String                             NAME        = "Blaze's Curse";
    private static final String                             DESCRIPTION = "Causes the player to be unharmed in lava and fire, but damages them in water and rain";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.NONE;

    private final NamespacedKey key;

    public BlazesCurse(
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
    public boolean onEntityDamage(@NotNull EntityDamageEvent event, int level, boolean usedHand) {
        if (event.getCause() == LAVA
            || event.getCause() == FIRE
            || event.getCause() == FIRE_TICK
            || event.getCause() == HOT_FLOOR
        ) {
            event.setCancelled(true);
            return true;
        }

        return false;
    }

    @Override
    public boolean onBeingHit(@NotNull EntityDamageByEntityEvent event, int level, boolean usedHand) {
        if (event.getDamager().getType() == EntityType.FIREBALL
            || event.getDamager().getType() == EntityType.SMALL_FIREBALL
        ) {
            event.setDamage(0);
            return true;
        }

        return false;
    }

    @Override
    public boolean onScan(@NotNull Player player, int level, boolean usedHand) {
        Block block = player.getLocation().getBlock();
        Material material = block.getType();

        if (material == WATER) {
            ADAPTER.damagePlayer(player, SUBMERGE_DAMAGE, DROWNING);
            return true;
        }

        material = block.getRelative(BlockFace.DOWN).getType();

        if (material == ICE || material == FROSTED_ICE) {
            ADAPTER.damagePlayer(player, RAIN_DAMAGE, MELTING);
            return true;
        }

        if (player.getWorld().hasStorm()
            && !Storage.COMPATIBILITY_ADAPTER.DryBiomes().contains(player.getLocation().getBlock().getBiome())
        ) {
            Location checkLocation = player.getLocation();
            while (checkLocation.getBlockY() < 256) {
                if (!Storage.COMPATIBILITY_ADAPTER.Airs().contains(checkLocation.getBlock().getType())) {
                    break;
                }

                checkLocation.setY(checkLocation.getBlockY() + 1);
            }

            if (checkLocation.getBlockY() == 256) {
                ADAPTER.damagePlayer(player, RAIN_DAMAGE, CUSTOM);
            }
        }

        player.setFireTicks(0);
        return true;
    }
}