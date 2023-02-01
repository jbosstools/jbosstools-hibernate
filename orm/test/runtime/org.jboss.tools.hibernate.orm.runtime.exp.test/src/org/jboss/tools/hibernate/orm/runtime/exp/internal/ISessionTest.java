package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.query.Query;
import org.hibernate.tool.orm.jbt.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.util.MockDialect;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.jboss.tools.hibernate.runtime.spi.IQuery;
import org.jboss.tools.hibernate.runtime.spi.ISession;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ISessionTest {

	private static final String TEST_CFG_XML_STRING =
			"<hibernate-configuration>" +
			"  <session-factory>" + 
			"    <property name='" + AvailableSettings.DIALECT + "'>" + MockDialect.class.getName() + "</property>" +
			"    <property name='" + AvailableSettings.CONNECTION_PROVIDER + "'>" + MockConnectionProvider.class.getName() + "</property>" +
			"  </session-factory>" +
			"</hibernate-configuration>";
	
	private static final String TEST_HBM_XML_STRING =
			"<hibernate-mapping package='org.jboss.tools.hibernate.orm.runtime.exp.internal'>" +
			"  <class name='ISessionTest$Foo'>" + 
			"    <id name='id' access='field' />" +
			"    <set name='bars' access='field' >" +
			"      <key column='barId' />" +
			"      <element column='barVal' type='string' />" +
			"    </set>" +
			"  </class>" +
			"</hibernate-mapping>";
	
	static class Foo {
		public String id;
		public Set<String> bars = new HashSet<String>();
	}
	
	private static final NewFacadeFactory FACADE_FACTORY = NewFacadeFactory.INSTANCE;
	
	@TempDir
	public File tempDir;
	
	private Session sessionTarget = null;
	private ISession sessionFacade = null;
	
	private ISessionFactory sessionFactoryFacade = null;
	
	@BeforeEach
	public void beforeEach() throws Exception {
		tempDir = Files.createTempDirectory("temp").toFile();
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
		sessionFacade = sessionFactoryFacade.openSession();
		sessionTarget = (Session)((IFacade)sessionFacade).getTarget();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(sessionFacade);
		assertNotNull(sessionTarget);
	}
	
	@Test
	public void testGetEntityName() {
		Foo foo = new Foo();
		foo.id = "bar";
		sessionTarget.persist(foo);
		assertEquals(Foo.class.getName(), sessionFacade.getEntityName(foo));
	}
	
	@Test
	public void testGetSessionFactory() {		
		assertEquals(sessionFactoryFacade, sessionFacade.getSessionFactory());
		assertSame(
				((IFacade)sessionFactoryFacade).getTarget(), 
				((IFacade)sessionFacade.getSessionFactory()).getTarget());
	}
	
	@Test
	public void testCreateQuery() {
		IQuery queryFacade = sessionFacade.createQuery("from " + Foo.class.getName());
		assertNotNull(queryFacade);
		assertTrue(Proxy.isProxyClass(queryFacade.getClass()));
		assertTrue(((IFacade)queryFacade).getTarget() instanceof Query<?>);
	}
	
	@Test
	public void testIsOpen() {
		assertTrue(sessionFacade.isOpen());
		sessionTarget.close();
		assertFalse(sessionFacade.isOpen());
	}
	
	@Test
	public void testClose() {
		assertTrue(sessionTarget.isOpen());
		sessionFacade.close();
		assertFalse(sessionTarget.isOpen());
	}
	
	@Test
	public void testContains() {
		Foo first = new Foo();
		first.id = "1";
		sessionTarget.persist(first);
		Foo second = new Foo();
		assertTrue(sessionFacade.contains(first));
		assertFalse(sessionFacade.contains(second));
		assertFalse(sessionFacade.contains("blah"));
	}
	
	@Test
	public void testCreateCriteria() {
		ICriteria criteria = sessionFacade.createCriteria(Foo.class);
		assertNotNull(criteria);
		assertTrue(((IFacade)criteria).getTarget() instanceof jakarta.persistence.Query);
	}

}
