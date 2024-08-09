package org.jboss.tools.hibernate.orm.runtime.v_6_6;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.orm.jbt.api.factory.WrapperFactory;
import org.hibernate.tool.orm.jbt.api.wrp.Wrapper;
import org.hibernate.tool.orm.jbt.internal.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.internal.util.MockDialect;
import org.jboss.tools.hibernate.orm.runtime.common.GenericFacadeFactory;
import org.jboss.tools.hibernate.orm.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.ICollectionMetadata;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.ISession;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ISessionFactoryTest {

	private static final String TEST_CFG_XML_STRING =
			"<hibernate-configuration>" +
			"  <session-factory>" + 
			"    <property name='" + AvailableSettings.DIALECT + "'>" + MockDialect.class.getName() + "</property>" +
			"    <property name='" + AvailableSettings.CONNECTION_PROVIDER + "'>" + MockConnectionProvider.class.getName() + "</property>" +
			"  </session-factory>" +
			"</hibernate-configuration>";
	
	private static final String TEST_HBM_XML_STRING =
			"<hibernate-mapping package='org.jboss.tools.hibernate.orm.runtime.v_6_6'>" +
			"  <class name='ISessionFactoryTest$Foo'>" + 
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
		IConfiguration configuration = (IConfiguration)GenericFacadeFactory.createFacade(
				IConfiguration.class, 
				WrapperFactory.createNativeConfigurationWrapper());
		configuration.addFile(hbmXmlFile);
		configuration.configure(cfgXmlFile);
		sessionFactoryFacade = configuration.buildSessionFactory();
		Wrapper wrapper = (Wrapper)((IFacade)sessionFactoryFacade).getTarget();
		sessionFactoryTarget = (SessionFactory)wrapper.getWrappedObject();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(sessionFactoryFacade);
		assertNotNull(sessionFactoryTarget);
	}
	
	@Test
	public void testClose() {
		assertFalse(sessionFactoryTarget.isClosed());
		sessionFactoryFacade.close();
		assertTrue(sessionFactoryTarget.isClosed());
	}
	
	@Test
	public void testGetAllClassMetadata() throws Exception {
		Map<String, IClassMetadata> allClassMetadata = sessionFactoryFacade.getAllClassMetadata();
		assertEquals(1, allClassMetadata.size());
		assertNotNull(allClassMetadata.get(Foo.class.getName()));
	}
	
	@Test
	public void testGetAllCollectionMetadata() throws Exception {
		Map<String, ICollectionMetadata> allCollectionMetadata = sessionFactoryFacade.getAllCollectionMetadata();
		assertEquals(1, allCollectionMetadata.size());
		assertNotNull(allCollectionMetadata.get(Foo.class.getName() + ".bars"));
	}
	
	@Test
	public void testOpenSession() throws Exception {
		ISession sessionFacade = sessionFactoryFacade.openSession();
		assertNotNull(sessionFacade);
		assertTrue(sessionFacade instanceof ISession);
		Wrapper sessionWrapper = (Wrapper)((IFacade)sessionFacade).getTarget();
		assertTrue(sessionWrapper.getWrappedObject() instanceof Session);
	}
	
	@Test
	public void testGetClassMetadata() throws Exception {
		assertNull(sessionFactoryFacade.getClassMetadata("foo"));
		assertNotNull(sessionFactoryFacade.getClassMetadata(Foo.class.getName()));
		assertNull(sessionFactoryFacade.getClassMetadata(Object.class));
		assertNotNull(sessionFactoryFacade.getClassMetadata(Foo.class));
	}
	
	@Test
	public void testGetCollectionMetadata() throws Exception {
		assertNull(sessionFactoryFacade.getCollectionMetadata("bars"));
		assertNotNull(sessionFactoryFacade.getCollectionMetadata(Foo.class.getName() + ".bars"));
	}	
	
}
