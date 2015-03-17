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
	
	@Override
	public void setFilePattern(String filePattern) {
		Util.invokeMethod(
				getTarget(), 
				"setFilePattern", 
				new Class[] { String.class }, 
				new Object[] { filePattern });
	}

}
