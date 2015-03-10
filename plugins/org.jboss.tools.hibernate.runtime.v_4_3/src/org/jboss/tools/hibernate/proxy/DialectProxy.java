package org.jboss.tools.hibernate.proxy;

import org.hibernate.dialect.Dialect;
import org.jboss.tools.hibernate.runtime.common.AbstractDialectFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class DialectProxy extends AbstractDialectFacade {
	
	private Dialect target;
	
	public DialectProxy(IFacadeFactory facadeFactory, Dialect dialect) {
		super(facadeFactory, dialect);
		target = dialect;
	}

	public Dialect getTarget() {
		return target;
	}
	
	@Override
	public String toString() {
		return getTarget().toString();
	}

	@Override
	public char openQuote() {
		return getTarget().openQuote();
	}

	@Override
	public char closeQuote() {
		return getTarget().closeQuote();
	}

}
