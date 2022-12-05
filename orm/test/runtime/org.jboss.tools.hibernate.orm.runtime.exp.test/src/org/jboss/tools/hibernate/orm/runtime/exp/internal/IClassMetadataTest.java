package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tool.orm.jbt.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.util.MockDialect;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class IClassMetadataTest {

	private static final String TEST_CFG_XML_STRING =
			"<hibernate-configuration>" +
			"  <session-factory>" + 
			"    <property name='" + AvailableSettings.DIALECT + "'>" + MockDialect.class.getName() + "</property>" +
			"    <property name='" + AvailableSettings.CONNECTION_PROVIDER + "'>" + MockConnectionProvider.class.getName() + "</property>" +
			"  </session-factory>" +
			"</hibernate-configuration>";
	
	private static final String TEST_HBM_XML_STRING =
			"<hibernate-mapping package='org.jboss.tools.hibernate.orm.runtime.exp.internal'>" +
			"  <class name='IClassMetadataTest$Foo'>" + 
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
	
	private EntityPersister classMetadataTarget = null;
	private IClassMetadata classMetadataFacade = null;
	
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
		ISessionFactory sessionFactoryFacade = configuration.buildSessionFactory();
		classMetadataFacade = sessionFactoryFacade.getClassMetadata(Foo.class.getName());
		classMetadataTarget = (EntityPersister)((IFacade)classMetadataFacade).getTarget();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(classMetadataTarget);
		assertNotNull(classMetadataFacade);
	}
	
	@Test
	public void testGetEntityName() {
		assertEquals(Foo.class.getName(), classMetadataFacade.getEntityName());
	}
	
	@Test
	public void testGetIdentifierPropertyName() {
		assertEquals("id", classMetadataFacade.getIdentifierPropertyName());
	}
	
	@Test
	public void testGetPropertyNames() {
		String[] propertyNames = classMetadataFacade.getPropertyNames();
 		assertEquals(1, propertyNames.length);
 		assertEquals("bars", propertyNames[0]);
	}
	
	@Test
	public void testGetPropertyTypes() {
		IType[] typeFacades = classMetadataFacade.getPropertyTypes();
		assertEquals(1, typeFacades.length);
		assertTrue(typeFacades[0].isCollectionType());
		assertEquals(
				"org.jboss.tools.hibernate.orm.runtime.exp.internal.IClassMetadataTest$Foo.bars",
				typeFacades[0].getRole());
 	}
	
}
