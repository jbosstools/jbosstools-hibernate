package org.jboss.tools.hibernate.proxy;

import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IPOJOClass;

public class POJOClassFacadeImpl implements IPOJOClass {
	
	private POJOClass target = null;
	
	public POJOClassFacadeImpl(IFacadeFactory facadeFactory, POJOClass pojoClass) {
		target = pojoClass;
	}

	@Override
	public String getQualifiedDeclarationName() {
		return target.getQualifiedDeclarationName();
	}
	
	POJOClass getTarget() {
		return target;
	}

}
