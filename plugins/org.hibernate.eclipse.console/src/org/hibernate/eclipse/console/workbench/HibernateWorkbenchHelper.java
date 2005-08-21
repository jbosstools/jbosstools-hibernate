package org.hibernate.eclipse.console.workbench;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Value;
import org.hibernate.util.StringHelper;

public class HibernateWorkbenchHelper {

	public static ImageDescriptor getImageDescriptor(Property property) {
		if(property==null) return null;
		if(property.getPersistentClass()!=null) {
			if(property.getPersistentClass().getIdentifierProperty()==property) {
				return EclipseImages.getImageDescriptor(ImageConstants.IDPROPERTY);
			}
		}
		String iconNameForValue = getIconNameForValue(property.getValue());
		
		return EclipseImages.getImageDescriptor(iconNameForValue);
	}
	
	public static Image getImage(Property property) {
		if(property==null) return null;
		if(property.getPersistentClass()!=null) {
			if(property.getPersistentClass().getIdentifierProperty()==property) {
				return EclipseImages.getImage(ImageConstants.IDPROPERTY);
			}
		}
		String iconNameForValue = getIconNameForValue(property.getValue());
		
		return EclipseImages.getImage(iconNameForValue);
	}
	
	static private String getIconNameForValue(Value value) {
		String result;
		
		result = (String) value.accept(new IconNameValueVisitor());
		
		if(result==null) {
			result = ImageConstants.UNKNOWNPROPERTY;
		}
		return result;
	}

	public static String getLabelForClassName(String classOrEntityName) {
		if(classOrEntityName.indexOf('.')>=0) {
			classOrEntityName = StringHelper.unqualify(classOrEntityName);
		}
		return classOrEntityName;
	}

}
