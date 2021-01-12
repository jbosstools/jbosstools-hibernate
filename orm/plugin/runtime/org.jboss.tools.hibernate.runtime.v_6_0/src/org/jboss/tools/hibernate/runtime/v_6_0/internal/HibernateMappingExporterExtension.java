package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import java.io.File;

import org.hibernate.tool.internal.export.hbm.HbmExporter;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IExportPOJODelegate;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.util.ConfigurationMetadataDescriptor;

public class HibernateMappingExporterExtension extends HbmExporter {
	
	private IExportPOJODelegate delegateExporter;

	public HibernateMappingExporterExtension(IFacadeFactory facadeFactory, IConfiguration cfg, File file) {
		getProperties().put(
				METADATA_DESCRIPTOR, 
				new ConfigurationMetadataDescriptor((ConfigurationFacadeImpl)cfg));
		if (file != null) {
			getProperties().put(OUTPUT_FILE_NAME, file);
		}
	}
	
	public void setDelegate(IExportPOJODelegate delegate) {
		delegateExporter = delegate;
	}

}
