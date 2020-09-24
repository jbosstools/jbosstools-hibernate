package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.common.GenericExporter;
import org.jboss.tools.hibernate.runtime.common.AbstractGenericExporterFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;

public class GenericExporterFacadeImpl extends AbstractGenericExporterFacade {

	public GenericExporterFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}
	
	@Override 
	public void setFilePattern(String filePattern) {
		((GenericExporter)getTarget()).getProperties().setProperty(
				ExporterConstants.FILE_PATTERN, 
				filePattern);
	}

}
