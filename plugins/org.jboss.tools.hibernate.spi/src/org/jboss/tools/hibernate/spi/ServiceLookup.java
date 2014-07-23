package org.jboss.tools.hibernate.spi;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.jboss.tools.hibernate.spi.internal.HibernateServicePlugin;

public class ServiceLookup {
	
	private static final String SERVICES_EXTENSION_ID = "org.jboss.tools.hibernate.spi.services"; //$NON-NLS-1$

	private static Map<String, IService> services = null;
	
	public static IService findService(String hibernateVersion) {
		if (services == null) {
			initializeServices();
		}
		return services.get(hibernateVersion);
	}
	
	private static void initializeServices() {
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = extensionRegistry.getExtensionPoint(SERVICES_EXTENSION_ID);
		for (IExtension extension : extensionPoint.getExtensions()) {
			for (IConfigurationElement configurationElement : extension.getConfigurationElements()) {
				try {
					Object object = configurationElement.createExecutableExtension("class");
					String name = configurationElement.getAttribute("name");
					services.put(name, (IService)object);
				} catch (CoreException e) {
					HibernateServicePlugin.getDefault().log(e);
				}
			}
		}
	}

}
