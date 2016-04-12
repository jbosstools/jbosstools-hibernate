package org.jboss.tools.hibernate.runtime.v_5_0.internal;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
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
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IExportPOJODelegate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HibernateMappingExporterExtensionTest {
	
	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private HibernateMappingExporterExtension hibernateMappingExporterExtension;

	private IExportPOJODelegate exportPojoDelegate;
	private POJOClass pojoClass;
	
	private String methodName = null;
	private Object[] arguments = null;
	
	@Before
	public void setUp() throws Exception {
		hibernateMappingExporterExtension = new HibernateMappingExporterExtension(FACADE_FACTORY, null, null);
		Method setTemplateHelperMethod = AbstractExporter.class.getDeclaredMethod(
				"setTemplateHelper", 
				new Class[] { TemplateHelper.class });
		setTemplateHelperMethod.setAccessible(true);
		TemplateHelper templateHelper = new TemplateHelper();
		templateHelper.init(null, new String[0]);
		setTemplateHelperMethod.invoke(hibernateMappingExporterExtension, new Object[] { templateHelper });
		RootClass persistentClass = new RootClass(null);
		Table rootTable = new Table();
		rootTable.setName("table");
		persistentClass.setTable(rootTable);
		persistentClass.setEntityName("Bar");
		persistentClass.setClassName("foo.Bar");
		pojoClass = new EntityPOJOClass(persistentClass, new Cfg2JavaTool());
		exportPojoDelegate = (IExportPOJODelegate)Proxy.newProxyInstance(
				HibernateMappingExporterExtension.class.getClassLoader(), 
				new Class[] { IExportPOJODelegate.class }, 
				new TestInvocationHandler());
	}
	
	@Test
	public void testSetDelegate() throws Exception {
		Field delegateField = HibernateMappingExporterExtension.class.getDeclaredField("delegateExporter");
		delegateField.setAccessible(true);
		Assert.assertNull(delegateField.get(hibernateMappingExporterExtension));
		hibernateMappingExporterExtension.setDelegate(exportPojoDelegate);
		Assert.assertSame(exportPojoDelegate, delegateField.get(hibernateMappingExporterExtension));
	}
	
	@Test
	public void testSuperExportPOJO() {
		ArtifactCollector artifactCollector = new ArtifactCollector();
		hibernateMappingExporterExtension.setArtifactCollector(artifactCollector);
		File[] hbmXmlFiles = artifactCollector.getFiles("hbm.xml");
		Assert.assertTrue(hbmXmlFiles.length == 0);
		Assert.assertFalse(new File("foo" + File.separator + "Bar.hbm.xml").exists());
		Map<String, Object> additionalContext = new HashMap<String, Object>();
		Cfg2HbmTool c2h = new Cfg2HbmTool();
		additionalContext.put("date", new Date().toString());
		additionalContext.put("version", Version.getDefault().toString());
		additionalContext.put("c2h", c2h);
		hibernateMappingExporterExtension.superExportPOJO(additionalContext, pojoClass);
		hbmXmlFiles = artifactCollector.getFiles("hbm.xml");
		Assert.assertTrue(hbmXmlFiles.length == 1);
		Assert.assertEquals("foo" + File.separator + "Bar.hbm.xml", hbmXmlFiles[0].getPath());
		Assert.assertTrue(new File("foo" + File.separator + "Bar.hbm.xml").exists());
	}
	
	@Test
	public void testExportPOJO() throws Exception {
		// first without a delegate exporter
		ArtifactCollector artifactCollector = new ArtifactCollector();
		hibernateMappingExporterExtension.setArtifactCollector(artifactCollector);
		File[] hbmXmlFiles = artifactCollector.getFiles("hbm.xml");
		Assert.assertTrue(hbmXmlFiles.length == 0);
		Assert.assertFalse(new File("foo" + File.separator + "Bar.hbm.xml").exists());
		Map<Object, Object> additionalContext = new HashMap<Object, Object>();
		Cfg2HbmTool c2h = new Cfg2HbmTool();
		additionalContext.put("date", new Date().toString());
		additionalContext.put("version", Version.getDefault().toString());
		additionalContext.put("c2h", c2h);
		hibernateMappingExporterExtension.exportPOJO(additionalContext, pojoClass);
		hbmXmlFiles = artifactCollector.getFiles("hbm.xml");
		Assert.assertTrue(hbmXmlFiles.length == 1);
		Assert.assertEquals("foo/Bar.hbm.xml", hbmXmlFiles[0].getPath());
		Assert.assertTrue(new File("foo" + File.separator + "Bar.hbm.xml").exists());
		Assert.assertNull(methodName);
		Assert.assertNull(arguments);
		// then with a delegate exporter
		artifactCollector = new ArtifactCollector();
		hibernateMappingExporterExtension.setArtifactCollector(artifactCollector);
		hbmXmlFiles = artifactCollector.getFiles("hbm.xml");
		Assert.assertTrue(hbmXmlFiles.length == 0);
		Field delegateField = HibernateMappingExporterExtension.class.getDeclaredField("delegateExporter");
		delegateField.setAccessible(true);
		delegateField.set(hibernateMappingExporterExtension, exportPojoDelegate);
		hibernateMappingExporterExtension.exportPOJO(additionalContext, pojoClass);
		Assert.assertTrue(hbmXmlFiles.length == 0);
		Assert.assertEquals("exportPOJO", methodName);
		Assert.assertSame(additionalContext, arguments[0]);
		Assert.assertSame(pojoClass, ((IFacade)arguments[1]).getTarget());
	}
	
	@After
	public void tearDown() {
		new File("foo" + File.separator + "Bar.hbm.xml").delete();
		new File("foo").delete();
	}
	
	private class TestInvocationHandler implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			methodName = method.getName();
			arguments = args;
			return null;
		}	
	}

}
