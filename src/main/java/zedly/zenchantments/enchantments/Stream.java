package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public final class Stream extends Zenchantment {
    public static final String KEY = "stream";

    private static final String                             NAME        = "Stream";
    private static final String                             DESCRIPTION = "Creates a trail of particles when in flight";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.NONE;

    private static final Particle[] TRAIL_TYPES = {
        Particle.CLOUD,
        Particle.CRIT,
        Particle.VILLAGER_HAPPY,
        Particle.REDSTONE,
        Particle.HEART
    };

    private final NamespacedKey key;

    public Stream(
        final @NotNull ZenchantmentsPlugin plugin,
        final @NotNull Set<Tool> enchantable,
        final int maxLevel,
        final int cooldown,
        final double power,
        final float probability
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
    public boolean onBlockInteract(@NotNull PlayerInteractEvent event, int level, boolean usedHand) {
        if (event.getItem() == null || event.getItem().getType() != Material.ELYTRA) {
            return false;
        }

        Player player = event.getPlayer();

        if (!event.getPlayer().hasMetadata("ze.stream.mode")) {
            player.setMetadata("ze.stream.mode", new FixedMetadataValue(this.getPlugin(), 0));
        }

        if (!player.isSneaking() || (event.getAction() != RIGHT_CLICK_AIR && event.getAction() != RIGHT_CLICK_BLOCK)) {
            return false;
        }

        int mode = player.getMetadata("ze.stream.mode").get(0).asInt();
        mode = mode == 4 ? 0 : mode + 1;

        player.setMetadata("ze.stream.mode", new FixedMetadataValue(this.getPlugin(), mode));

        switch (mode) {
            case 0:
                player.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "Clouds");
                break;
            case 1:
                player.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "Gold Sparks");
                break;
            case 2:
                player.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "Green Sparks");
                break;
            case 3:
                player.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "Rainbow Dust");
                break;
            case 4:
                player.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "Hearts");
                break;
        }

        event.setCancelled(true);

        PlayerInventory inventory = player.getInventory();

        // Prevent auto-equipping.
        if ((inventory.getChestplate() == null
            || inventory.getChestplate().getType() == Material.AIR)
        ) {
            this.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(this.getPlugin(), () -> {
                if (inventory.getItemInMainHand().getType() != Material.AIR) {
                    return;
                }

                ItemStack stack = inventory.getChestplate();
                inventory.setItemInMainHand(stack);
                inventory.setChestplate(new ItemStack(Material.AIR));
            }, 0);
        }

        return false;
    }

    @Override
    public boolean onFastScan(@NotNull Player player, int level, boolean usedHand) {
        if (!player.isGliding() || !(player.getVelocity().length() >= 0.5)) {
            return false;
        }

        if (!player.hasMetadata("ze.stream.mode")) {
            player.setMetadata("ze.stream.mode", new FixedMetadataValue(this.getPlugin(), 0));
        }

        int mode = player.getMetadata("ze.stream.mode").get(0).asInt();

        switch (mode) {
            case 0:
            case 1:
            case 2:
            case 4:
                player.getWorld().spawnParticle(TRAIL_TYPES[mode], player.getLocation(), 3);
                break;
            case 3:
                ThreadLocalRandom random = ThreadLocalRandom.current();
                player.getWorld().spawnParticle(
                    Particle.REDSTONE,
                    player.getLocation(),
                    1,
                    new Particle.DustOptions(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)), 1.0f)
                );
        }

        Utilities.displayParticle(player.getLocation(), TRAIL_TYPES[mode], 3, 0.1, 0, 0, 0);
        return true;
    }
}
