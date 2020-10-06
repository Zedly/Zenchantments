package zedly.zenchantments;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Zenchantment.Constructor;
import zedly.zenchantments.enchantments.*;

import java.util.Map;
import java.util.Set;

public class ZenchantmentFactory {
    private static final Map<Class<? extends Zenchantment>, Constructor<? extends Zenchantment>> CONSTRUCTOR_MAP;

    private final ZenchantmentsPlugin plugin;

    static {
        ImmutableMap.Builder<Class<? extends Zenchantment>, Constructor<? extends Zenchantment>> builder = ImmutableMap.builder();
        ZenchantmentFactory.addConstructor(builder, Anthropomorphism.class, Anthropomorphism::new);
        ZenchantmentFactory.addConstructor(builder, Apocalypse.class, Apocalypse::new);
        ZenchantmentFactory.addConstructor(builder, Arborist.class, Arborist::new);
        ZenchantmentFactory.addConstructor(builder, Bind.class, Bind::new);
        ZenchantmentFactory.addConstructor(builder, BlazesCurse.class, BlazesCurse::new);
        ZenchantmentFactory.addConstructor(builder, Blizzard.class, Blizzard::new);
        ZenchantmentFactory.addConstructor(builder, Bounce.class, Bounce::new);
        ZenchantmentFactory.addConstructor(builder, Burst.class, Burst::new);
        ZenchantmentFactory.addConstructor(builder, Combustion.class, Combustion::new);
        ZenchantmentFactory.addConstructor(builder, Conversion.class, Conversion::new);
        ZenchantmentFactory.addConstructor(builder, Decapitation.class, Decapitation::new);
        ZenchantmentFactory.addConstructor(builder, Ethereal.class, Ethereal::new);
        ZenchantmentFactory.addConstructor(builder, Extraction.class, Extraction::new);
        ZenchantmentFactory.addConstructor(builder, Fire.class, Fire::new);
        ZenchantmentFactory.addConstructor(builder, Firestorm.class, Firestorm::new);
        ZenchantmentFactory.addConstructor(builder, Fireworks.class, Fireworks::new);
        ZenchantmentFactory.addConstructor(builder, Force.class, Force::new);
        ZenchantmentFactory.addConstructor(builder, FrozenStep.class, FrozenStep::new);
        ZenchantmentFactory.addConstructor(builder, Fuse.class, Fuse::new);
        ZenchantmentFactory.addConstructor(builder, Germination.class, Germination::new);
        ZenchantmentFactory.addConstructor(builder, Glide.class, Glide::new);
        ZenchantmentFactory.addConstructor(builder, Gluttony.class, Gluttony::new);
        ZenchantmentFactory.addConstructor(builder, GoldRush.class, GoldRush::new);
        ZenchantmentFactory.addConstructor(builder, Grab.class, Grab::new);
        ZenchantmentFactory.addConstructor(builder, GreenThumb.class, GreenThumb::new);
        ZenchantmentFactory.addConstructor(builder, Gust.class, Gust::new);
        ZenchantmentFactory.addConstructor(builder, Harvest.class, Harvest::new);
        ZenchantmentFactory.addConstructor(builder, Haste.class, Haste::new);
        ZenchantmentFactory.addConstructor(builder, IceAspect.class, IceAspect::new);
        ZenchantmentFactory.addConstructor(builder, Jump.class, Jump::new);
        ZenchantmentFactory.addConstructor(builder, Laser.class, Laser::new);
        ZenchantmentFactory.addConstructor(builder, Level.class, Level::new);
        ZenchantmentFactory.addConstructor(builder, LongCast.class, LongCast::new);
        ZenchantmentFactory.addConstructor(builder, Lumber.class, Lumber::new);
        ZenchantmentFactory.addConstructor(builder, Magnetism.class, Magnetism::new);
        ZenchantmentFactory.addConstructor(builder, Meador.class, Meador::new);
        ZenchantmentFactory.addConstructor(builder, Missile.class, Missile::new);
        ZenchantmentFactory.addConstructor(builder, Mow.class, Mow::new);
        ZenchantmentFactory.addConstructor(builder, MysteryFish.class, MysteryFish::new);
        ZenchantmentFactory.addConstructor(builder, NetherStep.class, NetherStep::new);
        ZenchantmentFactory.addConstructor(builder, NightVision.class, NightVision::new);
        ZenchantmentFactory.addConstructor(builder, Persephone.class, Persephone::new);
        ZenchantmentFactory.addConstructor(builder, Pierce.class, Pierce::new);
        ZenchantmentFactory.addConstructor(builder, Plough.class, Plough::new);
        ZenchantmentFactory.addConstructor(builder, Potion.class, Potion::new);
        ZenchantmentFactory.addConstructor(builder, PotionResistance.class, PotionResistance::new);
        ZenchantmentFactory.addConstructor(builder, QuickShot.class, QuickShot::new);
        ZenchantmentFactory.addConstructor(builder, Rainbow.class, Rainbow::new);
        ZenchantmentFactory.addConstructor(builder, RainbowSlam.class, RainbowSlam::new);
        ZenchantmentFactory.addConstructor(builder, Reaper.class, Reaper::new);
        ZenchantmentFactory.addConstructor(builder, Reveal.class, Reveal::new);
        ZenchantmentFactory.addConstructor(builder, Saturation.class, Saturation::new);
        ZenchantmentFactory.addConstructor(builder, ShortCast.class, ShortCast::new);
        ZenchantmentFactory.addConstructor(builder, Shred.class, Shred::new);
        ZenchantmentFactory.addConstructor(builder, Singularity.class, Singularity::new);
        ZenchantmentFactory.addConstructor(builder, Siphon.class, Siphon::new);
        ZenchantmentFactory.addConstructor(builder, SonicShock.class, SonicShock::new);
        ZenchantmentFactory.addConstructor(builder, Spectral.class, Spectral::new);
        ZenchantmentFactory.addConstructor(builder, Speed.class, Speed::new);
        ZenchantmentFactory.addConstructor(builder, Spikes.class, Spikes::new);
        ZenchantmentFactory.addConstructor(builder, Spread.class, Spread::new);
        ZenchantmentFactory.addConstructor(builder, Stationary.class, Stationary::new);
        ZenchantmentFactory.addConstructor(builder, Stock.class, Stock::new);
        ZenchantmentFactory.addConstructor(builder, Stream.class, Stream::new);
        ZenchantmentFactory.addConstructor(builder, Switch.class, Switch::new);
        ZenchantmentFactory.addConstructor(builder, Terraformer.class, Terraformer::new);
        ZenchantmentFactory.addConstructor(builder, Toxic.class, Toxic::new);
        ZenchantmentFactory.addConstructor(builder, Tracer.class, Tracer::new);
        ZenchantmentFactory.addConstructor(builder, Transformation.class, Transformation::new);
        ZenchantmentFactory.addConstructor(builder, Unrepairable.class, Unrepairable::new);
        ZenchantmentFactory.addConstructor(builder, Variety.class, Variety::new);
        ZenchantmentFactory.addConstructor(builder, Vortex.class, Vortex::new);
        ZenchantmentFactory.addConstructor(builder, Weight.class, Weight::new);

        CONSTRUCTOR_MAP = builder.build();
    }

    public ZenchantmentFactory(@NotNull ZenchantmentsPlugin plugin) {
        this.plugin = plugin;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public static <T extends Zenchantment> Constructor<T> getConstructor(@NotNull Class<T> zenchantmentClass) {
        return (Constructor<T>) CONSTRUCTOR_MAP.get(zenchantmentClass);
    }

    @NotNull
    public <T extends Zenchantment> T createZenchantment(
        @NotNull Class<T> zenchantmentClass,
        @NotNull Set<Tool> enchantable,
        int maxLevel,
        int cooldown,
        double probability,
        float power
    ) {
        return ZenchantmentFactory
            .getConstructor(zenchantmentClass)
            .construct(
                this.plugin,
                enchantable,
                maxLevel,
                cooldown,
                probability,
                power
            );
    }

    // This method allows the compiler to ensure that 'constructor' returns an instance of 'zenchantmentClass'.
    // CONSTRUCTOR_MAP.put() by itself does not ensure that the generic types are the same, so always use this method!
    private static <T extends Zenchantment> void addConstructor(
        @NotNull ImmutableMap.Builder<Class<? extends Zenchantment>, Constructor<? extends Zenchantment>> builder,
        @NotNull Class<T> zenchantmentClass,
        @NotNull Constructor<T> constructor
    ) {
        builder.put(zenchantmentClass, constructor);
    }
}