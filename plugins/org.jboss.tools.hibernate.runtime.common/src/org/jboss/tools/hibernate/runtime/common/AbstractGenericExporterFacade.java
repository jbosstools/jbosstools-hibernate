package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IGenericExporter;

public abstract class AbstractGenericExporterFacade 
extends AbstractFacade 
implements IGenericExporter {

	public AbstractGenericExporterFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}
	
}
