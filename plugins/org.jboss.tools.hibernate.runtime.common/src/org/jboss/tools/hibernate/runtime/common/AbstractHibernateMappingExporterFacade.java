package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;

public abstract class AbstractHibernateMappingExporterFacade 
extends AbstractFacade 
implements IHibernateMappingExporter {

	public AbstractHibernateMappingExporterFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

}
