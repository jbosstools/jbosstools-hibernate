package org.jboss.tools.hibernate.orm.test.utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.IPackagesViewPart;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.hibernate.eclipse.console.HibernateConsolePerspectiveFactory;
import org.jboss.tools.hibernate.orm.test.utils.project.SimpleTestProject;

public class HibernateConsoleTestHelper {

	private SimpleTestProject project;

	public void setUp() throws Exception {

		this.project = new SimpleTestProject();

		PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().setPerspective(
						PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(HibernateConsolePerspectiveFactory.ID_CONSOLE_PERSPECTIVE));


		IPackagesViewPart packageExplorer = null;
		try {
			packageExplorer = (IPackagesViewPart) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().showView(JavaUI.ID_PACKAGES);
		} catch (PartInitException e) {
			throw new RuntimeException(e);
		}

		IType type = this.project.getTestClassType();
		packageExplorer.selectAndReveal(type);


		FileEditorInput input = new FileEditorInput((IFile) type.getCompilationUnit().getCorrespondingResource());

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, JavaUI.ID_CU_EDITOR );

	}

	public void tearDown() throws Exception {

		IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditor(editorPart, false);

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(
				PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId("org.eclipse.ui.resourcePerspective")); //$NON-NLS-1$


		this.project.deleteIProject();
		this.project = null;

	}
	
	public SimpleTestProject getProject() {
		return this.project;
	}

}
