package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import org.h2.Driver;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IQuery;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class IQueryTest {

	private static final String TEST_CFG_XML_STRING =
			"<hibernate-configuration>" +
			"  <session-factory>" + 
			"    <property name='hibernate.connection.url'>jdbc:h2:mem:test</property>" +
			"  </session-factory>" +
			"</hibernate-configuration>";
	
	private static final String TEST_HBM_XML_STRING =
			"<hibernate-mapping package='org.jboss.tools.hibernate.orm.runtime.exp.internal'>" +
			"  <class name='IQueryTest$Foo' table='FOO'>" + 
			"    <id name='id' access='field' />" +
			"    <property name='bars' access='field' type='string'/>" +
			"  </class>" +
			"</hibernate-mapping>";
	
	static class Foo {
		public int id;
		public String bars;
	}
	
	private static final NewFacadeFactory FACADE_FACTORY = NewFacadeFactory.INSTANCE;
	
	@BeforeAll
	public static void beforeAll() throws Exception {
		DriverManager.registerDriver(new Driver());		
	}

	@TempDir
	public File tempDir;
	
	private IQuery queryFacade = null;
	private Query<?> queryTarget = null;
	
	private ISessionFactory sessionFactoryFacade = null;
	
	@BeforeEach
	public void beforeEach() throws Exception {
		tempDir = Files.createTempDirectory("temp").toFile();
		createDatabase();
		createSessionFactoryFacade();
		queryFacade = sessionFactoryFacade.openSession().createQuery("from " + Foo.class.getName());
		queryTarget = (Query<?>)((IFacade)queryFacade).getTarget();
	}
	
	@AfterEach
	public void afterEach() throws Exception {
		dropDatabase();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(queryFacade);
		assertNotNull(queryTarget);
	}
	
	@Test
	public void testList() throws Exception {
		List<Object> result = queryFacade.list();
		assertTrue(result.isEmpty());
		statement.execute("INSERT INTO FOO VALUES(1, 'bars')");
		result = queryFacade.list();
		assertEquals(1, result.size());
		Object obj = result.get(0);
		assertTrue(obj instanceof Foo);
		Foo foo = (Foo)obj;
		assertEquals(1, foo.id);
		assertEquals("bars", foo.bars);
	}
	
	private Connection connection = null;
	private Statement statement = null;
	
	private void createDatabase() throws Exception {
		connection = DriverManager.getConnection("jdbc:h2:mem:test");
		statement = connection.createStatement();
		statement.execute("CREATE TABLE FOO(id int primary key, bars varchar(255))");
	}
	
	private void createSessionFactoryFacade() throws Exception {
		File cfgXmlFile = new File(tempDir, "hibernate.cfg.xml");
		FileWriter fileWriter = new FileWriter(cfgXmlFile);
		fileWriter.write(TEST_CFG_XML_STRING);
		fileWriter.close();
		File hbmXmlFile = new File(tempDir, "Foo.hbm.xml");
		fileWriter = new FileWriter(hbmXmlFile);
		fileWriter.write(TEST_HBM_XML_STRING);
		fileWriter.close();
		IConfiguration configuration = FACADE_FACTORY.createNativeConfiguration();
		configuration.addFile(hbmXmlFile);
		configuration.configure(cfgXmlFile);
		sessionFactoryFacade = configuration.buildSessionFactory();
	}
	
	private void dropDatabase() throws Exception {
		statement.execute("DROP TABLE FOO");
		statement.close();
		connection.close();
	}
	
}
