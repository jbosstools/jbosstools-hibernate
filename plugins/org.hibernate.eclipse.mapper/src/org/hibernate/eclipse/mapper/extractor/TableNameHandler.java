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

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.hibernate.eclipse.nature.HibernateNature;
import org.hibernate.mapping.Table;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

public class TableNameHandler implements HBMInfoHandler {

	
	public ICompletionProposal[] attributeCompletionProposals(
			IJavaProject javaProject, Node node, String attributeName,
			String start, int offset) {

		List tables = new ArrayList(); 
		
		HibernateNature nature = HibernateNature.getHibernateNature( javaProject );
		if(nature!=null) {
			tables = nature.getMatchingTables(start);
		}
		
		List proposals = new ArrayList();
		for (Iterator iter = tables.iterator(); iter.hasNext();) {
			Table element = (Table) iter.next();
			proposals.add(new CompletionProposal(element.getName(), offset, start.length(), element.getName().length(), null, null, null, null) );
		}
		
		return (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[proposals.size()]);
	}

	public IJavaElement getJavaElement(IJavaProject project, Node currentNode,
			Attr currentAttrNode) {
		return null;
	}

}
