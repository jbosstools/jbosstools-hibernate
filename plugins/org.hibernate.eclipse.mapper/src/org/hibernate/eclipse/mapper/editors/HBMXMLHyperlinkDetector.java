package org.hibernate.eclipse.mapper.editors;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.hibernate.eclipse.mapper.editors.xpl.BaseXMLHyperlinkSupport;
import org.hibernate.eclipse.mapper.extractor.HBMInfoExtractor;
import org.hibernate.eclipse.mapper.extractor.HBMInfoHandler;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;


/**
 * hyper link detector for hbm.xml
 */
public class HBMXMLHyperlinkDetector extends BaseXMLHyperlinkSupport implements IHyperlinkDetector {

	HBMInfoExtractor infoExtractor = new HBMInfoExtractor();
	
	public HBMXMLHyperlinkDetector() {
		
	}

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		if (region == null || textViewer == null) {
			return null;
		}
		IJavaProject jp = CFGXMLStructuredTextViewerConfiguration.findJavaProject(textViewer);
		if(jp==null) return new IHyperlink[0];
		
		IDocument document = textViewer.getDocument();
		Node currentNode = getCurrentNode(document, region.getOffset() );
		if (currentNode != null) {
		
			short nodeType = currentNode.getNodeType();
			if(nodeType == Node.ATTRIBUTE_NODE) {
									
			} else if (nodeType == Node.ELEMENT_NODE){

				Attr currentAttrNode = getCurrentAttrNode(currentNode, region.getOffset() );
				
				if(currentAttrNode!=null) {
					String path = currentNode.getNodeName() + ">" + currentAttrNode.getName();
			        HBMInfoHandler handler = infoExtractor.getAttributeHandler(path);
					if(handler!=null) {
						IJavaProject project = CFGXMLStructuredTextViewerConfiguration.findJavaProject(document);
						IJavaElement element = handler.getJavaElement(project, currentNode, currentAttrNode);
						if(element!=null) {
							return new IHyperlink[] {new HBMXMLHyperlink(getHyperlinkRegion(currentAttrNode), element)};
						} else {
							return null;
						}
						
					}
				}
			}
		}
		
		return null;
		
	}
}
