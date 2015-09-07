package org.jboss.tools.hibernate.runtime.common;

import java.util.HashMap;
import java.util.Map;

import org.jboss.tools.hibernate.runtime.spi.IService;
import org.jboss.tools.hibernate.runtime.spi.ServiceLookup;
import org.jboss.tools.usage.event.UsageEventType;
import org.jboss.tools.usage.event.UsageReporter;

public class UsageTracker {
	
	private static Map<IService, UsageTracker> INSTANCES;
	
	private static Map<IService, UsageTracker> getInstances() {
		if (INSTANCES == null) {
			INSTANCES = new HashMap<IService, UsageTracker>();
		}
		return INSTANCES;
	}
	
	public static UsageTracker getInstance(IService service) {
		UsageTracker result = getInstances().get(service);
		if (result == null) {
			result = create(service);
			getInstances().put(service, result);
		}
		return result;
	}
	
	private static UsageTracker create(IService service) {
		return new UsageTracker(determineHibernateVersion(service));
	}
	
	private static String determineHibernateVersion(IService service) {
		for (String version : ServiceLookup.getVersions()) {
			if (service == ServiceLookup.findService(version)) {
				return version;
			}
		}
		return "unknown version";
	}
	
	private String hibernateVersion;
	private UsageReporter usageReporter;
	private UsageEventType newConfigurationEventType;
	
	private UsageTracker(String version) {
		hibernateVersion = version;
		initializeUsageReporter();
		initializeUsageEventTypes();
	}
	
	private void initializeUsageReporter() {
		usageReporter = UsageReporter.getInstance();
	}
	
	private void initializeUsageEventTypes() {
		newConfigurationEventType = new UsageEventType(
				"org.jboss.tools.hibernate.runtime", 
				hibernateVersion, 
				null, 
				"new configuration");
		usageReporter.registerEvent(newConfigurationEventType);
	}
	
	public void trackNewConfigurationEvent() {
		usageReporter.trackEvent(newConfigurationEventType.event());
	}

}
