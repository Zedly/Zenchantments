package zedly.zenchantments.enchantments;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.Material.*;
import static org.bukkit.block.Biome.*;
import static org.bukkit.block.BlockFace.DOWN;
import static zedly.zenchantments.enums.Tool.CHESTPLATE;

public class BlazesCurse extends CustomEnchantment {

    private static final Biome[] noRainBiomes   = new Biome[]{DESERT, FROZEN_OCEAN, FROZEN_RIVER, ICE_FLATS,
                                                              ICE_MOUNTAINS, DESERT_HILLS, COLD_BEACH, TAIGA_COLD,
                                                              TAIGA_COLD_HILLS,
                                                              SAVANNA, SAVANNA_ROCK, MESA, MESA_ROCK,
                                                              MESA_CLEAR_ROCK, MUTATED_DESERT,
                                                              MUTATED_ICE_FLATS, MUTATED_TAIGA_COLD,
                                                              MUTATED_SAVANNA, MUTATED_SAVANNA_ROCK,
                                                              MUTATED_MESA, MUTATED_MESA_ROCK,
                                                              MUTATED_MESA_CLEAR_ROCK};
    private static final float   submergeDamage = 1.5f;
    private static final float   rainDamage     = .5f;

    public BlazesCurse() {
        super(5);
        maxLevel = 1;
        loreName = "Blaze's Curse";
        probability = 0;
        enchantable = new Tool[]{CHESTPLATE};
        conflicting = new Class[]{};
        description = "Causes the player to be unharmed in lava and fire, but damages them in water and rain";
        cooldown = 0;
        power = -1.0;
        handUse = Hand.NONE;
    }

    @Override
    public boolean onEntityDamage(EntityDamageEvent evt, int level, boolean usedHand) {
        if(evt.getCause() == EntityDamageEvent.DamageCause.HOT_FLOOR ||
           evt.getCause() == EntityDamageEvent.DamageCause.LAVA ||
           evt.getCause() == EntityDamageEvent.DamageCause.FIRE ||
           evt.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
            evt.setCancelled(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean onBeingHit(EntityDamageByEntityEvent evt, int level, boolean usedHand) {
        if(evt.getDamager().getType() == EntityType.FIREBALL
           || evt.getDamager().getType() == EntityType.SMALL_FIREBALL) {
            evt.setDamage(0);
            return true;
        }
        return false;
    }

    @Override
    public boolean onScan(Player player, int level, boolean usedHand) {
        Material mat = player.getLocation().getBlock().getType();
        if(mat == STATIONARY_WATER || mat == WATER) {
            ADAPTER.damagePlayer(player, submergeDamage, EntityDamageEvent.DamageCause.DROWNING);
            return true;
        }
        mat = player.getLocation().getBlock().getRelative(DOWN).getType();
        if(mat == ICE || mat == FROSTED_ICE) {
            ADAPTER.damagePlayer(player, rainDamage, EntityDamageEvent.DamageCause.MELTING);
            return true;
        }
        if(player.getWorld().hasStorm() == true
           && !ArrayUtils.contains(noRainBiomes, player.getLocation().getBlock().getBiome())
           && player.getLocation().getY() >= player.getWorld().getHighestBlockYAt(player.getLocation())) {
            ADAPTER.damagePlayer(player, rainDamage, EntityDamageEvent.DamageCause.CUSTOM);
        }
        player.setFireTicks(0);
        return true;
    }
}
