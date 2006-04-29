package org.hibernate.eclipse.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.contentassist.CompletionProposal;

public class JavaCompletionProposalComputer implements IJavaCompletionProposalComputer {

	public JavaCompletionProposalComputer() {
		super();
	}

	public List computeCompletionProposals(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		List proposals = new ArrayList();
		
		proposals.add( new CompletionProposal("hibernate tools", context.getInvocationOffset(), 7, context.getInvocationOffset()) );
		CharSequence computeIdentifierPrefix = null;
		try {
			computeIdentifierPrefix = context.computeIdentifierPrefix();
		}
		catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		proposals.add( new CompletionProposal("ctxid: [" + computeIdentifierPrefix + "]", context.getInvocationOffset(), 7, context.getInvocationOffset()) );
		proposals.add( new CompletionProposal("Class: " + context.getClass(), context.getInvocationOffset() ,7, context.getInvocationOffset()) );
		if(context instanceof JavaContentAssistInvocationContext) {
			JavaContentAssistInvocationContext jcaic = (JavaContentAssistInvocationContext) context;
			proposals.add( new CompletionProposal("Cu: " + jcaic.getCompilationUnit(), context.getInvocationOffset() ,7, context.getInvocationOffset()) );
			proposals.add( new CompletionProposal("Corectx: " + jcaic.getCoreContext(), context.getInvocationOffset() ,7, context.getInvocationOffset()) );
			proposals.add( new CompletionProposal("type: " + jcaic.getExpectedType(), context.getInvocationOffset() ,7, context.getInvocationOffset()) );
			
			
		}
		return proposals;
	}

	public List computeContextInformation(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		// TODO Auto-generated method stub
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
