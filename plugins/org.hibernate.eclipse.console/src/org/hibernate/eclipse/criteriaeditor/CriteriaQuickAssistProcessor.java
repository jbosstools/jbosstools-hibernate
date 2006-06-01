package org.hibernate.eclipse.criteriaeditor;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickAssistProcessor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.ui.part.FileEditorInput;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.eclipse.nature.HibernateNature;


public class CriteriaQuickAssistProcessor implements IQuickAssistProcessor {

	public IJavaCompletionProposal[] getAssists(IInvocationContext context,
			IProblemLocation[] locations) throws CoreException {
		
		IJavaCompletionProposal[] result = new IJavaCompletionProposal[0];
		if(!hasAssists( context )) return result;
		
		IDocument document = getDocument( context.getCompilationUnit() );
		try {
			String contents = document.get( context.getSelectionOffset(), context.getSelectionLength() );
			result = new IJavaCompletionProposal[1];			
			result[0] = new ExternalActionQuickAssistProposal(contents, EclipseImages.getImage(ImageConstants.CRITERIA_EDITOR), "Copy to Criteria Editor", context);
		}
		catch (BadLocationException e) {
			HibernateConsolePlugin.getDefault().logErrorMessage( "Could not get document contents for CriteriaQuickAssist", e );
		}
		return result;
	}
	
	private IDocument getDocument(ICompilationUnit cu) throws JavaModelException {
		IFile file= (IFile) cu.getResource();
		IDocument document= JavaUI.getDocumentProvider().getDocument(new FileEditorInput(file));
		if (document == null) {
			return new Document(cu.getSource()); // only used by test cases
		}
		return document;
	}


	public boolean hasAssists(IInvocationContext context) throws CoreException {
		IJavaProject javaProject = context.getCompilationUnit().getJavaProject();
		HibernateNature nature = HibernateNature.getHibernateNature( javaProject );
		if(nature!=null) {
			return nature.getDefaultConsoleConfiguration()!=null;
		} else {
			return false;
		}
	}

}
