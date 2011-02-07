/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.console.workbench;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.hibernate.eclipse.console.utils.EclipseImages;

/**
 * based on the class from DTP plugins
 * 
 * @author vy (vyemialyanchyk@gmail.com)
 */
public class OverlayImageIcon extends CompositeImageDescriptor {

	public static final int TOP_LEFT = 0;
	public static final int TOP_RIGHT = 1;
	public static final int BOTTOM_LEFT = 2;
	public static final int BOTTOM_RIGHT = 3;

	/**
	 * Base image of the object
	 */
	private Image baseImage;

	/**
	 * Size of the base image
	 */
	private Point sizeOfImage;

	/**
	 * Map of ovr image to place 
	 */
	private Map<String, Integer> imageMap;

	/**
	 * Constructor for overlayImageIcon.
	 */
	public OverlayImageIcon(Image baseImage, Map<String, Integer> imageMap) {
		// Base image of the object
		this.baseImage = baseImage;
		this.imageMap = imageMap;
		this.sizeOfImage = new Point(baseImage.getBounds().width, baseImage.getBounds().height);
	}

	/**
	 * @see org.eclipse.jface.resource.CompositeImageDescriptor#drawCompositeImage(int,
	 *      int) DrawCompositeImage is called to draw the composite image.
	 * 
	 */
	@Override
	protected void drawCompositeImage(int arg0, int arg1) {
		// Draw the base image
		drawImage(this.baseImage.getImageData(), 0, 0);
		Iterator<Map.Entry<String, Integer>> it = imageMap.entrySet().iterator();
		for (; it.hasNext(); ) {
			Map.Entry<String, Integer> entry = it.next();
			ImageData imageData = 
				EclipseImages.getImageDescriptor(entry.getKey()).getImageData();
			switch (entry.getValue()) {
			// Draw on the top left corner
			case TOP_LEFT:
				drawImage(imageData, 0, 0);
				break;

			// Draw on top right corner
			case TOP_RIGHT:
				drawImage(imageData, this.sizeOfImage.x - imageData.width, 0);
				break;

			// Draw on bottom left
			case BOTTOM_LEFT:
				drawImage(imageData, 0, this.sizeOfImage.y - imageData.height);
				break;

			// Draw on bottom right corner
			case BOTTOM_RIGHT:
				drawImage(imageData, this.sizeOfImage.x - imageData.width,
						this.sizeOfImage.y - imageData.height);
				break;

			}
		}

	}

	/**
	 * @see org.eclipse.jface.resource.CompositeImageDescriptor#getSize() get
	 *      the size of the object
	 */
	@Override
	protected Point getSize() {
		return this.sizeOfImage;
	}

	/**
	 * Get the image formed by overlaying different images on the base image
	 * 
	 * @return composite image
	 */
	public Image getImage() {
		return createImage();
	}
}
