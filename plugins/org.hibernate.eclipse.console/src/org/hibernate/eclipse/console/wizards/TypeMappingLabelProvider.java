/**
 * 
 */
package org.hibernate.eclipse.console.wizards;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.hibernate.eclipse.console.model.ITypeMapping;

public class TypeMappingLabelProvider extends LabelProvider implements ITableLabelProvider {

	public Image getColumnImage(Object element, int columnIndex) {
		return  null;
	}

	public String getColumnText(Object element, int columnIndex) {
		ITypeMapping tf = (ITypeMapping) element;
		String result = "";
		
		switch (columnIndex) {
		case 0:
			return tf.getJDBCType();
		case 1: 
			return tf.getHibernateType();
		case 2: 
			return safeToString(tf.getLength());
		case 3:
			return safeToString(tf.getScale());
		case 4: 
			return safeToString(tf.getPrecision());
		default:
			return result;
		}			
	}

	private String safeToString(Integer length) {
		if(length==null) return "";
		return length.toString();
	}
}