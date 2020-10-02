package zedly.zenchantments.task;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import org.bukkit.Bukkit;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Zenchantments;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A runnable class that will execute all events of the specified frequency.
 */
public class TaskRunner implements Runnable {
    private final Set<Method> tasks;
    private final Logger      logger;

    /**
     * Initializes this EventRunner by collecting all methods with an
     * {@link EffectTask} annotation of the specified frequency.
     *
     * @param frequency The frequency of annotation that we'll be running.
     */
    public TaskRunner(Frequency frequency) {
        this.logger = Bukkit.getLogger();
        this.tasks = new HashSet<>();

        new FastClasspathScanner(Zenchantments.class.getPackage().getName())
            .overrideClasspath(Storage.pluginPath)
            .matchClassesWithMethodAnnotation(
                EffectTask.class,
                (clazz, method) -> {
                    if (!Modifier.isStatic(method.getModifiers())) {
                        this.logger.warning(
                            "EffectTask on non-static method '" + method.getName() + "' in class '" + clazz.getName() + "'."
                        );
                    }

                    if (method.getAnnotation(EffectTask.class).value() == frequency) {
                        if (!(method instanceof Method)) {
                            throw new IllegalStateException("EffectTask annotation not valid on constructors.");
                        }

                        tasks.add((Method) method);
                    }
                }
            )
            .scan();
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
                method.invoke(null);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                this.logger.log(
                    Level.SEVERE,
                    "Could not invoke event '" + method.getName() + "' due to " + ex.getCause(),
                    ex
                );
                ex.printStackTrace();
            }
        }
    }
}