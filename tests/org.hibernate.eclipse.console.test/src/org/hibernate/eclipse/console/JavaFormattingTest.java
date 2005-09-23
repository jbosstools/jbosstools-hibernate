package org.hibernate.eclipse.console;

import javax.management.monitor.Monitor;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.actions.FormatAllAction;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;

public class JavaFormattingTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();

	}

	protected void tearDown() throws Exception {
	}

	public void testJavaFormatting() throws JavaModelException {

		IFile file = ResourcesPlugin
				.getWorkspace()
				.getRoot()
				.getFile(
						new Path(
								"C:/work/products/jboss-seam-1.0beta1/examples/noejb/src/org/jboss/seam/example/booking/RegisterAction.java" ) );

		IJavaElement element = JavaCore.create( file );
	
		((ICompilationUnit)element).becomeWorkingCopy(null, new NullProgressMonitor());
		
		IWorkbenchPartSite activePart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().getActivePart().getSite();
		FormatAllAction action = new FormatAllAction(activePart);
		
		action.runOnMultiple(new ICompilationUnit[] { (ICompilationUnit) element } );
		
		((ICompilationUnit)element).commit(true, new NullProgressMonitor());
	}

}
