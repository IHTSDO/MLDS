package ca.intelliware.commons.j5goodies.http;

import static ca.intelliware.commons.j5goodies.iterator.EmptyIterable.nullSafeIterable;
import static org.apache.commons.lang.StringUtils.split;

import org.apache.commons.lang.StringUtils;

/**
 * @since 1.10
 */
public class OperatingSystem {
	
	private OperatingSystemVersion version;
	private Platform platform;

	public enum Platform {
		WINDOWS, X11, MACINTOSH, UNKNOWN
	}

	public enum OperatingSystemVersion {
		WINDOWS_8("Windows NT 6.2"), 
		WINDOWS_7("Windows NT 6.1"), 
		WINDOWS_VISTA("Windows NT 6.0"), 
		WINDOWS_XP("Windows NT 5.1", "Windows NT 5.2"), 
		WINDOWS_2000("Windows NT 5.0"), WINDOWS_NT("WinNT4.0", "Windows NT 3.51", "Windows NT"), 
		WINDOWS_98("Win98", "Windows 98"), WINDOWS_95("Win95", "Windows 95"), WINDOWS_CE("Windows CE"),
		MAC_OS_X_10_MOUNTAIN_LION("Mac OS X 10_8", "Mac OS X 10.8"), 
		MAC_OS_X_10_LION("Mac OS X 10_7", "Mac OS X 10.7"), 
		MAC_OS_X_10_SNOW_LEOPARD("Mac OS X 10_6", "Mac OS X 10.6"), 
		MAC_OS_X_10_LEOPARD("Mac OS X 10_5", "Mac OS X 10.5"), 
		MAC_OS_X_10_TIGER("Mac OS X 10_4", "Mac OS X 10.4"), 
		LINUX("Linux x86_64", "Linux i686"),
		SUN_OS("SunOS i86pc", "SunOS sun4u"),
		UNKNOWN;
		
		private final String[] versionString;

		private OperatingSystemVersion(String... versionString) {
			this.versionString = versionString;
		}
		
		public static OperatingSystemVersion[] values(Platform platform) {
			switch (platform) {
			case WINDOWS:
				return new OperatingSystemVersion[] { 
						WINDOWS_8, WINDOWS_7, WINDOWS_VISTA, WINDOWS_XP, WINDOWS_NT, WINDOWS_CE, WINDOWS_2000, 
						WINDOWS_98, WINDOWS_95 };
			case X11:
				return new OperatingSystemVersion[] { LINUX, SUN_OS };
				
			case MACINTOSH:
				return new OperatingSystemVersion[] { MAC_OS_X_10_MOUNTAIN_LION, 
						MAC_OS_X_10_LION, MAC_OS_X_10_SNOW_LEOPARD, 
						MAC_OS_X_10_LEOPARD, MAC_OS_X_10_TIGER };

			default:
				return new OperatingSystemVersion[] { UNKNOWN };
			}
		}
	}

	OperatingSystem() {
		this.platform = Platform.UNKNOWN;
		this.version = OperatingSystemVersion.UNKNOWN;
	}
	
	public static OperatingSystem parse(String userAgent) {
		OperatingSystem result = new OperatingSystem();
		for (String string : nullSafeIterable(split(getBracketedInformation(userAgent), ";"))) {
			if (result.platform == Platform.UNKNOWN) {
				findPlatform(result, string);
			}
			if (result.version == OperatingSystemVersion.UNKNOWN) {
				findVersion(result, string);
			}
		}
		return result;
	}

	private static void findVersion(OperatingSystem result, String string) {
		outer: for (OperatingSystemVersion version : OperatingSystemVersion.values(result.platform)) {
			for (String key : version.versionString) {
				if (result.platform == Platform.WINDOWS && key.equalsIgnoreCase(string)) {
					result.version = version;
					break outer;
				} else if (string.contains(key)) {
					result.version = version;
					break outer;
				}
			}
		}
	}

	private static void findPlatform(OperatingSystem result, String string) {
		for (Platform platform : Platform.values()) {
			if (platform.name().equalsIgnoreCase(string)) {
				result.platform = platform;
				break;
			}
		}
	}

	private static String getBracketedInformation(String userAgent) {
		String s = StringUtils.substringAfter(userAgent, "(");
		return StringUtils.substringBefore(s, ")");
	}
	public OperatingSystemVersion getVersion() {
		return this.version;
	}
	public Platform getPlatform() {
		return this.platform;
	}
}
