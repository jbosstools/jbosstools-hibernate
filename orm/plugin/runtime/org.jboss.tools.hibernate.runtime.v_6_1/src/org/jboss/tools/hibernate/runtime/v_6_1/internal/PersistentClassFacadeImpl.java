package org.jboss.tools.hibernate.runtime.v_6_1.internal;

import org.jboss.tools.hibernate.runtime.common.AbstractPersistentClassFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.Util;
import org.jboss.tools.hibernate.runtime.spi.IValue;

public class PersistentClassFacadeImpl extends AbstractPersistentClassFacade {

	public PersistentClassFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}
	
	@Override
	public void setKey(IValue keyValueFacade) {
		Util.invokeMethod(
				getTarget(), 
				"setIdentifier", 
				new Class[] { getKeyValueClass() }, 
				new Object[] { ((IFacade)keyValueFacade).getTarget() });
	}

}
