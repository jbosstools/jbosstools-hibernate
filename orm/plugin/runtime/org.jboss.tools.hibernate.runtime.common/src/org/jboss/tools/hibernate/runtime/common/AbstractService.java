package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IService;

public abstract class AbstractService implements IService {
	
	protected UsageTracker getUsageTracker() {
		return UsageTracker.getInstance();
	}
	
}
