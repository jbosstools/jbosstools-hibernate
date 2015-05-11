package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import org.hibernate.Query;
import org.jboss.tools.hibernate.runtime.common.AbstractQueryFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;

public class QueryFacadeImpl extends AbstractQueryFacade {
	
	public QueryFacadeImpl(
			IFacadeFactory facadeFactory, 
			Query query) {
		super(facadeFactory, query);
	}

	public Query getTarget() {
		return (Query)super.getTarget();
	}

	@Override
	public IType[] getReturnTypes() {
		if (returnTypes == null) {
			initializeReturnTypes();
		}
		return returnTypes;
	}
	
}
