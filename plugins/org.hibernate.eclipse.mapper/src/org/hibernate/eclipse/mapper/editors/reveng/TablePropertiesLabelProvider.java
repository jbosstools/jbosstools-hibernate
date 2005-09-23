package org.hibernate.eclipse.mapper.editors.reveng;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.model.IRevEngColumn;
import org.hibernate.eclipse.console.model.IRevEngTable;
import org.hibernate.eclipse.console.utils.EclipseImages;

public class TablePropertiesLabelProvider extends LabelProvider {

	public String getText(Object element) {
		String internalText = getInternalText( element );
		if(internalText==null) {
			return "<n/a>";
		} else {
			return internalText;
		}
	}

	private String getInternalText(Object element) {
		if(element instanceof IRevEngTable) {
			IRevEngTable table = (IRevEngTable)element;
			return getLabel( table );			
		} else if(element instanceof IRevEngColumn) {
			return ((IRevEngColumn)element).getName();
		}
		return super.getText( element );
	}

	private String getLabel(IRevEngTable table) {
		StringBuffer res = new StringBuffer();
		if(table.getCatalog()!=null) {
			res.append(table.getCatalog());
		}

		if(table.getSchema()!=null) {
			if(res.length()!=0) res.append(".");
			res.append(table.getSchema());
		}
		
		if(table.getName()!=null) {
			if(res.length()!=0) res.append(".");
			res.append(table.getName());
		}
		return res.toString();
	}
	
	public Image getImage(Object element) {
		if(element instanceof IRevEngTable) {
			return EclipseImages.getImage(ImageConstants.TABLE);			
		} else if(element instanceof IRevEngColumn) {
			return EclipseImages.getImage(ImageConstants.COLUMN);
		}
		return null;
	}
}
