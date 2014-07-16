package org.jboss.tools.hibernate.spi;

import java.util.Iterator;

public interface IJoin {

	Iterator<IProperty> getPropertyIterator();

}
