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

import org.eclipse.jdt.core.CompletionContext;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.hibernate.eclipse.hqleditor.CompletionHelper;
import org.hibernate.eclipse.hqleditor.HibernateResultCollector;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

class FieldPropertyHandler implements HBMInfoHandler {

	/**
	 * 
	 */
	private final HBMInfoExtractor extractor;
	

	/**
	 * @param extractor
	 */
	FieldPropertyHandler(HBMInfoExtractor extractor) {
		this.extractor = extractor;
	}

	public ICompletionProposal[] attributeCompletionProposals(IJavaProject project, Node node, String attributeName, String start, int offset) {
		//	TODO: should also try to find properties getXXX()
	    if(project!=null) {
	    	
			Node parentNode = node.getParentNode();
			String typename = null;			
			
			typename = this.extractor.getNearestType( project, parentNode );
			
			if(typename==null) {
				return new IJavaCompletionProposal[0]; // could not locate type
			}
			HibernateResultCollector rc = null;
			try {
				IType type = project.findType(typename);
				if(type==null) return new ICompletionProposal[0]; //nothing to look for then
				rc = new HibernateResultCollector(project);
				rc.acceptContext(new CompletionContext());
				rc.setAccepts(false,false,false,false,true,false); // TODO: only handle properties ?
				//rc.reset(offset, javaProject, null);
				
				
				type.codeComplete(start.toCharArray(), -1, start.length(), new char[0][0], new char[0][0], new int[0], false, rc);
			} catch(JavaModelException jme) {
				// TODO: report
			}
			
			IJavaCompletionProposal[] results = rc.getJavaCompletionProposals();
			CompletionHelper.transpose(start, offset, results);
			return results; 
		}
		
		return new ICompletionProposal[0];            
	}

	
	public IJavaElement getJavaElement(IJavaProject project, Node currentNode, Attr currentAttrNode) {
		IType type = extractor.getNearestTypeJavaElement(project, currentNode.getParentNode() );
		if(type!=null) {				
			IField field = type.getField(currentAttrNode.getValue() );
			return field;
		}
		
		return null;
	}
}