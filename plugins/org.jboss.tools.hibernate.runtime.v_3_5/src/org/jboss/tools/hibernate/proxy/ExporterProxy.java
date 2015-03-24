package org.jboss.tools.hibernate.proxy;

import org.hibernate.tool.hbm2x.Exporter;
import org.jboss.tools.hibernate.runtime.common.AbstractExporterFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class ExporterProxy extends AbstractExporterFacade {
	
	public ExporterProxy(IFacadeFactory facadeFactory, Exporter target) {
		super(facadeFactory, target);
	}

	public Exporter getTarget() {
		return (Exporter)super.getTarget();
	}

}
