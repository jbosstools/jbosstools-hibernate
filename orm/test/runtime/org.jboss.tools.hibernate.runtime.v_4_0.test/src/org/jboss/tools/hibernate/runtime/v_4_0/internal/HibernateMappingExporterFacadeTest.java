package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Mappings;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Table;
import org.hibernate.tool.Version;
import org.hibernate.tool.hbm2x.AbstractExporter;
import org.hibernate.tool.hbm2x.Cfg2HbmTool;
import org.hibernate.tool.hbm2x.Cfg2JavaTool;
import org.hibernate.tool.hbm2x.TemplateHelper;
import org.hibernate.tool.hbm2x.pojo.EntityPOJOClass;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IExportPOJODelegate;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.IPOJOClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class HibernateMappingExporterFacadeTest {
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
			
	private IHibernateMappingExporter hibernateMappingExporterFacade = null; 
	private HibernateMappingExporterExtension hibernateMappingExporter = null;
	
	private File outputDir = null;
	
	@Before
	public void setUp() throws Exception {
		hibernateMappingExporter = new HibernateMappingExporterExtension(
				FACADE_FACTORY, null, null);
		hibernateMappingExporterFacade = 
				FACADE_FACTORY.createHibernateMappingExporter(hibernateMappingExporter);
		outputDir = temporaryFolder.getRoot();
	}
	
	@Test
	public void testStart() throws Exception {
		Configuration configuration = new Configuration();
		RootClass persistentClass = new RootClass();
		Table table = new Table("FOO");
		persistentClass.setClassName("Foo");
		persistentClass.setEntityName("Foo");
		persistentClass.setTable(table);
		Mappings mappings = configuration.createMappings();
		mappings.addClass(persistentClass);	
		hibernateMappingExporter.setConfiguration(configuration);
		hibernateMappingExporter.setOutputDirectory(outputDir);
		final File fooHbmXml = new File(outputDir, "Foo.hbm.xml");
		// First without a 'delegate' exporter
		Assert.assertFalse(fooHbmXml.exists());
		hibernateMappingExporterFacade.start();
		Assert.assertTrue(fooHbmXml.exists());
		Assert.assertTrue(fooHbmXml.delete());
		// Now set a 'delegate' and invoke 'start' again
		final File dummyDir = new File(outputDir, "dummy");
		dummyDir.mkdir();
		Assert.assertTrue(dummyDir.exists());
		IExportPOJODelegate delegate = new IExportPOJODelegate() {			
			@Override
			public void exportPOJO(Map<Object, Object> map, IPOJOClass pojoClass) {
				Assert.assertTrue(dummyDir.delete());
				hibernateMappingExporter.superExportPOJO(
						map, (POJOClass)((IFacade)pojoClass).getTarget());
			}
		};
		Field delegateField = HibernateMappingExporterExtension.class.getDeclaredField("delegateExporter");
		delegateField.setAccessible(true);
		delegateField.set(hibernateMappingExporter, delegate);
		hibernateMappingExporterFacade.start();
		Assert.assertFalse(dummyDir.exists());
		Assert.assertTrue(fooHbmXml.exists());
		Assert.assertTrue(fooHbmXml.delete());
		Assert.assertTrue(outputDir.exists());
	}
	
	@Test
	public void testGetOutputDirectory() {
		Assert.assertNull(hibernateMappingExporterFacade.getOutputDirectory());
		File file = new File("testGetOutputDirectory");
		hibernateMappingExporter.setOutputDirectory(file);
		Assert.assertSame(file, hibernateMappingExporterFacade.getOutputDirectory());
	}
	
	@Test
	public void testSetOutputDirectory() {
		Assert.assertNull(hibernateMappingExporter.getOutputDirectory());
		File file = new File("testSetOutputDirectory");
		hibernateMappingExporterFacade.setOutputDirectory(file);
		Assert.assertSame(file, hibernateMappingExporter.getOutputDirectory());
	}
	
	@Test
	public void testSetExportPOJODelegate() throws Exception {
		IExportPOJODelegate delegate = new IExportPOJODelegate() {			
			@Override
			public void exportPOJO(Map<Object, Object> map, IPOJOClass pojoClass) {}
		};
		Field delegateField = HibernateMappingExporterExtension.class.getDeclaredField("delegateExporter");
		delegateField.setAccessible(true);
		Assert.assertNull(delegateField.get(hibernateMappingExporter));
		hibernateMappingExporterFacade.setExportPOJODelegate(delegate);
		Assert.assertSame(delegate, delegateField.get(hibernateMappingExporter));
	}
	
	@Test
	public void testExportPOJO() throws Exception {
		RootClass persistentClass = new RootClass();
		Table rootTable = new Table();
		rootTable.setName("FOO");
		persistentClass.setTable(rootTable);
		persistentClass.setEntityName("Foo");
		persistentClass.setClassName("Foo");
		IPOJOClass pojoClass = 
				FACADE_FACTORY.createPOJOClass(
						new EntityPOJOClass(persistentClass, new Cfg2JavaTool()));		
		Map<Object, Object> additionalContext = new HashMap<Object, Object>();
		Cfg2HbmTool c2h = new Cfg2HbmTool();
		additionalContext.put("date", new Date().toString());
		additionalContext.put("version", Version.getDefault().toString());
		additionalContext.put("c2h", c2h);
		hibernateMappingExporter.setOutputDirectory(outputDir);
		Method setTemplateHelperMethod = AbstractExporter.class.getDeclaredMethod(
				"setTemplateHelper", 
				new Class[] { TemplateHelper.class });
		setTemplateHelperMethod.setAccessible(true);
		TemplateHelper templateHelper = new TemplateHelper();
		templateHelper.init(null, new String[0]);
		setTemplateHelperMethod.invoke(hibernateMappingExporter, new Object[] { templateHelper });
		final File fooHbmXml = new File(outputDir, "Foo.hbm.xml");
		Assert.assertFalse(fooHbmXml.exists());
		hibernateMappingExporterFacade.exportPOJO(additionalContext, pojoClass);
		Assert.assertTrue(fooHbmXml.exists());
		fooHbmXml.delete();
		outputDir.delete();		
	}
	
}
