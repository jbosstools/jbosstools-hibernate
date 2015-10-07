package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Table;
import org.hibernate.tool.Version;
import org.hibernate.tool.hbm2x.AbstractExporter;
import org.hibernate.tool.hbm2x.ArtifactCollector;
import org.hibernate.tool.hbm2x.Cfg2HbmTool;
import org.hibernate.tool.hbm2x.Cfg2JavaTool;
import org.hibernate.tool.hbm2x.TemplateHelper;
import org.hibernate.tool.hbm2x.pojo.EntityPOJOClass;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HibernateMappingExporterExtensionTest {
	
	private HibernateMappingExporterExtension hibernateMappingExporterExtension;
	private POJOClass pojoClass;
	private ArtifactCollector artifactCollector;
	
	@Before
	public void setUp() throws Exception {
		hibernateMappingExporterExtension = new HibernateMappingExporterExtension(null, null, null);
		Method setTemplateHelperMethod = AbstractExporter.class.getDeclaredMethod(
				"setTemplateHelper", 
				new Class[] { TemplateHelper.class });
		setTemplateHelperMethod.setAccessible(true);
		TemplateHelper templateHelper = new TemplateHelper();
		templateHelper.init(null, new String[0]);
		setTemplateHelperMethod.invoke(hibernateMappingExporterExtension, new Object[] { templateHelper });
		artifactCollector = new ArtifactCollector();
		hibernateMappingExporterExtension.setArtifactCollector(artifactCollector);
		RootClass persistentClass = new RootClass();
		Table rootTable = new Table();
		rootTable.setName("table");
		persistentClass.setTable(rootTable);
		persistentClass.setEntityName("Bar");
		persistentClass.setClassName("foo.Bar");
		pojoClass = new EntityPOJOClass(persistentClass, new Cfg2JavaTool());
	}
	
	@Test
	public void testSuperExportPOJO() {
		File[] hbmXmlFiles = artifactCollector.getFiles("hbm.xml");
		Assert.assertTrue(hbmXmlFiles.length == 0);
		Map<Object, Object> additionalContext = new HashMap<Object, Object>();
		Cfg2HbmTool c2h = new Cfg2HbmTool();
		additionalContext.put("date", new Date().toString());
		additionalContext.put("version", Version.getDefault().toString());
		additionalContext.put("c2h", c2h);
		hibernateMappingExporterExtension.superExportPOJO(additionalContext, pojoClass);
		hbmXmlFiles = artifactCollector.getFiles("hbm.xml");
		Assert.assertTrue(hbmXmlFiles.length == 1);
		Assert.assertEquals("foo/Bar.hbm.xml", hbmXmlFiles[0].getPath());
	}
	
	@After
	public void tearDown() {
		new File("foo/Bar.hbm.xml").delete();
		new File("foo").delete();
	}

}
