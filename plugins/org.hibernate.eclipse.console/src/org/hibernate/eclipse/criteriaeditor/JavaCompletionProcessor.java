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
package org.hibernate.eclipse.criteriaeditor;

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
import org.hibernate.eclipse.console.HibernateConsoleMessages;
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
				setErrorMessage( HibernateConsoleMessages.JavaCompletionProcessor_no_console_configuration_found );
				return new ICompletionProposal[0];
			}
			String prefix = HibernateConsoleMessages.JavaCompletionProcessor_session_session; // has to do this because of https://bugs.eclipse.org/bugs/show_bug.cgi?id=141518
			
			IJavaCompletionProposal[] results = new IJavaCompletionProposal[0];
			IJavaProject[] projects = null;
			//try {
				ProjectUtils.findJavaProjects(editor.getConsoleConfiguration());
			/*} catch (RuntimeException e){
				String mess = NLS.bind(HibernateConsoleMessages.JavaCompletionProcessor_error_find_project,
						editor.getConsoleConfiguration().getName());
				HibernateConsolePlugin.getDefault().logErrorMessage(mess, e);
			}*/
			for (int i = 0; projects != null && i < projects.length && results.length <= 0; i++) {
				IJavaProject javaProject = projects[i];
				collector = new CompletionProposalCollector( javaProject );
				collector.acceptContext( new CompletionContext() );
				try {
					editor.codeComplete( prefix, collector, position, javaProject );
				}
				catch (JavaModelException x) {
					Shell shell = viewer.getTextWidget().getShell();
					ErrorDialog
							.openError(
									shell,
									HibernateConsoleMessages.JavaCompletionProcessor_error, HibernateConsoleMessages.JavaCompletionProcessor_error_while_performing_code_completion, x.getStatus() );
					HibernateConsolePlugin.getDefault().log( x );
				}
				results = collector.getJavaCompletionProposals();
			}
			
			CompletionHelper.transpose( null, -prefix.length(), results );
			
			/*if (editor.getConsoleConfiguration().getConfiguration() == null){
				ICompletionProposal[] results2 = new ICompletionProposal[results.length + 1];
				System.arraycopy(results, 0, results2, 1, results.length);
				results2[0] = new LoadConsoleCFGCompletionProposal(editor.getConsoleConfiguration());
				return results2;
			}*/
			
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
