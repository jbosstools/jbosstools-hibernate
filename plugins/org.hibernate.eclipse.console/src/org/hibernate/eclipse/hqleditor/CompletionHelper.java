package org.hibernate.eclipse.hqleditor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.eval.IEvaluationContext;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.CompletionProposalComparator;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.hibernate.eclipse.HibernatePlugin;
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
			//rc.reset(offset, javaProject, null);
			rc.setAccepts(settings);
			try {
				// cannot send in my own document as it won't compile as
				// java - so we just send in
				// the smallest snippet possible
				context.codeComplete(start, start.length(), rc);
			} catch (JavaModelException jme) {
				HibernateConsolePlugin.getDefault().logErrorMessage("Could not complete java types", jme);
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
			if(results[i] instanceof JavaCompletionProposal) {
				JavaCompletionProposal proposal = (JavaCompletionProposal) results[i]; // TODO: eclipse bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=84998			
				int wanted = proposal.getReplacementOffset() + (offset /*- start.length()*/);
				if(wanted==proposal.getReplacementOffset() ) { 
					//System.out.println("NO TRANSPOSE!");
				}
				proposal.setReplacementOffset(wanted);
			} else {
				Class c = results[i].getClass();
				try {
					Method setMethod = c.getMethod("setReplacementOffset", new Class[] { int.class });
					Method GetMethod = c.getMethod("getReplacementOffset", new Class[0]);
					
					Integer offsetx = (Integer) GetMethod.invoke(results[i], null);
					int wanted = offsetx.intValue() + (offset /*- start.length()*/);
					setMethod.invoke(results[i], new Object[] { new Integer(wanted) });
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// M7
//				LazyJavaCompletionProposal proposal = (LazyJavaCompletionProposal) results[i]; // TODO: eclipse bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=84998
//				int wanted = proposal.getReplacementOffset() + (offset /*- start.length()*/);
//				if(wanted==proposal.getReplacementOffset() ) { 
//					System.out.println("NO TRANSPOSE!");
//				}
//				proposal.setReplacementOffset(proposal.getReplacementOffset() + (offset /*- start.length()*/) ); 
			}
		}
		Arrays.sort(results, new CompletionProposalComparator() );		
	}
}
