package org.jboss.tools.hibernate.runtime.v_5_3.internal.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.util.Properties;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.v_5_3.internal.ConfigurationFacadeImpl;
import org.jboss.tools.hibernate.runtime.v_5_3.internal.FacadeFactoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConfigurationMetadataDescriptorTest {

	private static final String TEST_HBM_XML_STRING =
			"<hibernate-mapping package='org.jboss.tools.hibernate.runtime.v_5_3.internal.util'>" +
			"  <class name='ConfigurationMetadataDescriptorTest$Foo'>" + 
			"    <id name='id'/>" +
			"  </class>" +
			"</hibernate-mapping>";
	
	static class Foo {
		public String id;
	}
	
	private static final FacadeFactoryImpl FACADE_FACTORY = new FacadeFactoryImpl();
	
	private ConfigurationMetadataDescriptor configurationMetadataDescriptor = null;
	
	private Configuration configurationTarget = null;
	private ConfigurationFacadeImpl configurationFacade = null;
	
	@BeforeEach
	public void beforeEach() {
		configurationTarget = new Configuration();
		configurationFacade = new ConfigurationFacadeImpl(FACADE_FACTORY, configurationTarget);
		configurationMetadataDescriptor = new ConfigurationMetadataDescriptor(configurationFacade);
	}
	
	@Test
	public void testConstruction() throws Exception {
		Field configurationFacadeField = 
				ConfigurationMetadataDescriptor.class.getDeclaredField("configuration");
		configurationFacadeField.setAccessible(true);
		assertNotNull(configurationMetadataDescriptor);
		assertSame(configurationFacade, configurationFacadeField.get(configurationMetadataDescriptor));
	}
	
	@Test 
	public void testGetProperties() {
		Properties properties = new Properties();
		configurationTarget.setProperties(properties);
		assertSame(properties, configurationMetadataDescriptor.getProperties());
	}
	
	@Test
	public void testCreateMetadata() {
		MetadataSources metadataSources = new MetadataSources();
		metadataSources.addInputStream(new ByteArrayInputStream(TEST_HBM_XML_STRING.getBytes()));
		Configuration configuration = new Configuration(metadataSources);
		configuration.setProperty(AvailableSettings.DIALECT, MockDialect.class.getName());
		configuration.setProperty(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		configurationFacade = new ConfigurationFacadeImpl(FACADE_FACTORY, configuration);
		PersistentClass persistentClass = new RootClass(null);
		persistentClass.setEntityName("Bar");
		IPersistentClass persistentClassFacade = 
				FACADE_FACTORY.createPersistentClass(persistentClass);	
		configurationFacade.addClass(persistentClassFacade);
		configurationMetadataDescriptor = new ConfigurationMetadataDescriptor(configurationFacade);
		Metadata metadata = configurationMetadataDescriptor.createMetadata();
		assertNotNull(metadata.getEntityBinding(Foo.class.getName()));
		assertNotNull(metadata.getEntityBinding("Bar"));
	}
	
}
