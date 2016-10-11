package org.jboss.tools.hibernate.runtime.v_5_0.internal;

import java.io.File;
import java.io.PrintWriter;
import java.util.Properties;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.util.MetadataHelper;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationFacadeTest2 {
	
	private static final String TEST_HBM_XML_STRING =
			"<!DOCTYPE hibernate-mapping PUBLIC" +
			"		'-//Hibernate/Hibernate Mapping DTD 3.0//EN'" +
			"		'http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd'>" +
			"<hibernate-mapping package='org.jboss.tools.hibernate.runtime.v_5_0.internal'>" +
			"  <class name='ConfigurationFacadeTest2$Foo'>" + 
			"    <id name='id'/>" +
			"  </class>" +
			"</hibernate-mapping>";
	
	static class Foo {
		public String id;
	}
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();

	private IConfiguration configurationFacade = null;
	private Configuration configuration = null;

	@Before
	public void setUp() {
		configuration = new Configuration();
		configurationFacade = FACADE_FACTORY.createConfiguration(configuration);
	}
	
	@Test
	public void testGetProperty() {
		Assert.assertNull(configurationFacade.getProperty("foo"));
		configuration.setProperty("foo", "bar");
		Assert.assertEquals("bar", configurationFacade.getProperty("foo"));
	}

	@Test 
	public void testSetProperties() {
		Properties testProperties = new Properties();
		Assert.assertNotSame(testProperties, configuration.getProperties());
		Assert.assertSame(
				configurationFacade, 
				configurationFacade.setProperties(testProperties));
		Assert.assertSame(testProperties, configuration.getProperties());
	}
	
	@Test
	public void testAddFile() throws Exception {
		File testFile = File.createTempFile("test", "hbm.xml");
		PrintWriter printWriter = new PrintWriter(testFile);
		printWriter.write(TEST_HBM_XML_STRING);
		printWriter.close();
		String fooClassName = 
				"org.jboss.tools.hibernate.runtime.v_5_0.internal.ConfigurationFacadeTest2$Foo";
		MetadataSources metadataSources = MetadataHelper.getMetadataSources(configuration);
		// make sure the metadata are built before checking whether the class exists
		Metadata metadata = metadataSources.buildMetadata();
		Assert.assertNull(metadata.getEntityBinding(fooClassName));
		Assert.assertSame(
				configurationFacade,
				configurationFacade.addFile(testFile));
		// now that the file has been added, rebuild the metadata 
		metadata = metadataSources.buildMetadata();
		// now the class should exist 
		Assert.assertNotNull(metadata.getEntityBinding(fooClassName));
		Assert.assertTrue(testFile.delete());
	}
	
}
