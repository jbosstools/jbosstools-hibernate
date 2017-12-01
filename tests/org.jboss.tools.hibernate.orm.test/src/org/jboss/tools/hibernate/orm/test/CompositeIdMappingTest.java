package org.jboss.tools.hibernate.orm.test;

import java.io.File;
import java.util.Collections;
import java.util.StringTokenizer;

import org.apache.tools.ant.filters.StringInputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.ui.IPackagesViewPart;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.hibernate.eclipse.console.workbench.ConfigurationWorkbenchAdapter;
import org.hibernate.eclipse.console.workbench.ConsoleConfigurationWorkbenchAdapter;
import org.jboss.tools.hibernate.orm.test.utils.ConsoleConfigUtils;
import org.jboss.tools.hibernate.orm.test.utils.ResourceReadUtils;
import org.jboss.tools.hibernate.orm.test.utils.TestConsoleMessages;
import org.jboss.tools.hibernate.orm.test.utils.project.FilesTransfer;
import org.jboss.tools.hibernate.orm.test.utils.project.TestProject;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class CompositeIdMappingTest {
	
	private static final String CFG_XML = 
		"<!DOCTYPE hibernate-configuration PUBLIC                                                                     \n" +
		"	'-//Hibernate/Hibernate Configuration DTD 3.0//EN'                                                        \n" +
		"	'http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd'>                                       \n" +
		"                                                                                                             \n" +
		"<hibernate-configuration>                                                                                    \n" +
		"	<session-factory>                                                                                         \n" +
		"       <property name='dialect'>org.hibernate.dialect.HSQLDialect</property>                                \n" +
		"       <mapping resource='/mapping/abstractembeddedcomponents/cid/abstractembeddedcomponents.cid.hbm.xml' /> \n" +
		"	</session-factory>                                                                                        \n" +
		"</hibernate-configuration>                                                                                    " ;
	
	@Rule
	public TestName testName = new TestName();
	
	private ConsoleConfigurationWorkbenchAdapter ccWorkbenchAdapter = new ConsoleConfigurationWorkbenchAdapter(); 
	private ConfigurationWorkbenchAdapter configWorkbenchAdapter = new ConfigurationWorkbenchAdapter();
	private TestProject testProject = null;
	
	@Before
	public void setUp() throws Exception {
		testProject = 
				new TestProject(
						"JUnitTestProj" + System.currentTimeMillis());
		
		String consoleConfigName = testProject.getIProject().getName();

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
		createCfgXMLFile();
		copyHbmXmlAndJavaFiles();

		IPath cfgFilePath = new Path(testProject.getIProject().getName() + File.separator +
				TestProject.SRC_FOLDER + File.separator + ConsoleConfigUtils.CFG_FILE_NAME);
		ConsoleConfigUtils.createConsoleConfig(consoleConfigName, 
				cfgFilePath, testProject.getIProject().getName());

		ProjectUtils.toggleHibernateOnProject(testProject.getIProject(), true, consoleConfigName);
		testProject.fullBuild();
	}
	
	@After
	public void tearDown() {
		String consoleConfigName = testProject.getIProject().getName();
		ProjectUtils.toggleHibernateOnProject(testProject.getIProject(), false, consoleConfigName);
		ConsoleConfigUtils.deleteConsoleConfig(consoleConfigName);
		testProject.deleteIProject(false);
		testProject = null;
		consoleConfigName = null;
	}
	
	@Test
	public void testCheckConsoleConfiguration() {
		Object[] persClasses = getPersistenceClasses(true);
		Assert.assertTrue(persClasses.length > 0);
		for (int i = 0; i < persClasses.length; i++) {
			Assert.assertTrue(persClasses[i] instanceof IPersistentClass);
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
	
	private void createCfgXMLFile() throws Exception {
		IPath cfgFilePath = new Path(
				TestProject.SRC_FOLDER + 
				File.separator + 
				ConsoleConfigUtils.CFG_FILE_NAME);	
		IFile cfgFile = testProject.getIProject().getFile(cfgFilePath);
		cfgFile.create(new StringInputStream(CFG_XML), true, null);
	}
	
	private void copyHbmXmlAndJavaFiles() throws Exception {
		File source = ResourceReadUtils.getResourceItem("res/project/src/mapping/abstractembeddedcomponents/cid");
		IFolder destination = createPackage("mapping.abstractembeddedcomponents.cid");
		FilesTransfer.copyFolder(source, destination);
	}
	
	private IFolder createPackage(String packageName) throws Exception {
		IFolder result = testProject.getIProject().getFolder("src");
		StringTokenizer st = new StringTokenizer(packageName, ".");
		while (st.hasMoreTokens()) {
			String segmentName = st.nextToken();
			result = result.getFolder(segmentName);
			result.create(true, true, null);
		}
		return result;
	}
	

}
