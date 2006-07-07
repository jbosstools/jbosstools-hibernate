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

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.hibernate.eclipse.hqleditor.CompletionHelper;
import org.hibernate.eclipse.hqleditor.HibernateResultCollector.Settings;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

public class JavaTypeHandler implements HBMInfoHandler {
	
	/**
	 * 
	 */
	protected final HBMInfoExtractor extractor;

	/**
	 * @param extractor
	 */
	JavaTypeHandler(HBMInfoExtractor extractor) {
		this.extractor = extractor;
	}

	public ICompletionProposal[] attributeCompletionProposals(IJavaProject project, Node node, String attributeName, String start, int offset) {
		Settings settings = new Settings();
        settings.setAcceptClasses(true);
        settings.setAcceptInterfaces(true);
        settings.setAcceptPackages(true);
        settings.setAcceptTypes(true);
	    return CompletionHelper.completeOnJavaTypes(project, settings,this.extractor.getPackageName(node), start, offset);            
	}
	
	
	
	public IJavaElement getJavaElement(IJavaProject project, Node currentNode, Attr currentAttrNode) {
		return getNearestTypeJavaElement(project, currentNode);
	}

	public IJavaElement getNearestTypeJavaElement(IJavaProject project, Node currentNode) {
		String nearestType = extractor.getNearestType(project, currentNode);
		if(nearestType!=null) {
			try {
				IType type = project.findType(nearestType);
				return type;
			} catch (JavaModelException e) {
				//ignore
			}
		}
		return null;
	}

}