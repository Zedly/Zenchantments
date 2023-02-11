package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableSet;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerFishEvent;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.*;
import zedly.zenchantments.task.EffectTask;
import zedly.zenchantments.task.Frequency;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.entity.EntityType.*;

public final class MysteryFish extends Zenchantment {
    public static final String KEY = "mystery_fish";

    private static final String                             NAME        = "Mystery Fish";
    private static final String                             DESCRIPTION = "Catches water mobs like Squid and Guardians";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.RIGHT;

    private static final Map<Entity, Player> ENTITIES_ATTRACTED_TO_PLAYERS = new HashMap<>();

    private static final Map<Integer, EntityType> MYSTERY_SPAWN_RATES = new LinkedHashMap<>();
    private static final int RANDOM_RANGE;

    private final NamespacedKey key;

    public MysteryFish(
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
    public boolean onPlayerFish(final @NotNull PlayerFishEvent event, final int level, final boolean usedHand) {
        if(event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return true;
        }

        if (!(ThreadLocalRandom.current().nextInt(10) < level * this.getPower())) {
            return true;
        }

        event.getCaught().remove();

        final Location location = event.getCaught().getLocation();
        EntityType mysteryMobType = chooseWeightedRandomEntityType();
        Entity ent = event.getPlayer().getWorld().spawnEntity(location, mysteryMobType);
        switch(mysteryMobType) {
            case AXOLOTL:
                ((Axolotl) ent).setVariant(Utilities.randomOfEnum(Axolotl.Variant.class));
                break;
            case TROPICAL_FISH:
                TropicalFish f = (TropicalFish) ent;
                f.setPattern(Utilities.randomOfEnum(TropicalFish.Pattern.class));
                f.setPatternColor(Utilities.randomOfEnum(DyeColor.class));
                f.setBodyColor(Utilities.randomOfEnum(DyeColor.class));
                break;
        }

        ENTITIES_ATTRACTED_TO_PLAYERS.put(ent, event.getPlayer());
        return true;
    }

    @EffectTask(Frequency.HIGH)
    public static void moveGuardians(final @NotNull ZenchantmentsPlugin plugin) {
        final Iterator<Entity> iterator = ENTITIES_ATTRACTED_TO_PLAYERS.keySet().iterator();
        while (iterator.hasNext()) {
            final Entity ent = iterator.next();
            final Player player = ENTITIES_ATTRACTED_TO_PLAYERS.get(ent);
            if (ent.getLocation().distance(player.getLocation()) > 2 && ent.getTicksLived() < 160) {
                ent.setVelocity(player.getLocation().toVector().subtract(ent.getLocation().toVector()).normalize());
            } else {
                iterator.remove();
            }
        }
    }

    private EntityType chooseWeightedRandomEntityType()  {
        int randomInt = ThreadLocalRandom.current().nextInt(RANDOM_RANGE);
        for(int range : MYSTERY_SPAWN_RATES.keySet()) {
            if(randomInt < range) {
                return MYSTERY_SPAWN_RATES.get(range);
            }
        }
        return EntityType.SQUID; // Should never happen but is a convenient inoffensive fallback
    }

    static {
        // TODO: Replace this with configurable values, add up each weight in the loop (make sure each value is >= 0)

        MYSTERY_SPAWN_RATES.put(30, SQUID);
        MYSTERY_SPAWN_RATES.put(40, COD);
        MYSTERY_SPAWN_RATES.put(50, SALMON);
        MYSTERY_SPAWN_RATES.put(60, PUFFERFISH);
        MYSTERY_SPAWN_RATES.put(70, TROPICAL_FISH);
        MYSTERY_SPAWN_RATES.put(80, GUARDIAN);
        MYSTERY_SPAWN_RATES.put(85, ELDER_GUARDIAN);
        MYSTERY_SPAWN_RATES.put(89, AXOLOTL);
        MYSTERY_SPAWN_RATES.put(93, GLOW_SQUID);
        MYSTERY_SPAWN_RATES.put(97, DOLPHIN);
        MYSTERY_SPAWN_RATES.put(100, TURTLE);

        RANDOM_RANGE = 100;
    }
}
