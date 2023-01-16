package org.jboss.tools.hibernate.runtime.v_6_2.internal;

import java.io.File;

import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.java.POJOClass;
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

	@Override
	protected String getPOJOClassClassName() {
		return POJOClass.class.getName();
	}

}
