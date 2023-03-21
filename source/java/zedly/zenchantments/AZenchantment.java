package zedly.zenchantments;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AZenchantment {
    Slots runInSlots();
    Class<? extends Zenchantment>[] conflicting();
}
