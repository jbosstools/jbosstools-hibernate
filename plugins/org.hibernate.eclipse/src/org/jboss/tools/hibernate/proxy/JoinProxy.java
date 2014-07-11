package org.jboss.tools.hibernate.proxy;

import java.util.Iterator;

import org.hibernate.mapping.Join;
import org.hibernate.mapping.Property;
import org.jboss.tools.hibernate.spi.IJoin;

public class JoinProxy implements IJoin {
	
	private Join target = null;

	public JoinProxy(Join join) {
		target = join;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<Property> getPropertyIterator() {
		return target.getPropertyIterator();
	}

}
