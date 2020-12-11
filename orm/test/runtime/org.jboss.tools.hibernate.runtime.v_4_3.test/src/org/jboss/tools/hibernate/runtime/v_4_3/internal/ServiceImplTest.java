package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.JDBCReader;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IDatabaseReader;
import org.jboss.tools.hibernate.runtime.spi.IProgressListener;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ITable;
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
	public void testNewDatabaseReader() {
		IConfiguration configuration = 
				FACADE_FACTORY.createConfiguration(
						new Configuration());
		IReverseEngineeringStrategy engineeringStrategy = 
				FACADE_FACTORY.createReverseEngineeringStrategy(
						new DefaultReverseEngineeringStrategy());
		IDatabaseReader databaseReaderFacade = service.newDatabaseReader(
				configuration.getProperties(), 
				engineeringStrategy);
		Assert.assertNotNull(databaseReaderFacade);
		JDBCReader reader = (JDBCReader)((IFacade)databaseReaderFacade).getTarget();
		Assert.assertNotNull(reader);		
	}
	
	@Test
	public void testCollectDatabaseTables() throws Exception {
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:test");
		Statement statement = connection.createStatement();
		statement.execute("CREATE TABLE FOO(id int primary key, bar varchar(255))");
		Properties properties = new Properties();
		properties.put("hibernate.connection.url", "jdbc:h2:mem:test");
		Map<String, List<ITable>> tableMap = service.collectDatabaseTables(
				properties, 
				FACADE_FACTORY.createReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy()),
				new IProgressListener() {				
					@Override public void startSubTask(String name) {}
				});
		assertEquals(1, tableMap.size());
		List<ITable> tables = tableMap.get("TEST.PUBLIC");
		assertEquals(1, tables.size());
		ITable table = tables.get(0);
		assertEquals("TEST", table.getCatalog());
		assertEquals("PUBLIC", table.getSchema());
		assertEquals("FOO", table.getName());
		statement.execute("DROP TABLE FOO");
		statement.close();
		connection.close();
	}
	
	@Test
	public void testNewDialect() throws Exception {
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:");
		String dialect = service.newDialect(new Properties(), connection);
		Assert.assertEquals("org.hibernate.dialect.H2Dialect", dialect);
	}

	@Test
	public void testNewTable() {
		ITable table = service.newTable("foo");
		Assert.assertNotNull(table);
		Object target = ((IFacade)table).getTarget();
		Assert.assertNotNull(target);
		Assert.assertTrue(target instanceof Table);
		Assert.assertEquals("foo", ((Table)target).getName());
		Assert.assertNotNull(((Table)target).getPrimaryKey());
	}
	
}
