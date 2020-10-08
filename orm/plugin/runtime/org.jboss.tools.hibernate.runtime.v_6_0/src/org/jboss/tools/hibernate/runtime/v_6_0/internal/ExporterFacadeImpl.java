package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.jboss.tools.hibernate.runtime.common.AbstractExporterFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.util.ConfigurationMetadataDescriptor;

public class ExporterFacadeImpl extends AbstractExporterFacade {

	public ExporterFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}

	@Override
	public void setConfiguration(IConfiguration configuration) {
		Exporter exporter = (Exporter)getTarget();
		setCustomProperties(configuration.getProperties());
		exporter.getProperties().put(
				ExporterConstants.METADATA_DESCRIPTOR, 
				new ConfigurationMetadataDescriptor((ConfigurationFacadeImpl)configuration));
	}

	@Override
	protected String getHibernateConfigurationExporterClassName() {
		return "org.hibernate.tool.internal.export.cfg.CfgExporter";
	}

}
