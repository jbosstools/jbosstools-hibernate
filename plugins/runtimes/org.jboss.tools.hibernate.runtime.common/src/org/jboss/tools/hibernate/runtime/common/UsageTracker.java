package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.common.internal.HibernateRuntimeCommon;
import org.jboss.tools.usage.event.UsageEventType;
import org.jboss.tools.usage.event.UsageReporter;

public class UsageTracker {
	
	private static UsageTracker INSTANCE;
	
	public static UsageTracker getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new UsageTracker();
		}
		return INSTANCE;
	}
	
	private UsageEventType newConfigurationEventType;
	
	private UsageTracker() {
		initializeUsageEventType();
	}
	
	private void initializeUsageEventType() {
		newConfigurationEventType = new UsageEventType(
				"hibernate", 
				UsageEventType.getVersion(HibernateRuntimeCommon.getDefault()), 
				null, 
				"new configuration",
				"Hibernate runtime version");
		UsageReporter.getInstance().registerEvent(newConfigurationEventType);
	}
	
	public void trackNewConfigurationEvent(String hibernateVersion) {
		UsageReporter.getInstance().trackEvent(newConfigurationEventType.event(hibernateVersion));
	}

}
