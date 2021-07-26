package zedly.zenchantments.task;

import java.lang.annotation.*;

/**
 * Method annotation used by {@link TaskRunner} to control frequency of execution of scheduled
 * events. Annotations must only be on static methods.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EffectTask {
    Frequency value();
}