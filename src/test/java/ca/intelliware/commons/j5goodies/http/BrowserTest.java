package ca.intelliware.commons.j5goodies.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BrowserTest {

	@Test
    public void testParseInternetExplorer() throws Exception {

        assertBrowser("Mozilla/4.0 (compatible; MSIE 5.01; Windows NT 5.0)",
                Browser.INTERNET_EXPLORER, "5.01", 5);
        assertBrowser("Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 4.0)",
                Browser.INTERNET_EXPLORER, "5.5", 5);
        assertBrowser("Mozilla/4.0 (compatible; MSIE 6.0; Windows 98)",
                Browser.INTERNET_EXPLORER, "6.0", 6);
        assertBrowser("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)",
                Browser.INTERNET_EXPLORER, "6.0", 6);
        assertBrowser(
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; .NET CLR 1.1.4322)",
                Browser.INTERNET_EXPLORER, "6.0", 6);
        assertBrowser("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)",
                Browser.INTERNET_EXPLORER, "6.0", 6);
        assertBrowser(
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.0.3705)",
                Browser.INTERNET_EXPLORER, "6.0", 6);
        assertBrowser(
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.1.4322)",
                Browser.INTERNET_EXPLORER, "6.0", 6);
        assertBrowser(
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; Crazy Browser 1.0.5; .NET CLR 1.1.4322)",
                Browser.INTERNET_EXPLORER, "6.0", 6);
        assertBrowser("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)",
                Browser.INTERNET_EXPLORER, "6.0", 6);
        assertBrowser(
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.0.3705)",
                Browser.INTERNET_EXPLORER, "6.0", 6);
        assertBrowser(
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)",
                Browser.INTERNET_EXPLORER, "6.0", 6);
        assertBrowser("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;)",
                Browser.INTERNET_EXPLORER, "6.0", 6);
        assertBrowser(
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; .NET CLR 1.1.4322)",
                Browser.INTERNET_EXPLORER, "6.0", 6);
        assertBrowser(
                "Mozilla/4.0 (compatible; MSIE 6.0b; Windows NT 5.0; MyIE2; Maxthon; i-NavFourF)",
                Browser.INTERNET_EXPLORER, "6.0b", 6);
        assertBrowser(
        		"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.04506.648; .NET CLR 3.5.21022; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)",
        		Browser.INTERNET_EXPLORER, "6.0", 6);
        assertBrowser(
        		"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; .NET CLR 1.1.4322; .NET CLR 2.0.50727)",
        		Browser.INTERNET_EXPLORER, "7.0", 7);
        assertBrowser(
        		"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.04506.648)",
        		Browser.INTERNET_EXPLORER, "8.0", 8);
        assertBrowser(
        		"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)",
        		Browser.INTERNET_EXPLORER, "9.0", 9);

    }
	
	@Test
	public void shouldRecognizeMobileDevices() throws Exception {
		assertMobileDevice("Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_0 like Mac OS X; en-us) AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0 Mobile/7A341 Safari/528.16", DeviceType.IPHONE);
		assertMobileDevice("Mozilla/5.0 (Linux; U; Android 1.6; en-gb; Dell Streak Build/Donut AppleWebKit/528.5+ (KHTML, like Gecko) Version/3.1.2 Mobile Safari/ 525.20.1", DeviceType.ANDROID);
	}

	private void assertMobileDevice(String string, DeviceType iphone) {
		Browser browser = Browser.parse(string);
		assertTrue(string + " is mobile: ", browser.isMobileDevice());
		assertEquals(string + " device type", iphone, browser.getDeviceType());
	}

	@Test
    public void testParseLynx() throws Exception {
        assertBrowser(
                "Lynx 2.7.2 libwww-FM/2.14",
                Browser.LYNX, "2.7.2", 2);
        assertBrowser(
                "Lynx/2.7.2 libwww-FM/2.14",
                Browser.LYNX, "2.7.2", 2);
    }
    
	@Test
    public void testParseBlackBerry() throws Exception {
        assertBrowser(
                "BlackBerry8700/4.1.0 Profile/MIDP-2.0 Configuration/CLDC-1.1 VendorID/107",
                Browser.BLACKBERRY, null);
    }
    
	@Test
    public void testParseChrome() throws Exception {
    	assertBrowser(
    			"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/525.19 (KHTML, like Gecko) Chrome/1.0.154.36 Safari/525.19",
    			Browser.CHROME, "1.0.154.36");
    	assertBrowser(
    			"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/525.19 (KHTML, like Gecko) Chrome/1.0.154.39 Safari/525.19",
    			Browser.CHROME, "1.0.154.39");
    }
    
	@Test
    public void testParseNetscape() throws Exception {
        assertBrowser(
                "Mozilla/5.0 (Windows; U; WinNT4.0; en-US; rv:1.0.2) Gecko/20030208 Netscape/7.02 (CK-DNJ702R1)",
                Browser.NETSCAPE, "7.02");
    }
    
	@Test
    public void testParseSafari() throws Exception {
        assertBrowser(
                "Mozilla/5.0 (Macintosh; U; PPC Mac OS X; en) AppleWebKit/417.9 (KHTML, like Gecko) Safari/417.8",
                Browser.SAFARI, "2.0.3");
        assertBrowser(
        		"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_5_5; en-us) AppleWebKit/525.18 (KHTML, like Gecko) Version/3.1.2 Safari/525.20.1",
        		Browser.SAFARI, "3.1.2", 3);
        assertBrowser(
        		"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_5_6; en-us) AppleWebKit/525.27.1 (KHTML, like Gecko) Version/3.2.1 Safari/525.27.1",
        		Browser.SAFARI, "3.2.1", 3);
    }
    
	@Test
    public void testParseKonqueror() throws Exception {
        assertBrowser(
                "Mozilla/5.0 (compatible; Konqueror/3.0; i686 Linux; 20021223)",
                Browser.KONQUEROR, "3.0");
        assertBrowser(
                "Mozilla/5.0 (compatible; Konqueror/3.1-rc3; i686 Linux; 20020226)",
                Browser.KONQUEROR, "3.1-rc3");
    }
    
	@Test
    public void testParseFirefox() throws Exception {
        assertBrowser(
                "Mozilla/5.0 (Windows; U; Windows NT 5.0; en-US; rv:1.7.12) Gecko/20050915 Firefox/1.0.7 (ax)",
                Browser.FIREFOX, "1.0.7");
        assertBrowser(
                "Mozilla/5.0 (Windows; U; Windows NT 5.0; en-US; rv:1.8) Gecko/20051111 Firefox/1.5",
                Browser.FIREFOX, "1.5");
        assertBrowser(
                "Mozilla/5.0 (Windows; U; Windows NT 5.0; ja; rv:1.8) Gecko/20051111 Firefox/1.5",
                Browser.FIREFOX, "1.5");
        assertBrowser(
                "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.12) Gecko/20050915 Firefox/1.0.7",
                Browser.FIREFOX, "1.0.7");
        assertBrowser(
                "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.5) Gecko/20041107 Firefox/1.0",
                Browser.FIREFOX, "1.0");
        assertBrowser(
                "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.8) Gecko/20050511 Firefox/1.0.4 (ax)",
                Browser.FIREFOX, "1.0.4");
        assertBrowser(
                "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8) Gecko/20051111 Firefox/1.5",
                Browser.FIREFOX, "1.5");
        assertBrowser(
                "Mozilla/5.0 (Windows; U; Windows NT 5.1; fi-FI; rv:1.7.12) Gecko/20050919 Firefox/1.0.7",
                Browser.FIREFOX, "1.0.7");
        assertBrowser(
                "Mozilla/5.0 (Windows; U; Windows NT 5.1; pt-BR; rv:1.8) Gecko/20051111 Firefox/1.5",
                Browser.FIREFOX, "1.5");
        assertBrowser(
                "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.7.12) Gecko/20050920 Firefox/1.0.7 SUSE/1.0.7-0.1",
                Browser.FIREFOX, "1.0.7");
        assertBrowser(
                "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.7.12) Gecko/20050921 Red Hat/1.0.7-1.4.1 Firefox/1.0.7",
                Browser.FIREFOX, "1.0.7");
        assertBrowser(
                "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.7.12) Gecko/20051010 Firefox/1.0.7 (Ubuntu package 1.0.7)",
                Browser.FIREFOX, "1.0.7");
        assertBrowser(
                "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8) Gecko/20051111 Firefox/1.5",
                Browser.FIREFOX, "1.5");
        assertBrowser(
                "Mozilla/5.0 (X11; U; Linux i686; pl-PL; rv:1.7.12) Gecko/20051010 Firefox/1.0.7 (Ubuntu package 1.0.7)",
                Browser.FIREFOX, "1.0.7");
        assertBrowser(
        		"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.5; en-US; rv:1.9.1b1) Gecko/20081007 Firefox/3.1b1",
        		Browser.FIREFOX, "3.1b1", 3);
        assertBrowser(
        		"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.1) Gecko/2008072820 Firefox/3.0.1",
        		Browser.FIREFOX, "3.0.1", 3);
        assertBrowser(
        		"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.5) Gecko/2008121621 Ubuntu/8.04 (hardy) Firefox/3.0.5",
        		Browser.FIREFOX, "3.0.5", 3);

    	assertFalse("Firefox 3 SVG", Browser.parse("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.5) Gecko/2008121621 Ubuntu/8.04 (hardy) Firefox/3.0.5").isInlineSvgSupported());
    	assertTrue("Firefox 4 SVG", Browser.parse("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.5) Gecko/2008121621 Ubuntu/8.04 (hardy) Firefox/4.0").isInlineSvgSupported());
	}

    private void assertBrowser(String header, Browser.BrowserType type, String version, int majorVersion) {
    	assertBrowser(header, type, version);

    	Browser browser = Browser.parse(header);
    	assertEquals(header + " major version", majorVersion, browser.getMajorVersion());
    }
    
    private void assertBrowser(String header, Browser.BrowserType type, String version) {
    	
    	Browser browser = Browser.parse(header);
    	assertNotNull(header + " parsed", browser);
    	assertEquals(header + " type", type, browser.getType());
    	
    	assertEquals(header + " version", version, browser.getVersion());
    }
}
