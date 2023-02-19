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
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.EnumSet;
import java.util.Set;

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
        SAVANNA,
        SAVANNA_PLATEAU,
        WINDSWEPT_SAVANNA,
        BADLANDS,
        WOODED_BADLANDS,
        ERODED_BADLANDS
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
    public boolean onEntityDamage(final @NotNull EntityDamageEvent event, final int level, final EquipmentSlot slot) {
        if (event.getCause() == LAVA || event.getCause() == FIRE || event.getCause() == FIRE_TICK || event.getCause() == HOT_FLOOR) {
            event.setCancelled(true);
            return true;
        }

        return false;
    }

    @Override
    public boolean onBeingHit(final @NotNull EntityDamageByEntityEvent event, final int level, final EquipmentSlot slot) {
        if (event.getDamager().getType() == EntityType.FIREBALL || event.getDamager().getType() == EntityType.SMALL_FIREBALL) {
            event.setDamage(0);
            return true;
        }

        return false;
    }

    @Override
    public boolean onScan(final @NotNull Player player, final int level, final EquipmentSlot slot) {
        final Block block = player.getLocation().getBlock();
        Material material = block.getType();

        switch(material) {
            case ICE:
            case FROSTED_ICE:
            case PACKED_ICE:
            case BLUE_ICE:
            case SNOW_BLOCK:
            case POWDER_SNOW:
                CompatibilityAdapter.instance().damagePlayer(player, RAIN_DAMAGE, MELTING);
                return true;
        }

        material = block.getRelative(BlockFace.DOWN).getType();

        switch(material) {
            case ICE:
            case FROSTED_ICE:
            case PACKED_ICE:
            case BLUE_ICE:
            case SNOW_BLOCK:
            case POWDER_SNOW:
                CompatibilityAdapter.instance().damagePlayer(player, RAIN_DAMAGE, MELTING);
                return true;
        }

        if (player.getWorld().hasStorm() && !DRY_BIOMES.contains(player.getLocation().getBlock().getBiome())) {
            final Location checkLocation = player.getLocation();
            if (checkLocation.getWorld().getHighestBlockAt(checkLocation).getY() <= checkLocation.getY() + 1) {
                CompatibilityAdapter.instance().damagePlayer(player, RAIN_DAMAGE, EntityDamageEvent.DamageCause.CUSTOM);
            }
        }

        player.setFireTicks(0);
        return true;
    }
}
