package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.arrows.MultiArrow;
import zedly.zenchantments.arrows.ZenchantedArrow;
import zedly.zenchantments.event.ZenEntityShootBowEvent;

import java.util.Collection;
import java.util.Set;

import static org.bukkit.Material.ARROW;

public final class Burst extends Zenchantment {
    public static final String KEY = "burst";

    private static final String NAME = "Burst";
    private static final String DESCRIPTION = "Rapidly fires arrows in series";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();

    private final NamespacedKey key;

    public Burst(
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
    public Collection<EquipmentSlot> getApplyToSlots() {
        return Slots.HANDS;
    }

    @Override
    public boolean onEntityShootBow(final @NotNull EntityShootBowEvent event, final int level, final EquipmentSlot slot) {
        if (event instanceof ZenEntityShootBowEvent) {
            return false;
        }

        if (!(event.getProjectile() instanceof AbstractArrow)) {
            return false;
        }

        final Player player = (Player) event.getEntity();
        final ItemStack itemInHand = player.getInventory().getItem(slot);
        AbstractArrow originalArrow = (AbstractArrow) event.getProjectile();

        boolean hasInfinity = player.getGameMode() == GameMode.CREATIVE || itemInHand.containsEnchantment(Enchantment.ARROW_INFINITE);

        // We subtract 1 because this happens before the NMS code removes an arrow
        int maxArrows = hasInfinity ? 64 : Utilities.countItems(player.getInventory(), (i) -> i != null && i.getType() == ARROW) - 1;
        maxArrows = Math.min(maxArrows, (int) Math.round((this.getPower() * level) + 1));

        if(itemInHand.getType() == Material.CROSSBOW) {
            if(itemInHand.getEnchantmentLevel(Enchantment.MULTISHOT) != 0) {
                maxArrows /= 3;
            }
        }


        int unbreakingLevel = Utilities.getUnbreakingLevel(itemInHand);
        int remainingDurability = Utilities.getUsesRemainingOnTool(itemInHand);
        int shotArrows = 0;
        int appliedDamage = 0;
        for (int i = 0; i < maxArrows && remainingDurability > appliedDamage; i++) {
            shotArrows++;
            if (Utilities.decideRandomlyIfDamageToolRespectUnbreaking(unbreakingLevel)) {
                appliedDamage++;
            }
        }

        ZenchantedArrow.addZenchantedArrowToArrowEntity(originalArrow, new MultiArrow(originalArrow), player);
        for (int i = 0; i < shotArrows; i++) {
            //player.getInventory().setItem(slot, itemInHand);
            ZenchantmentsPlugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(ZenchantmentsPlugin.getInstance(), () -> {
                shootMultiArrow(player, originalArrow, itemInHand, event.getConsumable(), slot);
            }, i * 2L);
        }

        if (!hasInfinity) {
            // Apparently NMS forgets to remove arrows because we interfere with the inventory
            Utilities.removeMaterialsFromPlayer(player, ARROW, shotArrows + 1);
        }

        final int finalAppliedDamage = appliedDamage;
        Bukkit.getScheduler().scheduleSyncDelayedTask(ZenchantmentsPlugin.getInstance(), () ->
        Utilities.damageItemStackIgnoreUnbreaking(player, finalAppliedDamage, slot), 0);

        return maxArrows > 0;
    }

    private void shootMultiArrow(Player player, AbstractArrow originalArrow, ItemStack itemInHand, ItemStack consumable, EquipmentSlot slot) {
        float velocity = (float) originalArrow.getVelocity().length();
        boolean critical = originalArrow.isCritical();


        final AbstractArrow arrow = player.getWorld().spawnArrow(
            player.getEyeLocation(),
            player.getLocation().getDirection(),
            velocity,
            12
        );

        arrow.setShooter(player);

        if (itemInHand.containsEnchantment(Enchantment.ARROW_FIRE)) {
            arrow.setFireTicks(Integer.MAX_VALUE);
        }


        // Some of the parameters below have been added since this class was last updated.
        // This zenchantment may need more testing to determine whether or not it still works properly.
        final EntityShootBowEvent shootEvent = new ZenEntityShootBowEvent(
            player,
            itemInHand,
            consumable,
            arrow,
            slot,
            1f,
            false
        );

        final ProjectileLaunchEvent launchEvent = new ProjectileLaunchEvent(arrow);

        ZenchantmentsPlugin.getInstance().getServer().getPluginManager().callEvent(shootEvent);
        if (!shootEvent.isCancelled()) {
            ZenchantmentsPlugin.getInstance().getServer().getPluginManager().callEvent(launchEvent);
        }

        if (shootEvent.isCancelled() || launchEvent.isCancelled()) {
            arrow.remove();
        } else {
            arrow.setCritical(critical);
            arrow.setPickupStatus(AbstractArrow.PickupStatus.ALLOWED);
            ZenchantedArrow.addZenchantedArrowToArrowEntity(arrow, new MultiArrow(arrow), player);
        }
    }
}
