package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCReaderFactory;
import org.hibernate.cfg.reveng.DatabaseCollector;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.JDBCReader;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IDatabaseCollector;
import org.jboss.tools.hibernate.runtime.spi.IJDBCReader;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.junit.Assert;
import org.junit.Test;

public class ServiceImplTest {
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private ServiceImpl service = new ServiceImpl();

	@Test
	public void testNewAnnotationConfiguration() {
		IConfiguration configuration = service.newAnnotationConfiguration();
		Assert.assertNotNull(configuration);
		Object target = ((IFacade)configuration).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof Configuration);
	}
	
	@Test
	public void testNewJDBCReader() {
		IConfiguration configuration = 
				FACADE_FACTORY.createConfiguration(
						new Configuration());
		IReverseEngineeringStrategy engineeringStrategy = 
				FACADE_FACTORY.createReverseEngineeringStrategy(
						new DefaultReverseEngineeringStrategy());
		IJDBCReader jdbcReaderFacade = service.newJDBCReader(
				configuration, 
				engineeringStrategy);
		Assert.assertNotNull(jdbcReaderFacade);
		JDBCReader reader = (JDBCReader)((IFacade)jdbcReaderFacade).getTarget();
		Assert.assertNotNull(reader);		
	}
	
	@Test
	public void testNewDatabaseCollector() {
		IJDBCReader jdbcReader = FACADE_FACTORY.createJDBCReader(
				JDBCReaderFactory.newJDBCReader(
						new Configuration().getProperties(), 
						new DefaultReverseEngineeringStrategy(),
						new StandardServiceRegistryBuilder().build()));
		IDatabaseCollector databaseCollectorFacade = 
				service.newDatabaseCollector(jdbcReader);
		Assert.assertNotNull(databaseCollectorFacade);
		DatabaseCollector databaseCollector = 
				(DatabaseCollector)((IFacade)databaseCollectorFacade).getTarget();
		Assert.assertNotNull(databaseCollector);
	}
	
	@Test
	public void testNewDialect() throws Exception {
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:");
		String dialect = service.newDialect(new Properties(), connection);
		Assert.assertEquals("org.hibernate.dialect.H2Dialect", dialect);
	}

}
