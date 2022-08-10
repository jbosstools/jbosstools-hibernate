package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IService;

public abstract class AbstractService implements IService {
	
	public IExporter createCfgExporter() {
		return createExporter(getCfgExporterClassName());
	}
	
	protected UsageTracker getUsageTracker() {
		return UsageTracker.getInstance();
	}
	
	protected String getCfgExporterClassName() {
		return "org.hibernate.tool.hbm2x.HibernateConfigurationExporter";
	}
	
}
