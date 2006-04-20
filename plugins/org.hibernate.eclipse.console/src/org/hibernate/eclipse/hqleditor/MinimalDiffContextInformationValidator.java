package org.hibernate.eclipse.hqleditor;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationPresenter;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

/** Will validate context information that were generated within a certain offset. */
public class MinimalDiffContextInformationValidator implements IContextInformationValidator, IContextInformationPresenter {
    protected int basedOffset;
	private final int minimal;

    public MinimalDiffContextInformationValidator(int minimal) {
		this.minimal = minimal;
	}

	public boolean isContextInformationValid( int offset ) {
        return Math.abs( basedOffset - offset ) < minimal;
    }

    public boolean updatePresentation( int position, TextPresentation presentation ) {
        return true;
    }
    
    public void install( IContextInformation info, ITextViewer viewer, int offset ) {
        basedOffset = offset;
    }
    
}