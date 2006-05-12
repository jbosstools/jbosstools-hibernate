package org.hibernate.eclipse.hqleditor;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickAssistProcessor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.part.FileEditorInput;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.utils.EclipseImages;


public class HQLQuickAssistProcessor implements IQuickAssistProcessor {

	public IJavaCompletionProposal[] getAssists(IInvocationContext context,
			IProblemLocation[] locations) throws CoreException {
		
		IJavaCompletionProposal[] result = new IJavaCompletionProposal[0];
		if(!hasAssists( context )) return result;
		
		ASTNode coveringNode = context.getCoveringNode();
		if(!(coveringNode instanceof StringLiteral)) {
			return result;
		}
		
		StringLiteral stringLiteral= (StringLiteral) coveringNode;
		String contents= stringLiteral.getLiteralValue();
		result = new IJavaCompletionProposal[1];			
		result[0] = new ExternalActionQuickAssistProposal(contents, EclipseImages.getImage(ImageConstants.HQL_EDITOR), "Copy to HQL Editor", context);
		
		return result;
	}
	
	public boolean hasAssists(IInvocationContext context) throws CoreException {
		IJavaProject javaProject = context.getCompilationUnit().getJavaProject();
		if(javaProject != null) {
			String name = javaProject.getProject().getName();
			return KnownConfigurations.getInstance().find( name )!=null;
		}
		return false;
	}

}
