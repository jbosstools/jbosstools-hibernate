/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.diagram.editors.actions;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.FileDialog;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.jboss.tools.hibernate.ui.diagram.DiagramViewerMessages;
import org.jboss.tools.hibernate.ui.diagram.editors.DiagramViewer;

public class ExportImageAction extends DiagramBaseAction {

	public static final String ACTION_ID = "export_as_image_id"; //$NON-NLS-1$
	public static final String[] dialogFilterExtensions = new String[] { "*.png", "*.jpg", "*.bmp" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	public static final String[] dialogFilterNames = new String[] { DiagramViewerMessages.ExportImageAction_png_format,
		DiagramViewerMessages.ExportImageAction_jpg_format, DiagramViewerMessages.ExportImageAction_bmp_format };

	private FileDialog saveDialog = null;
	private boolean showErrDialog = true;
	public static final ImageDescriptor img = 
		ImageDescriptor.createFromFile(DiagramViewer.class, "icons/export.png"); //$NON-NLS-1$

	public ExportImageAction(DiagramViewer editor) {
		super(editor);
		setId(ACTION_ID);
		setText(DiagramViewerMessages.ExportImageAction_export_as_image);
		setImageDescriptor(img);
	}
	
	/**
	 * main goal of this function to allow tests for 
	 * ExportImageAction functionality
	 * 
	 * @param saveDialog
	 */
	public void setSaveDialog(FileDialog saveDialog) {
		this.saveDialog = saveDialog;
	}

	public void setShowErrDialog(boolean showErrDialog) {
		this.showErrDialog = showErrDialog;
	}

	public void run() {

		if (saveDialog == null) {
			saveDialog = new FileDialog(
				getDiagramViewer().getSite().getShell(), SWT.SAVE);
		}
		saveDialog.setFilterExtensions(dialogFilterExtensions);
		saveDialog.setFilterNames(dialogFilterNames);

		String filePath = saveDialog.open();
		saveDialog = null;
		if (filePath == null || filePath.trim().length() == 0) {
			return;
		}

		IFigure fig = ((ScalableFreeformRootEditPart) getDiagramViewer()
				.getEditPartViewer().getRootEditPart())
				.getLayer(LayerConstants.PRINTABLE_LAYERS);
		int imageType = SWT.IMAGE_BMP;
		if (filePath.toLowerCase().endsWith(".jpg")) { //$NON-NLS-1$
			imageType = SWT.IMAGE_JPEG;
		} else if (filePath.toLowerCase().endsWith(".png")) { //$NON-NLS-1$
			imageType = SWT.IMAGE_PNG;
		}
		FileOutputStream outStream = null;
		try {
			byte[] imageData = createImage(fig, imageType);
			outStream = new FileOutputStream(filePath);
			outStream.write(imageData);
			outStream.flush();
		} catch (Exception e) {
			HibernateConsolePlugin.getDefault().logErrorMessage("ExportImageAction", e); //$NON-NLS-1$
			if (showErrDialog) {
				MessageDialog.openInformation(getDiagramViewer().getSite().getShell(),
					DiagramViewerMessages.ExportImageAction_error, DiagramViewerMessages.ExportImageAction_failed_to_export_image + e.getMessage());
			}
		}
		finally {
			if (outStream != null) {
				try {
					outStream.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	/***
	 * Returns the bytes of an encoded image for the specified
	 * IFigure in the specified format.
	 *
	 * @param figure the Figure to create an image for.
	 * @param format one of SWT.IMAGE_BMP, SWT.IMAGE_BMP_RLE, SWT.IMAGE_GIF
	 *          SWT.IMAGE_ICO, SWT.IMAGE_JPEG, or SWT.IMAGE_PNG
	 * @return the bytes of an encoded image for the specified Figure
	 */
	private byte[] createImage(IFigure figure, int format) throws Exception {

		Device device = getDiagramViewer().getEditPartViewer().getControl()
				.getDisplay();
		Rectangle r = figure.getBounds();

		ByteArrayOutputStream result = new ByteArrayOutputStream();

		Image image = null;
		GC gc = null;
		Graphics g = null;
		Exception error = null;
		try {
			image = new Image(device, r.width, r.height);
			gc = new GC(image);
			g = new SWTGraphics(gc);
			g.translate(r.x * -1, r.y * -1);

			figure.paint(g);

			ImageLoader imageLoader = new ImageLoader();
			imageLoader.data = new ImageData[] { image.getImageData() };
			imageLoader.save(result, format);
		} catch (Exception ex) {
			error = ex;
		} finally {
			if (g != null) {
				g.dispose();
			}
			if (gc != null) {
				gc.dispose();
			}
			if (image != null) {
				image.dispose();
			}
		}
		if (error != null) {
			throw error;
		}
		return result.toByteArray();
	}

}
