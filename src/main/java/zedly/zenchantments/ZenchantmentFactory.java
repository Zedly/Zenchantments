package zedly.zenchantments;

import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Zenchantment.Constructor;
import zedly.zenchantments.enchantments.Anthropomorphism;

import java.util.HashMap;
import java.util.Map;

public class ZenchantmentFactory {
    private static final Map<Class<? extends Zenchantment>, Constructor<? extends Zenchantment>> CONSTRUCTOR_MAP
        = new HashMap<>();

    private final ZenchantmentsPlugin plugin;

    static {
        ZenchantmentFactory.addConstructor(Anthropomorphism.class, Anthropomorphism::new);
    }

    public ZenchantmentFactory(@NotNull ZenchantmentsPlugin plugin) {
        this.plugin = plugin;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public static <T extends Zenchantment> Constructor<T> getConstructor(@NotNull Class<T> zenchantmentClass) {
        return (Constructor<T>) ZenchantmentFactory.CONSTRUCTOR_MAP.get(zenchantmentClass);
    }

    @NotNull
    public <T extends Zenchantment> T createZenchantment(
        @NotNull Class<T> zenchantmentClass,
        @NotNull Tool[] enchantable,
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
        @NotNull Class<T> zenchantmentClass,
        @NotNull Constructor<T> constructor
    ) {
        ZenchantmentFactory.CONSTRUCTOR_MAP.put(zenchantmentClass, constructor);
    }
}