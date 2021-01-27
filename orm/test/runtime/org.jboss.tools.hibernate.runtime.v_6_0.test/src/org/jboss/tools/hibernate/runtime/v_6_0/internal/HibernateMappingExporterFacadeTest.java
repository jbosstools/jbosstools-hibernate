package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.hibernate.boot.Metadata;
import org.hibernate.cfg.Environment;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.internal.export.java.POJOClass;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IExportPOJODelegate;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.IPOJOClass;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.util.DummyMetadataBuildingContext;
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
		hibernateMappingExporter = new HibernateMappingExporterExtension(FACADE_FACTORY, null, null);
		hibernateMappingExporterFacade = 
				new HibernateMappingExporterFacadeImpl(FACADE_FACTORY, hibernateMappingExporter);
		outputDir = temporaryFolder.getRoot();
	}
	
	@Test
	public void testStart() throws Exception {
		MetadataDescriptor descriptor = new TestMetadataDescriptor();
		Properties properties = hibernateMappingExporter.getProperties();
		properties.put(ExporterConstants.METADATA_DESCRIPTOR, descriptor);
		properties.put(ExporterConstants.DESTINATION_FOLDER, outputDir);
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
	public void testGetOutputDirectory() {
		assertNull(hibernateMappingExporterFacade.getOutputDirectory());
		File file = new File("testGetOutputDirectory");
		hibernateMappingExporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, file);
		assertSame(file, hibernateMappingExporterFacade.getOutputDirectory());
	}
	
	@Test
	public void testSetOutputDirectory() {
		assertNull(hibernateMappingExporter.getProperties().get(ExporterConstants.DESTINATION_FOLDER));
		File file = new File("testSetOutputDirectory");
		hibernateMappingExporterFacade.setOutputDirectory(file);
		assertSame(file, hibernateMappingExporter.getProperties().get(ExporterConstants.DESTINATION_FOLDER));
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
