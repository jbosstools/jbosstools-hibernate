package org.hibernate.eclipse.console.workbench;

import java.util.Iterator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.mapping.PersistentClass;

public class ConfigurationWorkbenchAdapter extends BasicWorkbenchAdapter {

	public Object[] getChildren(Object o) {
		Configuration cfg = (Configuration) o;
		Iterator classMappings = cfg.getClassMappings();
		return toArray(classMappings, PersistentClass.class);
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return EclipseImages.getImageDescriptor(ImageConstants.CONFIGURATION);
	}

	public String getLabel(Object o) {
		return "Configuration";
	}

	public Object getParent(Object o) {
		return KnownConfigurations.getInstance();
	}

	public boolean isContainer() {
		return true;
	}
	
	

}
