package org.jboss.tools.hibernate.spi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
	private static String[] versions = null;
	
	public static IService findService(String hibernateVersion) {
		if (services == null) {
			initializeServices();
		}
		return services.get(hibernateVersion);
	}
	
	public static String[] getVersions() {
		if (services == null) {
			initializeServices();
		}
		return versions;
	}

	private static void initializeServices() {
		services = new HashMap<String, IService>();
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = extensionRegistry.getExtensionPoint(SERVICES_EXTENSION_ID);
		for (IExtension extension : extensionPoint.getExtensions()) {
			for (IConfigurationElement configurationElement : extension.getConfigurationElements()) {
				try {
					Object object = configurationElement.createExecutableExtension("class");
					String name = configurationElement.getAttribute("name");
					if (object != null && name != null && object instanceof IService) {
						services.put(name, (IService)object);
					}
				} catch (CoreException e) {
					HibernateServicePlugin.getDefault().log(e);
				}
			}
		}
		ArrayList<String> list = new ArrayList<String>(services.keySet());
		Collections.sort(list);
		versions = list.toArray(new String[list.size()]);
	}
	
}
