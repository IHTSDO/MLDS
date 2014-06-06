package ca.intelliware.commons.j5goodies.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UrlEncodingUtil {

	@SuppressWarnings("deprecation")
	public static String encodeUtf8(String string) {
		try {
			return URLEncoder.encode(string, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return URLEncoder.encode(string);
		}
	}
}
