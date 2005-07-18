package org.hibernate.eclipse.console.workbench;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.util.JoinedIterator;

public class PersistentClassWorkbenchAdapter extends BasicWorkbenchAdapter {

	public Object[] getChildren(Object o) {
		PersistentClass pc = (PersistentClass) o;
		Property identifierProperty = pc.getIdentifierProperty();
		List properties = new ArrayList();
		
		if(identifierProperty!=null) {
			properties.add(identifierProperty);
		}
		
		Iterator propertyClosureIterator = new JoinedIterator(properties.iterator(), pc.getPropertyClosureIterator());
		return toArray(propertyClosureIterator, Property.class);
	}

	
	public ImageDescriptor getImageDescriptor(Object object) {
		return EclipseImages.getImageDescriptor(ImageConstants.MAPPEDCLASS);
	}

	public String getLabel(Object o) {
		PersistentClass pc = (PersistentClass) o;
		return pc.getClassName();
	}

	public Object getParent(Object o) {
		return null;
	}

	
}
