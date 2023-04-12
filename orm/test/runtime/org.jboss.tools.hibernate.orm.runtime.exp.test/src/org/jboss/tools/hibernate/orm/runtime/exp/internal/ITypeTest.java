package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.hibernate.tool.orm.jbt.type.ClassType;
import org.hibernate.tool.orm.jbt.wrp.TypeWrapperFactory;
import org.hibernate.type.AnyType;
import org.hibernate.type.ArrayType;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.GenericFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.junit.jupiter.api.Test;

public class ITypeTest {
	
	@Test
	public void testToString() {
		// first try type that is string representable
		IType classTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(new ClassType()));
		assertEquals(
				TypeFacadeTest.class.getName(), 
				classTypeFacade.toString(TypeFacadeTest.class));
		// next try a type that cannot be represented by a string
		try {
			IType arrayTypeFacade = (IType)GenericFacadeFactory.createFacade(
					IType.class, 
					TypeWrapperFactory.createTypeWrapper(new ArrayType("foo", "bar", String.class)));
			arrayTypeFacade.toString(new String[] { "foo", "bar" });
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'toString(Object)'"));
		}
	}
	
	@Test
	public void testIsAnyType() {
		// first try type that is not a any type
		IType classTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(new ClassType()));
		assertFalse(classTypeFacade.isAnyType());
		// next try a any type
		IType anyTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(new AnyType(null, null, null, true)));
		assertTrue(anyTypeFacade.isAnyType());
	}
	
	@Test
	public void testIsCollectionType() {
		// first try type that is not a collection type
		IType classTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(new ClassType()));
		assertFalse(classTypeFacade.isCollectionType());
		// next try a collection type
		IType arrayTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(new ArrayType(null, null, String.class)));
		assertTrue(arrayTypeFacade.isCollectionType());
	}
	
}
