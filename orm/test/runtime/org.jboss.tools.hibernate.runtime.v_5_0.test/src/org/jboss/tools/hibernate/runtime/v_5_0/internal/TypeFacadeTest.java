package org.jboss.tools.hibernate.runtime.v_5_0.internal;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.internal.MetadataBuilderImpl;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataBuildingOptions;
import org.hibernate.boot.spi.MetadataImplementor;
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
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.junit.Assert;
import org.junit.Test;

public class TypeFacadeTest {

	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	@Test
	public void testToString() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		Assert.assertEquals(
				TypeFacadeTest.class.getName(), 
				typeFacade.toString(TypeFacadeTest.class));
		ArrayType arrayType = new ArrayType(null, "foo", "bar", String.class);
		typeFacade = FACADE_FACTORY.createType(arrayType);
		Assert.assertNull(typeFacade.toString(new String[] { "foo", "bar" }));
	}
	
	@Test
	public void testGetName() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		Assert.assertEquals("class", typeFacade.getName());
		ArrayType arrayType = new ArrayType(null, "foo", "bar", String.class);
		typeFacade = FACADE_FACTORY.createType(arrayType);
		Assert.assertEquals("[Ljava.lang.String;(foo)", typeFacade.getName());
	}
	
	@Test
	public void testFromStringValue() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		Assert.assertEquals(
				TypeFacadeTest.class, 
				typeFacade.fromStringValue(TypeFacadeTest.class.getName()));
		ArrayType arrayType = new ArrayType(null, "foo", "bar", String.class);
		typeFacade = FACADE_FACTORY.createType(arrayType);
		Assert.assertNull(typeFacade.fromStringValue("just a random string"));
	}
	
	@Test
	public void testIsEntityType() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		Assert.assertFalse(typeFacade.isEntityType());
		EntityType entityType = new ManyToOneType(null, null);
		typeFacade = FACADE_FACTORY.createType(entityType);
		Assert.assertTrue(entityType.isEntityType());
	}
	
	@Test
	public void testIsOneToOne() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		Assert.assertFalse(typeFacade.isOneToOne());
		EntityType entityType = new ManyToOneType(null, null);
		typeFacade = FACADE_FACTORY.createType(entityType);
		Assert.assertFalse(entityType.isOneToOne());
		OneToOneType oneToOneType = new OneToOneType(
				null, null, null, false, null, false, false, null, null);
		typeFacade = FACADE_FACTORY.createType(oneToOneType);
		Assert.assertTrue(oneToOneType.isOneToOne());
	}
	
	@Test
	public void testIsAnyType() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		Assert.assertFalse(typeFacade.isAnyType());
		AnyType anyType = new AnyType(null, null, null);
		typeFacade = FACADE_FACTORY.createType(anyType);
		Assert.assertTrue(typeFacade.isAnyType());
	}
	
	@Test
	public void testIsComponentType() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		Assert.assertFalse(typeFacade.isComponentType());
		MetadataBuildingOptions mdbo = 
				new MetadataBuilderImpl.MetadataBuildingOptionsImpl(
						new StandardServiceRegistryBuilder().build());
		MetadataImplementor mdi = 
				(MetadataImplementor)new MetadataBuilderImpl(
						new MetadataSources()).build();
		ComponentType componentType = 
				new ComponentType(
						null,
						new ComponentMetamodel(
								new Component(mdi, new RootClass(null)),
								mdbo));
		typeFacade = FACADE_FACTORY.createType(componentType);
		Assert.assertTrue(typeFacade.isComponentType());
	}
	
	@Test
	public void testIsCollectionType() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		Assert.assertFalse(typeFacade.isCollectionType());
		ArrayType arrayType = new ArrayType(null, null, null, String.class);
		typeFacade = FACADE_FACTORY.createType(arrayType);
		Assert.assertTrue(typeFacade.isCollectionType());
	}
	
	@Test
	public void testGetReturnedClass() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		Assert.assertEquals(Class.class, typeFacade.getReturnedClass());
		ArrayType arrayType = new ArrayType(null, null, null, String.class);
		typeFacade = FACADE_FACTORY.createType(arrayType);
		Assert.assertEquals(String[].class, typeFacade.getReturnedClass());
	}
	
	@Test
	public void testGetAssociatedEntityName() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		Assert.assertNull(typeFacade.getAssociatedEntityName());
		EntityType entityType = new ManyToOneType(null, "foo");
		typeFacade = FACADE_FACTORY.createType(entityType);
		Assert.assertEquals("foo", typeFacade.getAssociatedEntityName());
	}
	
	@Test
	public void testIsIntegerType() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		Assert.assertFalse(typeFacade.isIntegerType());
		IntegerType integerType = new IntegerType();
		typeFacade = FACADE_FACTORY.createType(integerType);
		Assert.assertTrue(typeFacade.isIntegerType());
	}
	
	@Test
	public void testIsArrayType() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		Assert.assertFalse(typeFacade.isArrayType());
		BagType bagType = new BagType(null, null, null);
		typeFacade = FACADE_FACTORY.createType(bagType);
		Assert.assertFalse(typeFacade.isArrayType());
		ArrayType arrayType = new ArrayType(null, null, null, String.class);
		typeFacade = FACADE_FACTORY.createType(arrayType);
		Assert.assertTrue(typeFacade.isArrayType());
	}
	
	@Test
	public void testIsInstanceOfPrimitiveType() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		Assert.assertFalse(typeFacade.isInstanceOfPrimitiveType());
		StringType stringType = new StringType();
		typeFacade = FACADE_FACTORY.createType(stringType);
		Assert.assertFalse(typeFacade.isInstanceOfPrimitiveType());
		IntegerType integerType = new IntegerType();
		typeFacade = FACADE_FACTORY.createType(integerType);
		Assert.assertTrue(typeFacade.isInstanceOfPrimitiveType());
	}
	
	@Test
	public void testGetPrimitiveClass() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		Assert.assertNull(typeFacade.getPrimitiveClass());
		IntegerType integerType = new IntegerType();
		typeFacade = FACADE_FACTORY.createType(integerType);
		Assert.assertEquals(int.class, typeFacade.getPrimitiveClass());
	}
	
	@Test
	public void testGetRole() {
		IType typeFacade = null;
		ClassType classType = new ClassType();
		typeFacade = FACADE_FACTORY.createType(classType);
		Assert.assertNull(typeFacade.getRole());
		ArrayType arrayType = new ArrayType(null, "foo", null, String.class);
		typeFacade = FACADE_FACTORY.createType(arrayType);
		Assert.assertEquals("foo", typeFacade.getRole());
	}
	
}
