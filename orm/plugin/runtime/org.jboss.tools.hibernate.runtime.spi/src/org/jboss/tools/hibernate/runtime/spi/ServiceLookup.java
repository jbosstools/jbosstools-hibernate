package org.jboss.tools.hibernate.runtime.spi;

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
	
	private static final String SERVICES_EXTENSION_ID = "org.jboss.tools.hibernate.runtime.spi.services"; //$NON-NLS-1$

	private static IService DEFAULT_SERVICE = null;
	private static Map<String, IService> SERVICES_MAP = null;
	private static String[] VERSIONS = null;
	
	public static IService findService(String hibernateVersion) {
		if (SERVICES_MAP == null) {
			initialize();
		}
		return SERVICES_MAP.get(hibernateVersion);
	}
	
	public static String[] getVersions() {
		if (SERVICES_MAP == null) {
			initialize();
		}
		return VERSIONS;
	}
	
	public static IService getDefault() {
		if (DEFAULT_SERVICE == null) {
			initialize();
		}
		return DEFAULT_SERVICE;
	}

	public static boolean isServiceEnabled(String serviceName) {
		return true;
	}
	
	private static void initialize() {
		SERVICES_MAP = new HashMap<String, IService>();
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = extensionRegistry.getExtensionPoint(SERVICES_EXTENSION_ID);
		for (IExtension extension : extensionPoint.getExtensions()) {
			for (IConfigurationElement configurationElement : extension.getConfigurationElements()) {
				try {
					Object object = configurationElement.createExecutableExtension("class");
					String name = configurationElement.getAttribute("name");
					if (object != null && name != null && object instanceof IService) {
						SERVICES_MAP.put(name, (IService)object);
					}
				} catch (CoreException e) {
					HibernateServicePlugin.getDefault().log(e);
				}
			}
		}
		ArrayList<String> list = new ArrayList<String>(SERVICES_MAP.keySet());
		Collections.sort(list);
		VERSIONS = list.toArray(new String[list.size()]);
		DEFAULT_SERVICE = SERVICES_MAP.get(VERSIONS[VERSIONS.length - 1]);
	}
	
}
