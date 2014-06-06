package ca.intelliware.commons.j5goodies.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * <p>This class models a web browser and provides some convenience methods for 
 * understanding the meaning of a variety of different user agent headers.
 * 
 * @author <a href="http://www.intelliware.ca/">Intelliware Development</a>
 * @author BC Holmes
 */
public class Browser {
    
    public enum BrowserType {
    	INTERNET_EXPLORER("MSIE"),
    	UNKNOWN("unknown"),
    	FIREFOX("Firefox"),
    	KONQUEROR("Konqueror"),
    	OPERA("Opera"),
    	LYNX("Lynx"),
    	NETSCAPE("Netscape"),
    	SAFARI("Safari"),
    	BLACKBERRY("BlackBerry"),
    	CHROME("Chrome");
    	
        private final String name;

        private BrowserType(String name) {
            this.name = name;
        }
        public String toString() {
            return this.name;
        }
    }
    
    public static final BrowserType INTERNET_EXPLORER = BrowserType.INTERNET_EXPLORER;
    public static final BrowserType UNKNOWN = BrowserType.UNKNOWN;
    public static final BrowserType FIREFOX = BrowserType.FIREFOX;
    public static final BrowserType KONQUEROR = BrowserType.KONQUEROR;
    public static final BrowserType OPERA = BrowserType.OPERA;
    public static final BrowserType LYNX = BrowserType.LYNX;
    public static final BrowserType NETSCAPE = BrowserType.NETSCAPE;
    public static final BrowserType SAFARI = BrowserType.SAFARI;
    public static final BrowserType BLACKBERRY = BrowserType.BLACKBERRY;
    public static final BrowserType CHROME = BrowserType.CHROME;
    private static final Browser UNKNOWN_BROWSER = new Browser(UNKNOWN, DeviceType.OTHER);

    private static final Map<String,String> SAFARI_VERSIONS;
    
    static {
    	Map<String,String> map = new HashMap<String, String>();
    	map.put("85.5", "1.0");
    	map.put("85.8", "1.0.3");
    	map.put("85.8.1", "1.0.3");
    	map.put("125", "1.2");
    	map.put("125.1", "1.2");
    	map.put("312", "1.3");
    	map.put("412", "2.0");
    	map.put("412.2", "2.0");
    	map.put("412.2.2", "2.0");
    	map.put("412.5", "2.0.1");
    	map.put("412.6", "2.0.1");
    	map.put("416.12", "2.0.2");
    	map.put("416.13", "2.0.2");
    	map.put("416.13_Adobe", "2.0.2");
    	map.put("417.8", "2.0.3");
    	map.put("417.8_Adobe", "2.0.3");
    	map.put("417.9.2", "2.0.3");
    	map.put("417.9.3", "2.0.3");
    	map.put("419.3", "2.0.4");
    	SAFARI_VERSIONS = Collections.unmodifiableMap(map);
    }

    private BrowserType browserType;
    private OperatingSystem operatingSystem = new OperatingSystem();
	private String version;
	private final DeviceType deviceType;
    
    public Browser(BrowserType browserType, DeviceType deviceType) {
        this.browserType = browserType;
		this.deviceType = deviceType;
    }

    public static Browser parse(String userAgent) {
        try {
            if (userAgent == null || userAgent.trim().length() == 0) {
                return UNKNOWN_BROWSER;
            } else {
            	DeviceType deviceType = determineDeviceType(userAgent);
            	
                if (userAgent.indexOf(INTERNET_EXPLORER.toString()) >= 0) {
                    return parse(userAgent, INTERNET_EXPLORER, deviceType);
                } else if (userAgent.indexOf(FIREFOX.toString()) >= 0) {
                    return parseFirefox(userAgent, deviceType);
                } else if (userAgent.indexOf(KONQUEROR.toString()) >= 0) {
                    return parse(userAgent, KONQUEROR, deviceType);
                } else if (userAgent.indexOf(CHROME.toString()) >= 0) {
                    return parse(userAgent, CHROME, deviceType);
                } else if (userAgent.indexOf(NETSCAPE.toString()) >= 0) {
                    return parse(userAgent, NETSCAPE, deviceType);
                } else if (userAgent.indexOf(BLACKBERRY.toString()) >= 0) {
                    return new Browser(BLACKBERRY, DeviceType.BLACKBERRY);
                } else if (userAgent.indexOf(SAFARI.toString()) >= 0) {
                    return parseSafari(userAgent, deviceType);
                } else if (userAgent.indexOf(LYNX.toString()) >= 0) {
                    return parse(userAgent, LYNX, deviceType);
                } else {
                    return UNKNOWN_BROWSER;
                }
            }
        } catch (Throwable t) {
            return UNKNOWN_BROWSER;
        }
    }

    private static DeviceType determineDeviceType(String userAgent) {
    	DeviceType result = null;
    	for (DeviceType deviceType : DeviceType.values()) {
			if (userAgent.toUpperCase().contains(deviceType.name())) {
				result = deviceType;
				break;
			}
		}
    	
		return result == null ? DeviceType.OTHER : result;
	}

	private static Browser parseFirefox(String userAgent, DeviceType deviceType) {
        Browser browser = new Browser(FIREFOX, deviceType);
        parseVersion(userAgent, browser, FIREFOX, null);
        browser.operatingSystem = OperatingSystem.parse(userAgent);
        return browser;
    }

    private static Browser parseSafari(String userAgent, DeviceType deviceType) {
    	Browser browser = new Browser(SAFARI, deviceType);
    	if (userAgent.contains("Version/")) {
    		String[] words = StringUtils.split(StringUtils.substringAfter(userAgent, "Version/"));
    		browser.version = words.length > 0 ? words[0] : null;
    	} else if (userAgent.contains("Safari/")) {
    		String[] words = StringUtils.split(StringUtils.substringAfter(userAgent, "Safari/"));
    		browser.version = words.length > 0 ? SAFARI_VERSIONS.get(words[0]) : null;
    	}
        browser.operatingSystem = OperatingSystem.parse(userAgent);
    	return browser;
    }
    
    private static Browser parse(String userAgent, BrowserType type, DeviceType deviceType) {
        Browser browser = new Browser(type, deviceType);
        parseVersion(userAgent, browser, type, ";");
        browser.operatingSystem = OperatingSystem.parse(userAgent);
        return browser;
    }
    
    /**
     * @param userAgent
     * @param browser
     * @param type
     */
    private static void parseVersion(String userAgent, Browser browser,
            BrowserType type, String delimiter) {
        int index = userAgent.indexOf(type.toString());
        String[] words = delimiter == null ? StringUtils
                .split(userAgent.substring(index)) : StringUtils.split(userAgent
                .substring(index), delimiter);
        if (ArrayUtils.getLength(words) > 0) {
            String[] parts = StringUtils.split(words[0], " /");
            if (ArrayUtils.getLength(parts) >= 2) {
                browser.version = parts[1];
            }
        }
    }

    public BrowserType getType() {
        return this.browserType;
    }
    
    public boolean isInternetExplorer() {
        return INTERNET_EXPLORER.equals(getType());
    }

    public boolean isFirefox() {
        return FIREFOX.equals(getType());
    }
    
    public String getVersion() {
        return this.version;
    }

    /**
     * @since 1.10
     */
    public OperatingSystem getOperatingSystem() {
		return this.operatingSystem;
	}

    
    public static Browser parse(HttpServletRequest httpRequest) {
        return parse(HttpUtil.getUserAgent(httpRequest));
    }
    
    public String toString() {
        return "" + this.browserType.toString() + " " + this.version;
    }
    
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        return builder.append(this.browserType).append(this.version).toHashCode();
    }
    
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        } else if (this == object) {
            return true;
        } else if (getClass() == object.getClass()) {
            return equals((Browser) object);
        } else {
            return false;
        }
    }
    private boolean equals(Browser that) {
        EqualsBuilder builder = new EqualsBuilder();
        return builder.append(this.browserType, that.browserType)
                .append(this.version, that.version)
                .isEquals();
    }

	public int getMajorVersion() {
		String majorVersion = StringUtils.substringBefore(getVersion(), ".");
		try {
			return Integer.parseInt(majorVersion);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public boolean isInlineSvgSupported() {
		if (isFirefox() && getMajorVersion() >= 4) {
			return true;
		} else if (isInternetExplorer() && getMajorVersion() >= 9) {
			return true;
		} else if (getType() == BrowserType.CHROME && getMajorVersion() >= 7) {
			return true;
		} else if (getType() == BrowserType.SAFARI && (getMajorVersion() > 5 || (getMajorVersion() == 5 && getVersion().startsWith("5.1")))) {
			return true;
		} else if (getType() == BrowserType.OPERA && (getMajorVersion() > 11 || 
				(getMajorVersion() == 11 && getVersion().startsWith("11.6")))) {
			return true;
		} else {
			return false;
		}
	}
	public SvgSupport getSvgSupport() {
		if (isInternetExplorer()) {
			return getMajorVersion() >= 9 ? SvgSupport.SVG_1_1 : SvgSupport.NONE;
		} else if (isFirefox()) {
			return getMajorVersion() <= 2 ? SvgSupport.NONE : SvgSupport.SVG_1_1;
		} else if (getType() == Browser.SAFARI) {
			return getMajorVersion() < 2 ? SvgSupport.NONE : SvgSupport.SVG_1_1;
		} else if (getType() == Browser.CHROME) {
			return getMajorVersion() < 1 ? SvgSupport.NONE : SvgSupport.SVG_1_1;
		} else {
			return SvgSupport.UNKNOWN;
		}
	}

	public boolean isMobileDevice() {
		return this.deviceType != null && this.deviceType.isMobile();
	}

	public DeviceType getDeviceType() {
		return this.deviceType;
	}
}
