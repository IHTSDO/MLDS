/**
 * Binding of IHTSDO web authentication service into our app.
 * {@link HttpAuthAdaptor} makes the various calls.
 * 
 * 
 * {@link CentralAuthUserInfo} and {@link CentralAuthUserPermission} are
 * beans that contain the JSON responses.
 * 
 * {@link HttpAuthAuthenticationProvider} wraps the whole thing up to plug into Spring via
 * {@link ca.intelliware.ihtsdo.mlds.config.SecurityConfiguration}
 */
package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

