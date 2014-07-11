package org.jboss.tools.hibernate.spi;

import java.util.Iterator;

import org.hibernate.mapping.Property;

public interface IJoin {

	Iterator<Property> getPropertyIterator();

}
