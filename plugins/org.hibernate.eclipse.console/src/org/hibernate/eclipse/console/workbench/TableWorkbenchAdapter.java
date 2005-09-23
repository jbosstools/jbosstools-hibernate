package org.hibernate.eclipse.console.workbench;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;

public class TableWorkbenchAdapter extends BasicWorkbenchAdapter {

	public Object[] getChildren(Object o) {
		Table t = getTable( o );
		
		List items = new ArrayList();
		
		PrimaryKey primaryKey = t.getPrimaryKey();
		if(primaryKey!=null) {
			items.add(primaryKey);			
		}
		
		Iterator columnIterator = t.getColumnIterator();
		while ( columnIterator.hasNext() ) {
			Column col = (Column) columnIterator.next();
			if(primaryKey==null || !primaryKey.containsColumn(col)) {
				items.add(col); // only add non-pk columns here
			}			
		}
		
		return items.toArray(new Object[items.size()]);
	}

	private Table getTable(Object o) {
		return (Table) o;
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return EclipseImages.getImageDescriptor(ImageConstants.TABLE);
	}

	public String getLabel(Object o) {
		Table table = getTable(o);
		//return Table.qualify(table.getCatalog(), table.getSchema(), table.getName(), '.');
		return table.getName();
	}

	public Object getParent(Object o) {
		return null;
	}

}
