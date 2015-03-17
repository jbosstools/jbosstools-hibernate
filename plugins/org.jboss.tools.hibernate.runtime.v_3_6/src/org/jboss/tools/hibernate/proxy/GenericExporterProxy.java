package org.jboss.tools.hibernate.proxy;

import org.hibernate.tool.hbm2x.GenericExporter;
import org.jboss.tools.hibernate.runtime.common.AbstractGenericExporterFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class GenericExporterProxy extends AbstractGenericExporterFacade {
	
	private GenericExporter target;

	public GenericExporterProxy(
			IFacadeFactory facadeFactory, 
			GenericExporter exporter) {
		super(facadeFactory, exporter);
		this.target = exporter;
	}
	
	public GenericExporter getTarget() {
		return (GenericExporter)super.getTarget();
	}

	@Override
	public void setFilePattern(String filePattern) {
		target.setFilePattern(filePattern);
	}

	@Override
	public void setTemplateName(String templateName) {
		target.setTemplateName(templateName);
	}

	@Override
	public void setForEach(String foreach) {
		target.setForEach(foreach);
	}

	@Override
	public String getFilePattern() {
		return target.getFilePattern();
	}

	@Override
	public String getTemplateName() {
		return target.getTemplateName();
	}

}
