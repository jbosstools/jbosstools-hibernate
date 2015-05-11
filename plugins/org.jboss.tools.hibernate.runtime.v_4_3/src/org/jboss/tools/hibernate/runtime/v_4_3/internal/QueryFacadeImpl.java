package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import org.hibernate.Query;
import org.jboss.tools.hibernate.runtime.common.AbstractQueryFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class QueryFacadeImpl extends AbstractQueryFacade {
	
	public QueryFacadeImpl(
			IFacadeFactory facadeFactory, 
			Query query) {
		super(facadeFactory, query);
	}
	
	public Query getTarget() {
		return (Query)super.getTarget();
	}

}
