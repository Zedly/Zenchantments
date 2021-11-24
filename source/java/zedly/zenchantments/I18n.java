package zedly.zenchantments;

import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;

public class I18n {
    private final Map<String, MessageFormat> messageFormatCache = new HashMap<>();
    private final ZenchantmentsPlugin plugin;

    private ResourceBundle fallbackBundle;
    private ResourceBundle localeBundle;

    public I18n(final @NotNull ZenchantmentsPlugin plugin) {
        this.plugin = plugin;
        this.fallbackBundle = ResourceBundle.getBundle("strings", Locale.ENGLISH);
        this.localeBundle = this.fallbackBundle;
    }

    public String translate(final @NotNull String key, final @NotNull Object... objects) {
        final var message = this.getMessage(key);

        var messageFormat = this.messageFormatCache.get(message);
        if (messageFormat == null) {
            messageFormat = new MessageFormat(message);
            this.messageFormatCache.put(message, messageFormat);
        }

        return messageFormat.format(objects);
    }

    private String getMessage(final @NotNull String key) {
        try {
            return this.localeBundle.getString(key);
        } catch (final MissingResourceException ex) {
            this.plugin.getLogger().log(
                Level.WARNING,
                "Missing translation key '" + key + "' in resource file '" + this.localeBundle.getLocale() + "'"
            );
            return this.fallbackBundle.getString(key);
        }
    }
}
