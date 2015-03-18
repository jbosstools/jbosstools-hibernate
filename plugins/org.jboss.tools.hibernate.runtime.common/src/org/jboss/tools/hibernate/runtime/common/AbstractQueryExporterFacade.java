package org.jboss.tools.hibernate.runtime.common;

import java.util.List;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IQueryExporter;

public abstract class AbstractQueryExporterFacade 
extends AbstractFacade 
implements IQueryExporter {

	public AbstractQueryExporterFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public void setQueries(List<String> queryStrings) {
		Util.invokeMethod(
				getTarget(), 
				"setQueries", 
				new Class[] { List.class }, 
				new Object[] { queryStrings });
	}

	@Override
	public void setFilename(String filename) {
		Util.invokeMethod(
				getTarget(), 
				"setFileName", 
				new Class[] { String.class }, 
				new Object[] { filename });
	}

}
