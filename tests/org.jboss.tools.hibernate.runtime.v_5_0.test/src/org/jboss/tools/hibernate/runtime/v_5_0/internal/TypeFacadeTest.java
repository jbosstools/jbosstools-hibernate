package org.jboss.tools.hibernate.runtime.v_5_0.internal;

import org.hibernate.type.AnyType;
import org.hibernate.type.ArrayType;
import org.hibernate.type.ClassType;
import org.hibernate.type.EntityType;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.OneToOneType;
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
	
}
