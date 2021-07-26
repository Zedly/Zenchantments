package zedly.zenchantments.task;

import zedly.zenchantments.ZenchantmentFactory;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.arrows.ZenchantedArrow;
import zedly.zenchantments.event.listener.ZenchantmentListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

/**
 * A runnable class that will execute all events of the specified frequency.
 */
public class TaskRunner implements Runnable {
    private static final Set<Class<?>> TASK_CLASSES = new HashSet<>();

    private final ZenchantmentsPlugin plugin;
    private final Set<Method>         tasks;

    static {
        TASK_CLASSES.addAll(ZenchantmentFactory.getZenchantmentClasses());
        TASK_CLASSES.add(ZenchantmentListener.class);
        TASK_CLASSES.add(ZenchantedArrow.class);
    }

    /**
     * Initializes this EventRunner by collecting all methods with an
     * {@link EffectTask} annotation of the specified frequency.
     *
     * @param frequency
     *     The frequency of annotation that we'll be running.
     */
    public TaskRunner(ZenchantmentsPlugin plugin, Frequency frequency) {
        this.plugin = plugin;
        this.tasks = new HashSet<>();

        for (final Class<?> taskClass : TASK_CLASSES) {
            // Use getDeclaredMethods over getMethods to include private methods.
            for (final Method method : taskClass.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(EffectTask.class)) {
                    // Skip all methods without @EffectTask annotation.
                    continue;
                }

                if (!Modifier.isStatic(method.getModifiers())) {
                    this.plugin.getLogger().warning(
                        "EffectTask on non-static method '" + method.getName() + "' in class '" + taskClass.getName() + "'."
                    );
                    continue;
                }

                if (method.getParameterCount() != 1 || method.getParameterTypes()[0] != ZenchantmentsPlugin.class) {
                    this.plugin.getLogger().warning("EffectTask method doesn't have one parameter type of ZenchantmentsPlugin.");
                    continue;
                }

                if (method.getAnnotation(EffectTask.class).value() == frequency) {
                    tasks.add(method);
                }
            }
        }
    }

    /**
     * Runs all methods on subclasses of CustomEnchantment that are annotated
     * with {@link EffectTask} and have the same event frequency as this
     * EventRunner.
     *
     * @see Frequency
     */
    @Override
    public void run() {
        for (Method method : this.tasks) {
            try {
                method.invoke(null, this.plugin);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                this.plugin.getLogger().log(Level.SEVERE, "Could not invoke EffectTask '" + method.getName() + "'.", ex);
            }
        }
    }
}
