package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryDelegatingImpl;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class SessionFactoryFacadeTest {

	public static class TestDialect extends Dialect {}
	
	private static final String TEST_CFG_XML_STRING =
			"<hibernate-configuration>" +
			"  <session-factory name='bar'>" + 
			"    <property name='hibernate.dialect'>" + TestDialect.class.getName() + "</property>" +
			"  </session-factory>" +
			"</hibernate-configuration>";
	
	private static final String TEST_HBM_XML_STRING =
			"<hibernate-mapping package='org.jboss.tools.hibernate.runtime.v_5_5.internal'>" +
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
	public File tempDir = new File("tempDir");
	
	private SessionFactory sessionFactoryTarget = null;
	private ISessionFactory sessionFactoryFacade = null;
	
	@BeforeEach
	public void beforeEach() throws Exception {
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
