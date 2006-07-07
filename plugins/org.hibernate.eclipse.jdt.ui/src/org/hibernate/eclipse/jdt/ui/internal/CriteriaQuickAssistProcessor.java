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


import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.part.FileEditorInput;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.EclipseImages;


public class CriteriaQuickAssistProcessor extends BasicQuickAssistProcessor  {

	public IJavaCompletionProposal[] getAssists(IInvocationContext context,
			IProblemLocation[] locations) throws CoreException {
		
		IJavaCompletionProposal[] result = new IJavaCompletionProposal[0];
		if(!hasAssists( context )) return result;
		
		IDocument document = getDocument( context.getCompilationUnit() );
		try {
			String contents = document.get( context.getSelectionOffset(), context.getSelectionLength() );
			result = new IJavaCompletionProposal[1];			
			result[0] = new ExternalActionQuickAssistProposal(contents, EclipseImages.getImage(ImageConstants.CRITERIA_EDITOR), "Copy to Criteria Editor", context) {
				public void apply(IDocument target) {
					HibernateConsolePlugin.getDefault().openCriteriaEditor(getName(), getContents());
				}
			};
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

}
