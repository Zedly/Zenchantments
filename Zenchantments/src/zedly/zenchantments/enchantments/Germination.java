package zedly.zenchantments.enchantments;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import static org.bukkit.GameMode.CREATIVE;
import static org.bukkit.Material.INK_SACK;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import static zedly.zenchantments.enums.Tool.HOE;

public class Germination extends CustomEnchantment {

    private static final ItemStack BONE_MEAL = new ItemStack(Material.INK_SACK, 1, (short) 15);

    public Germination() {
        super(19);
        maxLevel = 3;
        loreName = "Germination";
        probability = 0;
        enchantable = new Tool[]{HOE};
        conflicting = new Class[]{};
        description = "Uses bonemeal from the player's inventory to grow nearby plants";
        cooldown = 0;
        power = 1.0;
        handUse = Hand.RIGHT;
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        if(evt.getAction() != RIGHT_CLICK_BLOCK) {
            return false;
        }
        Player player = evt.getPlayer();
        Location loc = evt.getClickedBlock().getLocation();
        Block clickedBlock = evt.getClickedBlock();
        int radiusXZ = (int) Math.round(power * level + 2);
        int radiusY = 2;
        boolean applied = false;
        for(int x = -(radiusXZ); x <= radiusXZ; x++) {
            for(int y = -(radiusY) - 1; y <= radiusY - 1; y++) {
                for(int z = -(radiusXZ); z <= radiusXZ; z++) {
                    Block relativeBlock = clickedBlock.getRelative(x, y, z);
                    if(relativeBlock.getLocation().distanceSquared(loc) < radiusXZ * radiusXZ
                       && player.getInventory().containsAtLeast(BONE_MEAL, 1)
                       && ADAPTER.grow(relativeBlock, player)) {
                        applied = true;
                        if(Storage.rnd.nextBoolean()) {
                            ADAPTER.grow(relativeBlock, player);
                        }
                        Utilities.display(Utilities.getCenter(relativeBlock), Particle.VILLAGER_HAPPY, 30, 1f, .3f,
                                          .3f, .3f);
                        if(Storage.rnd.nextInt(10) <= 3) {
                            Utilities.damageTool(player, 1, usedHand);
                        }
                        if(!player.getGameMode().equals(CREATIVE)) {
                            Utilities.removeItem(player, INK_SACK, (short) 15, 1);
                        }
                    }
                }
            }
        }
        return applied;
    }
}
