package org.jboss.tools.hibernate.proxy;

import org.hibernate.dialect.Dialect;
import org.jboss.tools.hibernate.runtime.spi.IDialect;

public class DialectProxy implements IDialect {
	
	private Dialect target;
	
	public DialectProxy(Dialect dialect) {
		target = dialect;
	}

	Dialect getTarget() {
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
