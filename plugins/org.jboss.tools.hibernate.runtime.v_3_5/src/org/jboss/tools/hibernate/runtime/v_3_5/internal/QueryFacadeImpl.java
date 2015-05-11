package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import org.hibernate.Query;
import org.jboss.tools.hibernate.runtime.common.AbstractQueryFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class QueryFacadeImpl extends AbstractQueryFacade {
	
	public QueryFacadeImpl(
			IFacadeFactory facadeFactory, 
			Query query) {
		super(facadeFactory, query);
	}
	
}
