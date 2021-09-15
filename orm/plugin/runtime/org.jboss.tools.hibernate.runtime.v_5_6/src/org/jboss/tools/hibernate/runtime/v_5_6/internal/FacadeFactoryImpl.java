package org.jboss.tools.hibernate.runtime.v_5_6.internal;

import org.jboss.tools.hibernate.runtime.common.AbstractFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;

public class FacadeFactoryImpl extends AbstractFacadeFactory {

	@Override
	public ClassLoader getClassLoader() {
		return FacadeFactoryImpl.class.getClassLoader();
	}

	@Override
	public IConfiguration createConfiguration(Object target) {
		return new ConfigurationFacadeImpl(this, target);
	}
	
	@Override
	public IPersistentClass createSpecialRootClass(IProperty property) {
		// TODO Auto-generated method stub
		return null;
	}

}
