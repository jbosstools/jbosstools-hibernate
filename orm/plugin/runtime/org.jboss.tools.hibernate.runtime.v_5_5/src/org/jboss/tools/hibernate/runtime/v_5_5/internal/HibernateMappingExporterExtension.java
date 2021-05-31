package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import java.io.File;

import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IExportPOJODelegate;
import org.jboss.tools.hibernate.runtime.v_5_5.internal.util.ConfigurationMetadataDescriptor;

public class HibernateMappingExporterExtension extends HibernateMappingExporter {

	private IFacadeFactory facadeFactory;
	private IExportPOJODelegate delegateExporter;

	public HibernateMappingExporterExtension(IFacadeFactory facadeFactory, IConfiguration cfg, File file) {
		this.facadeFactory = facadeFactory;
		setMetadataDescriptor(new ConfigurationMetadataDescriptor(cfg));
		setOutputDirectory(file);
	}

	public void setDelegate(IExportPOJODelegate delegate) {
		delegateExporter = delegate;
	}

}
