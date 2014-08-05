package ca.intelliware.ihtsdo.mlds.design;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;

import org.apache.commons.lang.ClassUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Sets;

import ca.intelliware.ihtsdo.mlds.design.ControllerMethodTraversal.MethodVisitor;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;

public class RequestMappingSecurityTest {
	
	@Test
	public void shouldUseRolesAllowedSecurityForAllRequestMethods() throws Exception {
		ControllerMethodTraversal controllerMethodTraversal = new ControllerMethodTraversal();
		controllerMethodTraversal.visitClasses(new MethodVisitor() {
			@Override
			public void visit(Class<?> controllerClass, Method method) {
				if(method.isAnnotationPresent(RequestMapping.class)){
					boolean hasSecurityAnnotation = method.isAnnotationPresent(RolesAllowed.class) || method.isAnnotationPresent(PermitAll.class);
					Assert.assertTrue(ClassUtils.getShortClassName(controllerClass) + "." + method.getName() + " has no permissions set", hasSecurityAnnotation);
				}
			}
		});
	}

	/**
	 * Make sure we use a limited set of role lists, in a standard order
	 */
	@Test
	public void shouldUseStereotypedRolesLists() throws Exception {
		final Set<List<String>> allowedRoleLists = Sets.newHashSet();
		allowedRoleLists.add(Arrays.asList(AuthoritiesConstants.PUBLIC));
		allowedRoleLists.add(Arrays.asList(AuthoritiesConstants.STAFF_OR_ADMIN));
		allowedRoleLists.add(Arrays.asList(AuthoritiesConstants.AUTHENTICATED));
		allowedRoleLists.add(Arrays.asList(AuthoritiesConstants.ADMIN_ONLY));
		allowedRoleLists.add(Arrays.asList(AuthoritiesConstants.USER_ONLY));
		allowedRoleLists.add(Arrays.asList(AuthoritiesConstants.UNAUTHENTICATED));
		
		ControllerMethodTraversal controllerMethodTraversal = new ControllerMethodTraversal();
		controllerMethodTraversal.visitClasses(new MethodVisitor() {
			@Override
			public void visit(Class<?> controllerClass, Method method) {
				if(method.isAnnotationPresent(RequestMapping.class) && method.isAnnotationPresent(RolesAllowed.class)){
					List<String> roles = Arrays.asList(method.getAnnotation(RolesAllowed.class).value());
					
					Assert.assertTrue(ClassUtils.getShortClassName(controllerClass) + "." + method.getName() + " fails to use a standard role list", allowedRoleLists.contains(roles));
				}
			}
		});
	}
}
