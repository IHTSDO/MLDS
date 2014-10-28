package ca.intelliware.ihtsdo.mlds.search;

import java.util.Locale;

import org.hibernate.search.bridge.StringBridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dumb mapping of our enums to search, assuming English.
 * 
 * Uses the client-side translations to generate search text.
 */
abstract public class TranslatedEnumFieldBridge implements StringBridge {
    final Logger log = LoggerFactory.getLogger(TranslatedEnumFieldBridge.class);

	public TranslatedEnumFieldBridge() {
	}
	
	@Override
	public String objectToString(Object object) {
		if (object == null) {
			return null;
		}
		if (object instanceof Enum<?>) {
			Enum<?> affiliateType = (Enum<?>) object;
			String path = getBundleKeyPrefix() + affiliateType.name();
			AngularTranslateService translateInstance = AngularTranslateService.getInstance();
			return translateInstance.lookup(Locale.ENGLISH,path);
		} else {
			log.error("objectToString() {} {}", object.getClass().getName(), object);
			return null;
		}
	}

	abstract protected String getBundleKeyPrefix();
}
