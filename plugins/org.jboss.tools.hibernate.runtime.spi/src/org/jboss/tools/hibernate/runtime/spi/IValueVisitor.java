package org.jboss.tools.hibernate.runtime.spi;

public interface IValueVisitor {
	
	public Object accept(IValue value);

}
