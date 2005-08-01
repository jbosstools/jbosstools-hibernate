package org.hibernate.eclipse.console.editors;


import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.graphics.Point;

/**
 * This class implements the hover popup text service for SQL code.
 */
public class HQLTextHover implements ITextHover {

	/**
     * Returns the information which should be presented when a hover popup is shown
     * for the specified hover region.  The hover region has the same semantics
     * as the region returned by <code>getHoverRegion</code>. If the returned
     * information is <code>null</code> or empty no hover popup will be shown.
     * 
	 * @see org.eclipse.jface.text.ITextHover#getHoverInfo(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
	 */
	public String getHoverInfo( ITextViewer textViewer, IRegion hoverRegion ) {
        // TODO: make this do something useful
		if (hoverRegion != null) {
			try {
				if (hoverRegion.getLength() > -1)
					return textViewer.getDocument().get( hoverRegion.getOffset(), hoverRegion.getLength() );
			} catch (BadLocationException x) {
			}
		}
		return "Empty Selection";
	}
	
	/**
	 * Returns the text region which should serve as the source of information 
     * to compute the hover popup display information. The popup has been requested
     * for the given offset.
     * 
     * @see org.eclipse.jface.text.ITextHover#getHoverRegion(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IRegion getHoverRegion( ITextViewer textViewer, int offset ) {
		Point selection = textViewer.getSelectedRange();
		if (selection.x <= offset && offset < selection.x + selection.y)
			return new Region( selection.x, selection.y );
		return new Region( offset, 0 );
	}
}
