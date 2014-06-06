package ca.intelliware.commons.j5goodies.http;

import static ca.intelliware.commons.j5goodies.http.OperatingSystem.Platform.WINDOWS;
import static ca.intelliware.commons.j5goodies.http.OperatingSystem.Platform.MACINTOSH;
import static ca.intelliware.commons.j5goodies.http.OperatingSystem.Platform.X11;
import ca.intelliware.commons.j5goodies.http.OperatingSystem.OperatingSystemVersion;
import junit.framework.TestCase;

public class OperatingSystemTest extends TestCase {

	public void testShouldDetectedWindowsVersions() throws Exception {
		assertOperatingSystem("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9b5pre) Gecko/2008030706 Firefox/3.0b5pre", WINDOWS, OperatingSystem.OperatingSystemVersion.WINDOWS_XP);
		assertOperatingSystem("Mozilla/5.0 (Windows; U; Windows NT 6.0; fi) AppleWebKit/522.12.1 (KHTML, like Gecko) Version/3.0.1 Safari/522.12.2", WINDOWS, OperatingSystem.OperatingSystemVersion.WINDOWS_VISTA);
	}

	public void testShouldDetectedMacVersions() throws Exception {
		assertOperatingSystem("Mozilla/5.0 (Macintosh; U; PPC Mac OS X 10.4; en-GB; rv:1.9b5) Gecko/2008032619 Firefox/3.0b5", MACINTOSH, OperatingSystemVersion.MAC_OS_X_10_TIGER);
		assertOperatingSystem("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_5_6; fr-fr) AppleWebKit/525.27.1 (KHTML, like Gecko) Version/3.2.1 Safari/525.27.1", MACINTOSH, OperatingSystemVersion.MAC_OS_X_10_LEOPARD);
	}
	
	public void testShouldDetectedX11Versions() throws Exception {
		assertOperatingSystem("Mozilla/5.0 (X11; U; SunOS i86pc; en-US; rv:1.9.0.4) Gecko/2008111710 Firefox/3.0.4", X11, OperatingSystemVersion.SUN_OS);
	}
	
	private void assertOperatingSystem(String userAgent, OperatingSystem.Platform platform, OperatingSystemVersion operatingSystemVersion) {
		OperatingSystem operatingSystem = OperatingSystem.parse(userAgent);
		assertNotNull(userAgent + " not null", operatingSystem);
		assertEquals(userAgent, platform, operatingSystem.getPlatform());
		assertEquals(userAgent, operatingSystemVersion, operatingSystem.getVersion());
	}
}
