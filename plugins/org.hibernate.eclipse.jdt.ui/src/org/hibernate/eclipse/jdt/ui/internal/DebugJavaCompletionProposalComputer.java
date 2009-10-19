/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
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

	public List<ICompletionProposal> computeCompletionProposals(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();

		proposals.add( new CompletionProposal( "hibernate tools", context.getInvocationOffset(), 7, context.getInvocationOffset()) ); //$NON-NLS-1$
		CharSequence computeIdentifierPrefix = null;
		try {
			computeIdentifierPrefix = context.computeIdentifierPrefix();
		}
		catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		proposals.add( new CompletionProposal("ctxid: [" + computeIdentifierPrefix + "]", context.getInvocationOffset(), 7, context.getInvocationOffset()) );  //$NON-NLS-1$//$NON-NLS-2$
		proposals.add( new CompletionProposal("Class: " + context.getClass(), context.getInvocationOffset() ,7, context.getInvocationOffset()) ); //$NON-NLS-1$
		if(context instanceof JavaContentAssistInvocationContext) {
			JavaContentAssistInvocationContext jcaic = (JavaContentAssistInvocationContext) context;
			proposals.add( new CompletionProposal("Cu: " + jcaic.getCompilationUnit(), context.getInvocationOffset() ,7, context.getInvocationOffset()) ); //$NON-NLS-1$
			proposals.add( new CompletionProposal("Corectx: " + jcaic.getCoreContext(), context.getInvocationOffset() ,7, context.getInvocationOffset()) ); //$NON-NLS-1$
			proposals.add( new CompletionProposal("type: " + jcaic.getExpectedType(), context.getInvocationOffset() ,7, context.getInvocationOffset()) ); //$NON-NLS-1$
		}

		proposals.add( new MyCompletionProposal() );
		return proposals;
	}

	public static class MyCompletionProposal implements ICompletionProposal {

		public void apply(IDocument document) {
			ErrorDialog.openError( null, "My Loce", "Can't get", new Status(Status.ERROR, "bla", 3, "sdfsdf", null) );   //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$
			new CompletionProposal("Test", 2 ,7, 3).apply(document);			 //$NON-NLS-1$
		}

		public String getAdditionalProposalInfo() {
			return null;
		}

		public IContextInformation getContextInformation() {
			return null;
		}

		public String getDisplayString() {
			return JdtUiMessages.DebugJavaCompletionProposalComputer_displaystring;
		}

		public Image getImage() {
			return null;
		}

		public Point getSelection(IDocument document) {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
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
