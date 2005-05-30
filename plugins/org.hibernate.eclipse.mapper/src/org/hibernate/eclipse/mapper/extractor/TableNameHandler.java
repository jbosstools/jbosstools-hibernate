package org.hibernate.eclipse.mapper.extractor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.nature.HibernateNature;
import org.hibernate.mapping.Table;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

public class TableNameHandler implements HBMInfoHandler {

	
	public ICompletionProposal[] attributeCompletionProposals(
			IJavaProject javaProject, Node node, String attributeName,
			String start, int offset) {

		List tables = new ArrayList(); 
		
		try {
			if(javaProject.getProject().hasNature(HibernateNature.ID)) {
				HibernateNature nature = (HibernateNature) javaProject.getProject().getNature(HibernateNature.ID);
				Iterator tableMappings = nature.getTables().iterator();
				while (tableMappings.hasNext()) {
					Table table = (Table) tableMappings.next();
					if(table.getName().startsWith(start)) {
						tables.add(table);
					}
				}
			}
		} catch (CoreException e) {
			HibernateConsolePlugin.getDefault().logErrorMessage("Error while fetching table completions", e);
		}
		
		List proposals = new ArrayList();
		for (Iterator iter = tables.iterator(); iter.hasNext();) {
			Table element = (Table) iter.next();
			proposals.add(new CompletionProposal(element.getName(), offset, start.length(), element.getName().length(), null, null, null, null));
		}
		
		return (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[proposals.size()]);
	}

	public IJavaElement getJavaElement(IJavaProject project, Node currentNode,
			Attr currentAttrNode) {
		return null;
	}

}
