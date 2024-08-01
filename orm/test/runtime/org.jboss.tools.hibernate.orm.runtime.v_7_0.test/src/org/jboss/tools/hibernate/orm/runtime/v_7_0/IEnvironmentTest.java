package org.jboss.tools.hibernate.orm.runtime.v_7_0;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.hibernate.cfg.Environment;
import org.hibernate.tool.orm.jbt.api.factory.WrapperFactory;
import org.jboss.tools.hibernate.orm.runtime.common.GenericFacadeFactory;
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
		assertEquals("jakarta.persistence.jdbc.driver", environmentFacade.getDriver());
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
		assertEquals("jakarta.persistence.jtaDataSource", environmentFacade.getDataSource());
	}
	
	@Test
	public void testGetConnectionProvider() {
		assertEquals("hibernate.connection.provider_class", environmentFacade.getConnectionProvider());
	}
	
	@Test
	public void testGetURL() {
		assertEquals("jakarta.persistence.jdbc.url", environmentFacade.getURL());
	}
	
	@Test
	public void testGetUser() {
		assertEquals("jakarta.persistence.jdbc.user", environmentFacade.getUser());
	}
	
	@Test
	public void testGetPass() {
		assertEquals("jakarta.persistence.jdbc.password", environmentFacade.getPass());
	}
	
	@Test
	public void testGetSessionFactoryName() {
		assertEquals("hibernate.session_factory_name", environmentFacade.getSessionFactoryName());
	}
	
	@Test
	public void testGetDefaultCatalog() {
		assertEquals("hibernate.default_catalog", environmentFacade.getDefaultCatalog());
	}
	
	@Test
	public void testGetDefaultSchema() {
		assertEquals("hibernate.default_schema", environmentFacade.getDefaultSchema());
	}
	
	@Test
	public void testGetWrappedClass() {
		assertSame(Environment.class, environmentFacade.getWrappedClass());
	}
	
}
