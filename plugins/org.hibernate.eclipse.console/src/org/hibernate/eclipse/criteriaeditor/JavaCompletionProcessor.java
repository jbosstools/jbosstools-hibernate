package org.hibernate.eclipse.criteriaeditor;

import java.util.Arrays;

import org.eclipse.jdt.core.CompletionContext;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.text.java.CompletionProposalCollector;
import org.eclipse.jdt.ui.text.java.CompletionProposalComparator;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.widgets.Shell;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.hibernate.eclipse.hqleditor.CompletionHelper;
import org.hibernate.util.StringHelper;

public class JavaCompletionProcessor implements IContentAssistProcessor {

	private CompletionProposalCollector collector;

	private CriteriaEditor editor;

	private CompletionProposalComparator comparator;

	private String lastErrorMessage;

	private char[] proposalAutoActivationSet;

	public JavaCompletionProcessor(CriteriaEditor editor) {
		this.editor = editor;
		comparator = new CompletionProposalComparator();
	}

	public String getErrorMessage() {
		return lastErrorMessage;
	}

	protected void setErrorMessage(String message) {
		if ( StringHelper.isEmpty(message) ) {
			message = null;
		}
		lastErrorMessage = message;
	}

	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		return null;
	}
	
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int position) {
		try {
			setErrorMessage( null );
			if(editor.getConsoleConfiguration()==null) {
				setErrorMessage( "No console configuration found" );
				return new ICompletionProposal[0];
			}
			String prefix = "Session session;";
			try {
				IJavaProject javaProject = ProjectUtils.findJavaProject( editor.getConsoleConfiguration().getName() );
				collector = new CompletionProposalCollector( javaProject );
				collector.acceptContext( new CompletionContext() );
				
				editor.codeComplete( prefix, collector, position, javaProject );
			}
			catch (JavaModelException x) {
				Shell shell = viewer.getTextWidget().getShell();
				ErrorDialog
						.openError(
								shell,
								"Error", "Error while performing code completion", x.getStatus() ); 
				HibernateConsolePlugin.getDefault().log( x );
			}

			IJavaCompletionProposal[] results = collector
					.getJavaCompletionProposals();

			Arrays.sort( results, comparator );
			CompletionHelper.transpose( null, -prefix.length(), results );
			return results;
		}
		finally {
			if(collector!=null) {
				setErrorMessage( collector.getErrorMessage() );
				collector = null;	
			}
		}		
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		return proposalAutoActivationSet;
	}

	public void setCompletionProposalAutoActivationCharacters(
			char[] activationSet) {
		proposalAutoActivationSet = activationSet;
	}

	public void orderProposalsAlphabetically(boolean order) {
		comparator.setOrderAlphabetically( order );
	}
}
