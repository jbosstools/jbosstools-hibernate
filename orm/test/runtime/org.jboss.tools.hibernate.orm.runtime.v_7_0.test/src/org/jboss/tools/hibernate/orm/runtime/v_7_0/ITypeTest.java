package org.jboss.tools.hibernate.orm.runtime.v_7_0;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.tool.orm.jbt.internal.factory.TypeWrapperFactory;
import org.hibernate.tool.orm.jbt.internal.util.DummyMetadataBuildingContext;
import org.hibernate.type.AnyType;
import org.hibernate.type.ArrayType;
import org.hibernate.type.BagType;
import org.hibernate.type.BasicType;
import org.hibernate.type.ComponentType;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.OneToOneType;
import org.hibernate.type.spi.TypeConfiguration;
import org.jboss.tools.hibernate.orm.runtime.common.GenericFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ITypeTest {
	
	private BasicType<?> classType = null;
	private BasicType<?> integerType = null;
	private BasicType<?> stringType = null;
	private TypeConfiguration typeConfiguration = null;
	
	@BeforeEach
	public void beforeEach() {
		typeConfiguration = new TypeConfiguration();
		classType = typeConfiguration.getBasicTypeForJavaType(Class.class);
		integerType = typeConfiguration.getBasicTypeForJavaType(Integer.class);
		stringType = typeConfiguration.getBasicTypeForJavaType(String.class);
	}
	
	@Test
	public void testToString() {
		// first try type that is string representable
		IType classTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(classType));
		assertEquals(
				ITypeTest.class.getName(), 
				classTypeFacade.toString(ITypeTest.class));
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
				TypeWrapperFactory.createTypeWrapper(classType));
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
				TypeWrapperFactory.createTypeWrapper(classType));
		assertEquals(
				ITypeTest.class, 
				classTypeFacade.fromStringValue(ITypeTest.class.getName()));
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
				TypeWrapperFactory.createTypeWrapper(classType));
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
					TypeWrapperFactory.createTypeWrapper(classType));
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
				TypeWrapperFactory.createTypeWrapper(classType));
		assertFalse(classTypeFacade.isAnyType());
		// next try a any type
		IType anyTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(new AnyType(null, null, null, true)));
		assertTrue(anyTypeFacade.isAnyType());
	}
	
	@Test
	public void testIsComponentType() {
		// first try type that is not a component type
		IType classTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(classType));
		assertFalse(classTypeFacade.isComponentType());
		// next try a component type
		Component component = new Component(
				DummyMetadataBuildingContext.INSTANCE, 
				new RootClass(DummyMetadataBuildingContext.INSTANCE));
		component.setComponentClassName("java.lang.Object");
		IType componentTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(new ComponentType(component, null)));
		assertTrue(componentTypeFacade.isComponentType());
	}
	
	@Test
	public void testIsCollectionType() {
		// first try type that is not a collection type
		IType classTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(classType));
		assertFalse(classTypeFacade.isCollectionType());
		// next try a collection type
		IType arrayTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(new ArrayType(null, null, String.class)));
		assertTrue(arrayTypeFacade.isCollectionType());
	}
	
	@Test
	public void testGetReturnedClassName() {
		// first try a class type
		IType classTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(classType));
		assertEquals(Class.class.getName(), classTypeFacade.getReturnedClassName());
		// next try an array type of string values
		IType arrayTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(new ArrayType("foo", "bar", String.class)));
		assertEquals(String[].class.getName(), arrayTypeFacade.getReturnedClassName());
		// next try a many to one type 
		TypeConfiguration typeConfiguration = new TypeConfiguration();
		MetadataBuildingContext metadataBuildingContext = DummyMetadataBuildingContext.INSTANCE;
		PersistentClass orgFooBarClass = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		orgFooBarClass.setEntityName("org.foo.bar");
		orgFooBarClass.setClassName(OrgFooBar.class.getName());
		metadataBuildingContext.getMetadataCollector().getEntityBindingMap().put(
				"org.foo.bar", 
				orgFooBarClass);
		typeConfiguration.scope(DummyMetadataBuildingContext.INSTANCE);
		IType manyToOneTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(
						new ManyToOneType(typeConfiguration, "org.foo.bar")));
		assertEquals(OrgFooBar.class.getName(), manyToOneTypeFacade.getReturnedClassName());
	}
	
	@Test
	public void testGetAssociatedEntityName() {
		// first try a class type
		IType classTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(classType));
		assertNull(classTypeFacade.getAssociatedEntityName());
		// next try a many to one type 
		IType manyToOneTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(
						new ManyToOneType((TypeConfiguration)null, "foo")));
		assertEquals("foo", manyToOneTypeFacade.getAssociatedEntityName());
	}
	
	@Test
	public void testIsIntegerType() {
		// first try a class type
		IType classTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(classType));
		assertFalse(classTypeFacade.isIntegerType());
		// next try a integer type 
		IType integerTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(integerType));
		assertTrue(integerTypeFacade.isIntegerType());
	}
	
	@Test
	public void testIsArrayType() {
		// first try a class type
		IType classTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(classType));
		assertFalse(classTypeFacade.isArrayType());
		// next try a bag type
		IType bagTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(new BagType(null, null)));
		assertFalse(bagTypeFacade.isArrayType());
		// finally try a array type
		IType arrayTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(new ArrayType(null, null, String.class)));
		assertTrue(arrayTypeFacade.isArrayType());
	}
	
	@Test
	public void testIsInstanceOfPrimitiveType() {
		// first try a class type
		IType classTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(classType));
		assertFalse(classTypeFacade.isInstanceOfPrimitiveType());
		// next try a string type
		IType stringTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(stringType));
		assertFalse(stringTypeFacade.isInstanceOfPrimitiveType());
		// finally try a integer type 
		IType integerTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(integerType));
		assertTrue(integerTypeFacade.isInstanceOfPrimitiveType());
	}
	
	@Test
	public void testGetPrimitiveClass() {
		// first try a class type
		try {
			IType classTypeFacade = (IType)GenericFacadeFactory.createFacade(
					IType.class, 
					TypeWrapperFactory.createTypeWrapper(classType));
			classTypeFacade.getPrimitiveClass();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getPrimitiveClass()'"));
		}
		// next try a integer type 
		IType integerTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(integerType));
		assertEquals(int.class, integerTypeFacade.getPrimitiveClass());
	}

	@Test
	public void testGetRole() {
		// first try a class type
		try {
			IType classTypeFacade = (IType)GenericFacadeFactory.createFacade(
					IType.class, 
					TypeWrapperFactory.createTypeWrapper(classType));
			classTypeFacade.getRole();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getRole()'"));
		}
		// finally try a array type
		IType arrayTypeFacade = (IType)GenericFacadeFactory.createFacade(
				IType.class, 
				TypeWrapperFactory.createTypeWrapper(new ArrayType("foo", null, String.class)));
		assertEquals("foo", arrayTypeFacade.getRole());
	}
	
	public static class OrgFooBar {}

}
