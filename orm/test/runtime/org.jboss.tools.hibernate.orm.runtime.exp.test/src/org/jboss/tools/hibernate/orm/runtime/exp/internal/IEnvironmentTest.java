package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.GenericFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IEnvironmentTest {
	
	IEnvironment environmentFacade = null;
	
	@BeforeEach
	public void beforeEach() {
		environmentFacade = (IEnvironment)GenericFacadeFactory.createFacade(
				IEnvironment.class, 
				WrapperFactory.createEnvironmentWrapper());
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(environmentFacade);
	}

	@Test
	public void testGetTransactionManagerStrategy() {
		assertEquals("hibernate.transaction.coordinator_class", environmentFacade.getTransactionManagerStrategy());
	}
	
	@Test
	public void testGetDriver() {
		assertEquals("hibernate.connection.driver_class", environmentFacade.getDriver());
	}
	
	@Test
	public void testGetHBM2DDLAuto() {
		assertEquals("hibernate.hbm2ddl.auto", environmentFacade.getHBM2DDLAuto());
	}
	
	@Test
	public void testGetDialect() {
		assertEquals("hibernate.dialect", environmentFacade.getDialect());
	}
	
	@Test
	public void testGetDataSource() {
		assertEquals("hibernate.connection.datasource", environmentFacade.getDataSource());
	}
	
}
