package org.hibernate.eclipse.console.workbench;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.utils.EclipseImages;

public class ValueWorkbenchAdapter extends BasicWorkbenchAdapter implements
		IDeferredWorkbenchAdapter {

	public Object[] getChildren(Object o) {
		return new Object[0];
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return EclipseImages.getImageDescriptor(ImageConstants.MANYTOONE);
	}

	public String getLabel(Object o) {
		return "value " + o.getClass().getName();
	}

	public Object getParent(Object o) {
		return null;
	}

}
