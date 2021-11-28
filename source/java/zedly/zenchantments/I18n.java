package zedly.zenchantments;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    @NotNull
    public String translate(final @NotNull String key, final @NotNull Object... objects) {
        final var message = this.getMessage(key);

        if (message == null) {
            return "TRANSLATION MISSING";
        }

        var messageFormat = this.messageFormatCache.get(message);
        if (messageFormat == null) {
            messageFormat = new MessageFormat(message);
            this.messageFormatCache.put(message, messageFormat);
        }

        return messageFormat.format(objects);
    }

    @Nullable
    private String getMessage(final @NotNull String key) {
        try {
            return this.localeBundle.getString(key);
        } catch (final MissingResourceException ex) {
            this.plugin.getLogger().log(
                Level.WARNING,
                "Missing translation key '" + key + "' in resource file '" + this.localeBundle.getLocale() + "', using fallback (en)"
            );

            try {
                return FALLBACK_BUNDLE.getString(key);
            } catch (final MissingResourceException somethingsWrongICanFeelIt) {
                // This should REALLY, REALLY never happen. It means something is missing from the English translation file.
                // Let's face it though: it's probably gonna happen, and I'd rather spend an extra minute or two writing the
                // code to handle it than dealing with the inevitable fallout later.
                return null;
            }
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
