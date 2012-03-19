package org.hibernate.eclipse.jdt.ui.test;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.test.project.SimpleTestProject;
import org.hibernate.eclipse.console.test.project.SimpleTestProjectWithMapping;
import org.hibernate.eclipse.console.test.project.TestProject;
import org.hibernate.eclipse.console.test.utils.ConsoleConfigUtils;
import org.hibernate.eclipse.jdt.ui.internal.HQLJavaCompletionProposalComputer;

/**
 * 
 * @author Dmitry Geraskov (geraskov@gmail.com)
 * 
 */
public class JavaHBMQueryTest extends TestCase {
	
	private static final String PROJ_NAME = "JavaHBMQueryTest"; //$NON-NLS-1$
	private static final String CONSOLE_NAME = PROJ_NAME;
	
	private SimpleTestProjectWithMapping project = null;
	private ConsoleConfiguration cc;
	
	protected void setUp() throws Exception {
		project = new SimpleTestProjectWithMapping(PROJ_NAME);

		IPackageFragmentRoot sourceFolder = project.createSourceFolder();
		IPackageFragment pf = sourceFolder.createPackageFragment(
				SimpleTestProject.PACKAGE_NAME, false, null);
		ConsoleConfigUtils.customizeCfgXmlForPack(pf);
		List<IPath> libs = new ArrayList<IPath>();
		project.generateClassPath(libs, sourceFolder);
		project.setDefaultConsoleConfiguration(CONSOLE_NAME);
		project.fullBuild();

		// setup console configuration
		IPath cfgFilePath = new Path(project.getIProject().getName()
				+ File.separator + TestProject.SRC_FOLDER + File.separator
				+ ConsoleConfigUtils.CFG_FILE_NAME);
		ConsoleConfigUtils.createConsoleConfig(PROJ_NAME, cfgFilePath,
				CONSOLE_NAME);
		ConsoleConfiguration cc = KnownConfigurations.getInstance().find(
				CONSOLE_NAME);
		assertNotNull("Console Configuration not found", cc); //$NON-NLS-1$
		cc.build();
	}

	protected void tearDown() throws Exception {
		cleanUpProject();
	}
	
	protected void cleanUpProject() {
		if (project != null) {
			project.deleteIProject();
			project = null;
		}
	}

	@SuppressWarnings("unused")
	public void testJavaHQLQueryCodeCompletion() throws JavaModelException,
			CoreException, NoSuchFieldException, IllegalAccessException {
		IPackageFragment pack = project.getTestClassType().getPackageFragment();

		String testCP = "TestCompletionProposals.java";
		ICompilationUnit cu = pack
				.createCompilationUnit(testCP, "", true, null); //$NON-NLS-1$

		cu.createPackageDeclaration(pack.getElementName(), null);
		IType type = cu.createType(
				"public class " + testCP + " {}", null, false, null); //$NON-NLS-1$//$NON-NLS-2$
		type.createMethod(
				"public static void main(String[] args){String query = \"from \";}", null, false, null); //$NON-NLS-1$

		IWorkbenchPage p = JavaPlugin.getActivePage();
		IEditorPart part = EditorUtility.openInEditor(type, true);
		if (part instanceof CompilationUnitEditor) {
			CompilationUnitEditor editor = (CompilationUnitEditor) part;
			IDocument doc = editor.getDocumentProvider().getDocument(
					editor.getEditorInput());

			HQLJavaCompletionProposalComputer proposalComputer = new HQLJavaCompletionProposalComputer();
			ContentAssistInvocationContext context = new JavaContentAssistInvocationContext(editor.getViewer(), 125, editor);
			List<ICompletionProposal> computeCompletionProposals = proposalComputer.computeCompletionProposals(context, null);
			for (ICompletionProposal iCompletionProposal : computeCompletionProposals) {
				Class<? extends ICompletionProposal> class1 = iCompletionProposal.getClass();
				if (class1.getPackage().getName().indexOf("org.jboss.tools.hibernate") == 0){
					//this is our completion proposal
					Field declaredField = class1.getDeclaredField("documentOffset");
					declaredField.setAccessible(true);
					Integer offset = (Integer) declaredField.get(iCompletionProposal);
					Assert.assertTrue(offset > 0);
				}
			}
		} else {
			fail("Can't open CompilationUnitEditor");
		}
	}

}
