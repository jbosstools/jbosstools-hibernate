package org.jboss.tools.hibernate.runtime.spi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.jboss.tools.hibernate.spi.internal.HibernateServicePlugin;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class RuntimeServiceManager {
	
	private static final String SERVICES_EXTENSION_ID = "org.jboss.tools.hibernate.runtime.spi.services"; //$NON-NLS-1$

	private static IService DEFAULT_SERVICE = null;
	private static Map<String, IService> SERVICES_MAP = null;
	private static String[] ALL_VERSIONS = null;
	private static Set<String> ENABLED_VERSIONS = null;
	
	public static IService findService(String hibernateVersion) {
		if (SERVICES_MAP == null) {
			initialize();
		}
		return SERVICES_MAP.get(hibernateVersion);
	}
	
	public static String[] getVersions() {
		if (ALL_VERSIONS == null) {
			initialize();
		}
		return ALL_VERSIONS;
	}
	
	public static IService getDefault() {
		if (DEFAULT_SERVICE == null) {
			initialize();
		}
		return DEFAULT_SERVICE;
	}
	
	public static void enableService(String version, boolean enabled) {
		getPreferences().putBoolean(version, enabled);
		if (enabled) {
			ENABLED_VERSIONS.add(version);
		} else {
			ENABLED_VERSIONS.remove(version);
		}
		try {
			getPreferences().flush();
		} catch (BackingStoreException bse) {
			throw new RuntimeException(bse);
		}
	}

	public static boolean isServiceEnabled(String version) {
		return ENABLED_VERSIONS.contains(version);
	}
	
	private static void initialize() {
		initializeServicesMap();
		initializeAllVersions();
		initializeEnabledVersions();
		initializeDefaultService();
	}
	
	private static void initializeServicesMap() {
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
	}
	
	private static void initializeAllVersions() {
		ArrayList<String> list = new ArrayList<String>(SERVICES_MAP.keySet());
		Collections.sort(list);
		ALL_VERSIONS = list.toArray(new String[list.size()]);
	}
	
	private static void initializeEnabledVersions() {
		ENABLED_VERSIONS = new HashSet<String>();
		for (String version : ALL_VERSIONS) {
			if ((getPreferences().getBoolean(version, true))) {
				ENABLED_VERSIONS.add(version);
			}
		}		
	}
	
	private static void initializeDefaultService() {
		DEFAULT_SERVICE = SERVICES_MAP.get(ALL_VERSIONS[ALL_VERSIONS.length - 1]);
	}
	
	private static Preferences getPreferences() {
		return InstanceScope.INSTANCE.getNode("org.jboss.tools.hibernate.runtime.spi");
	}
	
}
