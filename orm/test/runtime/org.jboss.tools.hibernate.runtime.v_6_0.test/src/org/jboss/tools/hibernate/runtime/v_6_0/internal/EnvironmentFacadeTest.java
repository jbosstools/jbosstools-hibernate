package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertSame;

import org.hibernate.cfg.Environment;
import org.jboss.tools.hibernate.runtime.common.AbstractEnvironmentFacade;
import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.junit.Before;
import org.junit.Test;

public class EnvironmentFacadeTest {
	
	private IEnvironment environmentFacade = null; 
	
	@Before
	public void before() {
		environmentFacade = new AbstractEnvironmentFacade(new FacadeFactoryImpl(), null) {};		
	}
	
	@Test
	public void testGetDriver() {
		assertSame(Environment.DRIVER, environmentFacade.getDriver());
	}
	
	@Test
	public void testGetHBM2DDLAuto() {
		assertSame(Environment.HBM2DDL_AUTO, environmentFacade.getHBM2DDLAuto());
	}
	
	@Test
	public void testGetDialect() {
		assertSame(Environment.DIALECT, environmentFacade.getDialect());
	}
	
	@Test
	public void testGetDataSource() {
		assertSame(Environment.DATASOURCE, environmentFacade.getDataSource());
	}
	
	@Test
	public void testGetConnectionProvider() {
		assertSame(Environment.CONNECTION_PROVIDER, environmentFacade.getConnectionProvider());
	}
	
	@Test
	public void testGetURL() {
		assertSame(Environment.URL, environmentFacade.getURL());
	}
	
	@Test
	public void testGetUser() {
		assertSame(Environment.USER, environmentFacade.getUser());
	}
	
	@Test
	public void testGetPass() {
		assertSame(Environment.PASS, environmentFacade.getPass());
	}
	
	@Test
	public void testGetSessionFactoryName() {
		assertSame(Environment.SESSION_FACTORY_NAME, environmentFacade.getSessionFactoryName());
	}
	
	@Test
	public void testGetDefaultCatalog() {
		assertSame(Environment.DEFAULT_CATALOG, environmentFacade.getDefaultCatalog());
	}
	
	@Test
	public void testGetDefaultSchema() {
		assertSame(Environment.DEFAULT_SCHEMA, environmentFacade.getDefaultSchema());
	}
	
	@Test
	public void testGetWrappedClass() {
		assertSame(Environment.class, environmentFacade.getWrappedClass());
	}
	
}
