package org.hibernate.eclipse.jdt.ui.test.hbmexporter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.util.NLS;
import org.hibernate.cfg.Configuration;
import org.hibernate.eclipse.console.test.ConsoleTestMessages;
import org.hibernate.eclipse.console.test.mappingproject.TestUtilsCommon;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.hibernate.eclipse.jdt.ui.internal.jpa.collect.AllEntitiesInfoCollector;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.Utils;
import org.hibernate.eclipse.jdt.ui.internal.jpa.process.AllEntitiesProcessor;
import org.hibernate.eclipse.jdt.ui.test.HibernateJDTuiTestPlugin;
import org.hibernate.eclipse.jdt.ui.wizards.ConfigurationActor;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimitiveArray;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Value;
import org.hibernate.type.IntegerType;

public class HbmExporterTest extends TestCase {
	
	public static final String PROJECT_NAME = "TestProject"; //$NON-NLS-1$
	public static final String RESOURCE_PATH = "res/hbm/".replaceAll("//", File.separator); //$NON-NLS-1$ //$NON-NLS-2$
	public static final String TESTRESOURCE_PATH = "testresources"; //$NON-NLS-1$

	protected AllEntitiesInfoCollector collector = new AllEntitiesInfoCollector();
	protected AllEntitiesProcessor processor = new AllEntitiesProcessor();

	protected IProject project;
	protected IJavaProject javaProject;
	
	protected void setUp() throws Exception {
		try {
			createTestProject();
		} catch (JavaModelException e1) {
			fail(e1.getMessage());
		} catch (CoreException e1) {
			fail(e1.getMessage());
		} catch (IOException e1) {
			fail(e1.getMessage());
		}
		assertNotNull(project);
		assertNotNull(javaProject);
		assertNotNull(ProjectUtils.findJavaProject(PROJECT_NAME));
		try {
			javaProject.getProject().open(null);
		} catch (CoreException e) {
			fail(e.getMessage());
		}
	}
	
	/**
	 * Method returns Configuration object for selected ICompilationUnits.
	 * Fails if configuration is null.
	 * @return
	 */
	protected Configuration getConfigurationFor(String... cuNames){
		Set<ICompilationUnit> selectionCU = new HashSet();
		for (int i = 0; i < cuNames.length; i++) {
			ICompilationUnit icu = Utils.findCompilationUnit(javaProject,
					cuNames[i]);
			assertNotNull(icu);
			selectionCU.add(icu);
		}
		ConfigurationActor actor = new ConfigurationActor(selectionCU);
		Map<IJavaProject, Configuration> configurations = actor.createConfigurations();
		assertEquals(1, configurations.size());
		Configuration config = configurations.get(javaProject);
		assertNotNull(config);
		return config;
	}
	
	protected void checkClassesMaped(Configuration config, String... classesNames){
		for (int i = 0; i < classesNames.length; i++) {
			assertNotNull(config.getClassMapping(classesNames[i]));
		}
	}
	
	public void testId(){
		Configuration config = getConfigurationFor("pack.A");
		checkClassesMaped(config, "A", "B");
		PersistentClass a = config.getClassMapping("A");
		PersistentClass b = config.getClassMapping("B");
		
		Property aId= a.getIdentifierProperty();
		Property bId= b.getIdentifierProperty();
		assertNotNull(aId);
		assertNotNull(bId);
		assertEquals("id", aId.getName());
		assertEquals("id", bId.getName());
	}
	
	public void testProperty(){
		Configuration config = getConfigurationFor("pack.A");
		checkClassesMaped(config, "A", "B");
		PersistentClass a = config.getClassMapping("A");
		
		Property prop = a.getProperty("prop");
		Value value = prop.getValue();
		assertNotNull(value);
		assertTrue("Expected to get ManyToOne-type mapping", value.getClass()== ManyToOne.class);
		ManyToOne mto = (ManyToOne)value;
		assertEquals("pack.B", mto.getTypeName());		
	}
	
	public void testArray(){
		Configuration config = getConfigurationFor("pack.A");
		checkClassesMaped(config, "A", "B");
		PersistentClass a = config.getClassMapping("A");
		PersistentClass b = config.getClassMapping("B");
		
		Property bs = a.getProperty("bs");
		Value value = bs.getValue();
		assertNotNull(value);
		assertTrue("Expected to get Array-type mapping", value.getClass()==Array.class);
		Array ar = (Array)value;
		assertEquals("pack.B", ar.getElementClassName());
		assertTrue("Expected to get one-to-many array's element type",
				ar.getElement().getClass() == OneToMany.class);
		
		Property testIntArray = b.getProperty("testIntArray");
		assertNotNull(testIntArray);
		value = testIntArray.getValue();
		assertNotNull(value);
		assertTrue("Expected to get PrimitiveArray-type mapping",  
				value.getClass()==PrimitiveArray.class);
		PrimitiveArray pAr = (PrimitiveArray) value;
		assertNotNull(pAr.getElement());
		assertTrue("Expected to get int-type primitive array", pAr.getElement().getType().getClass()==IntegerType.class);
	}
	
	public void testList(){
		Configuration config = getConfigurationFor("pack.A");
		checkClassesMaped(config, "A", "B");
		PersistentClass a = config.getClassMapping("A");
		PersistentClass b = config.getClassMapping("B");
		
		Property listProp = a.getProperty("list");
		Value value = listProp.getValue();
		assertNotNull(value);
		assertTrue("Expected to get List-type mapping", 
				value.getClass()==org.hibernate.mapping.List.class);
		org.hibernate.mapping.List list = (org.hibernate.mapping.List)value;
		assertTrue(list.getElement() instanceof OneToMany);
		assertTrue(list.getCollectionTable().equals(b.getTable()));
		assertNotNull(list.getIndex());
		assertNotNull(list.getKey());
	}
	
	public void testSet(){
		Configuration config = getConfigurationFor("pack.A");
		checkClassesMaped(config, "A", "B");
		PersistentClass a = config.getClassMapping("A");
		PersistentClass b = config.getClassMapping("B");
		
		Property setProp = a.getProperty("set");
		Value value = setProp.getValue();
		assertNotNull(value);
		assertTrue("Expected to get Set-type mapping", 
				value.getClass()==org.hibernate.mapping.Set.class);
		org.hibernate.mapping.Set set = (org.hibernate.mapping.Set)value;
		assertTrue(set.getElement() instanceof OneToMany);
		assertTrue(set.getCollectionTable().equals(b.getTable()));
		assertNotNull(set.getKey());
	}
	
	public void testMap(){
		Configuration config = getConfigurationFor("pack.A");
		checkClassesMaped(config, "A", "B");
		PersistentClass a = config.getClassMapping("A");
		PersistentClass b = config.getClassMapping("B");
		
		Property mapValue = a.getProperty("mapValue");
		Value value = mapValue.getValue();
		assertNotNull(value);
		assertTrue("Expected to get Map-type mapping", 
				value.getClass()==org.hibernate.mapping.Map.class);
		org.hibernate.mapping.Map map = (org.hibernate.mapping.Map)value;
		assertTrue(map.getElement() instanceof OneToMany);
		assertTrue(map.getCollectionTable().equals(b.getTable()));
		assertNotNull(map.getKey());
		assertEquals("string", map.getKey().getType().getName());
	}
	

	protected void createTestProject() throws JavaModelException,
			CoreException, IOException {
		TestUtilsCommon commonUtil = new TestUtilsCommon();
		project = commonUtil.buildNewProject(PROJECT_NAME);
		javaProject = commonUtil.buildJavaProject(project);
		File resourceFolder = getResourceItem(RESOURCE_PATH);
		if (!resourceFolder.exists()) {
			String out = NLS.bind(
					ConsoleTestMessages.MappingTestProject_folder_not_found,
					RESOURCE_PATH);
			throw new RuntimeException(out);
		}
		IPackageFragmentRoot sourceFolder = commonUtil.createSourceFolder(
				project, javaProject);
		commonUtil.recursiveCopyFiles(resourceFolder, (IFolder) sourceFolder
				.getResource());
		File resourceFolderLib = getResourceItem(TESTRESOURCE_PATH);
		if (!resourceFolderLib.exists()) {
			String out = NLS.bind(
					ConsoleTestMessages.MappingTestProject_folder_not_found,
					RESOURCE_PATH);
			throw new RuntimeException(out);
		}
		List<IPath> libs = commonUtil.copyLibs2(project, javaProject,
				resourceFolderLib.getAbsolutePath());
		commonUtil.generateClassPath(javaProject, libs, sourceFolder);
	}
	
	protected File getResourceItem(String strResPath) throws IOException {
		IPath resourcePath = new Path(strResPath);
		File resourceFolder = resourcePath.toFile();
		URL entry = HibernateJDTuiTestPlugin.getDefault().getBundle().getEntry(
				strResPath);
		URL resProject = FileLocator.resolve(entry);
		String tplPrjLcStr = FileLocator.resolve(resProject).getFile();
		resourceFolder = new File(tplPrjLcStr);
		return resourceFolder;
	}
	
	protected void tearDown() throws Exception {
		try {
			project.delete(true, true, null);
			project = null;
			javaProject = null;
		} catch (CoreException e) {
			fail(e.getMessage());
		}
		assertNull(project);
		assertNull(javaProject);
	}

}
