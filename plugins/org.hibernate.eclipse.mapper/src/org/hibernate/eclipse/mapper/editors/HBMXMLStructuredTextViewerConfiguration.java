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
