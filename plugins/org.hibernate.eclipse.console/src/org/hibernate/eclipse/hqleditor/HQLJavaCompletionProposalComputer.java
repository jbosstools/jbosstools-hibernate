package org.hibernate.eclipse.hqleditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.CompletionContext;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

public class HQLJavaCompletionProposalComputer implements IJavaCompletionProposalComputer {

	HQLCompletionProcessor hqlProcessor;
	
	public HQLJavaCompletionProposalComputer() {
		super();
		hqlProcessor = new HQLCompletionProcessor(null);
	}

	ConsoleConfiguration getConfiguration(IJavaProject javaProject) {
		if(javaProject != null) {
			String name = javaProject.getProject().getName();
			return KnownConfigurations.getInstance().find( name );
		} else {
			return null;
		}
	}
	public List computeCompletionProposals(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		List proposals = new ArrayList();
		
		JavaContentAssistInvocationContext ctx = (JavaContentAssistInvocationContext)context;
		CompletionContext coreContext = ctx.getCoreContext();
		try {
		if(coreContext!=null) {
			 int kind = coreContext.getTokenKind();
			 if(kind==CompletionContext.TOKEN_KIND_STRING_LITERAL) { 
				 ConsoleConfiguration configuration = getConfiguration( ctx.getProject() );
				 if(configuration!=null) {
					 proposals = Arrays.asList( hqlProcessor.computeProposals( context.getDocument(), 0, context.getInvocationOffset(), configuration ));
				 }
			 } else {
				 
			 }
		}	
		} catch(RuntimeException re) {
			HibernateConsolePlugin.getDefault().logErrorMessage( "Error while performing HQL completion in java", re );
		}
		return proposals;
	}

	public List computeContextInformation(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		return Collections.EMPTY_LIST;
	}

	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	public void sessionEnded() {
		// TODO Auto-generated method stub
		
	}

	public void sessionStarted() {
		// TODO Auto-generated method stub
		
	}

}
