package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.Value;
import org.jboss.tools.hibernate.proxy.PersistentClassProxy;
import org.jboss.tools.hibernate.runtime.common.AbstractValueFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;

public class ValueFacadeImpl extends AbstractValueFacade {
		
	public ValueFacadeImpl(IFacadeFactory facadeFactory, Value value) {
		super(facadeFactory, value);
	}

	public Value getTarget() {
		return (Value)super.getTarget();
	}

	@Override
	public void setAssociatedClass(IPersistentClass persistentClass) {
		assert getTarget() instanceof OneToMany;
		assert persistentClass instanceof PersistentClassProxy;
		((OneToMany)getTarget()).setAssociatedClass(((PersistentClassProxy)persistentClass).getTarget());
	}

}
