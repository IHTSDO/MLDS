package ca.intelliware.ihtsdo.mlds.search;

import java.util.Locale;

import jakarta.persistence.AttributeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dumb mapping of our enums to search, assuming English.
 *
 * Uses the client-side translations to generate search text.
 */
public abstract class TranslatedEnumFieldBridge implements AttributeConverter<Enum<?>, String> {
    final Logger log = LoggerFactory.getLogger(TranslatedEnumFieldBridge.class);

    protected TranslatedEnumFieldBridge() {
    }

    @Override
    public String convertToDatabaseColumn(Enum<?> attribute) {
        if (attribute == null) {
            return null;
        }
        String path = getBundleKeyPrefix() + attribute.name();
        AngularTranslateService translateInstance = AngularTranslateService.getInstance();
        return translateInstance.lookup(Locale.ENGLISH, path);
    }

    @Override
    public Enum<?> convertToEntityAttribute(String dbData) {
        // Since it's not feasible to convert the string back to the enum
        // using translations, this method might not be needed.
        // You can throw an UnsupportedOperationException or handle it accordingly.
        log.error("convertToEntityAttribute() is not supported in this context.");
        throw new UnsupportedOperationException("Conversion from String to Enum is not supported.");
    }

    protected abstract String getBundleKeyPrefix();
}
