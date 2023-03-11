package zedly.zenchantments;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Zenchantment.Constructor;
import zedly.zenchantments.enchantments.*;

import java.util.Map;
import java.util.Set;

public class ZenchantmentFactory {
    private static final Map<Class<? extends Zenchantment>, Constructor<? extends Zenchantment>> CONSTRUCTOR_MAP;

    private final ZenchantmentsPlugin plugin;

    static {
        final Builder<Class<? extends Zenchantment>, Constructor<? extends Zenchantment>> builder = ImmutableMap.builder();

        addConstructor(builder, Anthropomorphism.class, Anthropomorphism::new);
        addConstructor(builder, Apocalypse.class, Apocalypse::new);
        addConstructor(builder, Arborist.class, Arborist::new);
        addConstructor(builder, Bind.class, Bind::new);
        addConstructor(builder, Blanket.class, Blanket::new);
        addConstructor(builder, BlazesCurse.class, BlazesCurse::new);
        addConstructor(builder, Blizzard.class, Blizzard::new);
        addConstructor(builder, Bounce.class, Bounce::new);
        addConstructor(builder, Burst.class, Burst::new);
        addConstructor(builder, Combustion.class, Combustion::new);
        addConstructor(builder, Caffeine.class, Caffeine::new);
        addConstructor(builder, Chitin.class, Chitin::new);
        addConstructor(builder, Conversion.class, Conversion::new);
        addConstructor(builder, Decapitation.class, Decapitation::new);
        addConstructor(builder, Ethereal.class, Ethereal::new);
        addConstructor(builder, Fire.class, Fire::new);
        addConstructor(builder, Firestorm.class, Firestorm::new);
        addConstructor(builder, Fireworks.class, Fireworks::new);
        addConstructor(builder, Force.class, Force::new);
        addConstructor(builder, Fuse.class, Fuse::new);
        addConstructor(builder, Germination.class, Germination::new);
        addConstructor(builder, Glide.class, Glide::new);
        addConstructor(builder, Gluttony.class, Gluttony::new);
        addConstructor(builder, GoldRush.class, GoldRush::new);
        addConstructor(builder, Grab.class, Grab::new);
        addConstructor(builder, GreenThumb.class, GreenThumb::new);
        addConstructor(builder, Harvest.class, Harvest::new);
        addConstructor(builder, Haste.class, Haste::new);
        addConstructor(builder, HelpingHand.class, HelpingHand::new);
        addConstructor(builder, IceAspect.class, IceAspect::new);
        addConstructor(builder, Jump.class, Jump::new);
        addConstructor(builder, Laser.class, Laser::new);
        addConstructor(builder, Level.class, Level::new);
        addConstructor(builder, LongCast.class, LongCast::new);
        addConstructor(builder, Lumber.class, Lumber::new);
        addConstructor(builder, Magnetism.class, Magnetism::new);
        addConstructor(builder, MasterKey.class, MasterKey::new);
        addConstructor(builder, Meador.class, Meador::new);
        addConstructor(builder, Missile.class, Missile::new);
        addConstructor(builder, Mow.class, Mow::new);
        addConstructor(builder, MysteryFish.class, MysteryFish::new);
        addConstructor(builder, NetherStep.class, NetherStep::new);
        addConstructor(builder, NightVision.class, NightVision::new);
        addConstructor(builder, Persephone.class, Persephone::new);
        addConstructor(builder, Pierce.class, Pierce::new);
        addConstructor(builder, Plough.class, Plough::new);
        addConstructor(builder, Potion.class, Potion::new);
        addConstructor(builder, PotionResistance.class, PotionResistance::new);
        addConstructor(builder, Quake.class, Quake::new);
        addConstructor(builder, QuickShot.class, QuickShot::new);
        addConstructor(builder, Rainbow.class, Rainbow::new);
        addConstructor(builder, RainbowSlam.class, RainbowSlam::new);
        addConstructor(builder, Reaper.class, Reaper::new);
        addConstructor(builder, Reveal.class, Reveal::new);
        addConstructor(builder, Saturation.class, Saturation::new);
        addConstructor(builder, ShortCast.class, ShortCast::new);
        addConstructor(builder, Shred.class, Shred::new);
        addConstructor(builder, Singularity.class, Singularity::new);
        addConstructor(builder, Siphon.class, Siphon::new);
        addConstructor(builder, SonicShock.class, SonicShock::new);
        addConstructor(builder, Spectral.class, Spectral::new);
        addConstructor(builder, Speed.class, Speed::new);
        addConstructor(builder, Spikes.class, Spikes::new);
        addConstructor(builder, Stationary.class, Stationary::new);
        addConstructor(builder, Stock.class, Stock::new);
        addConstructor(builder, Stream.class, Stream::new);
        addConstructor(builder, Switch.class, Switch::new);
        addConstructor(builder, Terraformer.class, Terraformer::new);
        addConstructor(builder, Toxic.class, Toxic::new);
        addConstructor(builder, Tracer.class, Tracer::new);
        addConstructor(builder, Transformation.class, Transformation::new);
        addConstructor(builder, Unrepairable.class, Unrepairable::new);
        addConstructor(builder, Variety.class, Variety::new);
        addConstructor(builder, Vortex.class, Vortex::new);
        addConstructor(builder, Weight.class, Weight::new);

        CONSTRUCTOR_MAP = builder.build();
    }

    public ZenchantmentFactory(final @NotNull ZenchantmentsPlugin plugin) {
        this.plugin = plugin;
    }

    @NotNull
    public static Set<Class<? extends Zenchantment>> getZenchantmentClasses() {
        return CONSTRUCTOR_MAP.keySet();
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public static <T extends Zenchantment> Constructor<T> getConstructor(final @NotNull Class<T> zenchantmentClass) {
        return (Constructor<T>) CONSTRUCTOR_MAP.get(zenchantmentClass);
    }

    @NotNull
    public <T extends Zenchantment> T createZenchantment(
        final @NotNull Class<T> zenchantmentClass,
        final @NotNull Set<Tool> enchantable,
        final int maxLevel,
        final int cooldown,
        final double probability,
        final float power
    ) {
        return getConstructor(zenchantmentClass).construct(
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
        final @NotNull Builder<Class<? extends Zenchantment>, Constructor<? extends Zenchantment>> builder,
        final @NotNull Class<T> zenchantmentClass,
        final @NotNull Constructor<T> constructor
    ) {
        builder.put(zenchantmentClass, constructor);
    }
}
