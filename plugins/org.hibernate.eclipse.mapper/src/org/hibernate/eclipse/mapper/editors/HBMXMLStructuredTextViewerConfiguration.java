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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitionTypes;
import org.eclipse.wst.xml.core.internal.provisional.text.IXMLPartitions;
import org.eclipse.wst.xml.ui.StructuredTextViewerConfigurationXML;
import org.eclipse.wst.xml.ui.internal.contentassist.NoRegionContentAssistProcessor;

public class HBMXMLStructuredTextViewerConfiguration extends StructuredTextViewerConfigurationXML {

	public HBMXMLStructuredTextViewerConfiguration() {
		
	}
	
	static Map partitionToContentAssist = new HashMap();
	static {
		IContentAssistProcessor[] contentAssistProcessor = new IContentAssistProcessor[] { new HBMXMLContentAssistProcessor() };
		partitionToContentAssist.put(IStructuredPartitionTypes.DEFAULT_PARTITION, contentAssistProcessor);
		partitionToContentAssist.put(IXMLPartitions.XML_DEFAULT, contentAssistProcessor);
		
		contentAssistProcessor = new IContentAssistProcessor[] { new NoRegionContentAssistProcessor() };
		partitionToContentAssist.put(IStructuredPartitionTypes.UNKNOWN_PARTITION, contentAssistProcessor );
	}
	
	protected IContentAssistProcessor[] getContentAssistProcessors(ISourceViewer sourceViewer, String partitionType) {
		return (IContentAssistProcessor[]) partitionToContentAssist.get(partitionType);
	}
	
	
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		if (sourceViewer == null || hyperLinksEnabled() ) {
			return null;
		}
		
		IHyperlinkDetector[] baseDetectors =  super.getHyperlinkDetectors(sourceViewer);
		HBMXMLHyperlinkDetector hyperlinkDetector = new HBMXMLHyperlinkDetector();
		
		if(baseDetectors==null || baseDetectors.length==0) {
			return new IHyperlinkDetector[] { hyperlinkDetector };
		} else {
			IHyperlinkDetector[] result = new IHyperlinkDetector[baseDetectors.length+1];
			result[0] = hyperlinkDetector;
			for (int i = 0; i < baseDetectors.length; i++) {
				result[i+1] = baseDetectors[i]; 
			}
			return result;
		}
		
	}
	

	private boolean hyperLinksEnabled() {
		return !fPreferenceStore.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINKS_ENABLED);
	}
}
