package org.jboss.tools.hibernate.runtime.v_5_0.internal;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmHibernateMapping;
import org.hibernate.boot.jaxb.spi.Binding;
import org.hibernate.cfg.Configuration;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.v_5_0.test.MetadataHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();

	private static final String TEST_HBM_STRING =
			"<hibernate-mapping>" +
			"  <class name='Foo'>" + 
			"    <id name='id'/>" + 
			"  </class>" + 
			"</hibernate-mapping>";

	private ConfigurationFacadeImpl configurationFacade = null;
	private Configuration configuration = null;

	@Before
	public void setUp() throws Exception {
		configuration = new Configuration();
		configurationFacade = new ConfigurationFacadeImpl(
				FACADE_FACTORY, 
				configuration);
	}
	
	@Test
	public void testGetProperty() {
		configuration.setProperty("foo", "bar");
		String foo = configurationFacade.getProperty("foo");
		Assert.assertEquals("bar", foo);
	}

	@Test
	public void testAddFile() throws Exception {
		File testFile = File.createTempFile("test", "tmp");
		testFile.deleteOnExit();
		FileWriter fileWriter = new FileWriter(testFile);
		fileWriter.write(TEST_HBM_STRING);
		fileWriter.close();
		configurationFacade.addFile(testFile);
		MetadataSources mds = MetadataHelper.getMetadataSources(configuration);
		Assert.assertEquals(1, mds.getXmlBindings().size());
		Binding<?> binding = mds.getXmlBindings().get(0);
		Assert.assertEquals(
				testFile.getAbsolutePath(), 
				binding.getOrigin().getName());
	}
	
}
