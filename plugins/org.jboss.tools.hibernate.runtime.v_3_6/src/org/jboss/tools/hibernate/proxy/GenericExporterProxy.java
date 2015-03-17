package org.jboss.tools.hibernate.proxy;

import org.hibernate.tool.hbm2x.GenericExporter;
import org.jboss.tools.hibernate.runtime.common.AbstractGenericExporterFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class GenericExporterProxy extends AbstractGenericExporterFacade {
	
	public GenericExporterProxy(
			IFacadeFactory facadeFactory, 
			GenericExporter exporter) {
		super(facadeFactory, exporter);
	}
	
	public GenericExporter getTarget() {
		return (GenericExporter)super.getTarget();
	}

	@Override
	public void setForEach(String foreach) {
		getTarget().setForEach(foreach);
	}

	@Override
	public String getFilePattern() {
		return getTarget().getFilePattern();
	}

	@Override
	public String getTemplateName() {
		return getTarget().getTemplateName();
	}

}
