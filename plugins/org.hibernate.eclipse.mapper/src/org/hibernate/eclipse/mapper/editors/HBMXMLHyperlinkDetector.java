package org.hibernate.eclipse.mapper.editors;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.util.StringUtils;
import org.eclipse.wst.xml.core.document.XMLAttr;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.hibernate.eclipse.mapper.extractor.HBMInfoExtractor;
import org.hibernate.eclipse.mapper.extractor.HBMInfoHandler;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


/**
 * hyper link detector for hbm.xml
 */
public class HBMXMLHyperlinkDetector implements IHyperlinkDetector {

	HBMInfoExtractor infoExtractor = new HBMInfoExtractor();
	
	private final IJavaProject project;

	/**
	 * Creates a new hbm.xml element hyperlink detector.
	 * @param project 
	 *  
	 * @param editor the editor in which to detect the hyperlink
	 */
	public HBMXMLHyperlinkDetector(IJavaProject project) {
		Assert.isNotNull(project);
		this.project = project;		
	}

	/*
	 * @see org.eclipse.jface.text.hyperlink.IHyperlinkDetector#detectHyperlinks(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion, boolean)
	 */
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		if (region == null || textViewer == null) {
			return null;
		}
		
		IDocument document = textViewer.getDocument();
		Node currentNode = getCurrentNode(document, region.getOffset());
		if (currentNode != null) {
		
			short nodeType = currentNode.getNodeType();
			if(nodeType == Node.ATTRIBUTE_NODE) {
									
			} else if (nodeType == Node.ELEMENT_NODE){

				Attr currentAttrNode = getCurrentAttrNode(currentNode, region.getOffset());
				
				if(currentAttrNode!=null) {
					String path = currentNode.getNodeName() + ">" + currentAttrNode.getName();
			        HBMInfoHandler handler = infoExtractor.getAttributeHandler(path);
					if(handler!=null) {
						IJavaElement element = handler.getJavaElement(project, currentNode, currentAttrNode);
						if(element!=null) {
							return new IHyperlink[] {new HBMXMLHyperlink(getHyperlinkRegion(currentAttrNode), element, project)};
						} else {
							return null;
						}
						
					}
				}
			}
		}
		
		return null;
		
	}
	
	private IRegion getHyperlinkRegion(Node node) {
		IRegion hyperRegion = null;

		if (node != null) {
			short nodeType = node.getNodeType();
			if (nodeType == Node.DOCUMENT_TYPE_NODE) {
				// handle doc type node
				XMLNode docNode = (XMLNode) node;
				hyperRegion = new Region(docNode.getStartOffset(), docNode.getEndOffset() - docNode.getStartOffset());
			}
			else if (nodeType == Node.ATTRIBUTE_NODE) {
				// handle attribute nodes
				XMLAttr att = (XMLAttr) node;
				// do not include quotes in attribute value region
				int regOffset = att.getValueRegionStartOffset();
				int regLength = att.getValueRegion().getTextLength();
				String attValue = att.getValueRegionText();
				if (StringUtils.isQuoted(attValue)) {
					regOffset = ++regOffset;
					regLength = regLength - 2;
				}
				hyperRegion = new Region(regOffset, regLength);
			}
		}
		return hyperRegion;
	}
	
	private Attr getCurrentAttrNode(Node node, int offset) {
		if ((node instanceof IndexedRegion) && ((IndexedRegion) node).contains(offset) && (node.hasAttributes())) {
			NamedNodeMap attrs = node.getAttributes();
			// go through each attribute in node and if attribute contains
			// offset, return that attribute
			for (int i = 0; i < attrs.getLength(); ++i) {
				// assumption that if parent node is of type IndexedRegion,
				// then its attributes will also be of type IndexedRegion
				IndexedRegion attRegion = (IndexedRegion) attrs.item(i);
				if (attRegion.contains(offset)) {
					return (Attr) attrs.item(i);
				}
			}
		}
		return null;
	}
	
	private IRegion selectWord(IDocument document, int anchor) {
	
		try {		
			int offset= anchor;
			char c;

			while (offset >= 0) {
				c= document.getChar(offset);
				if (!Character.isJavaIdentifierPart(c) && '.'!=c)
					break;
				--offset;
			}

			int start= offset;

			offset= anchor;
			int length= document.getLength();

			while (offset < length) {
				c= document.getChar(offset);
				if (!Character.isJavaIdentifierPart(c) && '.'!=c)
					break;
				++offset;
			}
			
			int end= offset;
			
			IRegion reg = null;
			if (start == end) {
				reg = new Region(start, 0);
			} else {
				reg = new Region(start + 1, end - start - 1);
			}
			return reg;
			
		} catch (BadLocationException x) {
			return null;
		}
	}
	
	/**
	 * Returns the node the cursor is currently on in the document. null if no
	 * node is selected
	 * 
	 * @param offset
	 * @return Node either element, doctype, text, or null
	 */
	private Node getCurrentNode(IDocument document, int offset) {
		// get the current node at the offset (returns either: element,
		// doctype, text)
		IndexedRegion inode = null;
		IStructuredModel sModel = null;
		try {
			sModel = StructuredModelManager.getModelManager().getExistingModelForRead(document);
			inode = sModel.getIndexedRegion(offset);
			if (inode == null)
				inode = sModel.getIndexedRegion(offset - 1);
		}
		finally {
			if (sModel != null)
				sModel.releaseFromRead();
		}

		if (inode instanceof Node) {
			return (Node) inode;
		}
		return null;
	}
}
