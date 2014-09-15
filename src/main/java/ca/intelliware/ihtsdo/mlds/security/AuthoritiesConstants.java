package ca.intelliware.ihtsdo.mlds.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    private AuthoritiesConstants() {
    }

    public static final String ADMIN = "ROLE_ADMIN";
    
    public static final String STAFF = "ROLE_STAFF";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";
    public static final String IHTSDO = "IHTSDO";
    
    // allowed combinations
    public static final String[] STAFF_OR_ADMIN = { STAFF, ADMIN };
    public static final String[] AUTHENTICATED = { USER, STAFF, ADMIN };
    public static final String[] PUBLIC = { ANONYMOUS, USER, STAFF, ADMIN };
    public static final String[] ADMIN_ONLY = { ADMIN };
    public static final String[] USER_ONLY = { USER };
    public static final String[] UNAUTHENTICATED = { ANONYMOUS };
    public static final String[] USER_OR_ADMIN = { USER, ADMIN };

	public static String staffRoleForMember(String memberKey) {
		return STAFF + "_" + memberKey;
	}
}
