package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.query.QueryExporter;
import org.jboss.tools.hibernate.runtime.common.AbstractQueryExporterFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;

public class QueryExporterFacadeImpl extends AbstractQueryExporterFacade {

	public QueryExporterFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}
	
	@Override
	public void setFilename(String fileName) {
		((QueryExporter)getTarget()).getProperties().put(ExporterConstants.OUTPUT_FILE_NAME, fileName);
	}

}
