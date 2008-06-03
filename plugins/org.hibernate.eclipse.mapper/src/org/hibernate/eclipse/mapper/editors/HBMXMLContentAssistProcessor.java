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
package org.hibernate.eclipse.mapper.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.hibernate.eclipse.mapper.extractor.HBMInfoExtractor;
import org.hibernate.eclipse.mapper.extractor.HBMInfoHandler;
import org.w3c.dom.Node;

public class HBMXMLContentAssistProcessor extends HibernateContentAssistProcessor {

	private HBMInfoExtractor sourceLocator = new HBMInfoExtractor();
	
	public HBMXMLContentAssistProcessor() {
		super();
	}

	private static final boolean DEBUG = false;
	
	public List getAttributeValueProposals(String attributeName, String start, int offset, ContentAssistRequest contentAssistRequest) {
		Node node = contentAssistRequest.getNode();
		List proposals = new ArrayList();
		
		String path = node.getNodeName() + ">" + attributeName;
        HBMInfoHandler handler = sourceLocator.getAttributeHandler(path);
		if (handler != null) {
			proposals.addAll(Arrays.asList(handler.attributeCompletionProposals(getJavaProject(contentAssistRequest), node, attributeName, start, offset) ) );
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
