package org.jboss.tools.hibernate.runtime.spi;

import java.util.ArrayList;
import java.util.Arrays;
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
	
	private static final RuntimeServiceManager INSTANCE = new RuntimeServiceManager();

	public static RuntimeServiceManager getInstance() {
		return INSTANCE;
	}
	
	private Preferences servicePreferences = null;
	private Map<String, IService> servicesMap = null;
	private String[] allVersions = null;
	private Set<String> enabledVersions = null;
		
	private RuntimeServiceManager() {	
		initializePreferences();
		initializeServicesMap();
		initializeAllVersions();
		initializeEnabledVersions();
	}
	
	public void enableService(String version, boolean enabled) {
		servicePreferences.putBoolean(version, enabled);
		if (enabled) {
			enabledVersions.add(version);
		} else {
			enabledVersions.remove(version);
		}
		try {
			servicePreferences.flush();
		} catch (BackingStoreException bse) {
			throw new RuntimeException(bse);
		}
	}

	public IService findService(String hibernateVersion) {
		return servicesMap.get(hibernateVersion);
	}
	
	public String[] getAllVersions() {
		return Arrays.copyOf(allVersions, allVersions.length);
	}
	
	public IService getDefaultService() {
		return findService(getDefaultVersion());
	}
	
	public String getDefaultVersion() {
		String defaultVersion = servicePreferences.get("default", null);
		if (defaultVersion != null) return defaultVersion;
		if (enabledVersions.isEmpty()) throw new RuntimeException("No Hibernate runtimes are enabled.");
		String[] enabled = enabledVersions.toArray(new String[enabledVersions.size()]);
		Arrays.sort(enabled);
		return enabled[enabled.length - 1];
	}
	
	public boolean isServiceEnabled(String version) {
		return enabledVersions.contains(version);
	}
	
	private void initializePreferences() {
		servicePreferences = InstanceScope.INSTANCE.getNode(SERVICES_EXTENSION_ID);
	}
	
	private void initializeServicesMap() {
		servicesMap = new HashMap<String, IService>();
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = extensionRegistry.getExtensionPoint(SERVICES_EXTENSION_ID);
		for (IExtension extension : extensionPoint.getExtensions()) {
			for (IConfigurationElement configurationElement : extension.getConfigurationElements()) {
				try {
					Object object = configurationElement.createExecutableExtension("class");
					String name = configurationElement.getAttribute("name");
					if (object != null && name != null && object instanceof IService) {
						servicesMap.put(name, (IService)object);
					}
				} catch (CoreException e) {
					HibernateServicePlugin.getDefault().log(e);
				}
			}
		}		
	}
	
	private void initializeAllVersions() {
		ArrayList<String> list = new ArrayList<String>(servicesMap.keySet());
		Collections.sort(list);
		allVersions = list.toArray(new String[list.size()]);
	}
	
	private void initializeEnabledVersions() {
		enabledVersions = new HashSet<String>();
		for (String version : allVersions) {
			if (servicePreferences.getBoolean(version, true)) {
				enabledVersions.add(version);
			}
		}		
	}
	
}
