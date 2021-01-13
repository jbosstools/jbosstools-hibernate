package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import java.io.File;

import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.jboss.tools.hibernate.runtime.common.AbstractHibernateMappingExporterFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;

public class HibernateMappingExporterFacadeImpl extends AbstractHibernateMappingExporterFacade {

	public HibernateMappingExporterFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}

	@Override
	public File getOutputDirectory() {
		return (File)((Exporter)getTarget()).getProperties().get(ExporterConstants.DESTINATION_FOLDER);
	}
	
	@Override
	public void setOutputDirectory(File directory) {
		((Exporter)getTarget()).getProperties().put(ExporterConstants.DESTINATION_FOLDER, directory);
	}


}
