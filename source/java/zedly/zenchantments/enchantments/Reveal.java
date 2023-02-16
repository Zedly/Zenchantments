package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class Reveal extends Zenchantment {
    public static final String KEY = "reveal";

    public static final Map<Block, Integer> GLOWING_BLOCKS = new HashMap<>();

    private static final String                             NAME        = "Reveal";
    private static final String                             DESCRIPTION = "Makes nearby ores glow white through the stone";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of(Switch.class, Pierce.class, Spectral.class);
    private static final Hand                               HAND_USE    = Hand.NONE;

    private final NamespacedKey key;

    public Reveal(
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
    public boolean onBlockInteract(final @NotNull PlayerInteractEvent event, final int level, final boolean usedHand) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }

        final Player player = event.getPlayer();

        if (!player.isSneaking()) {
            return false;
        }

        final int radius = (int) Math.max(2, Math.round((2 + level) * this.getPower()));

        int found = 0;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    final Block block = player.getLocation().getBlock().getRelative(x, y, z);

                    if (!MaterialList.ORES.contains(block.getType())) {
                        continue;
                    }

                    boolean exposed = false;

                    for (final BlockFace face : Utilities.CARDINAL_BLOCK_FACES) {
                        if (MaterialList.AIR.contains(block.getRelative(face).getType())) {
                            exposed = true;
                        }
                    }

                    if (exposed) {
                        continue;
                    }

                    found++;

                    final int entityId = 2000000000 + (block.hashCode()) % 10000000;

                    if (GLOWING_BLOCKS.containsKey(block)) {
                        GLOWING_BLOCKS.put(block, GLOWING_BLOCKS.get(block) + 1);
                    } else {
                        GLOWING_BLOCKS.put(block, 1);
                    }

                    if (!CompatibilityAdapter.instance().showShulker(block, entityId, player)) {
                        return false;
                    }

                    ZenchantmentsPlugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(ZenchantmentsPlugin.getInstance(), () -> {
                        CompatibilityAdapter.instance().hideShulker(entityId, player);
                        if (GLOWING_BLOCKS.containsKey(block) && GLOWING_BLOCKS.get(block) > 1) {
                            GLOWING_BLOCKS.put(block, GLOWING_BLOCKS.get(block) - 1);
                        } else {
                            GLOWING_BLOCKS.remove(block);
                        }
                    }, 100);
                }
            }
        }

        Utilities.damageItemStackRespectUnbreaking(player, Math.max(16, (int) Math.round(found * 1.3)), usedHand);
        return true;
    }
}
