/*
 * Created on 19-Nov-2004
 *
 */
package org.hibernate.eclipse.mapper.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.wst.xml.ui.contentassist.ContentAssistRequest;
import org.hibernate.eclipse.mapper.extractor.HBMInfoExtractor;
import org.hibernate.eclipse.mapper.extractor.HBMInfoHandler;
import org.w3c.dom.Node;

/**
 * @author max
 *
 */
public class HBMXMLTypeContributor {

	
	private final IJavaProject javaProject;

	private HBMInfoExtractor sourceLocator = new HBMInfoExtractor();

	private static final boolean DEBUG = false;
	
    public HBMXMLTypeContributor(IJavaProject javaProject) {
		this.javaProject = javaProject;			
	}

	public List getAttributeValueProposals(String attributeName, String start, int offset, ContentAssistRequest contentAssistRequest) {
		Node node = contentAssistRequest.getNode();
		List proposals = new ArrayList();
		
		String path = node.getNodeName() + ">" + attributeName;
        HBMInfoHandler handler = sourceLocator.getAttributeHandler(path);
		if (handler != null) {
			proposals.addAll(Arrays.asList(handler.attributeCompletionProposals(javaProject, node, attributeName, start, offset)));
		}
		
		if (DEBUG) {
			String string = contentAssistRequest.getDocumentRegion().getText();
			string = string.replace('<', '[');
			string = string.replace('>', ']');
			CompletionProposal completionProposal = new CompletionProposal("[" + start + "],[" + path + "],[" + offset + "]", offset, 1, 4, null, null, null, string);
			
			proposals.add(completionProposal);
		}

		return proposals;
	}

}
