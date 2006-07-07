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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.hibernate.eclipse.mapper.extractor.HBMInfoExtractor;

public class CFGXMLContentAssistProcessor extends HibernateContentAssistProcessor {

	HBMInfoExtractor extractor;
	
	static String[] propertyNames;
	
	public CFGXMLContentAssistProcessor() {
		super();
	
		extractor = new HBMInfoExtractor();
		
	}

	
	protected List getAttributeValueProposals(String attributeName, String matchString, int offset, ContentAssistRequest contentAssistRequest) {
		String nodeName = contentAssistRequest.getNode().getNodeName();
		if("property".equals(nodeName) && "name".equals(attributeName)) {
			List types = this.extractor.findMatchingPropertyTypes(matchString);
			
			List proposals = new ArrayList(types.size() );		
			for (Iterator iter = types.iterator(); iter.hasNext();) {
				String element = (String) iter.next();
				proposals.add(new CompletionProposal(element, offset, matchString.length(), element.length(), null, null, null, null) );
			}
			return proposals;
		}
		if("mapping".equals(nodeName) && "resource".equals(attributeName)) {
			
		}
		
		return Collections.EMPTY_LIST;
	}

	
}
