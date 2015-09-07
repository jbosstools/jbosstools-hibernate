package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IService;

public abstract class AbstractService implements IService {
	
	private UsageTracker usageTracker = null;
	
	protected UsageTracker getUsageTracker() {
		if (usageTracker == null) {
			usageTracker = UsageTracker.getInstance(this);
		}
		return usageTracker;
	}

}
