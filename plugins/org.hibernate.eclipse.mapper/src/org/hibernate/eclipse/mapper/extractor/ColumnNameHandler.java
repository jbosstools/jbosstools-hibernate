package org.hibernate.eclipse.mapper.extractor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.nature.HibernateNature;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Table;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

public class ColumnNameHandler implements HBMInfoHandler {

	private HBMInfoExtractor extractor;

	ColumnNameHandler(HBMInfoExtractor hie) {
		this.extractor = hie;
	}
	
	public ICompletionProposal[] attributeCompletionProposals(
			IJavaProject javaProject, Node node, String attributeName,
			String start, int offset) {

		List columns = new ArrayList(); 
		
		try {
			if(javaProject.getProject().hasNature(HibernateNature.ID) ) {
				HibernateNature nature = (HibernateNature) javaProject.getProject().getNature(HibernateNature.ID);
				TableIdentifier nearestTableName = extractor.getNearestTableName(node);
				if(nearestTableName!=null) {
					Table table = nature.getTable(nearestTableName);
					if (table!=null) {
						Iterator tableMappings = table.getColumnIterator();
						while (tableMappings.hasNext() ) {
							Column column = (Column) tableMappings.next();
							if(column.getName().toUpperCase().startsWith(start.toUpperCase()) ) {
								columns.add(column);
							}
						}
					}
				}
			}
		} catch (CoreException e) {
			HibernateConsolePlugin.getDefault().logErrorMessage("Error while fetching table completions", e);
		}
		
		List proposals = new ArrayList();
		for (Iterator iter = columns.iterator(); iter.hasNext();) {
			Column element = (Column) iter.next();
			proposals.add(new CompletionProposal(element.getName(), offset, start.length(), element.getName().length(), null, null, null, null) );
		}
		
		return (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[proposals.size()]);
	}

	public IJavaElement getJavaElement(IJavaProject project, Node currentNode,
			Attr currentAttrNode) {
		return null;
	}

}
