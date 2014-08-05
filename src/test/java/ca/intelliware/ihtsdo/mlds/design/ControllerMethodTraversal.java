package ca.intelliware.ihtsdo.mlds.design;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import ca.intelliware.ihtsdo.mlds.Application;
import ca.intelliware.ihtsdo.mlds.web.rest.CountriesResource;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

public class ControllerMethodTraversal {
	private List<Class<?>> classes = new ArrayList<>();
	
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
		ClassPath classPath;
		try {
			classPath = ClassPath.from(CountriesResource.class.getClassLoader());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		ImmutableSet<ClassInfo> classInfos = classPath.getTopLevelClassesRecursive(Application.class.getPackage().getName());
		for (ClassInfo classInfo : classInfos) {
			classes.add(classInfo.load());
		}
	}

	public void addException(Class<?> controllerClass) {
		classes.remove(controllerClass);
	}

}