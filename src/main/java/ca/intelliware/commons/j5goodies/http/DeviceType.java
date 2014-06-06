package ca.intelliware.commons.j5goodies.http;

public enum DeviceType {
	ANDROID, IPHONE, IPAD, BLACKBERRY, IPOD, OTHER;

	public boolean isMobile() {
		return this == ANDROID || this == IPHONE || this == IPAD || this == BLACKBERRY;
	}
}
