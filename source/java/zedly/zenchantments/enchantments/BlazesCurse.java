package zedly.zenchantments.enchantments;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.EnumSet;

import static org.bukkit.block.Biome.*;
import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.FIRE;
import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.LAVA;
import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.*;

@AZenchantment(runInSlots = Slots.ARMOR , conflicting = {})
public final class BlazesCurse extends Zenchantment {
    private static final float SUBMERGE_DAMAGE = 1.5f;
    private static final float RAIN_DAMAGE = 0.5f;
    private static final float SNOWBALL_DAMAGE = 1.0f;
    private static final EnumSet<Biome> DRY_BIOMES = EnumSet.of(
        DESERT,
        SAVANNA,
        SAVANNA_PLATEAU,
        WINDSWEPT_SAVANNA,
        BADLANDS,
        WOODED_BADLANDS,
        ERODED_BADLANDS
    );

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
    public boolean onHitByProjectile(final @NotNull ProjectileHitEvent event, final int level, final EquipmentSlot slot) {
        Player target = (Player) event.getHitEntity();
        if (event.getEntity().getType() == EntityType.SNOWBALL) {
            if(event.getEntity().getShooter() != null && event.getEntity().getShooter() instanceof Player attacker) {
                WorldInteractionUtil.attackEntity(target, attacker, SNOWBALL_DAMAGE);
            } else {
                WorldInteractionUtil.damagePlayer(target, SNOWBALL_DAMAGE, MELTING);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onScan(final @NotNull Player player, final int level, final EquipmentSlot slot) {
        final Block block = player.getLocation().getBlock();
        Material material = block.getType();

        switch(material) {
            case WATER:
            case WATER_CAULDRON:
            case ICE:
            case FROSTED_ICE:
            case PACKED_ICE:
            case BLUE_ICE:
            case SNOW_BLOCK:
            case POWDER_SNOW:
                WorldInteractionUtil.damagePlayer(player, SUBMERGE_DAMAGE, MELTING);
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
                WorldInteractionUtil.damagePlayer(player, RAIN_DAMAGE, MELTING);
                return true;
        }

        if (player.getWorld().hasStorm() && !DRY_BIOMES.contains(player.getLocation().getBlock().getBiome())) {
            final Location checkLocation = player.getLocation();
            if (checkLocation.getWorld().getHighestBlockAt(checkLocation).getY() <= checkLocation.getY() + 1) {
                WorldInteractionUtil.damagePlayer(player, RAIN_DAMAGE, EntityDamageEvent.DamageCause.CUSTOM);
            }
        }

        player.setFireTicks(0);
        return true;
    }
}
