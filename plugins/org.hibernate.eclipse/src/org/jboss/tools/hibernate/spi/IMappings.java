package org.jboss.tools.hibernate.spi;

import org.hibernate.mapping.PersistentClass;

public interface IMappings {

	void addClass(PersistentClass persistentClass);

}
