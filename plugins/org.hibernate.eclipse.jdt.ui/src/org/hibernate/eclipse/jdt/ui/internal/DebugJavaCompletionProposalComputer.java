package org.hibernate.eclipse.jdt.ui.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

public class DebugJavaCompletionProposalComputer implements IJavaCompletionProposalComputer {

	public DebugJavaCompletionProposalComputer() {
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
		
		proposals.add( new MyCompletionProposal() );
		return proposals;
	}

	public static class MyCompletionProposal implements ICompletionProposal {

		public void apply(IDocument document) {
			ErrorDialog.openError( null, "My Loce", "Can't get", new Status(Status.ERROR, "bla", 3, "sdfsdf", null) );
			new CompletionProposal("Test", 2 ,7, 3).apply(document);			
		}

		public String getAdditionalProposalInfo() {
			return null;
		}

		public IContextInformation getContextInformation() {
			return null;
		}

		public String getDisplayString() {
			return "I wanna show a dialog!";
		}

		public Image getImage() {
			return null;
		}

		public Point getSelection(IDocument document) {
			return null;
		}

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
