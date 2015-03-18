package org.jboss.tools.hibernate.proxy;

import org.hibernate.tool.hbm2x.QueryExporter;
import org.jboss.tools.hibernate.runtime.common.AbstractQueryExporterFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class QueryExporterProxy extends AbstractQueryExporterFacade {
	
	public QueryExporterProxy(
			IFacadeFactory facadeFactory, 
			QueryExporter exporter) {
		super(facadeFactory, exporter);
	}

	public QueryExporter getTarget() {
		return (QueryExporter)super.getTarget();
	}

	@Override
	public void setFilename(String filename) {
		getTarget().setFilename(filename);
	}

}
