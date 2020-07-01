package org.jboss.tools.hibernate.runtime.v_4_0.internal.util;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Mappings;

public class DummyMappings {
	
	public final static Mappings INSTANCE = createInstance();
	
	private static Mappings createInstance() {
		return new Configuration().createMappings();
	}

}
