package org.jboss.tools.hibernate.orm.test.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collections;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.IPackagesViewPart;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.actions.OpenMappingAction;
import org.hibernate.eclipse.console.actions.OpenSourceAction;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.hibernate.eclipse.console.workbench.ConfigurationWorkbenchAdapter;
import org.hibernate.eclipse.console.workbench.ConsoleConfigurationWorkbenchAdapter;
import org.hibernate.eclipse.console.workbench.PersistentClassWorkbenchAdapter;
import org.hibernate.eclipse.console.workbench.PropertyWorkbenchAdapter;
import org.jboss.tools.hibernate.orm.test.utils.project.FilesTransfer;
import org.jboss.tools.hibernate.orm.test.utils.project.TestProject;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.IService;
import org.jboss.tools.hibernate.runtime.spi.ServiceLookup;
import org.jboss.tools.hibernate.ui.view.OpenDiagramActionDelegate;
import org.junit.Assert;
import org.junit.rules.TestName;

public class JpaMappingTestHelper {
	
	public JpaMappingTestHelper(TestName testName) {
		this.testName = testName;
	}

	private TestName testName = null;
	
	private ConsoleConfigurationWorkbenchAdapter ccWorkbenchAdapter = new ConsoleConfigurationWorkbenchAdapter(); 
	private ConfigurationWorkbenchAdapter configWorkbenchAdapter = new ConfigurationWorkbenchAdapter();
	private PersistentClassWorkbenchAdapter pcWorkbenchAdapter = new PersistentClassWorkbenchAdapter(); 
	private PropertyWorkbenchAdapter propertyWorkbenchAdapter = new PropertyWorkbenchAdapter(); 
	private TestProject testProject = null;
	
	public void beforeClass() throws Exception {
		testProject = 
				new TestProject(
						"JUnitTestProj" + System.currentTimeMillis());		
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(
				PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId("org.eclipse.ui.resourcePerspective")); //$NON-NLS-1$

		IPackagesViewPart packageExplorer = null;
		try {
			packageExplorer = (IPackagesViewPart) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().showView(JavaUI.ID_PACKAGES);
		} catch (PartInitException e) {
			throw new RuntimeException(e);
		}
		packageExplorer.selectAndReveal(testProject.getIJavaProject());		
		configureTestProject();
		copyHbmXmlAndJavaFiles();
	}
	
//	private Map<String, Integer> countMap = new HashMap<String, Integer>(1);
	
	public void before(String packageName) throws Exception {
//		Integer count = countMap.get(packageName);
//		if (count == null) {
			doBefore(packageName);
//			countMap.put(packageName, 4);
//		} else {
//			countMap.put(packageName, count - 1);
//		}
	}
	
	public void after(String packageName) throws Exception {
//		Integer count = countMap.get(packageName);
//		if (count == 0) {
			doAfter();
//			countMap.remove(packageName);
//		} 
	}
	
	private void doBefore(String packageName) throws Exception {
		String projectName = testProject.getIProject().getName();
		ConsoleConfigUtils.createJpaConsoleConfig(
				testProject.getIProject().getName(), 
				testProject.getIProject().getName(), 
				"PetClinic");
		ProjectUtils.toggleHibernateOnProject(
				testProject.getIProject(), 
				true, 
				projectName);
		testProject.fullBuild();		
	}
	
	private void doAfter() throws Exception {
		ConsoleConfigUtils.deleteConsoleConfig(testProject.getIProject().getName());
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(true);
	}
	
	public void afterClass() {
		String projectName = testProject.getIProject().getName();
		ProjectUtils.toggleHibernateOnProject(testProject.getIProject(), false, projectName);
		testProject.deleteIProject(false);
		testProject = null;
	}
	
	public void testCheckConsoleConfiguration() {
		Object[] persClasses = getPersistenceClasses(true);
		Assert.assertTrue(persClasses.length > 0);
		for (int i = 0; i < persClasses.length; i++) {
			Assert.assertTrue(persClasses[i] instanceof IPersistentClass);
		}
	}

	public void testOpenMappingDiagram() {
		final Object[] persClasses = getPersistenceClasses(true);
		final ConsoleConfiguration consCFG = getConsoleConfig();
		for (int i = 0; i < persClasses.length; i++) {
			Assert.assertTrue(persClasses[i] instanceof IPersistentClass);
			IPersistentClass persClass = (IPersistentClass) persClasses[i];
			IEditorPart editor = null;
			Throwable ex = null;
			try {
				editor = new OpenDiagramActionDelegate().openEditor(persClass, consCFG);
			} catch (PartInitException e) {
				ex = e;
			}
			if (ex == null ) {
				ex = Utils.getExceptionIfItOccured(editor);
			}
			if (ex != null) {
				ex.printStackTrace();
				String out = NLS.bind(TestConsoleMessages.OpenMappingDiagramTest_mapping_diagram_for_not_opened,
						new Object[]{persClass.getClassName(), ex.getMessage()});
				Assert.fail(out);
			}
		}
	}
	
	public void testOpenMappingFileTest(String packageName) {
		final Object[] persClasses = getPersistenceClasses(true);
		final ConsoleConfiguration consCFG = getConsoleConfig();
		final String testClass = "class"; //$NON-NLS-1$
		for (int i = 0; i < persClasses.length; i++) {
			Assert.assertTrue(persClasses[i] instanceof IPersistentClass);
			IPersistentClass persClass = (IPersistentClass) persClasses[i];
			openTestForPackage(persClass, consCFG, packageName);
			Object[] props =  pcWorkbenchAdapter.getChildren(persClass);
			for (int j = 0; j < props.length; j++) {
				if (!(props[j] instanceof IProperty && ((IProperty)props[j]).classIsPropertyClass())) {
					continue;
				}
				openTestForPackage(props[j], consCFG, packageName);
				Object[] compProperties = propertyWorkbenchAdapter.getChildren(props[j]);
				for (int k = 0; k < compProperties.length; k++) {
					//test Composite properties
					if (!(compProperties[k] instanceof IProperty && ((IProperty)props[j]).classIsPropertyClass())) {
						continue;
					}
					final IProperty prop = (IProperty)compProperties[k];
					if (testClass.equals(prop.getName()) || testClass.equals(prop.getName())) {
						continue;
					}
					openPropertyTestForPackage((IProperty)compProperties[k], (IProperty) props[j], consCFG, packageName);
				}
			}
		}
		//close all editors
	}

	public void testOpenSourceFileTest() {
		//fail("test fail");
		final Object[] persClasses = getPersistenceClasses(true);
		final ConsoleConfiguration consCFG = getConsoleConfig();
		for (int i = 0; i < persClasses.length; i++) {
			Assert.assertTrue(persClasses[i] instanceof IPersistentClass);
			IPersistentClass persClass = (IPersistentClass) persClasses[i];
			String fullyQualifiedName = persClass.getClassName();
			// test PersistentClasses
			openTest(persClass, consCFG, fullyQualifiedName);
			Object[] fields = pcWorkbenchAdapter.getChildren(persClass);
			for (int j = 0; j < fields.length; j++) {
				if (!(fields[j] instanceof IProperty && ((IProperty)fields[j]).classIsPropertyClass())) {
					continue;
				}
				fullyQualifiedName = persClass.getClassName();
				// test Properties
				openTest(fields[j], consCFG, fullyQualifiedName);
				if (fields[j] instanceof IProperty
					&& ((IProperty)fields[j]).isComposite()) {
					fullyQualifiedName =((IProperty) fields[j]).getValue().getComponentClassName();

					Object[] compProperties = propertyWorkbenchAdapter.getChildren(fields[j]);
					for (int k = 0; k < compProperties.length; k++) {
						if (!(compProperties[k] instanceof IProperty && ((IProperty)compProperties[k]).classIsPropertyClass())) {
							continue;
						}
						//test Composite properties
						openTest(compProperties[k], consCFG, fullyQualifiedName);
					}
				}
			}
		}
		//close all editors
	}

	public void testHbmExportExceptionTest(String packageName) throws Exception {
		String projectName = testProject.getIProject().getName();
		String pathName = "/" + projectName + "/src/" + packageName.replace('.', '/');
		IPackageFragment testPackage = testProject
				.getIJavaProject()
				.findPackageFragment(new Path(pathName));
		try {
			Object[] persClassesInit = getPersistenceClasses(true);
			ConsoleConfiguration consCFG = getConsoleConfig();
			IConfiguration config = consCFG.getConfiguration();
			//delete old hbm files
			Assert.assertNotNull(testPackage);
			int nDeleted = 0;
			if (testPackage.getNonJavaResources().length > 0) {
				Object[] ress = testPackage.getNonJavaResources();
				for (int i = 0; i < ress.length; i++) {
					if (ress[i] instanceof IFile){
						IFile res = (IFile)ress[i];
						if (res.getName().endsWith(".hbm.xml")) { //$NON-NLS-1$
							res.delete(true, false, null);
							nDeleted++;
						}
					}
				}
			}
			String[] versions = ServiceLookup.getVersions();
			IService service = ServiceLookup.findService(versions[0]);
			File srcFolder = testProject
					.getIProject()
					.getFolder(TestProject.SRC_FOLDER)
					.getLocation().toFile();
			IHibernateMappingExporter hce = service
					.newHibernateMappingExporter(config,srcFolder);
			try {
				hce.start();
				IArtifactCollector collector = service.newArtifactCollector();
				collector.formatFiles();
				
				IFolder metaInfFolder = testProject.getIProject().getFolder("src").getFolder("META-INF");
				metaInfFolder.delete(true, null);
	
				try {//build generated configuration
					testPackage.getResource().refreshLocal(IResource.DEPTH_INFINITE, null);
					testPackage.getJavaProject().getProject().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
					ConsoleConfigUtils.customizeCfgXmlForPack(testPackage);
					ConsoleConfigUtils.createConsoleConfig(
							"testHbmExportExceptionTest", 
							testProject.getIProject().getFullPath().append("src").append("hibernate.cfg.xml"), 
							testProject.getIProject().getName());
					consCFG = KnownConfigurations.getInstance().find("testHbmExportExceptionTest");
					Assert.assertNotNull(consCFG);
					consCFG.reset();
					consCFG.build();
					Assert.assertTrue(consCFG.hasConfiguration());
					consCFG.getConfiguration().buildMappings();
					config = consCFG.getConfiguration();
					consCFG = null;
					ConsoleConfigUtils.deleteConsoleConfig("testHbmExportExceptionTest");
				} catch (CoreException e) {
					String out = NLS.bind(TestConsoleMessages.UpdateConfigurationTest_error_customising_file_for_package,
							new Object[] { ConsoleConfigUtils.CFG_FILE_NAME, testPackage.getPath(), e.getMessage() } );
					Assert.fail(out);
				}
				
				File metaInfSource = ResourceReadUtils.getResourceItem("res/META-INF");
				metaInfFolder.create(true, true, null);
				FilesTransfer.copyFolder(metaInfSource, metaInfFolder);

				
			} catch (Exception e){
				throw e;
//				throw (Exception)e.getCause();
			}
			//
			Object[] persClassesReInit = getPersistenceClasses(false);
			//
			int nCreated = 0;
			if (testPackage.getNonJavaResources().length > 0) {
				Object[] ress = testPackage.getNonJavaResources();
				for (int i = 0; i < ress.length; i++) {
					if (ress[i] instanceof IFile) {
						IFile res = (IFile)ress[i];
						if (res.getName().endsWith(".hbm.xml")) { //$NON-NLS-1$
							nCreated++;
						}
					}
				}
			}
			//
			Assert.assertTrue(persClassesInit.length == persClassesReInit.length);
			Assert.assertTrue(nCreated > 0);
			Assert.assertTrue(nDeleted >= 0 && persClassesInit.length > 0);
			Assert.assertTrue(nCreated <= persClassesInit.length);
		} catch (Exception e){
			String newMessage = "\nPackage " + testPackage.getElementName() + ":"; //$NON-NLS-1$ //$NON-NLS-2$
			throw new WrapperException(newMessage, e);
		}
	}
	
	private Object[] getPersistenceClasses(boolean resetCC) {
		final ConsoleConfiguration consCFG = getConsoleConfig();
		if (resetCC) {
			consCFG.reset();
			consCFG.build();
		}
		Assert.assertTrue(consCFG.hasConfiguration());
		if (resetCC) {
			consCFG.buildMappings();
		}
		Object[] configs = null;
		Object[] persClasses = null;
		try {
			configs = ccWorkbenchAdapter.getChildren(consCFG);
			Assert.assertNotNull(configs);
			Assert.assertEquals(3, configs.length);
			Assert.assertTrue(configs[0] instanceof IConfiguration);
			persClasses = configWorkbenchAdapter.getChildren(configs[0]);
		} catch (Exception ex) {
			String out = NLS.bind(TestConsoleMessages.OpenMappingDiagramTest_mapping_diagrams_for_package_cannot_be_opened,
				new Object[] { testName.getMethodName(), ex.getMessage() });
			Assert.fail(out);
		}
		return persClasses;
	}
	
	private ConsoleConfiguration getConsoleConfig() {
		KnownConfigurations knownConfigurations = KnownConfigurations.getInstance();
		String consoleConfigName = testProject.getIProject().getName();
		final ConsoleConfiguration consCFG = knownConfigurations.find(consoleConfigName);
		Assert.assertNotNull(consCFG);
		return consCFG;
	}
	
	private void configureTestProject() throws Exception {
		testProject.generateClassPath(
				Collections.emptyList(), 
				testProject.createSourceFolder());
	}
	
	private void copyHbmXmlAndJavaFiles() throws Exception {
		IFolder sourceFolder = testProject.getIProject().getFolder("src");
		File jpaSource = ResourceReadUtils.getResourceItem("res/jpa/");
		IFolder jpaDestination = sourceFolder.getFolder("jpa");
		jpaDestination.create(true, true, null);
		FilesTransfer.copyFolder(jpaSource, jpaDestination);
		File metaInfSource = ResourceReadUtils.getResourceItem("res/META-INF");
		IFolder metaInfDestination = sourceFolder.getFolder("META-INF");
		metaInfDestination.create(true, true, null);
		FilesTransfer.copyFolder(metaInfSource, metaInfDestination);
	}
	
	private void openPropertyTestForPackage(IProperty compositeProperty, IProperty parentProperty, ConsoleConfiguration consCFG, String packageName){
		IEditorPart editor = null;
		Throwable ex = null;
		try {
			editor = OpenMappingAction.run(consCFG, compositeProperty, parentProperty);
			boolean highlighted = Utils.hasSelection(editor);
			if (!highlighted) {
				String out = NLS.bind(TestConsoleMessages.OpenMappingFileTest_highlighted_region_for_property_is_empty_package,
						new Object[]{compositeProperty.getName(), packageName });
				if (Customization.USE_CONSOLE_OUTPUT)
					System.err.println(out);
				Assert.fail(out);
			}
			Object[] compProperties = propertyWorkbenchAdapter.getChildren(compositeProperty);
			for (int k = 0; k < compProperties.length; k++) {
				//test Composite properties
				Assert.assertTrue(compProperties[k] instanceof IProperty);
				// use only first level to time safe
				//openPropertyTest((Property)compProperties[k], compositeProperty, consCFG);
			}
		} catch (PartInitException e) {
			ex = e;
		} catch (JavaModelException e) {
			ex = e;
		} catch (FileNotFoundException e) {
			ex = e;
		}
		if (ex == null ) {
			ex = Utils.getExceptionIfItOccured(editor);
		}
		if (ex != null) {
			String out = NLS.bind(TestConsoleMessages.OpenMappingFileTest_mapping_file_for_property_not_opened_package,
					new Object[]{compositeProperty.getName(), packageName, ex.getMessage()});
			Assert.fail(out);
		}
	}

	private void openTestForPackage(Object selection, ConsoleConfiguration consCFG, String packageName){
		IEditorPart editor = null;
		Throwable ex = null;
		try {
			editor = OpenMappingAction.run(consCFG, selection, null);
			boolean highlighted = Utils.hasSelection(editor);
			if (!highlighted) {
				String out = NLS.bind(TestConsoleMessages.OpenMappingFileTest_highlighted_region_for_is_empty_package,
						new Object[]{selection, packageName});
				Assert.fail(out);
			}
		} catch (PartInitException e) {
			ex = e;
		} catch (JavaModelException e) {
			ex = e;
		} catch (FileNotFoundException e) {
			ex = e;
		} catch (Exception e) {
			ex = e;
		}
		if (ex == null ) {
			ex = Utils.getExceptionIfItOccured(editor);
		}
		if (ex != null) {
			String out = NLS.bind(TestConsoleMessages.OpenMappingFileTest_mapping_file_for_not_opened_package,
					new Object[]{selection, packageName, ex.getMessage()});
			ex.printStackTrace();
			Assert.fail(out);
		}
	}

	private void openTest(Object selection, ConsoleConfiguration consCFG, String fullyQualifiedName){
		IEditorPart editor = null;
		Throwable ex = null;
		try {
			editor = OpenSourceAction.run(consCFG, selection, fullyQualifiedName);
			if (Object.class.getName().equals(fullyQualifiedName)){
				return;
			}
			boolean highlighted = Utils.hasSelection(editor);
			if (!highlighted) {
				String out = NLS.bind(TestConsoleMessages.OpenSourceFileTest_highlighted_region_for_is_empty, selection);
				if (Customization.USE_CONSOLE_OUTPUT)
					System.err.println(out);
				Assert.fail(out);
			}
		} catch (PartInitException e) {
			ex = e;
		} catch (JavaModelException e) {
			ex = e;
		} catch (FileNotFoundException e) {
			ex = e;
		}
		if (ex == null ) {
			ex = Utils.getExceptionIfItOccured(editor);
		}
		if (ex != null) {
			String out = NLS.bind(TestConsoleMessages.OpenSourceFileTest_mapping_file_for_not_opened,
					fullyQualifiedName/*.getClassName()*/, ex.getMessage());
			Assert.fail(out);
		}
	}

	private class WrapperException extends Exception {
		
		private static final long serialVersionUID = 8192540921613389467L;
		private String message;
		
		public WrapperException(String message, Exception cause){
			super(cause);
			this.message = message;
			setStackTrace(cause.getStackTrace());
		}
		
		@Override
		public Throwable getCause() {
			return null;
		}
		
		@Override
		public void printStackTrace(PrintWriter s) {
			s.println(message);
			super.getCause().printStackTrace(s);
		}
	}

}
