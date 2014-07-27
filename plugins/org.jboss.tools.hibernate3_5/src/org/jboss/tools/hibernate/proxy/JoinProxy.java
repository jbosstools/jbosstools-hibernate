package org.jboss.tools.hibernate.proxy;

import java.util.HashSet;
import java.util.Iterator;

import org.hibernate.mapping.Join;
import org.hibernate.mapping.Property;
import org.jboss.tools.hibernate.spi.IJoin;
import org.jboss.tools.hibernate.spi.IProperty;

public class JoinProxy implements IJoin {
	
	private Join target = null;
	private HashSet<IProperty> properties = null;

	public JoinProxy(Join join) {
		target = join;
	}

	@Override
	public Iterator<IProperty> getPropertyIterator() {
		if (properties == null) {
			initializeProperties();
		}
		return properties.iterator();
	}
	
	@SuppressWarnings("unchecked")
	private void initializeProperties() {
		properties = new HashSet<IProperty>();
		Iterator<Property> origin = target.getPropertyIterator();
		while (origin.hasNext()) {
			properties.add(new PropertyProxy(origin.next()));
		}
	}

}
