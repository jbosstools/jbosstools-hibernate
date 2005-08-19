package org.hibernate.eclipse.console.workbench;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySheetEntry;

public class BeanPropertyDescriptor implements IPropertyDescriptor {

	private final PropertyDescriptor descriptor;

	public BeanPropertyDescriptor(PropertyDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	public CellEditor createPropertyEditor(Composite parent) {
		return null;
	}

	public String getCategory() {
		return null;
	}

	public String getDescription() {
		return descriptor.getShortDescription();
	}

	public String getDisplayName() {
		return descriptor.getDisplayName();
	}

	public String[] getFilterFlags() {
		if(descriptor.isExpert()) {
			return new String[] { IPropertySheetEntry.FILTER_ID_EXPERT };
		} else {
			return null;
		}
	}

	public Object getHelpContextIds() {
		return null;
	}

	public Object getId() {
		return descriptor.getName();
	}

	public ILabelProvider getLabelProvider() {
		return new ILabelProvider() {
		
			public void removeListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub
		
			}
		
			public boolean isLabelProperty(Object element, String property) {
				// TODO Auto-generated method stub
				return false;
			}
		
			public void dispose() {
				// TODO Auto-generated method stub
		
			}
		
			public void addListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub
		
			}
		
			public String getText(Object element) {
				return "" + element; 
			}
		
			public Image getImage(Object element) {
				return null;//EclipseImages.getImage(ImageConstants.TABLE);
			}
		
		};
	}

	public boolean isCompatibleWith(IPropertyDescriptor anotherProperty) {
		return false;
	}

	public Object getValue(Object real) {
		try {
			Method readMethod = descriptor.getReadMethod();
			if (readMethod!=null) {
				return readMethod.invoke(real, new Object[0]);
			} else {
				return null;
			}
		}
		catch (Exception e) {
			return null;
		}
	}

}
