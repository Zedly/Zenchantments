package zedly.zenchantments.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.checkIndex;
import static java.util.Objects.requireNonNull;
import static org.bukkit.Material.AIR;
import static org.bukkit.event.block.Action.*;

@AZenchantment(runInSlots = Slots.MAIN_HAND, conflicting = {Pierce.class, Switch.class})
public class Quake extends Zenchantment {
    int delay = 5;

    @Override
    public boolean onBlockInteract(final @NotNull PlayerInteractEvent event, final int level, final EquipmentSlot slot) {
        final Player player = event.getPlayer();
        final ItemStack hand = player.getInventory().getItem(slot);

        if (event.getAction() == RIGHT_CLICK_AIR || event.getAction() == RIGHT_CLICK_BLOCK) {
            if (player.isSneaking()) {
                final Location center = player.getLocation();
                Set<Player> quakeViewers = event.getPlayer().getWorld().getPlayers().stream().filter((p) -> p.getLocation().distance(player.getLocation()) < Bukkit.getSimulationDistance()).collect(Collectors.toSet());

                Collection<LivingEntity> nearbyMonsters = center.getWorld().getNearbyEntities(center, level + 3, 1, level + 3).stream()
                    .filter((e) -> e instanceof Monster || e instanceof Slime)
                    .map((e) -> (LivingEntity) e)
                    .collect(Collectors.toSet());

                for (int i = 0; i < level + 3; i++) {
                    final int j = i;
                    Bukkit.getScheduler().scheduleSyncDelayedTask(ZenchantmentsPlugin.getInstance(), () -> startWave(player, quakeViewers, nearbyMonsters, center, 1 + j), 2 * i);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(ZenchantmentsPlugin.getInstance(), () -> stopWave(quakeViewers, center, 1 + j), 10 + 2 * i);
                }
                return true;
            }
        }
        return false;
    }

    private void startWave(Player attacker, Set<Player> quakeViewers, Collection<LivingEntity> nearbyMonsters, Location center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Location loc = center.clone().add(x, 0, z);
                if (loc.distance(center) < radius || loc.distance(center) >= radius + 1) {
                    continue;
                }
                Block sourceBlock = loc.getBlock();
                if (sourceBlock.getType() != AIR) {
                    continue;
                }
                sourceBlock = sourceBlock.getRelative(BlockFace.DOWN);
                if (!sourceBlock.getType().isSolid()) {
                    continue;
                }
                final int entityId = 2000000000 + (sourceBlock.hashCode()) % 10000000;
                for(Player q : quakeViewers) {
                    q.sendBlockChange(sourceBlock.getLocation(), Material.AIR, (byte) 0);
                    WorldInteractionUtil.showQuakeBlock(q, entityId, sourceBlock);
                }
            }
        }
        for(LivingEntity m : nearbyMonsters) {
            double r = m.getLocation().distance(center);
            if(r < radius + 1 && m.getNoDamageTicks() == 0) {
                WorldInteractionUtil.attackEntity(m, attacker, 1);
                m.setVelocity(m.getLocation().clone().subtract(center).toVector().normalize().multiply(1).add(new Vector(0, 0.5, 0)));
            }
        }
    }

    private void stopWave(Set<Player> quakeViewers, Location center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Location loc = center.clone().add(x, 0, z);
                Block sourceBlock = loc.getBlock().getRelative(BlockFace.DOWN);
                final int entityId = 2000000000 + (sourceBlock.hashCode()) % 10000000;
                for(Player q : quakeViewers) {
                    q.sendBlockChange(sourceBlock.getLocation(), sourceBlock.getBlockData());
                    WorldInteractionUtil.hideFakeEntity(entityId, q);
                }
            }
        }
    }
}
