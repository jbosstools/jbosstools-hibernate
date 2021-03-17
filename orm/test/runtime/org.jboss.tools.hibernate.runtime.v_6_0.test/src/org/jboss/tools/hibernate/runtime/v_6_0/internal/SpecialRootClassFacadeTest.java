package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.hibernate.mapping.Property;
import org.jboss.tools.hibernate.runtime.common.AbstractSpecialRootClassFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.junit.Before;
import org.junit.Test;

public class SpecialRootClassFacadeTest {

	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private SpecialRootClassFacadeImpl specialRootClassFacade = null;
	
	@Before
	public void before() {
		specialRootClassFacade = new SpecialRootClassFacadeImpl(
				FACADE_FACTORY, 
				FACADE_FACTORY.createProperty(new Property()));
	}
	
	@Test
	public void testIsInstanceOfSpecialRootClass() {
		assertTrue(specialRootClassFacade.isInstanceOfSpecialRootClass());
		assertFalse(specialRootClassFacade.isInstanceOfSubclass());
	}
	
	@Test
	public void testGetProperty() throws Exception {
		IProperty property = FACADE_FACTORY.createProperty(new Property());
		Field field = AbstractSpecialRootClassFacade.class.getDeclaredField("property");
		field.setAccessible(true);
		assertNotSame(property, specialRootClassFacade.getProperty());
		field.set(specialRootClassFacade, property);
		assertSame(property, specialRootClassFacade.getProperty());
	}
	
	@Test
	public void testGetParentProperty() throws Exception {
		IProperty property = FACADE_FACTORY.createProperty(new Property());
		Field field = AbstractSpecialRootClassFacade.class.getDeclaredField("parentProperty");
		field.setAccessible(true);
		assertNotSame(property, specialRootClassFacade.getParentProperty());
		field.set(specialRootClassFacade, property);
		assertSame(property, specialRootClassFacade.getParentProperty());
	}
	

}
