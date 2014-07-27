package org.jboss.tools.hibernate.proxy;

import java.util.Hashtable;

import org.hibernate.tool.hbm2x.Hbm2DDLExporter;
import org.jboss.tools.hibernate.spi.IHbm2DDLExporter;

public class Hbm2DDLExporterProxy implements IHbm2DDLExporter {
	
	private Hbm2DDLExporter target;

	public Hbm2DDLExporterProxy(Hbm2DDLExporter exporter) {
		target = exporter;
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
