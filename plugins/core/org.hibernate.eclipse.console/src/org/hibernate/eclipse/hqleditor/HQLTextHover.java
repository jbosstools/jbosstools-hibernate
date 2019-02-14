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


import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.graphics.Point;
import org.hibernate.eclipse.console.HibernateConsoleMessages;

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
		return HibernateConsoleMessages.HQLTextHover_empty_selection;
	}

	public IRegion getHoverRegion( ITextViewer textViewer, int offset ) {
		Point selection = textViewer.getSelectedRange();
		if (selection.x <= offset && offset < selection.x + selection.y)
			return new Region( selection.x, selection.y );
		return new Region( offset, 0 );
	}
}
