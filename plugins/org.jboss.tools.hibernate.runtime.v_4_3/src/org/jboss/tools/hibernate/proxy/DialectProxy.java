package org.jboss.tools.hibernate.proxy;

import org.hibernate.dialect.Dialect;
import org.jboss.tools.hibernate.runtime.common.AbstractDialectFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class DialectProxy extends AbstractDialectFacade {
	
	public DialectProxy(IFacadeFactory facadeFactory, Dialect dialect) {
		super(facadeFactory, dialect);
	}

	public Dialect getTarget() {
		return (Dialect)super.getTarget();
	}
	
	@Override
	public char closeQuote() {
		return getTarget().closeQuote();
	}

}
