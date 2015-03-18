package org.jboss.tools.hibernate.proxy;

import java.util.Hashtable;

import org.hibernate.tool.hbm2x.Hbm2DDLExporter;
import org.jboss.tools.hibernate.runtime.common.AbstractHbm2DDLExporterFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class Hbm2DDLExporterProxy extends AbstractHbm2DDLExporterFacade {
	
	public Hbm2DDLExporterProxy(
			IFacadeFactory facadeFactory, 
			Hbm2DDLExporter exporter) {
		super(facadeFactory, exporter);
	}
	
	public Hbm2DDLExporter getTarget() {
		return (Hbm2DDLExporter)super.getTarget();
	}

	@Override
	public Hashtable<Object, Object> getProperties() {
		return getTarget().getProperties();
	}

}
