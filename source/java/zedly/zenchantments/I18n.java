package zedly.zenchantments;

import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;

public class I18n {
    private static final ResourceBundle FALLBACK_BUNDLE = ResourceBundle.getBundle("strings", Locale.ENGLISH);

    private final Map<String, MessageFormat> messageFormatCache = new HashMap<>();
    private final ZenchantmentsPlugin plugin;

    private ResourceBundle localeBundle = FALLBACK_BUNDLE;

    public I18n(final @NotNull ZenchantmentsPlugin plugin) {
        this.plugin = plugin;
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
                "Missing translation key '" + key + "' in resource file '" + this.localeBundle.getLocale() + "', using fallback (en)"
            );
            return FALLBACK_BUNDLE.getString(key);
        }
    }

    public void updateLocale(final @NotNull Locale locale) {
        ResourceBundle.clearCache();
        this.messageFormatCache.clear();

        this.plugin.getLogger().log(Level.INFO, "Switching to locale " + locale);

        try {
            this.localeBundle = ResourceBundle.getBundle("strings", locale);
        } catch (final MissingResourceException ex) {
            this.plugin.getLogger().log(Level.WARNING, "Failed to load strings for the locale, using fallback (en)");
            this.localeBundle = FALLBACK_BUNDLE;
        }
    }
}
