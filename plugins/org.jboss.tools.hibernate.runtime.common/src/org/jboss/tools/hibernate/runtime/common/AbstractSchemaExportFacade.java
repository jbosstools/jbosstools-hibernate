package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;

public abstract class AbstractSchemaExportFacade 
extends AbstractFacade 
implements ISchemaExport {

	public AbstractSchemaExportFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

}
