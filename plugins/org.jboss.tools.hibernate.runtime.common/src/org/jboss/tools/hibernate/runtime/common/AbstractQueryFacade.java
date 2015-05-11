package org.jboss.tools.hibernate.runtime.common;

import java.util.List;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IQuery;

public abstract class AbstractQueryFacade 
extends AbstractFacade 
implements IQuery {

	public AbstractQueryFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Object> list() {
		return (List<Object>)Util.invokeMethod(
				getTarget(), 
				"list", 
				new Class[] {}, 
				new Object[] {});
	}

}
