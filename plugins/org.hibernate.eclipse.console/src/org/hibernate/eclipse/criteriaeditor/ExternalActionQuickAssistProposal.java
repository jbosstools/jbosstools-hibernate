package org.hibernate.eclipse.criteriaeditor;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

public class ExternalActionQuickAssistProposal implements
		IJavaCompletionProposal {

	private String contents;
	private ICompletionProposal proposal;
	private String name;

	public ExternalActionQuickAssistProposal(String contents, Image image, String description, IInvocationContext context) {
		this.contents = contents;
		this.name = context.getCompilationUnit().getJavaProject().getProject().getName();
		proposal = new CompletionProposal("",context.getSelectionLength(),0,context.getSelectionLength(), image, description, null,null);
	}
	
	public int getRelevance() {
		return 0;
	}

	public void apply(IDocument document) {
		HibernateConsolePlugin.getDefault().openCriteriaEditor(name, contents);
	}

	public String getAdditionalProposalInfo() {
		return proposal.getAdditionalProposalInfo();
	}

	public IContextInformation getContextInformation() {
		return proposal.getContextInformation();
	}

	public String getDisplayString() {
		return proposal.getDisplayString();
	}

	public Image getImage() {
		return proposal.getImage();
	}

	public Point getSelection(IDocument document) {
		return proposal.getSelection( document );
	}
	

}
