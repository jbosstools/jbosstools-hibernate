package org.hibernate.eclipse.console.workbench;

import org.eclipse.jface.resource.ImageDescriptor;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.mapping.Table;

public class TableContainerWorkbenchAdapter extends BasicWorkbenchAdapter {

	public Object[] getChildren(Object o) {
		TableContainer tc = getTableContainer( o );
		return toArray(tc.getTables().iterator(), Table.class);
	}

	private TableContainer getTableContainer(Object o) {
		return (TableContainer) o;
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return EclipseImages.getImageDescriptor(ImageConstants.SCHEMA);
	}

	public String getLabel(Object o) {
		String name = getTableContainer(o).getName();
		return "".equals(name)?"<default>":name;
	}

	public Object getParent(Object o) {
		return null;
	}

	
}
