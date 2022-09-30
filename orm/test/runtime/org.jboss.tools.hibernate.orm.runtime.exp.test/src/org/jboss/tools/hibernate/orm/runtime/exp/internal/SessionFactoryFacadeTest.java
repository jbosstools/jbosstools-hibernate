package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryDelegatingImpl;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.MockConnectionProvider;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.MockDialect;
import org.jboss.tools.hibernate.runtime.common.AbstractSessionFactoryFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.ICollectionMetadata;
import org.jboss.tools.hibernate.runtime.spi.ISession;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class SessionFactoryFacadeTest {
	
	private static final String TEST_CFG_XML_STRING =
			"<hibernate-configuration>" +
			"  <session-factory>" + 
			"    <property name='" + AvailableSettings.DIALECT + "'>" + MockDialect.class.getName() + "</property>" +
			"    <property name='" + AvailableSettings.CONNECTION_PROVIDER + "'>" + MockConnectionProvider.class.getName() + "</property>" +
			"  </session-factory>" +
			"</hibernate-configuration>";
	
	private static final String TEST_HBM_XML_STRING =
			"<hibernate-mapping package='org.jboss.tools.hibernate.orm.runtime.exp.internal'>" +
			"  <class name='SessionFactoryFacadeTest$Foo'>" + 
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
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	@TempDir
	public File tempDir;
	
	private SessionFactory sessionFactoryTarget = null;
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
		Configuration configuration = new Configuration();
		configuration.addFile(hbmXmlFile);
		configuration.configure(cfgXmlFile);
		sessionFactoryTarget = new TestSessionFactory(configuration.buildSessionFactory());
		sessionFactoryFacade = new SessionFactoryFacadeImpl(FACADE_FACTORY, sessionFactoryTarget);
	}
	
	@Test
	public void testClose() {
		assertFalse(sessionFactoryTarget.isClosed());
		sessionFactoryFacade.close();
		assertTrue(sessionFactoryTarget.isClosed());
	}
	
	@Test
	public void testGetAllClassMetadata() throws Exception {
		Field field = AbstractSessionFactoryFacade.class.getDeclaredField("allClassMetadata");
		field.setAccessible(true);
		assertNull(field.get(sessionFactoryFacade));
		Map<String, IClassMetadata> allClassMetadata = sessionFactoryFacade.getAllClassMetadata();
		assertNotNull(field.get(sessionFactoryFacade));
		assertEquals(1, allClassMetadata.size());
		assertNotNull(allClassMetadata.get(Foo.class.getName()));
	}
	
	@Test
	public void testGetAllCollectionMetadata() throws Exception {
		Field field = AbstractSessionFactoryFacade.class.getDeclaredField("allCollectionMetadata");
		field.setAccessible(true);
		assertNull(field.get(sessionFactoryFacade));
		Map<String, ICollectionMetadata> allCollectionMetadata = sessionFactoryFacade.getAllCollectionMetadata();
		assertNotNull(field.get(sessionFactoryFacade));
		assertEquals(1, allCollectionMetadata.size());
		assertNotNull(allCollectionMetadata.get(Foo.class.getName() + ".bars"));
	}
	
	@Test
	public void testOpenSession() throws Exception {
		assertNull(((TestSessionFactory)sessionFactoryTarget).session);
		ISession sessionFacade = sessionFactoryFacade.openSession();
		assertNotNull(((TestSessionFactory)sessionFactoryTarget).session);
		assertSame(((TestSessionFactory)sessionFactoryTarget).session, ((IFacade)sessionFacade).getTarget());
	}
	
	@Test
	public void testGetClassMetadata() throws Exception {
		Field field = AbstractSessionFactoryFacade.class.getDeclaredField("allClassMetadata");
		field.setAccessible(true);
		assertNull(field.get(sessionFactoryFacade));
		assertNull(sessionFactoryFacade.getClassMetadata("foo"));
		assertNotNull(field.get(sessionFactoryFacade));
		field.set(sessionFactoryFacade, null);
		assertNotNull(sessionFactoryFacade.getClassMetadata(Foo.class.getName()));
		assertNotNull(field.get(sessionFactoryFacade));
		field.set(sessionFactoryFacade, null);
		assertNull(sessionFactoryFacade.getClassMetadata(Object.class));
		assertNotNull(field.get(sessionFactoryFacade));
		field.set(sessionFactoryFacade, null);
		assertNotNull(sessionFactoryFacade.getClassMetadata(Foo.class));
		assertNotNull(field.get(sessionFactoryFacade));
	}
	
	@Test
	public void testGetCollectionMetadata() throws Exception {
		Field field = AbstractSessionFactoryFacade.class.getDeclaredField("allCollectionMetadata");
		field.setAccessible(true);
		assertNull(field.get(sessionFactoryFacade));
		assertNull(sessionFactoryFacade.getCollectionMetadata("bars"));
		assertNotNull(field.get(sessionFactoryFacade));
		field.set(sessionFactoryFacade, null);
		assertNotNull(sessionFactoryFacade.getCollectionMetadata(Foo.class.getName() + ".bars"));
		assertNotNull(field.get(sessionFactoryFacade));
	}	
	
	private class TestSessionFactory extends SessionFactoryDelegatingImpl {
		private static final long serialVersionUID = 1L;
		Session session = null;
		public TestSessionFactory(SessionFactory delegate) {
			super((SessionFactoryImplementor)delegate);
		}
		@Override
		public Session openSession() {
			this.session = super.openSession();
			return this.session;
		}
	}
	
}
