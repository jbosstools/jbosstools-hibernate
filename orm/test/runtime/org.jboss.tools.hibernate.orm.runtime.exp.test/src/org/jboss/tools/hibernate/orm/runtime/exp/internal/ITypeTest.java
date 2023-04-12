package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.hibernate.tool.orm.jbt.type.ClassType;
import org.hibernate.tool.orm.jbt.wrp.TypeWrapperFactory;
import org.hibernate.type.AnyType;
import org.hibernate.type.ArrayType;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.OneToOneType;
import org.hibernate.type.spi.TypeConfiguration;
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
	public void testGetName() {
		// first try a class type
		IType classTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(new ClassType()));
		assertEquals("class", classTypeFacade.getName());
		// next try a array type
		IType arrayTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(new ArrayType("foo", "bar", String.class)));
		assertEquals("[Ljava.lang.String;(foo)", arrayTypeFacade.getName());
	}
	
	@Test
	public void testFromStringValue() {
		// first try type that is string representable
		IType classTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(new ClassType()));
		assertEquals(
				TypeFacadeTest.class, 
				classTypeFacade.fromStringValue(TypeFacadeTest.class.getName()));
		// next try type that is not string representable
		try {
			IType arrayTypeFacade = (IType)GenericFacadeFactory.createFacade(
					IType.class, 
					TypeWrapperFactory.createTypeWrapper(new ArrayType("foo", "bar", String.class)));
			arrayTypeFacade.fromStringValue("just a random string");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'fromStringValue(Object)'"));
		}
	}
	
	@Test
	public void testIsEntityType() {
		// first try type that is not an entity type
		IType classTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(new ClassType()));
		assertFalse(classTypeFacade.isEntityType());
		// next try type that is an entity type
		IType entityTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(new ManyToOneType((TypeConfiguration)null, null)));
		assertTrue(entityTypeFacade.isEntityType());
	}
	
	@Test
	public void testIsOneToOne() {
		// first try type that is not a one to one type
		try {
			IType classTypeFacade = (IType)GenericFacadeFactory.createFacade(
					IType.class, 
					TypeWrapperFactory.createTypeWrapper(new ClassType()));
			classTypeFacade.isOneToOne();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isOneToOne()'"));
		}
		// next try another type that is not a one to one type
		IType entityTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(new ManyToOneType((TypeConfiguration)null, null)));
		assertFalse(entityTypeFacade.isOneToOne());
		// finally try a type that is a one to one type
		IType oneToOneTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(
						new OneToOneType(
								null, null, null, false, null, false, false, null, null, false)));
		assertTrue(oneToOneTypeFacade.isOneToOne());
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
