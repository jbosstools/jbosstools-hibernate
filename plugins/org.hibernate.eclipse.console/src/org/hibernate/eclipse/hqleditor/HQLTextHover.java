package org.hibernate.eclipse.hqleditor;


import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.graphics.Point;

/**
 * This class implements the hover popup text service for HQL code.
 */
public class HQLTextHover implements ITextHover {

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
	
	public IRegion getHoverRegion( ITextViewer textViewer, int offset ) {
		Point selection = textViewer.getSelectedRange();
		if (selection.x <= offset && offset < selection.x + selection.y)
			return new Region( selection.x, selection.y );
		return new Region( offset, 0 );
	}
}
