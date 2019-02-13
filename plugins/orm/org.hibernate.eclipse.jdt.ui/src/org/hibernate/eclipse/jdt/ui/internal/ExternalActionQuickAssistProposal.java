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
		
		proposal = new CompletionProposal("",context.getSelectionLength(),0,context.getSelectionOffset()+context.getSelectionLength(), image, description, null,null); //$NON-NLS-1$
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
