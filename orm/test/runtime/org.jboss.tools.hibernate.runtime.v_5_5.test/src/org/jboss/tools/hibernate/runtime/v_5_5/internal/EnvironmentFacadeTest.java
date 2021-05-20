package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import static org.junit.jupiter.api.Assertions.assertSame;

import org.hibernate.cfg.Environment;
import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EnvironmentFacadeTest {

	private IEnvironment environmentFacade = null; 
	
	@BeforeEach
	public void beforeEach() {
		environmentFacade = new EnvironmentFacadeImpl(new FacadeFactoryImpl());		
	}
	
	@Test
	public void testGetTransactionManagerStrategy() {
		assertSame("hibernate.transaction.coordinator_class", environmentFacade.getTransactionManagerStrategy());
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
	
}
