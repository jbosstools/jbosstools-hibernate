package org.hibernate.eclipse.console.workbench;

import org.eclipse.jface.resource.ImageDescriptor;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Table;

public class TableWorkbenchAdapter extends BasicWorkbenchAdapter {

	public Object[] getChildren(Object o) {
		Table t = getTable( o );
		return toArray(t.getColumnIterator(), Column.class);
	}

	private Table getTable(Object o) {
		return (Table) o;
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return EclipseImages.getImageDescriptor(ImageConstants.TABLE);
	}

	public String getLabel(Object o) {
		Table table = getTable(o);
		return Table.qualify(table.getCatalog(), table.getSchema(), table.getName(), '.');
	}

	public Object getParent(Object o) {
		return null;
	}

}
