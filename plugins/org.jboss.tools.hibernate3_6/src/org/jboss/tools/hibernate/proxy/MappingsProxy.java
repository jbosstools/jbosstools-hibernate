package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.Mappings;
import org.jboss.tools.hibernate.spi.IMappings;
import org.jboss.tools.hibernate.spi.IPersistentClass;

public class MappingsProxy implements IMappings {
	
	private Mappings target = null;

	public MappingsProxy(Mappings mappings) {
		target = mappings;
	}

	@Override
	public void addClass(IPersistentClass persistentClass) {
		assert persistentClass instanceof PersistentClassProxy;
		target.addClass(((PersistentClassProxy)persistentClass).getTarget());
	}

}
