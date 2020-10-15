package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.hibernate.type.ArrayType;
import org.hibernate.type.ClassType;
import org.hibernate.type.EntityType;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.OneToOneType;
import org.hibernate.type.spi.TypeConfiguration;
import org.jboss.tools.hibernate.runtime.common.AbstractTypeFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.junit.Assert;
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
	
	@Test
	public void testFromStringValue() {
		IType typeFacade = null;
		// first try type that is string representable
		ClassType classType = new ClassType();
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, classType){};
		assertEquals(
				TypeFacadeTest.class, 
				typeFacade.fromStringValue(TypeFacadeTest.class.getName()));
		// next try type that is not string representable
		ArrayType arrayType = new ArrayType(null, "foo", "bar", String.class);
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, arrayType){};
		assertNull(typeFacade.fromStringValue("just a random string"));
	}
	
	@Test
	public void testIsEntityType() {
		IType typeFacade = null;
		// first try type that is not an entity type
		ClassType classType = new ClassType();
		typeFacade =  new AbstractTypeFacade(FACADE_FACTORY, classType){};
		assertFalse(typeFacade.isEntityType());
		// next try type that is an entity type
		EntityType entityType = new ManyToOneType((TypeConfiguration)null, null);
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, entityType){};
		assertTrue(entityType.isEntityType());
	}
	
	@Test
	public void testIsOneToOne() {
		IType typeFacade = null;
		// first try type that is not a one to one type
		ClassType classType = new ClassType();
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, classType){};
		assertFalse(typeFacade.isOneToOne());
		// next try another type that is not a one to one type
		EntityType entityType = new ManyToOneType((TypeConfiguration)null, null);
		typeFacade = new AbstractTypeFacade(FACADE_FACTORY, entityType){};
		assertFalse(entityType.isOneToOne());
		// finally try a type that is a one to one type
		OneToOneType oneToOneType = new OneToOneType(
				null, null, null, false, null, false, false, null, null, false);
		typeFacade =  new AbstractTypeFacade(FACADE_FACTORY, oneToOneType){};
		Assert.assertTrue(oneToOneType.isOneToOne());
	}
	
	

}
