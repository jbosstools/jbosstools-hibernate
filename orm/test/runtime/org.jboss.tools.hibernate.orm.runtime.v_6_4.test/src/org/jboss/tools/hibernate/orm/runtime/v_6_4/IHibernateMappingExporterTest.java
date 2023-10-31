package org.jboss.tools.hibernate.orm.runtime.v_6_4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.internal.export.common.AbstractExporter;
import org.hibernate.tool.internal.export.common.TemplateHelper;
import org.hibernate.tool.internal.export.hbm.HbmExporter;
import org.hibernate.tool.internal.export.java.Cfg2JavaTool;
import org.hibernate.tool.internal.export.java.EntityPOJOClass;
import org.hibernate.tool.internal.export.java.POJOClass;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;
import org.hibernate.tool.orm.jbt.wrp.HbmExporterWrapper;
import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.orm.runtime.common.GenericFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IExportPOJODelegate;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class IHibernateMappingExporterTest {
	
	@TempDir
	public File outputDir;
	
	private IHibernateMappingExporter hbmExporterFacade = null;
	private HbmExporter hbmExporterTarget = null;
	
	private boolean delegateHasExported = false;
	private boolean templateProcessed = false;
	
	@BeforeEach
	public void beforeEach() throws Exception {
		outputDir = Files.createTempDirectory("output").toFile();
		Configuration configuration = new Configuration();
		File file = new File(outputDir, "foo");
		hbmExporterTarget = (HbmExporter)WrapperFactory.createHbmExporterWrapper(configuration, file);
		hbmExporterFacade = (IHibernateMappingExporter)GenericFacadeFactory
				.createFacade(IHibernateMappingExporter.class, hbmExporterTarget);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(hbmExporterTarget);
		assertNotNull(hbmExporterFacade);
	}

	@Test
	public void testStart() throws Exception {
		MetadataDescriptor descriptor = new TestMetadataDescriptor();
		Properties properties = hbmExporterTarget.getProperties();
		properties.put(ExporterConstants.METADATA_DESCRIPTOR, descriptor);
		properties.put(ExporterConstants.DESTINATION_FOLDER, outputDir);
		final File fooHbmXml = new File(outputDir, "Foo.hbm.xml");
		// First without a 'delegate' exporter
		assertFalse(fooHbmXml.exists());
		hbmExporterFacade.start();
		assertTrue(fooHbmXml.exists());
		assertTrue(fooHbmXml.delete());
		// Now set a 'delegate' and invoke 'start' again
		IExportPOJODelegate delegate = new IExportPOJODelegate() {			
			@Override
			public void exportPojo(Map<Object, Object> map, Object pojoClass, String qualifiedDeclarationName) {
				try {
					FileWriter fw = new FileWriter(fooHbmXml);
					fw.write("<someDummyXml/>");
					fw.close();
					delegateHasExported = true;
				} catch (Throwable t) {
					fail(t);
				}
			}
		};
		Field delegateField = HbmExporterWrapper.class.getDeclaredField("delegateExporter");
		delegateField.setAccessible(true);
		delegateField.set(hbmExporterTarget, delegate);
		assertFalse(delegateHasExported);
		hbmExporterFacade.start();
		assertTrue(delegateHasExported);
	}
	
	@Test
	public void testGetOutputDirectory() {
		assertNull(hbmExporterFacade.getOutputDirectory());
		File file = new File("testGetOutputDirectory");
		hbmExporterTarget.getProperties().put(ExporterConstants.DESTINATION_FOLDER, file);
		assertSame(file, hbmExporterFacade.getOutputDirectory());
	}
	
	@Test
	public void testSetOutputDirectory() {
		assertNull(hbmExporterTarget.getProperties().get(ExporterConstants.DESTINATION_FOLDER));
		File file = new File("testSetOutputDirectory");
		hbmExporterFacade.setOutputDirectory(file);
		assertSame(file, hbmExporterTarget.getProperties().get(ExporterConstants.DESTINATION_FOLDER));
	}
	
	@Test
	public void testSetExportPOJODelegate() throws Exception {
		IExportPOJODelegate delegate = new IExportPOJODelegate() {			
			@Override
			public void exportPojo(Map<Object, Object> map, Object pojoClass, String qualifiedDeclarationName) { }
		};
		Field delegateField = HbmExporterWrapper.class.getDeclaredField("delegateExporter");
		delegateField.setAccessible(true);
		assertNull(delegateField.get(hbmExporterTarget));
		hbmExporterFacade.setExportPOJODelegate(delegate);
		assertSame(delegate, delegateField.get(hbmExporterTarget));
	}
	
	@Test
	public void testExportPOJO() throws Exception {
		// first without a delegate exporter
		Map<Object, Object> context = new HashMap<>();
		PersistentClass pc = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		pc.setEntityName("foo");
		pc.setClassName("foo");
		POJOClass pojoClass = new EntityPOJOClass(pc, new Cfg2JavaTool());
		TemplateHelper templateHelper = new TemplateHelper() {
		    public void processTemplate(String templateName, Writer output, String rootContext) {
		    	templateProcessed = true;
		    }
		};
		templateHelper.init(null, new String[] {});
		Field templateHelperField = AbstractExporter.class.getDeclaredField("vh");
		templateHelperField.setAccessible(true);
		templateHelperField.set(hbmExporterTarget, templateHelper);
		assertFalse(templateProcessed);
		assertFalse(delegateHasExported);
		hbmExporterFacade.exportPOJO(context, pojoClass);
		assertTrue(templateProcessed);
		assertFalse(delegateHasExported);
		// now with a delegate exporter
		templateProcessed = false;
		Object delegate = new Object() {
			@SuppressWarnings("unused")
			private void exportPojo(Map<Object, Object> map, Object object, String string) {
				assertSame(map, context);
				assertSame(object, pojoClass);
				assertEquals(string, pojoClass.getQualifiedDeclarationName());
				delegateHasExported = true;
			}
		};
		Field delegateField = HbmExporterWrapper.class.getDeclaredField("delegateExporter");
		delegateField.setAccessible(true);
		delegateField.set(hbmExporterTarget, delegate);
		assertFalse(templateProcessed);
		assertFalse(delegateHasExported);
		hbmExporterFacade.exportPOJO(context, pojoClass);
		assertFalse(templateProcessed);
		assertTrue(delegateHasExported);
	}
	
	private static class TestMetadataDescriptor implements MetadataDescriptor {
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
	
	private static class TestInvocationHandler implements InvocationHandler {
		private ArrayList<PersistentClass> entities = new ArrayList<PersistentClass>();
		private ArrayList<Table> tables = new ArrayList<Table>();
		private TestInvocationHandler() {
			RootClass persistentClass = new RootClass(DummyMetadataBuildingContext.INSTANCE);
			Table table = new Table("JBoss Tools", "FOO");
			Column keyColumn = new Column("BAR");
			SimpleValue key = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
			key.setTable(table);
			key.setTypeName("String");
			key.addColumn(keyColumn);
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
