package zedly.zenchantments.annotations;

import zedly.zenchantments.TaskRunner;
import zedly.zenchantments.enums.Frequency;

import java.lang.annotation.*;

/**
 * Method annotation used by {@link TaskRunner} to control frequency of execution of scheduled
 * events. Annotations must only be on static methods.
 *
 * @author rfrowe
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EffectTask {
	Frequency value();
}
