package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

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
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IExportPOJODelegate;
import org.jboss.tools.hibernate.runtime.spi.IPOJOClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HibernateMappingExporterExtensionTest {

	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private HibernateMappingExporterExtension hibernateMappingExporterExtension;

	@Before
	public void setUp() throws Exception {
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
		Method setTemplateHelperMethod = AbstractExporter.class.getDeclaredMethod(
				"setTemplateHelper", 
				new Class[] { TemplateHelper.class });
		setTemplateHelperMethod.setAccessible(true);
		TemplateHelper templateHelper = new TemplateHelper();
		templateHelper.init(null, new String[0]);
		setTemplateHelperMethod.invoke(hibernateMappingExporterExtension, new Object[] { templateHelper });
		ArtifactCollector artifactCollector = new DefaultArtifactCollector();
		hibernateMappingExporterExtension.getProperties().put(
				ExporterConstants.ARTIFACT_COLLECTOR, 
				artifactCollector);
		File[] hbmXmlFiles = artifactCollector.getFiles("hbm.xml");
		Assert.assertTrue(hbmXmlFiles.length == 0);
		Assert.assertFalse(new File("foo" + File.separator + "Bar.hbm.xml").exists());
		Map<String, Object> additionalContext = new HashMap<String, Object>();
		Cfg2HbmTool c2h = new Cfg2HbmTool();
		additionalContext.put("date", new Date().toString());
		additionalContext.put("version", Version.CURRENT_VERSION);
		additionalContext.put("c2h", c2h);
		hibernateMappingExporterExtension.superExportPOJO(additionalContext, createPojoClass());
		hbmXmlFiles = artifactCollector.getFiles("hbm.xml");
		Assert.assertTrue(hbmXmlFiles.length == 1);
		Assert.assertEquals("foo" + File.separator + "Bar.hbm.xml", hbmXmlFiles[0].getPath());
		Assert.assertTrue(new File("foo" + File.separator + "Bar.hbm.xml").exists());
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
	
}	
