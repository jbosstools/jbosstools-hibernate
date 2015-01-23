package org.jboss.tools.hibernate.runtime.spi;

import java.util.Iterator;

public interface IJoin {

	Iterator<IProperty> getPropertyIterator();

}
