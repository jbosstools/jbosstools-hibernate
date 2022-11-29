package org.jboss.tools.hibernate.orm.runtime.exp.internal.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.File;
import java.io.PrintWriter;
import java.util.Properties;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.orm.jbt.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.util.MockDialect;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.ConfigurationFacadeImpl;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConfigurationMetadataDescriptorTest {
	
	private static final String TEST_HBM_XML_STRING =
			"<hibernate-mapping package='org.jboss.tools.hibernate.orm.runtime.exp.internal.util'>" +
			"  <class name='ConfigurationMetadataDescriptorTest$Foo'>" + 
			"    <id name='id'/>" +
			"  </class>" +
			"</hibernate-mapping>";
	
	static class Foo {
		public String id;
	}
	
	private static final NewFacadeFactory FACADE_FACTORY = NewFacadeFactory.INSTANCE;
	
	private ConfigurationMetadataDescriptor configurationMetadataDescriptor = null;
	
	private Configuration configurationTarget = null;
	private IConfiguration configurationFacade = null;
	
	@BeforeEach
	public void beforeEach() {
		configurationFacade = FACADE_FACTORY.createNativeConfiguration();
		configurationTarget = (Configuration)((IFacade)configurationFacade).getTarget();
		configurationMetadataDescriptor = new ConfigurationMetadataDescriptor(configurationFacade);
	}
	
	@Test
	public void testCreation() {
		assertNotNull(configurationMetadataDescriptor);
		assertSame(configurationFacade, configurationMetadataDescriptor.configurationFacade);
	}
	
	@Test 
	public void testGetProperties() {
		Properties properties = new Properties();
		configurationTarget.setProperties(properties);
		assertSame(properties, configurationMetadataDescriptor.getProperties());
	}
	
	@Test
	public void testCreateMetadata() throws Exception {
		File testFile = File.createTempFile("test", "hbm.xml");
		PrintWriter printWriter = new PrintWriter(testFile);
		printWriter.write(TEST_HBM_XML_STRING);
		printWriter.close();
		testFile.deleteOnExit();
		configurationFacade.setProperty(AvailableSettings.DIALECT, MockDialect.class.getName());
		configurationFacade.setProperty(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		configurationFacade.addFile(testFile);
		configurationMetadataDescriptor = new ConfigurationMetadataDescriptor(configurationFacade);
		Metadata metadata = configurationMetadataDescriptor.createMetadata();
		assertNotNull(metadata.getEntityBinding(Foo.class.getName()));
	}
	
}
