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
package org.hibernate.eclipse.jdt.ui.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jface.text.IDocument;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.EclipseImages;

public class HQLQuickAssistProcessor extends BasicQuickAssistProcessor {

	public IJavaCompletionProposal[] getAssists(IInvocationContext context,
			IProblemLocation[] locations) throws CoreException {
		
		IJavaCompletionProposal[] result = new IJavaCompletionProposal[0];
		if(!hasAssists( context )) return result;
		
		ASTNode coveringNode = context.getCoveringNode();
		if(!(coveringNode instanceof StringLiteral)) {
			return result;
		}
		
		StringLiteral stringLiteral= (StringLiteral) coveringNode;
		String contents= stringLiteral.getLiteralValue();
		result = new IJavaCompletionProposal[1];			
		result[0] = new ExternalActionQuickAssistProposal(contents, EclipseImages.getImage(ImageConstants.HQL_EDITOR), "Copy to HQL Editor", context) {
			public void apply(IDocument document) {
				HibernateConsolePlugin.getDefault().openScratchHQLEditor( getName(), getContents() );				
			}
		};
		
		return result;
	}
		
}
