package ca.intelliware.commons.j5goodies.http;

import junit.framework.TestCase;

public class UrlEncodingUtilTest extends TestCase {

	public void testEncode() throws Exception {
		assertEquals("spaces", "some+thing", UrlEncodingUtil.encodeUtf8("some thing"));
		assertEquals("non-breaking space", "some%C2%A0thing", UrlEncodingUtil.encodeUtf8("some" + ((char) 160) + "thing"));
		assertEquals("comma", "some%2Cthing", UrlEncodingUtil.encodeUtf8("some,thing"));
		assertEquals("question mark", "some%3Fthing", UrlEncodingUtil.encodeUtf8("some?thing"));
		assertEquals("percent", "some%25thing", UrlEncodingUtil.encodeUtf8("some%thing"));
		assertEquals("percent", "steve%27s", UrlEncodingUtil.encodeUtf8("steve's"));
	}
}
