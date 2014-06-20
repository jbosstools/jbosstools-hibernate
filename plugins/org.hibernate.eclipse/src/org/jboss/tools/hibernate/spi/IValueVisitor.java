package org.jboss.tools.hibernate.spi;

public interface IValueVisitor {
	
	public Object accept(IValue value);

}
