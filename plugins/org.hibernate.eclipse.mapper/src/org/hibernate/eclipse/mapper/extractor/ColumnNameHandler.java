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
		
		HibernateNature nature = HibernateNature.getHibernateNature( javaProject );
		if(nature!=null) {
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
