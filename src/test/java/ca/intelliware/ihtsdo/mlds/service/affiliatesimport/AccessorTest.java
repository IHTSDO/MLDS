package ca.intelliware.ihtsdo.mlds.service.affiliatesimport;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import ca.intelliware.ihtsdo.mlds.service.AuditEventService;

@RunWith(MockitoJUnitRunner.class)
public class AccessorTest {

	private Parent rootObject;

	@Before
	public void setUp() {
		rootObject = new Parent();
	}
	
	@Test
	public void shouldBeAbleToReadTopLevelAttribute() {
		Accessor accessor = new Accessor(Parent.class, "top");
		
		assertEquals(accessor.getValue(rootObject), "parent-value");
	}

	@Test
	public void shouldBeAbleToUpdateTopLevelAttribute() throws Exception {
		Accessor accessor = new Accessor(Parent.class, "top");
		
		accessor.setValue(rootObject, "new-value");
		
		assertEquals(rootObject.top, "new-value");
	}
	
	@Test
	public void shouldInsertDefaultParentObjectsDuringUpdateParentAttributes() throws Exception {
		rootObject.child = null;
		Accessor accessor = new Accessor(Parent.class, "child.value");
		
		accessor.setValue(rootObject, "totally-new");
		
		assertEquals(rootObject.child.value, "totally-new");
		
		assertEquals(rootObject.top, "parent-value");
	}


	@Test
	public void shouldBeAbleToReadChildAttribute() {
		Accessor accessor = new Accessor(Parent.class, "child.value");
		
		assertEquals(accessor.getValue(rootObject), "child-value");
	}

	@Test
	public void shouldBeAbleToUpdateChildLevelAttribute() throws Exception {
		Accessor accessor = new Accessor(Parent.class, "child.value");
		accessor.setValue(rootObject, "new-value");
		
		assertEquals(rootObject.child.value, "new-value");
		
		assertEquals(rootObject.top, "parent-value");
	}

	@Test
	public void shouldReturnNullOnGetIfParentMissing() {
		rootObject.child = null;
		Accessor accessor = new Accessor(Parent.class, "child.value");
		
		assertEquals(accessor.getValue(rootObject), null);
	}

	@Test
	public void shouldProvideAttributeClass() {
		Accessor accessor = new Accessor(Parent.class, "child.value");
		
		assertEquals(accessor.getAttributeClass(), String.class);
	}

	public static class Child {
		String value = "child-value";
	}
	
	public static class Parent {
		String top = "parent-value";
		Child child = new Child();
	}
}
