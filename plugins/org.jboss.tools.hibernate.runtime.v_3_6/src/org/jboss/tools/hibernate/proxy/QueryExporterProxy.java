package org.jboss.tools.hibernate.proxy;

import java.util.List;

import org.hibernate.tool.hbm2x.QueryExporter;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IQueryExporter;

public class QueryExporterProxy implements IQueryExporter {
	
	private QueryExporter target;

	public QueryExporterProxy(
			IFacadeFactory facadeFactory, 
			QueryExporter exporter) {
		target = exporter;
	}

	@Override
	public void setQueries(List<String> queryStrings) {
		target.setQueries(queryStrings);
	}

	@Override
	public void setFilename(String filename) {
		target.setFilename(filename);
	}

}
