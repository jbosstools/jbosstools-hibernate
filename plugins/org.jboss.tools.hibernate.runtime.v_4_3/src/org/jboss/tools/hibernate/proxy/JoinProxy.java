package org.jboss.tools.hibernate.proxy;

import java.util.HashSet;
import java.util.Iterator;

import org.hibernate.mapping.Join;
import org.hibernate.mapping.Property;
import org.jboss.tools.hibernate.runtime.common.AbstractJoinFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IProperty;

public class JoinProxy extends AbstractJoinFacade {
	
	private HashSet<IProperty> properties = null;

	public JoinProxy(
			IFacadeFactory facadeFactory,
			Join join) {
		super(facadeFactory, join);
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
		Iterator<Property> origin = ((Join)getTarget()).getPropertyIterator();
		while (origin.hasNext()) {
			properties.add(new PropertyProxy(getFacadeFactory(), origin.next()));
		}
	}

}
