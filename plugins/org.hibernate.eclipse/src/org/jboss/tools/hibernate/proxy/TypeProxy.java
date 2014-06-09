package org.jboss.tools.hibernate.proxy;

import org.hibernate.type.Type;
import org.jboss.tools.hibernate.spi.IType;

public class TypeProxy implements IType {
	
	private Type target = null;

	public TypeProxy(Type type) {
		target = type;
	}

}
