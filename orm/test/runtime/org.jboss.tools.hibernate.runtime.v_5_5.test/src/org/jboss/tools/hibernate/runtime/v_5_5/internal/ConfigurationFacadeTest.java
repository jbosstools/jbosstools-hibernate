package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.PrintWriter;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.jaxb.spi.Binding;
import org.hibernate.cfg.Configuration;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.v_5_5.internal.util.MetadataHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConfigurationFacadeTest {
	
	private static final String TEST_HBM_XML_STRING =
			"<hibernate-mapping package='org.jboss.tools.hibernate.runtime.v_5_5.internal'>" +
			"  <class name='ConfigurationFacadeTest$Foo'>" + 
			"    <id name='id'/>" +
			"  </class>" +
			"</hibernate-mapping>";
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();

	private IConfiguration configurationFacade = null;
	private Configuration configuration = null;

	@BeforeEach
	public void beforeEach() {
		configuration = new Configuration();
		configurationFacade = new ConfigurationFacadeImpl(FACADE_FACTORY, configuration);
	}	
	
	@Test
	public void testGetProperty() {
		assertNull(configurationFacade.getProperty("foo"));
		configuration.setProperty("foo", "bar");
		assertEquals("bar", configurationFacade.getProperty("foo"));
	}

	@Test
	public void testAddFile() throws Exception {
		File testFile = File.createTempFile("test", "hbm.xml");
		PrintWriter printWriter = new PrintWriter(testFile);
		printWriter.write(TEST_HBM_XML_STRING);
		printWriter.close();
		MetadataSources metadataSources = MetadataHelper.getMetadataSources(configuration);
		assertTrue(metadataSources.getXmlBindings().isEmpty());
		assertSame(
				configurationFacade,
				configurationFacade.addFile(testFile));
		assertFalse(metadataSources.getXmlBindings().isEmpty());
		Binding<?> binding = metadataSources.getXmlBindings().iterator().next();
		assertEquals(testFile.getAbsolutePath(), binding.getOrigin().getName());
		assertTrue(testFile.delete());
	}
	
	@Test 
	public void testSetProperty() {
		assertNull(configuration.getProperty("foo"));
		configurationFacade.setProperty("foo", "bar");
		assertEquals("bar", configuration.getProperty("foo"));
	}

}
