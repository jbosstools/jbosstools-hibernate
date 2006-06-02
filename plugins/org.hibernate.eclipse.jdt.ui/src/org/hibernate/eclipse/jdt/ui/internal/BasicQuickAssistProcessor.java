package org.hibernate.eclipse.jdt.ui.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickAssistProcessor;
import org.hibernate.eclipse.nature.HibernateNature;

public abstract class BasicQuickAssistProcessor implements IQuickAssistProcessor{

	public BasicQuickAssistProcessor() {
		super();
	}

	public boolean hasAssists(IInvocationContext context) throws CoreException {
		IJavaProject javaProject = context.getCompilationUnit().getJavaProject();
		HibernateNature nature = HibernateNature.getHibernateNature( javaProject );
		if(nature!=null) {
			return nature.getDefaultConsoleConfiguration()!=null;
		} else {
			return false;
		}
	}

	abstract public IJavaCompletionProposal[] getAssists(IInvocationContext context, IProblemLocation[] locations) throws CoreException;
	
}