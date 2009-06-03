package org.hibernate.eclipse.jdt.ui.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.ui.PlatformUI;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences.ConfigurationMode;
import org.hibernate.eclipse.console.EclipseConsoleConfiguration;
import org.hibernate.eclipse.console.EclipseConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.test.HibernateConsoleTest;
import org.hibernate.eclipse.console.test.project.SimpleTestProject;
import org.hibernate.eclipse.console.test.project.xpl.JavaProjectHelper;
import org.hibernate.eclipse.console.test.utils.FilesTransfer;
import org.hibernate.eclipse.console.utils.ProjectUtils;

public class HibernateErrorsTest2 extends HibernateConsoleTest {

	private SimpleTestProject project;

	private ConsoleConfiguration ccfg;

	private boolean deleted;

	public HibernateErrorsTest2(String name) {
		super(name);	
	}
	
	@Override
	protected SimpleTestProject createTestProject() {
		// the currentTime Millis can be removed once the classloader stop hanging on to the jars. see JBIDE-1012
		return new SimpleTestProject("hqlquerytest-" + System.currentTimeMillis()) { //$NON-NLS-1$
			
			@Override
			protected void buildProject() throws JavaModelException,
					CoreException, IOException {				
				super.buildProject();
				
				
				//set up project #3: file system structure with project as source folder
				//add an internal jar
				File ejb3lib= HibernateJDTuiTestPlugin.getDefault().getFileInPlugin(new Path("testresources/ejb3-persistence.jar")); //$NON-NLS-1$
				assertTrue("ejb3 lib not found", ejb3lib != null && ejb3lib.exists()); //$NON-NLS-1$
				
				JavaProjectHelper.addToClasspath(getIJavaProject(), JavaRuntime.getDefaultJREContainerEntry());
				
				IPackageFragmentRoot addLibraryWithImport = JavaProjectHelper.addLibraryWithImport(getIJavaProject(), Path.fromOSString(ejb3lib.getPath()), null, null);
				addLibraryWithImport.hasChildren();
				
				assertEquals(3,getIJavaProject().getRawClasspath().length);
			
				getIProject().getFolder("src/META-INF").create(true, true, new NullProgressMonitor()); //$NON-NLS-1$
				 getIProject().getFile("src/META-INF/persistence.xml").create( //$NON-NLS-1$
		                    new ByteArrayInputStream(("<persistence>\n" +  //$NON-NLS-1$
		                    		"   <persistence-unit name=\"manager1\" transaction-type=\"RESOURCE_LOCAL\">\n" +  //$NON-NLS-1$ 
		                    		"      <class>test.TestClass</class>\n" +   //$NON-NLS-1$
		                    		"      <properties>\n" +   //$NON-NLS-1$
		                    		"         <property name=\"hibernate.dialect\" value=\"org.hibernate.dialect.HSQLDialect\"/>\n" +  //$NON-NLS-1$ 
		                    		"         <property name=\"hibernate.connection.driver_class\" value=\"org.hsqldb.jdbcDriver\"/>\n" +   //$NON-NLS-1$
		                    		"         <property name=\"hibernate.connection.username\" value=\"sa\"/>\n" +   //$NON-NLS-1$
		                    		"         <property name=\"hibernate.connection.password\" value=\"\"/>\n" +   //$NON-NLS-1$
		                    		"         <property name=\"hibernate.connection.url\" value=\"jdbc:hsqldb:.\"/>\n" +  //$NON-NLS-1$
		                    		"         <property name=\"hibernate.query.startup_check\" value=\"false\"/>\n" +		             //$NON-NLS-1$         		
		                    		"      </properties>\n" +  //$NON-NLS-1$ 
		                    		"   </persistence-unit>\n" +   //$NON-NLS-1$
		                    		"</persistence>").getBytes()),  //$NON-NLS-1$
		                    false /* force */, new NullProgressMonitor());
				
				 getIProject().findMember("src/META-INF/persistence.xml");  //$NON-NLS-1$
				 getIProject().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
			}
			
			@Override
			protected IType buildType(IPackageFragment pack, String cuName)
					throws JavaModelException {
				ICompilationUnit cu = pack.createCompilationUnit(cuName,
						"", false, null); //$NON-NLS-1$
				
				cu.createPackageDeclaration(pack.getElementName(),null);
				IType type = cu.createType(
						"@javax.persistence.NamedQuery(name=\"fromUnknown\", query=\"from Unknown\")\n" + //$NON-NLS-1$
						"@javax.persistence.Entity\n" + //$NON-NLS-1$
						"public class " + TYPE_NAME + " {}",null,false,null);  //$NON-NLS-1$//$NON-NLS-2$
				type.createField("@javax.persistence.Id private int id;",null,false,null); //$NON-NLS-1$
				type.createField("private String testField;",null,false,null); //$NON-NLS-1$
				type.createMethod("public String getTestField() {return this.testField;}",null,false,null); //$NON-NLS-1$
				type.createMethod("public void setTestField(String testField) {this.testField = testField;}",null,false,null); //$NON-NLS-1$
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
				ConfigurationMode.JPA, prjName, true, null, null, null, new IPath[0], new IPath[0], null, null, null, null);
		
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
										"org.eclipse.ui.resourcePerspective")); //$NON-NLS-1$

		waitForJobs();
		// getProject().deleteIProject();

		// super.tearDown();

		final IProject proj = getProject().getIProject();
		String projRoot = proj.getLocation().toFile().getAbsolutePath();
		File file = new File(projRoot);
		deleted = false;
		int nTrys = 0;
		while (!deleted && nTrys++ < 5) {
			ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					try {
						proj.delete(true, true, null);
						deleted = true;
					} catch (ResourceException re) {
						//waitForJobs();
						delay(1000);
					}
				}
			}, new NullProgressMonitor());
		}
		waitForJobs();
		FilesTransfer.delete(file);
	}
	public void testDummy() throws JavaModelException {
	
	}
	
	protected SimpleTestProject getProject() {
		return this.project;
	}
}
