package org.hibernate.eclipse.console.test;

import java.util.Map;

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
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.ui.actions.FormatAllAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;

public class JavaFormattingTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();

	}

	protected void tearDown() throws Exception {
	}

	public void testJavaFormatting() throws JavaModelException, MalformedTreeException, BadLocationException {
/*
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
		
		((ICompilationUnit)element).commit(true, new NullProgressMonitor());*/
		
		Map codeFormatterOptions = null;
		CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(codeFormatterOptions);
		String contents = "java.lang.String str; System.out.println();";
		IDocument doc = new Document(contents);
		TextEdit edit = codeFormatter.format(CodeFormatter.K_UNKNOWN, doc.get(), 0, doc.get().length(), 0, null);
		
		edit.apply(doc);
		String newcontents = doc.get();
		//assertEquals(newcontents,"java.lang.String str; \nSystem.out.println();");
		
	}

}
