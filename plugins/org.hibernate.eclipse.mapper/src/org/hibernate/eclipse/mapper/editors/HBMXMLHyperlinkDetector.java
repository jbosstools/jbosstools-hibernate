package org.hibernate.eclipse.mapper.editors;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;


/**
 * hyper link detector for hbm.xml
 */
public class HBMXMLHyperlinkDetector implements IHyperlinkDetector {

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
		if (region == null) {
			return null;
		}
		
		int offset= region.getOffset();				

		try {
			IRegion reg = selectWord(textViewer.getDocument(), offset);
		
			String string = textViewer.getDocument().get(reg.getOffset(),reg.getLength());
			return new IHyperlink[] {new HBMXMLHyperlink(reg, string, project)};
		} catch (BadLocationException ble) {
			return null;
		}
		
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
}
