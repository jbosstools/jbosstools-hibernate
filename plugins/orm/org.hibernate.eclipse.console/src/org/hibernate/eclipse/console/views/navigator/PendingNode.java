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
package org.hibernate.eclipse.console.views.navigator;

import org.eclipse.swt.graphics.Image;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.EclipseImages;

public class PendingNode {

	private static Image[] loadingImage;

	static {
		try {
			loadingImage = new Image[4];
			loadingImage[0] = EclipseImages.getImage( ImageConstants.COMPONENT );
			loadingImage[1] = EclipseImages.getImage( ImageConstants.CLOSE_DISABLED );
			loadingImage[2] = EclipseImages.getImage( ImageConstants.EXECUTE );
			loadingImage[3] = EclipseImages.getImage( ImageConstants.HQL_EDITOR );
		} catch (RuntimeException e) {
			HibernateConsolePlugin.getDefault().logErrorMessage( HibernateConsoleMessages.PendingNode_error_getting_images, e );
			throw new ExceptionInInitializerError(e);
		}
	}

	private String text[];
	private int count = 0;

	public PendingNode(String type) {
		text = new String[4];
		text[0] = HibernateConsoleMessages.PendingNode_pending + type;
		text[1] = text[0] + "."; //$NON-NLS-1$
		text[2] = text[0] + ".."; //$NON-NLS-1$
		text[3] = text[0] + "..."; //$NON-NLS-1$
	}

	public String getText() {
		return text[count%4];
	}

	public Image getImage() {
		count++;
		return loadingImage[count % 4];
	}
}
