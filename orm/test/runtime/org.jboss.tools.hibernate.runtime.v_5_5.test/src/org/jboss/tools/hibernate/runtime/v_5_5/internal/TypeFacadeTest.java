package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.hibernate.type.ArrayType;
import org.hibernate.type.ClassType;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.junit.jupiter.api.Test;

public class TypeFacadeTest {

	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	@Test
	public void testToString() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		assertEquals(
				TypeFacadeTest.class.getName(), 
				typeFacade.toString(TypeFacadeTest.class));
		ArrayType arrayType = new ArrayType("foo", "bar", String.class);
		typeFacade = FACADE_FACTORY.createType(arrayType);
		assertNull(typeFacade.toString(new String[] { "foo", "bar" }));
	}
	
	@Test
	public void testGetName() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		assertEquals("class", typeFacade.getName());
		ArrayType arrayType = new ArrayType("foo", "bar", String.class);
		typeFacade = FACADE_FACTORY.createType(arrayType);
		assertEquals("[Ljava.lang.String;(foo)", typeFacade.getName());
	}
	
	@Test
	public void testFromStringValue() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		assertEquals(
				TypeFacadeTest.class, 
				typeFacade.fromStringValue(TypeFacadeTest.class.getName()));
		ArrayType arrayType = new ArrayType("foo", "bar", String.class);
		typeFacade = FACADE_FACTORY.createType(arrayType);
		assertNull(typeFacade.fromStringValue("just a random string"));
	}
	
}
