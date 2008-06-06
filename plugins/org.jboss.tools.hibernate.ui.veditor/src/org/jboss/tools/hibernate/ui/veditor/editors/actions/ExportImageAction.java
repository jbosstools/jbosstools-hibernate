package org.jboss.tools.hibernate.ui.veditor.editors.actions;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.FileDialog;
import org.jboss.tools.hibernate.ui.veditor.UIVEditorMessages;
import org.jboss.tools.hibernate.ui.veditor.editors.VisualEditor;

public class ExportImageAction extends Action {

	public static final String ACTION_ID = "Export as Image"; //$NON-NLS-1$

	private VisualEditor editor;

	public ExportImageAction(VisualEditor editor) {
		this.editor = editor;
		setId(ACTION_ID);
		setText(ACTION_ID);
		setImageDescriptor(ImageDescriptor.createFromFile(
				VisualEditor.class,"icons/export.png")); //$NON-NLS-1$
	}

	public void run() {

		FileDialog saveDialog = new FileDialog(
				this.editor.getSite().getShell(), SWT.SAVE);
		saveDialog
				.setFilterExtensions(new String[] { "*.png", "*.jpg", "*.bmp" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		saveDialog.setFilterNames(new String[] { UIVEditorMessages.ExportImageAction_png_format,
				UIVEditorMessages.ExportImageAction_jpg_format, UIVEditorMessages.ExportImageAction_bmp_format });

		String filePath = saveDialog.open();
		if (filePath == null || filePath.trim().length() == 0) {
			return;
		}

		IFigure fig = ((ScalableFreeformRootEditPart) this.editor
				.getEditPartViewer().getRootEditPart())
				.getLayer(LayerConstants.PRINTABLE_LAYERS);
		try {
			int imageType = SWT.IMAGE_BMP;
			if (filePath.toLowerCase().endsWith(".jpg")) { //$NON-NLS-1$
				imageType = SWT.IMAGE_JPEG;
			} else if (filePath.toLowerCase().endsWith(".png")) { //$NON-NLS-1$
				imageType = SWT.IMAGE_PNG;
			}

			byte[] imageData = createImage(fig, imageType);
			FileOutputStream outStream = new FileOutputStream(filePath);
			outStream.write(imageData);
			outStream.flush();
			outStream.close();
		} catch (Throwable e) {
			MessageDialog.openInformation(this.editor.getSite().getShell(),
					UIVEditorMessages.ExportImageAction_error, UIVEditorMessages.ExportImageAction_failed_to_export_image + e.getMessage());
			return;
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

		Device device = this.editor.getEditPartViewer().getControl()
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
