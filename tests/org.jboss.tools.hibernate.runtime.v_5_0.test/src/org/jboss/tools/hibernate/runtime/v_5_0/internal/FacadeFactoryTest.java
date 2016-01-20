package org.jboss.tools.hibernate.runtime.v_5_0.internal;

import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.v_5_0.internal.FacadeFactoryImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FacadeFactoryTest {

	private FacadeFactoryImpl facadeFactory;

	@Before
	public void setUp() throws Exception {
		facadeFactory = new FacadeFactoryImpl();
	}
	
	@Test
	public void testFacadeFactoryCreation() {
		Assert.assertNotNull(facadeFactory);
	}
	
	@Test
	public void testGetClassLoader() {
		Assert.assertSame(
				FacadeFactoryImpl.class.getClassLoader(), 
				facadeFactory.getClassLoader());
	}
	
	@Test
	public void testCreateEnvironment() {
		IEnvironment environment = facadeFactory.createEnvironment();
		Assert.assertNotNull(environment);
		Assert.assertTrue(environment instanceof EnvironmentFacadeImpl);
	}
	
	@Test
	public void testCreateSpecialRootClass() {
		Property target = new Property();
		IProperty property = facadeFactory.createProperty(target);
		IPersistentClass specialRootClass = facadeFactory.createSpecialRootClass(property);
		Assert.assertNotNull(specialRootClass);
		Object object = ((IFacade)specialRootClass).getTarget();
		Assert.assertNotNull(object);
		Assert.assertTrue(object instanceof RootClass);
		Assert.assertSame(property, specialRootClass.getProperty());
	}
	
}
