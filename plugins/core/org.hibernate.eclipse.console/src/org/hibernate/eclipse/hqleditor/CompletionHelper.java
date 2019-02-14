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
package org.hibernate.eclipse.hqleditor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.eclipse.jdt.core.CompletionContext;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.eval.IEvaluationContext;
import org.eclipse.jdt.internal.ui.text.java.AbstractJavaCompletionProposal;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposal;
import org.eclipse.jdt.internal.ui.text.java.LazyJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.CompletionProposalComparator;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.hqleditor.HibernateResultCollector.Settings;

public class CompletionHelper {

	static public ICompletionProposal[] completeOnJavaTypes(IJavaProject javaProject, Settings settings, String packageName, String start, int offset) {

		if (javaProject != null) {
			IEvaluationContext context = javaProject.newEvaluationContext();
			if(packageName!=null) {
				context.setPackageName(packageName);
			}

			HibernateResultCollector rc = new HibernateResultCollector(javaProject);
			rc.acceptContext(new CompletionContext());
			//rc.reset(offset, javaProject, null);
			rc.setAccepts(settings);
			try {
				// cannot send in my own document as it won't compile as
				// java - so we just send in
				// the smallest snippet possible
				context.codeComplete(start, start.length(), rc);
			} catch (JavaModelException jme) {
				HibernateConsolePlugin.getDefault().logErrorMessage(HibernateConsoleMessages.CompletionHelper_could_not_complete_java_types, jme);
			}
			IJavaCompletionProposal[] results = rc.getJavaCompletionProposals();
			transpose(start, offset, results);
			return results;
		}
		return new ICompletionProposal[0];
	}

	static public void transpose(String start, int offset, IJavaCompletionProposal[] results) {
		// As all completions have made with the assumption on a empty
		// (or almost empty) string
		// we move the replacementoffset on every proposol to fit nicely
		// into our non-java code
		for (int i = 0; i < results.length; i++) {
			if(results[i] instanceof AbstractJavaCompletionProposal) {
				AbstractJavaCompletionProposal proposal = (AbstractJavaCompletionProposal) results[i]; // TODO: eclipse bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=84998
				transpose(offset, proposal);
			} else {
				HibernateConsolePlugin.getDefault().log(HibernateConsoleMessages.CompletionHelper_error_unknown_completion_proposal_class);
			}
		}
		Arrays.sort(results, new CompletionProposalComparator() );
	}

	private static void transpose(int offset, AbstractJavaCompletionProposal proposal) {
		int wanted = proposal.getReplacementOffset() + (offset /*- start.length()*/);
		if(wanted==proposal.getReplacementOffset() ) {
			//System.out.println("NO TRANSPOSE!");
		}
		if(wanted<0) {
			wanted = 0;
		}
		proposal.setReplacementOffset(wanted);
	}
}
