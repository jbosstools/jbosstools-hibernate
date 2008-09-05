package org.hibernate.eclipse.jdt.ui.test;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.hibernate.HibernateException;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences.ConfigurationMode;
import org.hibernate.eclipse.console.EclipseConsoleConfiguration;
import org.hibernate.eclipse.console.EclipseConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.test.HibernateConsoleTest;
import org.hibernate.eclipse.console.test.SimpleTestProject;
import org.hibernate.eclipse.console.test.xpl.JavaProjectHelper;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.hibernate.eclipse.jdt.ui.internal.HQLDetector;
import org.hibernate.eclipse.jdt.ui.internal.HQLProblem;

public class HibernateErrorsTest2 extends HibernateConsoleTest {

	private SimpleTestProject project;

	private ConsoleConfiguration ccfg;

	public HibernateErrorsTest2(String name) {
		super(name);	
	}
	
	@Override
	protected SimpleTestProject createTestProject() {
		return new SimpleTestProject("hqlquerytest-" + System.currentTimeMillis()) { // the currentTime Millis can be removed once the classloader stop hanging on to the jars. see JBIDE-1012
			
			@Override
			protected void buildSimpleTestProject() throws JavaModelException,
					CoreException, IOException {				
				super.buildSimpleTestProject();
				
				
				//set up project #3: file system structure with project as source folder
				//add an internal jar
				File ejb3lib= HibernateJDTuiTestPlugin.getDefault().getFileInPlugin(new Path("testresources/ejb3-persistence.jar")); //$NON-NLS-1$
				assertTrue("ejb3 lib not found", ejb3lib != null && ejb3lib.exists()); //$NON-NLS-1$
				
				JavaProjectHelper.addToClasspath(getIJavaProject(), JavaRuntime.getDefaultJREContainerEntry());
				
				IPackageFragmentRoot addLibraryWithImport = JavaProjectHelper.addLibraryWithImport(getIJavaProject(), Path.fromOSString(ejb3lib.getPath()), null, null);
				
				assertEquals(3,getIJavaProject().getRawClasspath().length);
			
				getIProject().getFolder("src/META-INF").create(true, true, new NullProgressMonitor());
				 getIProject().getFile("src/META-INF/persistence.xml").create(
		                    new ByteArrayInputStream(("<persistence>\n" + 
		                    		"   <persistence-unit name=\"manager1\" transaction-type=\"RESOURCE_LOCAL\">\n" + 
		                    		"      <class>test.TestClass</class>\n" + 
		                    		"      <properties>\n" + 
		                    		"         <property name=\"hibernate.dialect\" value=\"org.hibernate.dialect.HSQLDialect\"/>\n" + 
		                    		"         <property name=\"hibernate.connection.driver_class\" value=\"org.hsqldb.jdbcDriver\"/>\n" + 
		                    		"         <property name=\"hibernate.connection.username\" value=\"sa\"/>\n" + 
		                    		"         <property name=\"hibernate.connection.password\" value=\"\"/>\n" + 
		                    		"         <property name=\"hibernate.connection.url\" value=\"jdbc:hsqldb:.\"/>\n" +
		                    		"         <property name=\"hibernate.query.startup_check\" value=\"false\"/>\n" +		                    		
		                    		"      </properties>\n" + 
		                    		"   </persistence-unit>\n" + 
		                    		"</persistence>").getBytes()),
		                    false /* force */, new NullProgressMonitor());
				
				 getIProject().findMember("src/META-INF/persistence.xml");
				 getIProject().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
			}
			
			@Override
			protected IType buildType(IPackageFragment pack, String cuName)
					throws JavaModelException {
				ICompilationUnit cu = pack.createCompilationUnit(cuName,
						"", false, null);
				
				cu.createPackageDeclaration(pack.getElementName(),null);
				IType type = cu.createType(
						"@javax.persistence.NamedQuery(name=\"fromUnknown\", query=\"from Unknown\")\n" +
						"@javax.persistence.Entity\n" +
						"public class " + TYPE_NAME + " {}",null,false,null);
				type.createField("@javax.persistence.Id private int id;",null,false,null);
				type.createField("private String testField;",null,false,null);
				type.createMethod("public String getTestField() {return this.testField;}",null,false,null);
				type.createMethod("public void setTestField(String testField) {this.testField = testField;}",null,false,null);
				return type;
			}
		};
	}

	@Override
	protected void setUp() throws Exception {
		
		this.project = createTestProject();
		waitForJobs();
		
		String prjName = getProject().getIProject().getName();
		
		EclipseConsoleConfigurationPreferences preferences = new EclipseConsoleConfigurationPreferences(prjName,
				ConfigurationMode.JPA, prjName, true, null, null, null, new IPath[0], new IPath[0], null, null, null, false);
		
		ccfg = KnownConfigurations.getInstance().addConfiguration(new EclipseConsoleConfiguration(preferences), false);
		
		assertTrue(ProjectUtils.toggleHibernateOnProject(getProject().getIProject(), true, prjName));
		
		ccfg.build();
		ccfg.buildSessionFactory();
		
	}
	
	@Override
	protected void tearDown() throws Exception {
		ccfg.reset();
		KnownConfigurations.getInstance().removeAllConfigurations();
		//super.tearDown();
		waitForJobs();

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.closeAllEditors( false);

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.setPerspective(
						PlatformUI.getWorkbench().getPerspectiveRegistry()
								.findPerspectiveWithId(
										"org.eclipse.ui.resourcePerspective"));

		waitForJobs();
		// getProject().deleteIProject();

		// super.tearDown();

		IProject proj = getProject().getIProject();
		String projRoot = proj.getLocation().toFile().getAbsolutePath();
		File file = new File(projRoot);

		getProject().getIProject().delete(false, true, null);
		waitForJobs();
		delete(file);
	}
	public void testDummy() throws JavaModelException {
	
	}
	
	private void delete(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					delete(files[i]);
				} else {
					deleteFile(files[i]);
				}
			}
		}
		deleteFile(path);

	}

	private void deleteFile(File file) {
		try {
			if (!file.delete())
				throw new RuntimeException("Cannot remove the " + file.getAbsolutePath() + " file. (" + Platform.getOS() + ")");
		} catch (Throwable e) {
			throw new RuntimeException("Cannot remove the " + file.getAbsolutePath() + " file. (" + Platform.getOS() + ")",e);
		}
	}
	
	protected SimpleTestProject getProject() {
		return this.project;
	}
}
