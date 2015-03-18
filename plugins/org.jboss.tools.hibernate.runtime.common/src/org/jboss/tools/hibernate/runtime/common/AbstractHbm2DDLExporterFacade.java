package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;

public abstract class AbstractHbm2DDLExporterFacade 
extends AbstractFacade 
implements IHbm2DDLExporter {

	public AbstractHbm2DDLExporterFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

}
