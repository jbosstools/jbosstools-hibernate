/**
 * 
 */
package org.hibernate.eclipse.console.wizards;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.model.ITableFilter;
import org.hibernate.eclipse.console.utils.EclipseImages;

public class TableFilterLabelProvider extends LabelProvider implements ITableLabelProvider {

	//		 Names of images used to represent checkboxes	
	public static final String CHECKED_IMAGE 	= "checked";
	public static final String UNCHECKED_IMAGE  = "unchecked";
	
	
	public Image getColumnImage(Object element, int columnIndex) {
		if(columnIndex==0) {
			ITableFilter tf = (ITableFilter)element;
			if(tf.getExclude()!=null) {
				String key = tf.getExclude().booleanValue() ? ImageConstants.CLOSE : null ; // TODO: find a better image
				return EclipseImages.getImage(key);
			} else {
				return null;
			}
			
		}
		return  null;
	}

	
	public String getColumnText(Object element, int columnIndex) {
		ITableFilter tf = (ITableFilter) element;
		String result = "";
		
		switch (columnIndex) {
		case 0:
			return result;
		case 1:
			return tf.getMatchCatalog();
		case 2: 
			return tf.getMatchSchema();
		case 3: 
			return tf.getMatchName();
		default:
			return result;
		}			
	}
}