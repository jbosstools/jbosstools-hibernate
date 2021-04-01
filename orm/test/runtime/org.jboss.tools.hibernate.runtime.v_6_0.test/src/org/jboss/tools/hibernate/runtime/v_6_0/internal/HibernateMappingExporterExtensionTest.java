package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.export.ArtifactCollector;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.version.Version;
import org.hibernate.tool.internal.export.common.AbstractExporter;
import org.hibernate.tool.internal.export.common.DefaultArtifactCollector;
import org.hibernate.tool.internal.export.common.TemplateHelper;
import org.hibernate.tool.internal.export.hbm.Cfg2HbmTool;
import org.hibernate.tool.internal.export.java.Cfg2JavaTool;
import org.hibernate.tool.internal.export.java.EntityPOJOClass;
import org.hibernate.tool.internal.export.java.POJOClass;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IExportPOJODelegate;
import org.jboss.tools.hibernate.runtime.spi.IPOJOClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HibernateMappingExporterExtensionTest {

	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private HibernateMappingExporterExtension hibernateMappingExporterExtension;

	@BeforeEach
	public void beforeEach() throws Exception {
		hibernateMappingExporterExtension = new HibernateMappingExporterExtension(FACADE_FACTORY, null, null);
	}
	
	@Test
	public void testSetDelegate() throws Exception {
		Field delegateField = HibernateMappingExporterExtension.class.getDeclaredField("delegateExporter");
		delegateField.setAccessible(true);
		IExportPOJODelegate exportPojoDelegate = new IExportPOJODelegate() {			
			@Override
			public void exportPOJO(Map<Object, Object> map, IPOJOClass pojoClass) { }
		};
		assertNull(delegateField.get(hibernateMappingExporterExtension));
		hibernateMappingExporterExtension.setDelegate(exportPojoDelegate);
		assertSame(exportPojoDelegate, delegateField.get(hibernateMappingExporterExtension));
	}
	
	@Test
	public void testSuperExportPOJO() throws Exception {
		initializeTemplateHelper();
		ArtifactCollector artifactCollector = new DefaultArtifactCollector();
		hibernateMappingExporterExtension.getProperties().put(
				ExporterConstants.ARTIFACT_COLLECTOR, 
				artifactCollector);
		File[] hbmXmlFiles = artifactCollector.getFiles("hbm.xml");
		assertTrue(hbmXmlFiles.length == 0);
		assertFalse(new File("foo" + File.separator + "Bar.hbm.xml").exists());
		Map<String, Object> additionalContext = new HashMap<String, Object>();
		Cfg2HbmTool c2h = new Cfg2HbmTool();
		additionalContext.put("date", new Date().toString());
		additionalContext.put("version", Version.CURRENT_VERSION);
		additionalContext.put("c2h", c2h);
		hibernateMappingExporterExtension.superExportPOJO(additionalContext, createPojoClass());
		hbmXmlFiles = artifactCollector.getFiles("hbm.xml");
		assertTrue(hbmXmlFiles.length == 1);
		assertEquals("foo" + File.separator + "Bar.hbm.xml", hbmXmlFiles[0].getPath());
		assertTrue(new File("foo" + File.separator + "Bar.hbm.xml").exists());
	}
	
	@Test
	public void testExportPOJO() throws Exception {
		initializeTemplateHelper();
		POJOClass pojoClass = createPojoClass();
		// first without a delegate exporter
		ArtifactCollector artifactCollector = new DefaultArtifactCollector();
		hibernateMappingExporterExtension.getProperties().put(
				ExporterConstants.ARTIFACT_COLLECTOR, 
				artifactCollector);
		File[] hbmXmlFiles = artifactCollector.getFiles("hbm.xml");
		Map<Object, Object> additionalContext = new HashMap<Object, Object>();
		Cfg2HbmTool c2h = new Cfg2HbmTool();
		additionalContext.put("date", new Date().toString());
		additionalContext.put("version", Version.CURRENT_VERSION);
		additionalContext.put("c2h", c2h);
		assertTrue(hbmXmlFiles.length == 0);
		assertFalse(new File("foo" + File.separator + "Bar.hbm.xml").exists());
		hibernateMappingExporterExtension.exportPOJO(additionalContext, pojoClass);
		hbmXmlFiles = artifactCollector.getFiles("hbm.xml");
		assertTrue(hbmXmlFiles.length == 1);
		assertEquals("foo" + File.separator + "Bar.hbm.xml", hbmXmlFiles[0].getPath());
		assertTrue(new File("foo" + File.separator + "Bar.hbm.xml").exists());
		// then with a delegate exporter
		artifactCollector = new DefaultArtifactCollector();
		hibernateMappingExporterExtension.getProperties().put(
				ExporterConstants.ARTIFACT_COLLECTOR, 
				artifactCollector);
		final HashMap<Object, Object> arguments = new HashMap<Object, Object>();
		IExportPOJODelegate exportPojoDelegate = new IExportPOJODelegate() {			
			@Override
			public void exportPOJO(Map<Object, Object> map, IPOJOClass pojoClass) {
				arguments.put("map", map);
				arguments.put("pojoClass", pojoClass);
			}
		};
		hbmXmlFiles = artifactCollector.getFiles("hbm.xml");
		Field delegateField = HibernateMappingExporterExtension.class.getDeclaredField("delegateExporter");
		delegateField.setAccessible(true);
		delegateField.set(hibernateMappingExporterExtension, exportPojoDelegate);
		assertTrue(hbmXmlFiles.length == 0);
		assertNull(arguments.get("map"));
		assertNull(arguments.get("pojoClass"));
		hibernateMappingExporterExtension.exportPOJO(additionalContext, pojoClass);
		assertTrue(hbmXmlFiles.length == 0);
		assertSame(additionalContext, arguments.get("map"));
		assertSame(pojoClass, ((IFacade)arguments.get("pojoClass")).getTarget());
	}
	
	@AfterEach
	public void afterEach() {
		new File("foo" + File.separator + "Bar.hbm.xml").delete();
		new File("foo").delete();
	}
		
	private POJOClass createPojoClass() {
		RootClass persistentClass = new RootClass(null);
		Table rootTable = new Table();
		rootTable.setName("table");
		persistentClass.setTable(rootTable);
		persistentClass.setEntityName("Bar");
		persistentClass.setClassName("foo.Bar");
		return new EntityPOJOClass(persistentClass, new Cfg2JavaTool());		
	}
	
	private void initializeTemplateHelper() throws Exception {
		Method setTemplateHelperMethod = AbstractExporter.class.getDeclaredMethod(
				"setTemplateHelper", 
				new Class[] { TemplateHelper.class });
		setTemplateHelperMethod.setAccessible(true);
		TemplateHelper templateHelper = new TemplateHelper();
		templateHelper.init(null, new String[0]);
		setTemplateHelperMethod.invoke(
				hibernateMappingExporterExtension, 
				new Object[] { templateHelper });		
	}
	
}	
