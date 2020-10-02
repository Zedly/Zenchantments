package zedly.zenchantments.enchantments;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import zedly.zenchantments.Zenchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.Hand;
import zedly.zenchantments.Tool;

import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import static zedly.zenchantments.Tool.HOE;

public class Germination extends Zenchantment {

    public static final int ID = 19;

    @Override
    public Builder<Germination> defaults() {
        return new Builder<>(Germination::new, ID)
                .maxLevel(3)
                .loreName("Germination")
                .probability(0)
                .enchantable(new Tool[]{HOE})
                .conflicting(new Class[]{})
                .description("Uses bonemeal from the player's inventory to grow nearby plants")
                .cooldown(0)
                .power(1.0)
                .handUse(Hand.RIGHT);
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent event, int level, boolean usedHand) {
        if (event.getAction() != RIGHT_CLICK_BLOCK) {
            return false;
        }
        Player player = event.getPlayer();
        Location loc = event.getClickedBlock().getLocation();
        Block clickedBlock = event.getClickedBlock();
        int radiusXZ = (int) Math.round(power * level + 2);
        int radiusY = 2;
        boolean applied = false;
        for (int x = -(radiusXZ); x <= radiusXZ; x++) {
            for (int y = -(radiusY) - 1; y <= radiusY - 1; y++) {
                for (int z = -(radiusXZ); z <= radiusXZ; z++) {

                    Block relativeBlock = clickedBlock.getRelative(x, y, z);
                    if (relativeBlock.getLocation().distanceSquared(loc) < radiusXZ * radiusXZ
                            && Utilities.hasItem(player, Material.BONE_MEAL, 1)
                            && ADAPTER.grow(relativeBlock, player)) {

                        applied = true;
                        if (Storage.rnd.nextBoolean()) {
                            ADAPTER.grow(relativeBlock, player);
                        }

                        Utilities.display(Utilities.getCenter(relativeBlock), Particle.VILLAGER_HAPPY, 30, 1f, .3f,
                                .3f,
                                .3f);

                        if (Storage.rnd.nextInt(10) <= 3) {
                            Utilities.damageTool(player, 1, usedHand);
                        }
                        Utilities.removeItem(player, Material.BONE_MEAL, 1);
                    }
                }
            }
        }
        return applied;
    }
}
