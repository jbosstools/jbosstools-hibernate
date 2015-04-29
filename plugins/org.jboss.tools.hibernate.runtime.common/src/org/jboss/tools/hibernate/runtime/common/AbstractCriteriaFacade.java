package org.jboss.tools.hibernate.runtime.common;

import java.util.List;

import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public abstract class AbstractCriteriaFacade 
extends AbstractFacade 
implements ICriteria {

	public AbstractCriteriaFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	public ICriteria createCriteria(String associationPath, String alias) {
		Object targetCriteria = Util.invokeMethod(
				getTarget(), 
				"createCriteria", 
				new Class[] { String.class,  String.class }, 
				new Object[] { associationPath, alias });
		return getFacadeFactory().createCriteria(targetCriteria);
	}

	@Override
	public void setMaxResults(int intValue) {
		Util.invokeMethod(
				getTarget(), 
				"setMaxResults", 
				new Class[] { int.class }, 
				new Object[] { intValue });
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> list() {
		return (List<Object>)Util.invokeMethod(
				getTarget(), 
				"setMaxResults", 
				new Class[] {}, 
				new Object[] {});
	}

}