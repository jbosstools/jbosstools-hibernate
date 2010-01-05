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
import org.hibernate.eclipse.console.test.project.TestProject;
import org.hibernate.eclipse.console.test.utils.FilesTransfer;
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
import org.hibernate.mapping.Value;
import org.hibernate.type.IntegerType;

public class HbmExporterTest extends TestCase {
	
	public static final String PROJECT_NAME = "TestProject"; //$NON-NLS-1$
	public static final String RESOURCE_PATH = "res/hbm/".replaceAll("//", File.separator); //$NON-NLS-1$ //$NON-NLS-2$
	public static final String TESTRESOURCE_PATH = "testresources"; //$NON-NLS-1$

	protected AllEntitiesInfoCollector collector = new AllEntitiesInfoCollector();
	protected AllEntitiesProcessor processor = new AllEntitiesProcessor();

	protected TestProject project = null;
	
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
		IJavaProject javaProject = ProjectUtils.findJavaProject(PROJECT_NAME);
		assertNotNull(javaProject);
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
		Set<ICompilationUnit> selectionCU = new HashSet<ICompilationUnit>();
		for (int i = 0; i < cuNames.length; i++) {
			ICompilationUnit icu = Utils.findCompilationUnit(project.getIJavaProject(),
					cuNames[i]);
			assertNotNull(icu);
			selectionCU.add(icu);
		}
		ConfigurationActor actor = new ConfigurationActor(selectionCU);
		Map<IJavaProject, Configuration> configurations = actor.createConfigurations(Integer.MAX_VALUE);
		assertEquals(1, configurations.size());
		Configuration config = configurations.get(project.getIJavaProject());
		assertNotNull(config);
		return config;
	}
	
	protected void checkClassesMaped(Configuration config, String... classesNames){
		for (int i = 0; i < classesNames.length; i++) {
			assertNotNull(config.getClassMapping(classesNames[i]));
		}
	}
	
	public void testId(){
		Configuration config = getConfigurationFor("pack.A"); //$NON-NLS-1$
		checkClassesMaped(config, "A", "B"); //$NON-NLS-1$ //$NON-NLS-2$
		PersistentClass a = config.getClassMapping("A"); //$NON-NLS-1$
		PersistentClass b = config.getClassMapping("B"); //$NON-NLS-1$
		
		Property aId= a.getIdentifierProperty();
		Property bId= b.getIdentifierProperty();
		assertNotNull(aId);
		assertNotNull(bId);
		assertEquals("id", aId.getName()); //$NON-NLS-1$
		assertEquals("id", bId.getName()); //$NON-NLS-1$
	}
	
	public void testProperty(){
		Configuration config = getConfigurationFor("pack.A"); //$NON-NLS-1$
		checkClassesMaped(config, "A", "B"); //$NON-NLS-1$ //$NON-NLS-2$
		PersistentClass a = config.getClassMapping("A"); //$NON-NLS-1$
		
		Property prop = a.getProperty("prop"); //$NON-NLS-1$
		Value value = prop.getValue();
		assertNotNull(value);
		assertTrue("Expected to get ManyToOne-type mapping", value.getClass()== ManyToOne.class); //$NON-NLS-1$
		ManyToOne mto = (ManyToOne)value;
		assertEquals("pack.B", mto.getTypeName()); //$NON-NLS-1$
	}
	
	public void testArray(){
		Configuration config = getConfigurationFor("pack.A"); //$NON-NLS-1$
		checkClassesMaped(config, "A", "B"); //$NON-NLS-1$ //$NON-NLS-2$
		PersistentClass a = config.getClassMapping("A"); //$NON-NLS-1$
		PersistentClass b = config.getClassMapping("B"); //$NON-NLS-1$
		
		Property bs = a.getProperty("bs"); //$NON-NLS-1$
		Value value = bs.getValue();
		assertNotNull(value);
		assertTrue("Expected to get Array-type mapping", value.getClass()==Array.class); //$NON-NLS-1$
		Array ar = (Array)value;
		assertEquals("pack.B", ar.getElementClassName()); //$NON-NLS-1$
		assertTrue("Expected to get one-to-many array's element type", //$NON-NLS-1$
				ar.getElement().getClass() == OneToMany.class);
		
		Property testIntArray = b.getProperty("testIntArray"); //$NON-NLS-1$
		assertNotNull(testIntArray);
		value = testIntArray.getValue();
		assertNotNull(value);
		assertTrue("Expected to get PrimitiveArray-type mapping", //$NON-NLS-1$  
				value.getClass()==PrimitiveArray.class);
		PrimitiveArray pAr = (PrimitiveArray) value;
		assertNotNull(pAr.getElement());
		assertTrue("Expected to get int-type primitive array", pAr.getElement().getType().getClass()==IntegerType.class); //$NON-NLS-1$
	}
	
	public void testList(){
		Configuration config = getConfigurationFor("pack.A"); //$NON-NLS-1$
		checkClassesMaped(config, "A", "B"); //$NON-NLS-1$ //$NON-NLS-2$
		PersistentClass a = config.getClassMapping("A"); //$NON-NLS-1$
		PersistentClass b = config.getClassMapping("B"); //$NON-NLS-1$
		
		Property listProp = a.getProperty("list"); //$NON-NLS-1$
		Value value = listProp.getValue();
		assertNotNull(value);
		assertTrue("Expected to get List-type mapping", //$NON-NLS-1$ 
				value.getClass()==org.hibernate.mapping.List.class);
		org.hibernate.mapping.List list = (org.hibernate.mapping.List)value;
		assertTrue(list.getElement() instanceof OneToMany);
		assertTrue(list.getCollectionTable().equals(b.getTable()));
		assertNotNull(list.getIndex());
		assertNotNull(list.getKey());
	}
	
	public void testSet(){
		Configuration config = getConfigurationFor("pack.A"); //$NON-NLS-1$
		checkClassesMaped(config, "A", "B"); //$NON-NLS-1$ //$NON-NLS-2$
		PersistentClass a = config.getClassMapping("A"); //$NON-NLS-1$
		PersistentClass b = config.getClassMapping("B"); //$NON-NLS-1$
		
		Property setProp = a.getProperty("set"); //$NON-NLS-1$
		Value value = setProp.getValue();
		assertNotNull(value);
		assertTrue("Expected to get Set-type mapping",  //$NON-NLS-1$
				value.getClass()==org.hibernate.mapping.Set.class);
		org.hibernate.mapping.Set set = (org.hibernate.mapping.Set)value;
		assertTrue(set.getElement() instanceof OneToMany);
		assertTrue(set.getCollectionTable().equals(b.getTable()));
		assertNotNull(set.getKey());
	}
	
	public void testMap(){
		Configuration config = getConfigurationFor("pack.A"); //$NON-NLS-1$
		checkClassesMaped(config, "A", "B"); //$NON-NLS-1$ //$NON-NLS-2$
		PersistentClass a = config.getClassMapping("A"); //$NON-NLS-1$
		PersistentClass b = config.getClassMapping("B"); //$NON-NLS-1$
		
		Property mapValue = a.getProperty("mapValue"); //$NON-NLS-1$
		Value value = mapValue.getValue();
		assertNotNull(value);
		assertTrue("Expected to get Map-type mapping", //$NON-NLS-1$ 
				value.getClass()==org.hibernate.mapping.Map.class);
		org.hibernate.mapping.Map map = (org.hibernate.mapping.Map)value;
		assertTrue(map.getElement() instanceof OneToMany);
		assertTrue(map.getCollectionTable().equals(b.getTable()));
		assertNotNull(map.getKey());
		assertEquals("string", map.getKey().getType().getName()); //$NON-NLS-1$
	}
	

	protected void createTestProject() throws JavaModelException,
			CoreException, IOException {
		project = new TestProject(PROJECT_NAME);
		File resourceFolder = getResourceItem(RESOURCE_PATH);
		if (!resourceFolder.exists()) {
			String out = NLS.bind(
					ConsoleTestMessages.MappingTestProject_folder_not_found,
					RESOURCE_PATH);
			throw new RuntimeException(out);
		}
		IPackageFragmentRoot sourceFolder = project.createSourceFolder();
		FilesTransfer.copyFolder(resourceFolder, (IFolder) sourceFolder
				.getResource());
		File resourceFolderLib = getResourceItem(TESTRESOURCE_PATH);
		if (!resourceFolderLib.exists()) {
			String out = NLS.bind(
					ConsoleTestMessages.MappingTestProject_folder_not_found,
					RESOURCE_PATH);
			throw new RuntimeException(out);
		}
		List<IPath> libs = project.copyLibs2(resourceFolderLib.getAbsolutePath());
		project.generateClassPath(libs, sourceFolder);
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
		assertNotNull(project);
		project.deleteIProject();
		project = null;
	}

}
