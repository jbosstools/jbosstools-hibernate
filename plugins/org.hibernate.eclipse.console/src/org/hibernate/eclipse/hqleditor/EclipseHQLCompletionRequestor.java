package org.hibernate.eclipse.hqleditor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.hibernate.tool.ide.completion.IHQLCompletionRequestor;
import org.hibernate.util.StringHelper;

public class EclipseHQLCompletionRequestor implements IHQLCompletionRequestor {

	private final List result = new ArrayList();
	private String lastErrorMessage;
	
	public EclipseHQLCompletionRequestor() {
		
	}
	
	public List getCompletionProposals() {		
		return result;
	}

	public boolean accept(HQLCompletionProposal proposal) {
		result.add(new CompletionProposal(proposal.getCompletion(), // replacementString 
										  proposal.getReplaceStart(), // replacementOffset 
										  proposal.getReplaceEnd()-proposal.getReplaceStart(), // replacementLength
										  proposal.getCompletion().length(), // cursorPosition (relativeTo replacementStart)
										  getImage(proposal.getCompletionKind()), 
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
				buf.append(" - ");
				buf.append(StringHelper.qualifier( proposal.getEntityName() ));
			} else if(proposal.getShortEntityName()!=null &&
					!(proposal.getSimpleName().equals( proposal.getEntityName()))) {
				buf.append( " - " + proposal.getShortEntityName() );
			} 
			break;
		case HQLCompletionProposal.ALIAS_REF:
			if(proposal.getShortEntityName()!=null) {
				buf.append( " - " + proposal.getShortEntityName() );
			} else if(proposal.getEntityName()!=null) {
				buf.append( " - " + proposal.getEntityName() );
			}
			break;
		case HQLCompletionProposal.PROPERTY:
			if(proposal.getShortEntityName()!=null) {
				buf.append( " - " + proposal.getShortEntityName() );
			} else if(proposal.getEntityName()!=null) {
				if(proposal.getEntityName().indexOf( "." )>=0) {
					buf.append( " - " + StringHelper.unqualify( proposal.getEntityName() ));
				} else {
					buf.append( " - " + proposal.getEntityName() );
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

	private Image getImage(int kind) {
		String key = null;
		
		switch(kind) {
		case HQLCompletionProposal.ENTITY_NAME:
		case HQLCompletionProposal.ALIAS_REF:
			key = ImageConstants.MAPPEDCLASS;
			break;
		case HQLCompletionProposal.PROPERTY:
			key = ImageConstants.PROPERTY;
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
