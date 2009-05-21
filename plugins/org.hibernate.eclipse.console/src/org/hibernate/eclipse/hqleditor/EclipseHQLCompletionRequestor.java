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
package org.hibernate.eclipse.hqleditor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.eclipse.console.workbench.HibernateWorkbenchHelper;
import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.hibernate.tool.ide.completion.IHQLCompletionRequestor;
import org.hibernate.util.StringHelper;

public class EclipseHQLCompletionRequestor implements IHQLCompletionRequestor {

	private final List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
	private String lastErrorMessage;
	private final int virtualOffset;
	
	public EclipseHQLCompletionRequestor() {
		virtualOffset = 0;
	}
	
	public EclipseHQLCompletionRequestor(int virtualOffset) {
		this.virtualOffset = virtualOffset;
	}

	public List<ICompletionProposal> getCompletionProposals() {		
		return result;
	}

	public boolean accept(HQLCompletionProposal proposal) {
		result.add(new CompletionProposal(proposal.getCompletion(), // replacementString 
										  proposal.getReplaceStart()+virtualOffset, // replacementOffset 
										  proposal.getReplaceEnd()-proposal.getReplaceStart(), // replacementLength
										  proposal.getCompletion().length(), // cursorPosition (relativeTo replacementStart)
										  getImage(proposal), 
										  getDisplayString(proposal), 
										  null, 
										  null));
		return true;
	}

	private String getDisplayString(HQLCompletionProposal proposal) {
		StringBuffer buf = new StringBuffer(proposal.getSimpleName());
		
		switch(proposal.getCompletionKind()) {
		case HQLCompletionProposal.ENTITY_NAME:
			if(proposal.getEntityName()!=null && 
					  !(proposal.getSimpleName().equals( proposal.getEntityName()))) {
				buf.append(" - "); //$NON-NLS-1$
				buf.append(StringHelper.qualifier( proposal.getEntityName() ));
			} else if(proposal.getShortEntityName()!=null &&
					!(proposal.getSimpleName().equals( proposal.getEntityName()))) {
				buf.append( " - " + proposal.getShortEntityName() ); //$NON-NLS-1$
			} 
			break;
		case HQLCompletionProposal.ALIAS_REF:
			if(proposal.getShortEntityName()!=null) {
				buf.append( " - " + proposal.getShortEntityName() ); //$NON-NLS-1$
			} else if(proposal.getEntityName()!=null) {
				buf.append( " - " + proposal.getEntityName() ); //$NON-NLS-1$
			}
			break;
		case HQLCompletionProposal.PROPERTY:
			if(proposal.getShortEntityName()!=null) {
				buf.append( " - " + proposal.getShortEntityName() ); //$NON-NLS-1$
			} else if(proposal.getEntityName()!=null) {
				if(proposal.getEntityName().indexOf( "." )>=0) { //$NON-NLS-1$
					buf.append( " - " + StringHelper.unqualify( proposal.getEntityName() )); //$NON-NLS-1$
				} else {
					buf.append( " - " + proposal.getEntityName() ); //$NON-NLS-1$
				}
			}
			break;
		case HQLCompletionProposal.KEYWORD:
			break;
		case HQLCompletionProposal.FUNCTION:
			break;
		default:
			
		}
		
		
		return buf.toString();
	}

	private Image getImage(HQLCompletionProposal proposal) {
		String key = null;
		
		switch(proposal.getCompletionKind()) {
		case HQLCompletionProposal.ENTITY_NAME:
		case HQLCompletionProposal.ALIAS_REF:
			key = ImageConstants.MAPPEDCLASS;
			break;
		case HQLCompletionProposal.PROPERTY:
			if(proposal.getProperty()!=null) {
				return HibernateWorkbenchHelper.getImage( proposal.getProperty() );
			} else {
				key = ImageConstants.PROPERTY;				
			}
			break;
		case HQLCompletionProposal.KEYWORD:
			key = null;
			break;
		case HQLCompletionProposal.FUNCTION:
			key = ImageConstants.FUNCTION;
			break;
		default:
			key = null;
		}
		
		return key==null?null:EclipseImages.getImage( key );
	}

	public void completionFailure(String errorMessage) {
		lastErrorMessage = errorMessage;		
	}
	
	public String getLastErrorMessage() {
		return lastErrorMessage;
	}
	
	public void clear() {
		result.clear();
		lastErrorMessage = null;
	}

}
