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
package org.hibernate.eclipse.hqleditor;

import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.hibernate.eclipse.util.xpl.PlatformStatusLineUtil;

/**
 * Source viewer for editing HQL source text. 
 * 
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

    public void doOperation(int operation) {

    	if (getTextWidget() == null || (!redraws() && operation != FORMAT))
			return;

		switch (operation) {
		case CONTENTASSIST_PROPOSALS:
			String err = fContentAssistant.showPossibleCompletions();
			if (err != null) {
				// don't wanna beep if there is no error
				PlatformStatusLineUtil.displayErrorMessage(err);
			}
			PlatformStatusLineUtil.addOneTimeClearListener();
			return;
		default:
			super.doOperation( operation );
		}
    }
}
