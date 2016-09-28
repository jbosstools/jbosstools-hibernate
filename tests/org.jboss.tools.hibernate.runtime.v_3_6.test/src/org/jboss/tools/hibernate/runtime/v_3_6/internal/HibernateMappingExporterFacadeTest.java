package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import java.io.File;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Table;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.IMappings;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HibernateMappingExporterFacadeTest {
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	private static final File TMP_DIR = new File(System.getProperty("java.io.tmpdir"));
	
	private IHibernateMappingExporter hibernateMappingExporterFacade = null; 
	private HibernateMappingExporter hibernateMappingExporter = null;
	
	private File outputDir = null;
	
	@Before
	public void setUp() throws Exception {
		Configuration configuration = new Configuration();
		configuration.setProperty(
				"hibernate.dialect", 
				"org.hibernate.dialect.H2Dialect");
		IConfiguration configurationFacade = 
				FACADE_FACTORY.createConfiguration(configuration);
		RootClass persistentClass = new RootClass();
		Table table = new Table("FOO");
		persistentClass.setClassName("Foo");
		persistentClass.setEntityName("Foo");
		persistentClass.setTable(table);
		IPersistentClass persistentClassFacade = 
				FACADE_FACTORY.createPersistentClass(persistentClass);
		IMappings mappings = configurationFacade.createMappings();
		mappings.addClass(persistentClassFacade);	
		outputDir = new File(TMP_DIR, "HibernateMappingExporterFacadeTest");
		outputDir.mkdir();
		hibernateMappingExporter = new HibernateMappingExporter(configuration, outputDir);
		hibernateMappingExporterFacade = 
				FACADE_FACTORY.createHibernateMappingExporter(hibernateMappingExporter);
	}
	
	@Test
	public void testStart() {
		File fooHbmXml = new File(outputDir, "Foo.hbm.xml");
		Assert.assertFalse(fooHbmXml.exists());
		hibernateMappingExporterFacade.start();
		Assert.assertTrue(fooHbmXml.exists());
	}
	
	@After
	public void tearDown() {
		delete(outputDir);
	}
	
	private void delete(File file) {
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				delete(child);
			}
		}
		file.delete();
	}

}
