package org.hibernate.eclipse.console.workbench;

import org.eclipse.jface.resource.ImageDescriptor;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.mapping.Column;

public class ColumnWorkbenchAdapter extends BasicWorkbenchAdapter {

	public Object[] getChildren(Object o) {		
		return NO_CHILDREN;
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return EclipseImages.getImageDescriptor(ImageConstants.COLUMN);
	}

	public String getLabel(Object o) {
		Column c = (Column) o;
		return c.getName();
	}

	public Object getParent(Object o) {
		return null;
	}
	
	public boolean isContainer() {
		return false;
	}

}
