package org.jboss.tools.hibernate.runtime.v_5_6.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Environment;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.tool.Version;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.hbm2x.AbstractExporter;
import org.hibernate.tool.hbm2x.Cfg2HbmTool;
import org.hibernate.tool.hbm2x.Cfg2JavaTool;
import org.hibernate.tool.hbm2x.TemplateHelper;
import org.hibernate.tool.hbm2x.pojo.EntityPOJOClass;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.jboss.tools.hibernate.runtime.common.AbstractHibernateMappingExporterFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IExportPOJODelegate;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.IPOJOClass;
import org.jboss.tools.hibernate.runtime.v_5_6.internal.util.DummyMetadataBuildingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class HibernateMappingExporterFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();

	@TempDir
	public File outputDir = new File("output");
	
	private IHibernateMappingExporter hibernateMappingExporterFacade = null; 
	private HibernateMappingExporterExtension hibernateMappingExporter = null;
		
	@BeforeEach
	public void beforeEach() throws Exception {
		hibernateMappingExporter = new HibernateMappingExporterExtension(FACADE_FACTORY, null, null);
		hibernateMappingExporterFacade = 
				new AbstractHibernateMappingExporterFacade(FACADE_FACTORY, hibernateMappingExporter) {};
	}
	
	@Test
	public void testStart() throws Exception {
		Field metadataDescriptorField = AbstractExporter.class.getDeclaredField("metadataDescriptor");
		metadataDescriptorField.setAccessible(true);
		metadataDescriptorField.set(hibernateMappingExporter, new TestMetadataDescriptor());
		Field outputDirectoryField = AbstractExporter.class.getDeclaredField("outputdir");
		outputDirectoryField.setAccessible(true);
		outputDirectoryField.set(hibernateMappingExporter, outputDir);
		final File fooHbmXml = new File(outputDir, "Foo.hbm.xml");
		// First without a 'delegate' exporter
		assertFalse(fooHbmXml.exists());
		hibernateMappingExporterFacade.start();
		assertTrue(fooHbmXml.exists());
		assertTrue(fooHbmXml.delete());
		// Now set a 'delegate' and invoke 'start' again
		final File dummyDir = new File(outputDir, "dummy");
		dummyDir.mkdir();
		assertTrue(dummyDir.exists());
		IExportPOJODelegate delegate = new IExportPOJODelegate() {			
			@Override
			public void exportPOJO(Map<Object, Object> map, IPOJOClass pojoClass) {
				assertTrue(dummyDir.delete());
				Map<String, Object> m = new HashMap<>();
				for (Object key : map.keySet()) {
					m.put((String)key, map.get(key));
				}
				hibernateMappingExporter.superExportPOJO(
					m,(POJOClass)((IFacade)pojoClass).getTarget());
			}
		};
		Field delegateField = HibernateMappingExporterExtension.class.getDeclaredField("delegateExporter");
		delegateField.setAccessible(true);
		delegateField.set(hibernateMappingExporter, delegate);
		hibernateMappingExporterFacade.start();
		assertFalse(dummyDir.exists());
		assertTrue(fooHbmXml.exists());
		assertTrue(fooHbmXml.delete());
		assertTrue(outputDir.exists());
	}
	
	@Test
	public void testGetOutputDirectory() throws Exception {
		assertNull(hibernateMappingExporterFacade.getOutputDirectory());
		Field outputdirField = AbstractExporter.class.getDeclaredField("outputdir");
		outputdirField.setAccessible(true);
		File file = new File("testGetOutputDirectory");
		outputdirField.set(hibernateMappingExporter, file);
		assertSame(file, hibernateMappingExporterFacade.getOutputDirectory());
	}
	
	@Test
	public void testSetOutputDirectory() throws Exception {
		Field outputdirField = AbstractExporter.class.getDeclaredField("outputdir");
		outputdirField.setAccessible(true);
		assertNull(outputdirField.get(hibernateMappingExporter));
		File file = new File("testSetOutputDirectory");
		hibernateMappingExporterFacade.setOutputDirectory(file);
		assertSame(file, outputdirField.get(hibernateMappingExporter));
	}
	
	@Test
	public void testSetExportPOJODelegate() throws Exception {
		IExportPOJODelegate delegate = new IExportPOJODelegate() {			
			@Override
			public void exportPOJO(Map<Object, Object> map, IPOJOClass pojoClass) {}
		};
		Field delegateField = HibernateMappingExporterExtension.class.getDeclaredField("delegateExporter");
		delegateField.setAccessible(true);
		assertNull(delegateField.get(hibernateMappingExporter));
		hibernateMappingExporterFacade.setExportPOJODelegate(delegate);
		assertSame(delegate, delegateField.get(hibernateMappingExporter));
	}
	
	@Test
	public void testExportPOJO() throws Exception {
		RootClass persistentClass = new RootClass(null);
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
		assertFalse(fooHbmXml.exists());
		hibernateMappingExporterFacade.exportPOJO(additionalContext, pojoClass);
		assertTrue(fooHbmXml.exists());
		fooHbmXml.delete();
		outputDir.delete();		
	}
	
	private class TestMetadataDescriptor implements MetadataDescriptor {
		@Override
		public Metadata createMetadata() {
			return (Metadata)Proxy.newProxyInstance(
					getClass().getClassLoader(), 
					new Class<?>[] { Metadata.class }, 
					new TestInvocationHandler());
		}
		@Override
		public Properties getProperties() {
			Properties properties = new Properties();
			properties.put(Environment.DIALECT, "org.hibernate.dialect.H2Dialect");
			return properties;
		}	
	}
	
	private class TestInvocationHandler implements InvocationHandler {
		private ArrayList<PersistentClass> entities = new ArrayList<PersistentClass>();
		private ArrayList<Table> tables = new ArrayList<Table>();
		private TestInvocationHandler() {
			RootClass persistentClass = new RootClass(null);
			Table table = new Table("FOO");
			Column keyColumn = new Column("BAR");
			SimpleValue key = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
			key.setTypeName("String");
			key.addColumn(keyColumn);
			key.setTable(table);
			persistentClass.setClassName("Foo");
			persistentClass.setEntityName("Foo");
			persistentClass.setJpaEntityName("Foo");
			persistentClass.setTable(table);
			persistentClass.setIdentifier(key);	
			entities.add(persistentClass);
			tables.add(table);
		}
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (method.getName().equals("getEntityBindings")) {
				return entities;
			} else if (method.getName().equals("collectTableMappings")) {
				return tables;
			} else if (method.getName().equals("getImports")) {
				return Collections.emptyMap();
			}
			return null;
		}		
	}
		
}
