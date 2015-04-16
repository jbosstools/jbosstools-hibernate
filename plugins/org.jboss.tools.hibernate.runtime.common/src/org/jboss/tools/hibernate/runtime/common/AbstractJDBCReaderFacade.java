package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IJDBCReader;

public abstract class AbstractJDBCReaderFacade 
extends AbstractFacade 
implements IJDBCReader {

	public AbstractJDBCReaderFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

}
