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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.jboss.tools.hibernate.ui.diagram.DiagramViewerMessages;
import org.jboss.tools.hibernate.ui.diagram.editors.DiagramViewer;

public class ExportImageAction extends DiagramBaseAction {

	public static final String ACTION_ID = "export_as_image_id"; //$NON-NLS-1$
	public static final String[] dialogFilterExtensions = new String[] { "*.png", "*.jpg", "*.bmp" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	public static final String[] dialogFilterNames = new String[] { DiagramViewerMessages.ExportImageAction_png_format,
		DiagramViewerMessages.ExportImageAction_jpg_format, DiagramViewerMessages.ExportImageAction_bmp_format };

	//private FileDialog saveDialog = null;
	private SaveAsDialog saveDialog = null;
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
	public void setSaveDialog(SaveAsDialog saveDialog) {
		this.saveDialog = saveDialog;
	}

	public void setShowErrDialog(boolean showErrDialog) {
		this.showErrDialog = showErrDialog;
	}

	public void run() {

		boolean createdSaveDialog = false;
		if (saveDialog == null) {
			saveDialog = new SaveAsDialog(getDiagramViewer().getSite().getWorkbenchWindow().getShell());
			createdSaveDialog = true;
		}
		saveDialog.setOriginalName(getDiagramViewer().getStoreFileName());
		saveDialog.open();
		final IPath pathSave = saveDialog.getResult();
		saveDialog = null;
		if (pathSave == null) {
			return;
		}
		final IFigure fig = ((ScalableFreeformRootEditPart) getDiagramViewer()
				.getEditPartViewer().getRootEditPart())
				.getLayer(LayerConstants.PRINTABLE_LAYERS);
		int imageTypeTmp = SWT.IMAGE_BMP;
		String ext = pathSave.getFileExtension();
		if (ext != null) {
			ext = ext.toLowerCase();
			if (ext.endsWith("jpg")) { //$NON-NLS-1$
				imageTypeTmp = SWT.IMAGE_JPEG;
			} else if (ext.endsWith("png")) { //$NON-NLS-1$
				imageTypeTmp = SWT.IMAGE_PNG;
			} else if (ext.endsWith("gif")) { //$NON-NLS-1$
				imageTypeTmp = SWT.IMAGE_GIF;
			} else if (ext.endsWith("bmp")) { //$NON-NLS-1$
				imageTypeTmp = SWT.IMAGE_BMP;
			}
		}
		IPath pathTmp = pathSave;
		if (ext == null) {
			pathTmp = pathTmp.addFileExtension("bmp"); //$NON-NLS-1$
		}
		final IPath path = pathTmp;
		final int imageType = imageTypeTmp;

		final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			public void execute(final IProgressMonitor monitor) {
				ByteArrayInputStream inputStream = null;
				try {
					if (file != null) {
						byte[] imageData = createImage(fig, imageType);
						if (file.exists()) {
							file.delete(true, null);
						}
						if (!file.exists()) {
							inputStream = new ByteArrayInputStream(imageData);
							file.create(inputStream, true, null);
						}
					}
				
				} catch (CoreException e) {
					HibernateConsolePlugin.getDefault().logErrorMessage("ExportImageAction", e); //$NON-NLS-1$
					if (showErrDialog) {
						MessageDialog.openInformation(getDiagramViewer().getSite().getShell(),
							DiagramViewerMessages.ExportImageAction_error, DiagramViewerMessages.ExportImageAction_failed_to_export_image + e.getMessage());
					}
				}
				finally {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (IOException e) {
							// ignore
						}
					}
				}
			}
		};
		try {
			new ProgressMonitorDialog(
					createdSaveDialog ? getDiagramViewer().getSite().getWorkbenchWindow().getShell() : null)
					.run(false, true, op);
		} catch (InvocationTargetException e) {
		} catch (InterruptedException e) {
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
	private byte[] createImage(IFigure figure, int format) {

		//Device device = getDiagramViewer().getEditPartViewer().getControl()
		//		.getDisplay();
		Device device = null;
		Rectangle r = figure.getBounds();

		ByteArrayOutputStream result = new ByteArrayOutputStream();

		Image image = null;
		GC gc = null;
		Graphics g = null;
		try {
			image = new Image(device, r.width, r.height);
			gc = new GC(image);
			g = new SWTGraphics(gc);
			g.translate(r.x * -1, r.y * -1);

			figure.paint(g);

			ImageLoader imageLoader = new ImageLoader();
			imageLoader.data = new ImageData[] { image.getImageData() };
			imageLoader.save(result, format);
		} catch (Throwable e) {
			HibernateConsolePlugin.getDefault().logErrorMessage("ExportImageAction - save:", e); //$NON-NLS-1$
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
		return result.toByteArray();
	}

}
