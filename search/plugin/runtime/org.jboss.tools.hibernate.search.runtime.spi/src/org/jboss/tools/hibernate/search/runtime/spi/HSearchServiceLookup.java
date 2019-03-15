package org.jboss.tools.hibernate.search.runtime.spi;

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

public class HSearchServiceLookup {

	private static final String SERVICES_EXTENSION_ID = "org.jboss.tools.hibernate.search.runtime.spi.services"; //$NON-NLS-1$

	private static Map<String, IHSearchService> services = null;
	private static String[] versions = null;
	
	public static IHSearchService findService(String hibernateSearchVersion) {
		if (services == null) {
			initializeServices();
		}
		return services.get(hibernateSearchVersion);
	}
	
	public static String[] getVersions() {
		if (services == null) {
			initializeServices();
		}
		return versions;
	}

	private static void initializeServices() {
		services = new HashMap<String, IHSearchService>();
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = extensionRegistry.getExtensionPoint(SERVICES_EXTENSION_ID);
		for (IExtension extension : extensionPoint.getExtensions()) {
			for (IConfigurationElement configurationElement : extension.getConfigurationElements()) {
				try {
					Object object = configurationElement.createExecutableExtension("class");
					String name = configurationElement.getAttribute("name");
					if (object != null && name != null && object instanceof IHSearchService) {
						services.put(name, (IHSearchService)object);
					}
				} catch (CoreException e) {
				}
			}
		}
		ArrayList<String> list = new ArrayList<String>(services.keySet());
		Collections.sort(list);
		versions = list.toArray(new String[list.size()]);
	}
}
