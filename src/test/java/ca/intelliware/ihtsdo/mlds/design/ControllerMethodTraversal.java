package ca.intelliware.ihtsdo.mlds.design;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import ca.intelliware.commons.j5goodies.jar.ClassFinder;
import ca.intelliware.commons.j5goodies.jar.ClassPredicate;
import ca.intelliware.ihtsdo.mlds.web.rest.ReleasePackagesResource;

public class ControllerMethodTraversal {
	private List<Class<?>> classes;
	
	public interface MethodVisitor {
		void visit(Class<?> controllerClass, Method method);
	}

	public ControllerMethodTraversal() {
		findClasses();
	}
	
	void visitClasses(MethodVisitor visitor) {
		for (Class<?> controllerClass : classes) {
			visitMethods(controllerClass, visitor);
		}
	}

	private void visitMethods(Class<?> controllerClass, MethodVisitor visitor) {
		for (Method method : controllerClass.getMethods()) {
			visitor.visit(controllerClass, method);
		}
	}

	void findClasses() {
		classes = new ClassFinder().findClasses(ReleasePackagesResource.class, new ClassPredicate() {
			@Override
			public boolean isSelected(Class<?> c) {
				return c != null && (c.isAnnotationPresent(Controller.class) || c.isAnnotationPresent(RestController.class));
			}

			@Override
			public boolean isSelected(String packageName, String className) {
				return true;
			}
		});
	}

	public void addException(Class<?> controllerClass) {
		classes.remove(controllerClass);
	}

}