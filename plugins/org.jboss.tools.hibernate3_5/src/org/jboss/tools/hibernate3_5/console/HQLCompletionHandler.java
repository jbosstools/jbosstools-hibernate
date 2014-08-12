package org.jboss.tools.hibernate3_5.console;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.common.CompletionProposal;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.eclipse.console.workbench.HibernateWorkbenchHelper;
import org.jboss.tools.hibernate.spi.IHQLCompletionProposal;
import org.jboss.tools.hibernate.spi.IHQLCompletionHandler;

public class HQLCompletionHandler implements IHQLCompletionHandler {

	private final List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
	private String lastErrorMessage;
	private final int virtualOffset;
	
	public HQLCompletionHandler(int virtualOffset) {
		this.virtualOffset = virtualOffset;
	}

	public List<ICompletionProposal> getCompletionProposals() {		
		return result;
	}

	public boolean accept(IHQLCompletionProposal proposal) {
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

	private String getDisplayString(IHQLCompletionProposal proposal) {
		StringBuffer buf = new StringBuffer(proposal.getSimpleName());
		int kind = proposal.getCompletionKind();
		if (kind == proposal.entityNameKind()) {
			if(proposal.getEntityName()!=null && 
					  !(proposal.getSimpleName().equals( proposal.getEntityName()))) {
				buf.append(" - "); //$NON-NLS-1$
				buf.append(qualifier( proposal.getEntityName() ));
			} else if(proposal.getShortEntityName()!=null &&
					!(proposal.getSimpleName().equals( proposal.getEntityName()))) {
				buf.append( " - " + proposal.getShortEntityName() ); //$NON-NLS-1$
			} 
		} else if (kind == proposal.aliasRefKind()) {
			if(proposal.getShortEntityName()!=null) {
				buf.append( " - " + proposal.getShortEntityName() ); //$NON-NLS-1$
			} else if(proposal.getEntityName()!=null) {
				buf.append( " - " + proposal.getEntityName() ); //$NON-NLS-1$
			}
		} else if (kind == proposal.propertyKind()) {
			if(proposal.getShortEntityName()!=null) {
				buf.append( " - " + proposal.getShortEntityName() ); //$NON-NLS-1$
			} else if(proposal.getEntityName()!=null) {
				if(proposal.getEntityName().indexOf( "." )>=0) { //$NON-NLS-1$
					buf.append( " - " + unqualify( proposal.getEntityName() )); //$NON-NLS-1$
				} else {
					buf.append( " - " + proposal.getEntityName() ); //$NON-NLS-1$
				}
			}
		}
		return buf.toString();
	}

	private Image getImage(IHQLCompletionProposal proposal) {
		String key = null;
		int kind = proposal.getCompletionKind();
		if (kind == proposal.entityNameKind() || kind == proposal.aliasRefKind()) {
			key = ImageConstants.MAPPEDCLASS;
		} else if (kind == proposal.propertyKind()) {
			if(proposal.getProperty()!=null) {
				return HibernateWorkbenchHelper.getImage(proposal.getProperty());
			} else {
				key = ImageConstants.PROPERTY;				
			}
		} else if (kind == proposal.keywordKind()) {
			key = null;
		} else if (kind == proposal.functionKind()) {
			key = ImageConstants.FUNCTION;
		} else {
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

	private String unqualify(String qualifiedName) {
		int loc = qualifiedName.lastIndexOf(".");
		return ( loc < 0 ) ? qualifiedName : qualifiedName.substring( loc + 1 );
	}

	private String qualifier(String qualifiedName) {
		int loc = qualifiedName.lastIndexOf(".");
		return ( loc < 0 ) ? "" : qualifiedName.substring( 0, loc );
	}

}
