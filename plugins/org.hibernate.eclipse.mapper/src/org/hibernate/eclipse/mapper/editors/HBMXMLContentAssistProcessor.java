package org.hibernate.eclipse.mapper.editors;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionList;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;
import org.eclipse.wst.xml.ui.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.contentassist.XMLContentAssistProcessor;

public class HBMXMLContentAssistProcessor extends XMLContentAssistProcessor {

	HBMXmlTypeContributor contributor;
	
	public HBMXMLContentAssistProcessor(IJavaProject javaProject) {
		contributor = new HBMXmlTypeContributor(javaProject);
	}
	
	protected void addAttributeValueProposals(ContentAssistRequest contentAssistRequest) {
		super.addAttributeValueProposals(contentAssistRequest);
		
		XMLNode node = (XMLNode) contentAssistRequest.getNode();

		// Find the attribute region and name for which this position should have a value proposed
		IStructuredDocumentRegion open = node.getFirstStructuredDocumentRegion();
		ITextRegionList openRegions = open.getRegions();
		int i = openRegions.indexOf(contentAssistRequest.getRegion());
		if (i < 0)
			return;
		ITextRegion nameRegion = null;
		while (i >= 0) {
			nameRegion = openRegions.get(i--);
			if (nameRegion.getType() == XMLRegionContext.XML_TAG_ATTRIBUTE_NAME)
				break;
		}

		String matchString = contentAssistRequest.getMatchString();
		int offset = contentAssistRequest.getReplacementBeginPosition();
		if (matchString == null) {
			matchString = ""; //$NON-NLS-1$			
		}
		if (matchString.length() > 0 && (matchString.startsWith("\"") || matchString.startsWith("'"))) {//$NON-NLS-2$//$NON-NLS-1$
			matchString = matchString.substring(1);
			offset = offset+1;
		}

		if (nameRegion != null) {
			String attributeName = open.getText(nameRegion);
			
			List attributeValueProposals = contributor.getAttributeValueProposals(attributeName, matchString, offset, contentAssistRequest);
			for (Iterator iter = attributeValueProposals.iterator(); iter.hasNext();) {
				ICompletionProposal element = (ICompletionProposal) iter.next();				
				contentAssistRequest.addProposal(element);					
			}			
			
		}
		
		
	}
	
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		return super.computeCompletionProposals(viewer, offset);
		
	}
	
}
