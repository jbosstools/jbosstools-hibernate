package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.RootClass;
import org.hibernate.tuple.component.ComponentMetamodel;
import org.hibernate.type.AnyType;
import org.hibernate.type.ArrayType;
import org.hibernate.type.BagType;
import org.hibernate.type.ClassType;
import org.hibernate.type.ComponentType;
import org.hibernate.type.EntityType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.OneToOneType;
import org.hibernate.type.StringType;
import org.hibernate.type.TypeFactory.TypeScope;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.junit.jupiter.api.Test;

public class TypeFacadeTest {

	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	public static class TestDialect extends Dialect {}
	
	@Test
	public void testToString() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		assertEquals(
				TypeFacadeTest.class.getName(), 
				typeFacade.toString(TypeFacadeTest.class));
		ArrayType arrayType = new ArrayType(null, "foo", "bar", String.class, false);
		typeFacade = FACADE_FACTORY.createType(arrayType);
		assertNull(typeFacade.toString(new String[] { "foo", "bar" }));
	}
	
	@Test
	public void testGetName() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		assertEquals("class", typeFacade.getName());
		ArrayType arrayType = new ArrayType(null, "foo", "bar", String.class, false);
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
		ArrayType arrayType = new ArrayType(null, "foo", "bar", String.class, false);
		typeFacade = FACADE_FACTORY.createType(arrayType);
		assertNull(typeFacade.fromStringValue("just a random string"));
	}
	
	@Test
	public void testIsEntityType() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		assertFalse(typeFacade.isEntityType());
		EntityType entityType = new ManyToOneType((TypeScope)null, null);
		typeFacade = FACADE_FACTORY.createType(entityType);
		assertTrue(entityType.isEntityType());
	}
	
	@Test
	public void testIsOneToOne() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		assertFalse(typeFacade.isOneToOne());
		EntityType entityType = new ManyToOneType((TypeScope)null, null);
		typeFacade = FACADE_FACTORY.createType(entityType);
		assertFalse(entityType.isOneToOne());
		OneToOneType oneToOneType = new OneToOneType(
				null, null, null, null, false, false, false, null, null);
		typeFacade = FACADE_FACTORY.createType(oneToOneType);
		assertTrue(oneToOneType.isOneToOne());
	}
	
	@Test
	public void testIsAnyType() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		assertFalse(typeFacade.isAnyType());
		AnyType anyType = new AnyType(null, null);
		typeFacade = FACADE_FACTORY.createType(anyType);
		assertTrue(typeFacade.isAnyType());
	}
	
	@Test
	public void testIsComponentType() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		assertFalse(typeFacade.isComponentType());
		ComponentType componentType = 
				new ComponentType(
						null,
						new ComponentMetamodel(
								new Component(new Configuration().createMappings(), 
								new RootClass())));
		typeFacade = FACADE_FACTORY.createType(componentType);
		assertTrue(typeFacade.isComponentType());
	}
	
	@Test
	public void testIsCollectionType() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		assertFalse(typeFacade.isCollectionType());
		ArrayType arrayType = new ArrayType(null, null, null, String.class, false);
		typeFacade = FACADE_FACTORY.createType(arrayType);
		assertTrue(typeFacade.isCollectionType());
	}
	
	@Test
	public void testGetReturnedClass() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		assertEquals(Class.class.getName(), typeFacade.getReturnedClassName());
		ArrayType arrayType = new ArrayType(null, null, null, String.class, false);
		typeFacade = FACADE_FACTORY.createType(arrayType);
		assertEquals(String[].class.getName(), typeFacade.getReturnedClassName());
	}
	
	@Test
	public void testGetAssociatedEntityName() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		assertNull(typeFacade.getAssociatedEntityName());
		EntityType entityType = new ManyToOneType((TypeScope)null, "foo");
		typeFacade = FACADE_FACTORY.createType(entityType);
		assertEquals("foo", typeFacade.getAssociatedEntityName());
	}
	
	@Test
	public void testIsIntegerType() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		assertFalse(typeFacade.isIntegerType());
		IntegerType integerType = new IntegerType();
		typeFacade = FACADE_FACTORY.createType(integerType);
		assertTrue(typeFacade.isIntegerType());
	}
	
	@Test
	public void testIsArrayType() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		assertFalse(typeFacade.isArrayType());
		BagType bagType = new BagType(null, null, null, false);
		typeFacade = FACADE_FACTORY.createType(bagType);
		assertFalse(typeFacade.isArrayType());
		ArrayType arrayType = new ArrayType(null, null, null, String.class, false);
		typeFacade = FACADE_FACTORY.createType(arrayType);
		assertTrue(typeFacade.isArrayType());
	}
	
	@Test
	public void testIsInstanceOfPrimitiveType() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		assertFalse(typeFacade.isInstanceOfPrimitiveType());
		StringType stringType = new StringType();
		typeFacade = FACADE_FACTORY.createType(stringType);
		assertFalse(typeFacade.isInstanceOfPrimitiveType());
		IntegerType integerType = new IntegerType();
		typeFacade = FACADE_FACTORY.createType(integerType);
		assertTrue(typeFacade.isInstanceOfPrimitiveType());
	}
	
	@Test
	public void testGetPrimitiveClass() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		assertNull(typeFacade.getPrimitiveClass());
		IntegerType integerType = new IntegerType();
		typeFacade = FACADE_FACTORY.createType(integerType);
		assertEquals(int.class, typeFacade.getPrimitiveClass());
	}
	
	@Test
	public void testGetRole() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		assertNull(typeFacade.getRole());
		ArrayType arrayType = new ArrayType(null, "foo", null, String.class, false);
		typeFacade = FACADE_FACTORY.createType(arrayType);
		assertEquals("foo", typeFacade.getRole());
	}
	
}
