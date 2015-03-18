package org.jboss.tools.hibernate.runtime.common;

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

}
