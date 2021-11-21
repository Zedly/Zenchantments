package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.EnumSet;
import java.util.Set;

import static org.bukkit.Material.*;
import static org.bukkit.block.Biome.*;
import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.FIRE;
import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.LAVA;
import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.*;

public final class BlazesCurse extends Zenchantment {
    private static final float SUBMERGE_DAMAGE = 1.5f;
    private static final float RAIN_DAMAGE     = 0.5f;

    public static final String KEY = "blazes_curse";

    private static final String                             NAME        = "Blaze's Curse";
    private static final String                             DESCRIPTION = "Causes the player to be unharmed in lava and fire, but damages them in water and rain";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.NONE;

    private static final EnumSet<Biome> DRY_BIOMES = EnumSet.of(
        DESERT,
        FROZEN_OCEAN,
        FROZEN_RIVER,
        SNOWY_TUNDRA,
        SNOWY_MOUNTAINS,
        DESERT_HILLS,
        SNOWY_BEACH,
        SNOWY_TAIGA,
        SNOWY_TAIGA_HILLS,
        SAVANNA,
        SAVANNA_PLATEAU,
        BADLANDS,
        WOODED_BADLANDS_PLATEAU,
        BADLANDS_PLATEAU,
        DESERT_LAKES,
        ICE_SPIKES,
        SNOWY_TAIGA_MOUNTAINS,
        SHATTERED_SAVANNA,
        SHATTERED_SAVANNA_PLATEAU,
        ERODED_BADLANDS,
        MODIFIED_WOODED_BADLANDS_PLATEAU,
        MODIFIED_BADLANDS_PLATEAU
    );

    private final NamespacedKey key;

    public BlazesCurse(
        final @NotNull Set<Tool> enchantable,
        final int maxLevel,
        final int cooldown,
        final double probability,
        final float power
    ) {
        super(enchantable, maxLevel, cooldown, probability, power);
        this.key = new NamespacedKey(ZenchantmentsPlugin.getInstance(), KEY);
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
    public boolean onEntityDamage(final @NotNull EntityDamageEvent event, final int level, final boolean usedHand) {
        if (event.getCause() == LAVA || event.getCause() == FIRE || event.getCause() == FIRE_TICK || event.getCause() == HOT_FLOOR) {
            event.setCancelled(true);
            return true;
        }

        return false;
    }

    @Override
    public boolean onBeingHit(final @NotNull EntityDamageByEntityEvent event, final int level, final boolean usedHand) {
        if (event.getDamager().getType() == EntityType.FIREBALL || event.getDamager().getType() == EntityType.SMALL_FIREBALL) {
            event.setDamage(0);
            return true;
        }

        return false;
    }

    @Override
    public boolean onScan(final @NotNull Player player, final int level, final boolean usedHand) {
        final Block block = player.getLocation().getBlock();
        Material material = block.getType();

        if (material == WATER) {
            ZenchantmentsPlugin.getInstance().getCompatibilityAdapter().damagePlayer(player, SUBMERGE_DAMAGE, DROWNING);
            return true;
        }

        material = block.getRelative(BlockFace.DOWN).getType();

        if (material == ICE || material == FROSTED_ICE) {
            ZenchantmentsPlugin.getInstance().getCompatibilityAdapter().damagePlayer(player, RAIN_DAMAGE, MELTING);
            return true;
        }

        if (player.getWorld().hasStorm() && !DRY_BIOMES.contains(player.getLocation().getBlock().getBiome())) {
            final Location checkLocation = player.getLocation();
            while (checkLocation.getBlockY() < 256) {
                if (!MaterialList.AIR.contains(checkLocation.getBlock().getType())) {
                    break;
                }

                checkLocation.setY(checkLocation.getBlockY() + 1);
            }

            if (checkLocation.getBlockY() == 256) {
                ZenchantmentsPlugin.getInstance().getCompatibilityAdapter().damagePlayer(player, RAIN_DAMAGE, EntityDamageEvent.DamageCause.CUSTOM);
            }
        }

        player.setFireTicks(0);
        return true;
    }
}
