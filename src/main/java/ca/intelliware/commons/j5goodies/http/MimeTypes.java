package ca.intelliware.commons.j5goodies.http;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>This class encapsulates some knowledges about various common Mime types used 
 * in web applications and other places.  It includes, for example, constants for 
 * Mime Types such as XML ("text/xml") or Microsoft Excel ("application/vnd.ms-excel").
 * The intention is to hold these in one spot.
 * 
 * @author administrator
 */
public class MimeTypes {
	
	public static final String IMAGE_PNG = "image/png";
	public static final String HTML = "text/html";
	public static final String XML = "text/xml";
	public static final String SVG = "image/svg+xml";
	public static final String JSON = "application/json";
	public static final String VCARD = "text/directory;profile=vCard";

    private static final Set<String> IMAGE_TYPES;
    
    static {
        Set<String> set = new HashSet<String>();
        set.add("image/gif");
        set.add(IMAGE_PNG);
        set.add("image/x-png");
        set.add("image/jpeg");
        set.add("image/pjpeg");
        set.add("image/bmp");
        set.add("image/tiff");
        IMAGE_TYPES = Collections.unmodifiableSet(set);
    }

    public static boolean isImage(String mimeType) {
        return mimeType == null ? false : MimeTypes.IMAGE_TYPES.contains(mimeType);
    }

	public static final String PLAIN_TEXT = "text/plain";

	public static final String PDF = "application/pdf";

	public static final String MICROSOFT_POWERPOINT = "application/vnd.ms-powerpoint";
	public static final String MICROSOFT_WORD = "application/msword";
	public static final String MICROSOFT_EXCEL = "application/vnd.ms-excel";

}
