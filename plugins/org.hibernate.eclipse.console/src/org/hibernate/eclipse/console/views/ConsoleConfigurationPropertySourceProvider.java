package org.hibernate.eclipse.console.views;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;

public class ConsoleConfigurationPropertySourceProvider implements
		IPropertySourceProvider {

	public IPropertySource getPropertySource(Object object) {
		if(object==null) return null;
		return (IPropertySource) Platform.getAdapterManager().getAdapter(object, IPropertySource.class);
	}

}
