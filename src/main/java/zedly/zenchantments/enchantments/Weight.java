package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;

import java.util.Set;

import static org.bukkit.potion.PotionEffectType.INCREASE_DAMAGE;

public class Weight extends Zenchantment {
    public static final String KEY = "weight";

    private static final String                             NAME        = "Weight";
    private static final String                             DESCRIPTION = "Slows the player down but makes them stronger and more resistant to knockback";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of(Meador.class, Speed.class);
    private static final Hand                               HAND_USE    = Hand.NONE;

    private final NamespacedKey key;

    public Weight(
        @NotNull ZenchantmentsPlugin plugin,
        @NotNull Set<Tool> enchantable,
        int maxLevel,
        int cooldown,
        double power,
        float probability
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
    public boolean onBeingHit(@NotNull EntityDamageByEntityEvent event, int level, boolean usedHand) {
        Player player = (Player) event.getEntity();

        if (!(event.getDamage() < player.getHealth())) {
            return true;
        }

        // Cancel event to prevent knockback, damage the player anyway.
        // There might be a much better way to do this.

        event.setCancelled(true);
        player.damage(event.getDamage());
        player.setVelocity(
            player.getLocation()
                .subtract(event.getDamager().getLocation())
                .toVector()
                .multiply((float) (1 / (level * this.getPower() + 1.5)))
        );

        ItemStack[] armour = player.getInventory().getArmorContents();

        for (int i = 0; i < 4; i++) {
            if (armour[i] != null) {
                Utilities.addUnbreaking(player, armour[i], 1);
                if (Utilities.getDamage(armour[i]) > armour[i].getType().getMaxDurability()) {
                    armour[i] = null;
                }
            }
        }

        player.getInventory().setArmorContents(armour);

        return true;
    }

    @Override
    public boolean onScan(@NotNull Player player, int level, boolean usedHand) {
        Utilities.addPotion(player, INCREASE_DAMAGE, 610, (int) Math.round(this.getPower() * level));
        player.setWalkSpeed((float) (0.164f - level * this.getPower() * 0.014f));
        player.setMetadata("ze.speed", new FixedMetadataValue(this.getPlugin(), System.currentTimeMillis()));
        return true;
    }
}