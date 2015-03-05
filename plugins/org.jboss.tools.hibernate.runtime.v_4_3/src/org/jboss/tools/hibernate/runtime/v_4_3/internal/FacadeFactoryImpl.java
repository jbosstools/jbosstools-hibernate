package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import org.jboss.tools.hibernate.runtime.common.AbstractFacadeFactory;

public class FacadeFactoryImpl extends AbstractFacadeFactory {
	
	public ClassLoader getClassLoader() {
		return FacadeFactoryImpl.class.getClassLoader();
	}
	
}
