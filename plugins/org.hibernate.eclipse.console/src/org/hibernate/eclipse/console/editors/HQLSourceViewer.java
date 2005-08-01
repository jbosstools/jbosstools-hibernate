package org.hibernate.eclipse.console.editors;

import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;

/**
 * This class implements a source viewer for editing SQL source text. This
 * class extends <code>ProjectViewer</code> in order to get the source folding
 * capability.
 * 
 * @see org.eclipse.jface.text.source.projection.ProjectionViewer
 */
public class HQLSourceViewer extends ProjectionViewer {

    /**
     * Creates an instance of this class with the given parameters.
     * 
     * @param parent the SWT parent control
     * @param ruler the vertical ruler (annotation area)
     * @param overviewRuler the overview ruler
     * @param showsAnnotationOverview <code>true</code> if the overview ruler should be shown
     * @param styles the SWT style bits
     */
    public HQLSourceViewer( Composite parent, IVerticalRuler ruler,
            IOverviewRuler overviewRuler, boolean showsAnnotationOverview,
            int styles ) {
        super( parent, ruler, overviewRuler, showsAnnotationOverview, styles );
    }

}
