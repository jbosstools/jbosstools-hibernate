package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import org.h2.Driver;
import org.hibernate.query.Query;
import org.hibernate.query.spi.QueryParameterBinding;
import org.hibernate.query.sqm.internal.QuerySqmImpl;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;
import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.GenericFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IQuery;
import org.jboss.tools.hibernate.runtime.spi.ISession;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;
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
	
	private static final IType DUMMY_TYPE = (IType)Proxy.newProxyInstance(
			IQueryTest.class.getClassLoader(), 
			new Class[] { IType.class }, 
			new InvocationHandler() {			
				@Override
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					return null;
				}
			});
	
	@BeforeAll
	public static void beforeAll() throws Exception {
		DriverManager.registerDriver(new Driver());		
	}

	@TempDir
	public File tempDir;
	
	private IQuery simpleQueryFacade = null;
	private Query<?> simpleQueryTarget = null;
	
	private IQuery namedParameterizedQueryFacade = null;
	private Query<?> namedParameterizedQueryTarget = null;
	
	private IQuery positionalParameterizedQueryFacade = null;
	private Query<?> positionalParameterizedQueryTarget = null;
	
	private ISessionFactory sessionFactoryFacade = null;
	private Connection connection = null;
	private Statement statement = null;
		
	@BeforeEach
	public void beforeEach() throws Exception {
		tempDir = Files.createTempDirectory("temp").toFile();
		createDatabase();
		createSessionFactoryFacade();
		ISession sessionFacade = sessionFactoryFacade.openSession();
		simpleQueryFacade = sessionFacade.createQuery("from " + Foo.class.getName());
		simpleQueryTarget = (Query<?>)((IFacade)simpleQueryFacade).getTarget();
		namedParameterizedQueryFacade = sessionFacade.createQuery(
				"from " + Foo.class.getName() + " where id = :foo");
		namedParameterizedQueryTarget = (Query<?>)((IFacade)namedParameterizedQueryFacade).getTarget();
		positionalParameterizedQueryFacade = sessionFacade.createQuery(
				"from " + Foo.class.getName() + " where id = ?1");
		positionalParameterizedQueryTarget = (Query<?>)((IFacade)positionalParameterizedQueryFacade).getTarget();
	}
	
	@AfterEach
	public void afterEach() throws Exception {
		dropDatabase();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(simpleQueryFacade);
		assertNotNull(simpleQueryTarget);
		assertTrue(simpleQueryTarget instanceof Wrapper);
		assertNotNull(namedParameterizedQueryFacade);
		assertNotNull(namedParameterizedQueryTarget);
		assertTrue(namedParameterizedQueryTarget instanceof Wrapper);
		assertNotNull(positionalParameterizedQueryFacade);
		assertNotNull(positionalParameterizedQueryTarget);
		assertTrue(positionalParameterizedQueryTarget instanceof Wrapper);
	}
	
	@Test
	public void testList() throws Exception {
		List<Object> result = simpleQueryFacade.list();
		assertTrue(result.isEmpty());
		statement.execute("INSERT INTO FOO VALUES(1, 'bars')");
		result = simpleQueryFacade.list();
		assertEquals(1, result.size());
		Object obj = result.get(0);
		assertTrue(obj instanceof Foo);
		Foo foo = (Foo)obj;
		assertEquals(1, foo.id);
		assertEquals("bars", foo.bars);
	}
	
	@Test
	public void testSetMaxResults() {
		simpleQueryFacade.setMaxResults(1);
		assertEquals(1, simpleQueryTarget.getMaxResults());
		simpleQueryFacade.setMaxResults(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, simpleQueryTarget.getMaxResults());
	}
	
	@Test
	public void testSetParameterList() {
		QueryParameterBinding<?> binding = 
				((QuerySqmImpl<?>)((Wrapper)namedParameterizedQueryTarget).getWrappedObject())
				.getParameterBindings()
				.getBinding("foo");
		assertFalse(binding.isBound());
		namedParameterizedQueryFacade.setParameterList("foo", Arrays.asList(1), DUMMY_TYPE);
		assertTrue(binding.isBound());
	}
	
	@Test
	public void testSetNamedParameter() {
		QueryParameterBinding<?> binding = 
				((QuerySqmImpl<?>)((Wrapper)namedParameterizedQueryTarget).getWrappedObject())
				.getParameterBindings()
				.getBinding("foo");
		assertFalse(binding.isBound());
		namedParameterizedQueryFacade.setParameter("foo", 1, DUMMY_TYPE);
		assertTrue(binding.isBound());
		assertEquals(1, binding.getBindValue());
	}
	
	@Test
	public void testSetPositionalParameter() {
		QueryParameterBinding<?> binding = 
				((QuerySqmImpl<?>)((Wrapper)positionalParameterizedQueryTarget).getWrappedObject())
				.getParameterBindings()
				.getBinding(1);
		assertFalse(binding.isBound());
		positionalParameterizedQueryFacade.setParameter(1, 1, DUMMY_TYPE);
		assertTrue(binding.isBound());
		assertEquals(1, binding.getBindValue());
	}
	
	@Test
	public void testGetReturnAliases() {
		String[] returnAliases = simpleQueryFacade.getReturnAliases();
		assertNotNull(returnAliases);
		assertEquals(0, returnAliases.length);
	}
	
	@Test
	public void testGetReturnTypes() {
		IType[] returnTypes = simpleQueryFacade.getReturnTypes();
		assertNotNull(returnTypes);
		assertEquals(0, returnTypes.length);
	}
	
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
		IConfiguration configuration = (IConfiguration)GenericFacadeFactory.createFacade(
				IConfiguration.class, 
				WrapperFactory.createNativeConfigurationWrapper());
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
