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

	private static Map<String, IService> SERVICES_MAP = null;
	private static String[] ALL_VERSIONS = null;
	private static Set<String> ENABLED_VERSIONS = null;
	
	public static RuntimeServiceManager getInstance() {
		return INSTANCE;
	}
	
	private Preferences servicePreferences = null;
	
	private RuntimeServiceManager() {	
		initializePreferences();
	}
	
	public void enableService(String version, boolean enabled) {
		servicePreferences.putBoolean(version, enabled);
		if (enabled) {
			getEnabledVersons().add(version);
		} else {
			getEnabledVersons().remove(version);
		}
		try {
			servicePreferences.flush();
		} catch (BackingStoreException bse) {
			throw new RuntimeException(bse);
		}
	}

	public IService findService(String hibernateVersion) {
		if (SERVICES_MAP == null) {
			initialize();
		}
		return SERVICES_MAP.get(hibernateVersion);
	}
	
	public String[] getAllVersions() {
		if (ALL_VERSIONS == null) {
			initialize();
		}
		return Arrays.copyOf(ALL_VERSIONS, ALL_VERSIONS.length);
	}
	
	public IService getDefaultService() {
		return findService(getDefaultVersion());
	}
	
	public String getDefaultVersion() {
		if (ALL_VERSIONS == null) {
			initialize();
		}
		return ALL_VERSIONS[ALL_VERSIONS.length - 1];
	}
	
	public boolean isServiceEnabled(String version) {
		return getEnabledVersons().contains(version);
	}
	
	private Set<String> getEnabledVersons() {
		if (ENABLED_VERSIONS == null) {
			initialize();
		}
		return ENABLED_VERSIONS;
	}
	
	private void initialize() {
		initializeServicesMap();
		initializeAllVersions();
		initializeEnabledVersions();
	}
	
	private void initializePreferences() {
		servicePreferences = InstanceScope.INSTANCE.getNode(SERVICES_EXTENSION_ID);
	}
	
	private void initializeServicesMap() {
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
	
	private void initializeAllVersions() {
		ArrayList<String> list = new ArrayList<String>(SERVICES_MAP.keySet());
		Collections.sort(list);
		ALL_VERSIONS = list.toArray(new String[list.size()]);
	}
	
	private void initializeEnabledVersions() {
		ENABLED_VERSIONS = new HashSet<String>();
		for (String version : ALL_VERSIONS) {
			if (servicePreferences.getBoolean(version, true)) {
				ENABLED_VERSIONS.add(version);
			}
		}		
	}
	
}
