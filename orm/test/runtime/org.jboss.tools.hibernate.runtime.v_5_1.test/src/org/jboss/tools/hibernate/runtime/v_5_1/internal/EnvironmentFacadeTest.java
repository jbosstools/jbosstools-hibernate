package org.jboss.tools.hibernate.runtime.v_5_1.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.hibernate.cfg.Environment;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EnvironmentFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IEnvironment environmentFacade = null; 
	
	@BeforeEach
	public void beforeEach() throws Exception {
		environmentFacade = new EnvironmentFacadeImpl(FACADE_FACTORY);
	}
	
	@Test
	public void testGetTransactionManagerStrategy() {
		assertEquals(
				"hibernate.transaction.coordinator_class", 
				environmentFacade.getTransactionManagerStrategy());
	}
	
	@Test
	public void testGetDriver() {
		assertEquals(
				Environment.DRIVER, 
				environmentFacade.getDriver());
	}
	
	@Test
	public void testGetHBM2DDLAuto() {
		assertEquals(
				Environment.HBM2DDL_AUTO, 
				environmentFacade.getHBM2DDLAuto());
	}
	
	@Test
	public void testGetDialect() {
		assertEquals(
				Environment.DIALECT, 
				environmentFacade.getDialect());
	}
	
	@Test
	public void testGetDataSource() {
		assertEquals(
				Environment.DATASOURCE, 
				environmentFacade.getDataSource());
	}
	
	@Test
	public void testGetConnectionProvider() {
		assertEquals(
				Environment.CONNECTION_PROVIDER, 
				environmentFacade.getConnectionProvider());
	}
	
	@Test
	public void testGetURL() {
		assertEquals(
				Environment.URL, 
				environmentFacade.getURL());
	}
	
	@Test
	public void testGetUser() {
		assertEquals(
				Environment.USER, 
				environmentFacade.getUser());
	}
	
	@Test
	public void testGetPass() {
		assertEquals(
				Environment.PASS, 
				environmentFacade.getPass());
	}
	
	@Test
	public void testGetSessionFactoryName() {
		assertEquals(
				Environment.SESSION_FACTORY_NAME, 
				environmentFacade.getSessionFactoryName());
	}
	
	@Test
	public void testGetDefaultCatalog() {
		assertEquals(
				Environment.DEFAULT_CATALOG, 
				environmentFacade.getDefaultCatalog());
	}
	
	@Test
	public void testGetDefaultSchema() {
		assertEquals(
				Environment.DEFAULT_SCHEMA, 
				environmentFacade.getDefaultSchema());
	}
	
	@Test
	public void testWrappedClass() {
		assertSame(
				Environment.class, 
				environmentFacade.getWrappedClass());
	}
	
}
