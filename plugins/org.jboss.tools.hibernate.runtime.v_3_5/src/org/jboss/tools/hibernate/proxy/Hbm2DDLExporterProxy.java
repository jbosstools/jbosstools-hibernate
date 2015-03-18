package org.jboss.tools.hibernate.proxy;

import java.util.Hashtable;

import org.hibernate.tool.hbm2x.Hbm2DDLExporter;
import org.jboss.tools.hibernate.runtime.common.AbstractHbm2DDLExporterFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class Hbm2DDLExporterProxy extends AbstractHbm2DDLExporterFacade {
	
	private Hbm2DDLExporter target;

	public Hbm2DDLExporterProxy(
			IFacadeFactory facadeFactory, 
			Hbm2DDLExporter exporter) {
		super(facadeFactory, exporter);
		target = exporter;
	}

	public Hbm2DDLExporter getTarget() {
		return (Hbm2DDLExporter)super.getTarget();
	}

	@Override
	public void setExport(boolean export) {
		target.setExport(export);
	}

	@Override
	public Hashtable<Object, Object> getProperties() {
		return target.getProperties();
	}

}
