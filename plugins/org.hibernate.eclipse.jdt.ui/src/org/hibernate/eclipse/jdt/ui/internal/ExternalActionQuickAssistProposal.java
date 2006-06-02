package org.hibernate.eclipse.jdt.ui.internal;

import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.hibernate.eclipse.nature.HibernateNature;

public abstract class ExternalActionQuickAssistProposal implements
		IJavaCompletionProposal {

	private String contents;
	private ICompletionProposal proposal;
	private String name;

	public ExternalActionQuickAssistProposal(String contents, Image image, String description, IInvocationContext context) {
		this.contents = contents;
		HibernateNature hibernateNature = HibernateNature.getHibernateNature(context.getCompilationUnit().getJavaProject());
		if(hibernateNature!=null) {
			name = hibernateNature.getDefaultConsoleConfigurationName();
		} else {
			name = null;
		}
		
		proposal = new CompletionProposal("",context.getSelectionLength(),0,context.getSelectionOffset()+context.getSelectionLength(), image, description, null,null);
	}
	
	public String getContents() {
		return contents;
	}
	
	public String getName() {
		return name;
	}
	
	public int getRelevance() {
		return 0;
	}

	abstract public void apply(IDocument document);

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
