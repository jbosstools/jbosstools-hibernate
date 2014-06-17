package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.Mappings;
import org.hibernate.mapping.PersistentClass;
import org.jboss.tools.hibernate.spi.IMappings;

public class MappingsProxy implements IMappings {
	
	private Mappings target = null;

	public MappingsProxy(Mappings mappings) {
		target = mappings;
	}

	@Override
	public void addClass(PersistentClass persistentClass) {
		target.addClass(persistentClass);
	}

}
