package org.jboss.tools.hibernate.proxy;

import org.hibernate.dialect.Dialect;
import org.jboss.tools.hibernate.runtime.spi.IDialect;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class DialectProxy implements IDialect {
	
	private Dialect target;
	
	public DialectProxy(IFacadeFactory facadeFactory, Dialect dialect) {
		target = dialect;
	}

	public Dialect getTarget() {
		return target;
	}
	
	@Override
	public String toString() {
		return target.toString();
	}

	@Override
	public char openQuote() {
		return target.openQuote();
	}

	@Override
	public char closeQuote() {
		return target.closeQuote();
	}

}
