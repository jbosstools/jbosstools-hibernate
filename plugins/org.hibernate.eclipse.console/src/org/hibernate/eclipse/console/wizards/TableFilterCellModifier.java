/**
 * 
 */
package org.hibernate.eclipse.console.wizards;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;
import org.hibernate.eclipse.console.model.ITableFilter;

final class TableFilterCellModifier implements ICellModifier {
	private final TableViewer tv;

	public TableFilterCellModifier(TableViewer tv) {
		this.tv = tv;
	}

	public void modify(Object element, String property, Object value) {
		ITableFilter tf = (ITableFilter) ((TableItem)element).getData();
		if("inclusion".equals(property)) {
			if(!value.equals(tf.getExclude())) {
				tf.setExclude((Boolean) value);
			}
		}
		if("catalog".equals(property)) {
			if(!value.equals(tf.getMatchCatalog())) {
				tf.setMatchCatalog((String) value);
			}
		}
		if("schema".equals(property)) {
			if(!value.equals(tf.getMatchSchema())) {
				tf.setMatchSchema((String) value);
			}
		}
		if("name".equals(property)) {
			if(!value.equals(tf.getMatchName())) {
				tf.setMatchName((String) value);
			}
		}			
		tv.update(new Object[] { tf }, new String[] { property });
	}

	public Object getValue(Object element, String property) {
		ITableFilter tf = (ITableFilter) element;
		if("inclusion".equals(property)) {
			return tf.getExclude();
		}
		if("catalog".equals(property)) {
			return tf.getMatchCatalog();
		}
		if("schema".equals(property)) {
			return tf.getMatchSchema();
		}
		if("name".equals(property)) {
			return tf.getMatchName();
		}		
		return null;
	}

	public boolean canModify(Object element, String property) {
		return true;
	}
}