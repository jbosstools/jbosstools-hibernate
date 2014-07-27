package org.jboss.tools.hibernate.proxy;

import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.jboss.tools.hibernate.spi.IPOJOClass;

public class POJOClassProxy implements IPOJOClass {
	
	private POJOClass target = null;
	
	public POJOClassProxy(POJOClass pojoClass) {
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
