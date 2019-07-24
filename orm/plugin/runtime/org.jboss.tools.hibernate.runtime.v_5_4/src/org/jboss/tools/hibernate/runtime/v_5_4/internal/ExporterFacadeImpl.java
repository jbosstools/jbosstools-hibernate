package org.jboss.tools.hibernate.runtime.v_5_4.internal;

import org.hibernate.tool.hbm2x.Exporter;
import org.jboss.tools.hibernate.runtime.common.AbstractExporterFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.v_5_4.internal.util.ConfigurationMetadataDescriptor;

public class ExporterFacadeImpl extends AbstractExporterFacade {

	public ExporterFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}
	
	@Override
	public void setConfiguration(IConfiguration configuration) {
		Exporter exporter = (Exporter)getTarget();
		setCustomProperties(configuration.getProperties());
		exporter.setMetadataDescriptor(new ConfigurationMetadataDescriptor(configuration));
	}

}
