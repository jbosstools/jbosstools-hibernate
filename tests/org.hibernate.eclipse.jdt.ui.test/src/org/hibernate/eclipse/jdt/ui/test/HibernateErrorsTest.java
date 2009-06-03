package org.hibernate.eclipse.jdt.ui.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.hibernate.eclipse.console.test.HibernateConsoleTest;
import org.hibernate.eclipse.console.test.project.SimpleTestProject;
import org.hibernate.eclipse.console.test.project.xpl.JavaProjectHelper;
import org.hibernate.eclipse.console.test.utils.FilesTransfer;

public class HibernateErrorsTest extends HibernateConsoleTest {

	private SimpleTestProject project;

	public HibernateErrorsTest(String name) {
		super(name);
	}

	@Override
	protected SimpleTestProject createTestProject() {
		return new SimpleTestProject("hqlquerytest-" //$NON-NLS-1$
				+ System.currentTimeMillis()) { // the currentTime Millis can be
												// removed once the classloader
												// stop hanging on to the jars.
												// see JBIDE-1012

			@Override
			protected void buildProject() throws JavaModelException,
					CoreException, IOException {
				super.buildProject();

				// set up project #3: file system structure with project as
				// source folder
				// add an internal jar
				File ejb3lib = HibernateJDTuiTestPlugin.getDefault()
						.getFileInPlugin(
								new Path("testresources/ejb3-persistence.jar")); //$NON-NLS-1$
				assertTrue(
						"ejb3 lib not found", ejb3lib != null && ejb3lib.exists()); //$NON-NLS-1$

				JavaProjectHelper.addToClasspath(getIJavaProject(), JavaRuntime
						.getDefaultJREContainerEntry());

				IPackageFragmentRoot addLibraryWithImport = JavaProjectHelper
						.addLibraryWithImport(getIJavaProject(), Path
								.fromOSString(ejb3lib.getPath()), null, null);
				addLibraryWithImport.hasChildren();

				assertEquals(3, getIJavaProject().getRawClasspath().length);

				getIProject().getFolder("src/META-INF").create(true, true, //$NON-NLS-1$
						new NullProgressMonitor());
				getIProject()
						.getFile("src/META-INF/persistence.xml") //$NON-NLS-1$
						.create(
								new ByteArrayInputStream(
										("<persistence>\n" //$NON-NLS-1$
												+ "   <persistence-unit name=\"manager1\" transaction-type=\"RESOURCE_LOCAL\">\n" //$NON-NLS-1$
												+ "      <class>test.TestClass</class>\n" //$NON-NLS-1$
												+ "      <properties>\n" //$NON-NLS-1$
												+ "         <property name=\"hibernate.dialect\" value=\"org.hibernate.dialect.HSQLDialect\"/>\n" //$NON-NLS-1$
												+ "         <property name=\"hibernate.connection.driver_class\" value=\"org.hsqldb.jdbcDriver\"/>\n" //$NON-NLS-1$
												+ "         <property name=\"hibernate.connection.username\" value=\"sa\"/>\n" //$NON-NLS-1$
												+ "         <property name=\"hibernate.connection.password\" value=\"\"/>\n" //$NON-NLS-1$
												+ "         <property name=\"hibernate.connection.url\" value=\"jdbc:hsqldb:.\"/>\n" //$NON-NLS-1$
												+ "         <property name=\"hibernate.query.startup_check\" value=\"false\"/>\n" //$NON-NLS-1$
												+ "      </properties>\n" //$NON-NLS-1$
												+ "   </persistence-unit>\n" //$NON-NLS-1$
												+ "</persistence>").getBytes()), //$NON-NLS-1$
								false /* force */, new NullProgressMonitor());

				getIProject().findMember("src/META-INF/persistence.xml"); //$NON-NLS-1$
				getIProject().build(IncrementalProjectBuilder.FULL_BUILD,
						new NullProgressMonitor());
			}

			@Override
			protected IType buildType(IPackageFragment pack, String cuName)
					throws JavaModelException {
				ICompilationUnit cu = pack.createCompilationUnit(cuName, "", //$NON-NLS-1$
						false, null);

				cu.createPackageDeclaration(pack.getElementName(), null);
				IType type = cu.createType(
						"@javax.persistence.NamedQuery(name=\"fromUnknown\", query=\"from Unknown\")\n" //$NON-NLS-1$
								+ "@javax.persistence.Entity\n" //$NON-NLS-1$
								+ "public class " + TYPE_NAME + " {}", null, //$NON-NLS-1$ //$NON-NLS-2$
						false, null);
				type.createField("@javax.persistence.Id private int id;", null, //$NON-NLS-1$
						false, null);
				type
						.createField("private String testField;", null, false, //$NON-NLS-1$
								null);
				type
						.createMethod(
								"public String getTestField() {return this.testField;}", //$NON-NLS-1$
								null, false, null);
				type
						.createMethod(
								"public void setTestField(String testField) {this.testField = testField;}", //$NON-NLS-1$
								null, false, null);
				return type;
			}
		};
	}

	@Override
	protected void setUp() throws Exception {
		this.project = createTestProject();
		waitForJobs();

	}

	@Override
	protected void tearDown() throws Exception {
		// ccfg.reset();
		// super.tearDown();
		waitForJobs();

		IEditorPart editorPart = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.closeEditor(editorPart, false);

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.setPerspective(
						PlatformUI.getWorkbench().getPerspectiveRegistry()
								.findPerspectiveWithId(
										"org.eclipse.ui.resourcePerspective")); //$NON-NLS-1$

		waitForJobs();
		// getProject().deleteIProject();

		// super.tearDown();

		IProject proj = getProject().getIProject();
		String projRoot = proj.getLocation().toFile().getAbsolutePath();
		File file = new File(projRoot);

		getProject().getIProject().delete(false, true, null);
		waitForJobs();
		FilesTransfer.delete(file);
	}

	public void testDummy() throws JavaModelException {

	}

	protected SimpleTestProject getProject() {
		return this.project;
	}

}
