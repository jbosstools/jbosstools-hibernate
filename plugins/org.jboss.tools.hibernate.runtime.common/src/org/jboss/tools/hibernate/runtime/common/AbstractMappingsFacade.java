package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IMappings;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;

public abstract class AbstractMappingsFacade 
extends AbstractFacade 
implements IMappings {

	public AbstractMappingsFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public void addClass(IPersistentClass persistentClass) {
		assert persistentClass instanceof IFacade;
		Util.invokeMethod(
				getTarget(), 
				"addClass", 
				new Class[] { getPersistentClassClass() }, 
				new Object[] { getPersistentClassTarget(persistentClass) });
	}
	
	private Object getPersistentClassTarget(IPersistentClass persistentClass) {
		return Util.invokeMethod(
				persistentClass, 
				"getTarget", 
				new Class[] {}, 
				new Object[] {});
	}
	
	protected Class<?> getPersistentClassClass() {
		return Util.getClass(
				getPersistentClassClassName(), 
				getFacadeFactoryClassLoader());
	}
	
	protected String getPersistentClassClassName() {
		return "org.hibernate.mapping.PersistentClass";
	}

}
