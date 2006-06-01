package org.hibernate.eclipse.console.views.navigator;

import org.eclipse.swt.graphics.Image;
import org.hibernate.console.ImageConstants;
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
			HibernateConsolePlugin.getDefault().logErrorMessage( "Error while getting images", e );
			throw new ExceptionInInitializerError(e);
		}
	}
 
	private String text[]; 
	private int count = 0;
	
	public PendingNode(String type) {
		text = new String[4];
		text[0] = "Pending " + type;
		text[1] = text  + "."; //$NON-NLS-1$
		text[2] = text  + ".."; //$NON-NLS-1$
		text[3] = text  + "..."; //$NON-NLS-1$
	}

	public String getText() {
		return text[count%4];
	}

	public Image getImage() {
		count++;
		return loadingImage[count % 4];
	} 
}
