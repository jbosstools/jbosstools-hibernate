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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

public class PropertyAccessHandler implements HBMInfoHandler {

	private final HBMInfoExtractor extractor;

	public PropertyAccessHandler(HBMInfoExtractor extractor) {
		this.extractor = extractor;
	}

	public ICompletionProposal[] attributeCompletionProposals(IJavaProject project, Node node, String attributeName, String start, int offset) {
	    List types = this.extractor.findMatchingAccessMethods(start);
		
		List proposals = new ArrayList(types.size() );		
		for (Iterator iter = types.iterator(); iter.hasNext();) {
			HibernateTypeDescriptor element = (HibernateTypeDescriptor) iter.next();
			String extendedinfo = "<b>Access method</b>: " + element.getName();
			if(element.getReturnClass()!=null) {
				extendedinfo += "<br><b>Description</b>: " + element.getReturnClass();				
			}
			proposals.add(new CompletionProposal(element.getName(), offset, start.length(), element.getName().length(), null, null, null, extendedinfo) );
		}
		
		try {
			IType typeInterface = project.findType("org.hibernate.property.PropertyAccessor");
			Set alreadyFound = new HashSet();			
			if (typeInterface != null) {
				ITypeHierarchy hier = typeInterface.newTypeHierarchy(project, new NullProgressMonitor() );
				IType[] classes = hier.getAllSubtypes(typeInterface); // TODO: cache these results ?
				this.extractor.generateTypeProposals(start, offset, proposals, alreadyFound, classes, "org.hibernate.property");				
			}
		} catch (CoreException e) {
			throw new RuntimeException(e); // TODO: log as error!
		}
		
		ICompletionProposal[] result = (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[proposals.size()]);
		return result;            
	}

	public IJavaElement getJavaElement(IJavaProject project, Node currentNode, Attr currentAttrNode) {
		return extractor.getNearestTypeJavaElement(project, currentNode);
	}
}
