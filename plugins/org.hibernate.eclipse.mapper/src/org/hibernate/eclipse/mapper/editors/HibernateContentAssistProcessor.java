package org.hibernate.eclipse.mapper.editors;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLContentAssistProcessor;

public abstract class HibernateContentAssistProcessor extends XMLContentAssistProcessor {

	private final IJavaProject project;

	public HibernateContentAssistProcessor(IJavaProject project) {
		this.project = project;
	}

	protected void addAttributeValueProposals(ContentAssistRequest contentAssistRequest) {
		super.addAttributeValueProposals(contentAssistRequest);
		
		IDOMNode node = (IDOMNode) contentAssistRequest.getNode();
	
		// Find the attribute region and name for which this position should have a value proposed
		IStructuredDocumentRegion open = node.getFirstStructuredDocumentRegion();
		ITextRegionList openRegions = open.getRegions();
		int i = openRegions.indexOf(contentAssistRequest.getRegion() );
		if (i < 0)
			return;
		ITextRegion nameRegion = null;
		while (i >= 0) {
			nameRegion = openRegions.get(i--);
			if (nameRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME)
				break;
		}
	
		String matchString = contentAssistRequest.getMatchString();
		int offset = contentAssistRequest.getReplacementBeginPosition();
		if (matchString == null) {
			matchString = ""; //$NON-NLS-1$			
		}
		if (matchString.length() > 0 && (matchString.startsWith("\"") || matchString.startsWith("'") ) ) {//$NON-NLS-2$//$NON-NLS-1$
			matchString = matchString.substring(1);
			offset = offset+1;
		}
	
		if (nameRegion != null) {
			String attributeName = open.getText(nameRegion);
			
			List attributeValueProposals = getAttributeValueProposals(attributeName, matchString, offset, contentAssistRequest);
			if(attributeValueProposals!=null) {
				for (Iterator iter = attributeValueProposals.iterator(); iter.hasNext();) {
					ICompletionProposal element = (ICompletionProposal) iter.next();				
					contentAssistRequest.addProposal(element);					
				}
			}
			
		}
		
	}

	abstract protected List getAttributeValueProposals(String attributeName, String matchString, int offset, ContentAssistRequest contentAssistRequest);
		
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		return super.computeCompletionProposals(viewer, offset);
		
	}
	
	protected IJavaProject getJavaProject() {
		return project;
	}
}
