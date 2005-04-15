package org.hibernate.eclipse.mapper.editors;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.hibernate.eclipse.mapper.extractor.HBMInfoExtractor;

public class CFGXMLContentAssistProcessor extends HibernateContentAssistProcessor {

	HBMInfoExtractor extractor;
	
	static String[] propertyNames;
	
	public CFGXMLContentAssistProcessor(IJavaProject project) {
		super(project);
	
		extractor = new HBMInfoExtractor();
		
	}

	
	protected List getAttributeValueProposals(String attributeName, String matchString, int offset, ContentAssistRequest contentAssistRequest) {
		List types = this.extractor.findMatchingPropertyTypes(matchString);
		
		List proposals = new ArrayList(types.size());		
		for (Iterator iter = types.iterator(); iter.hasNext();) {
			String element = (String) iter.next();
			proposals.add(new CompletionProposal(element, offset, matchString.length(), element.length(), null, null, null, null));
		}
		return proposals;
	}

	
}
