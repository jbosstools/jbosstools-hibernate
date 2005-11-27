package org.hibernate.eclipse.console.workbench;

import org.eclipse.jface.resource.ImageDescriptor;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;

public class PrimaryKeyWorkbenchAdapter extends BasicWorkbenchAdapter {

	public Object[] getChildren(Object o) {
		PrimaryKey t = getPrimaryKey( o );
		
		if(t.getColumnSpan()==1) {
			return NO_CHILDREN;
		} else {
			return t.getColumns().toArray(new Column[0]);
		}
	}

	private PrimaryKey getPrimaryKey(Object o) {
		return (PrimaryKey)o;
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return EclipseImages.getImageDescriptor(ImageConstants.COLUMN);
	}

	public String getLabel(Object o) {
		PrimaryKey table = getPrimaryKey(o);
		if(table.getColumnSpan()==1) {
			return ColumnWorkbenchAdapter.getColumnLabel(table.getColumn(0));
		} else {
			return "Composite primary key";
		}
	}

	public Object getParent(Object o) {
		return getPrimaryKey(o).getTable();
	}

}
