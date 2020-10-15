package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.hibernate.type.ArrayType;
import org.hibernate.type.ClassType;
import org.jboss.tools.hibernate.runtime.common.AbstractTypeFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.junit.Test;

public class TypeFacadeTest {
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	@Test
	public void testToString() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, classType){};
		assertEquals(
				TypeFacadeTest.class.getName(), 
				typeFacade.toString(TypeFacadeTest.class));
		ArrayType arrayType = new ArrayType(null, "foo", "bar", String.class);
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, arrayType){};
		assertNull(typeFacade.toString(new String[] { "foo", "bar" }));
	}
	
	@Test
	public void testGetName() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, classType){};
		assertEquals("class", typeFacade.getName());
		ArrayType arrayType = new ArrayType(null, "foo", "bar", String.class);
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, arrayType){};
		assertEquals("[Ljava.lang.String;(foo)", typeFacade.getName());
	}
	
	

}
