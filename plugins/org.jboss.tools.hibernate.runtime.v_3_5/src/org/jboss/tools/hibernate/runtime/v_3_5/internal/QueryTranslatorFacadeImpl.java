package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import org.hibernate.hql.QueryTranslator;
import org.jboss.tools.hibernate.runtime.common.AbstractQueryTranslatorFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class QueryTranslatorFacadeImpl extends AbstractQueryTranslatorFacade {
	
	public QueryTranslatorFacadeImpl(
			IFacadeFactory facadeFactory,
			QueryTranslator translator) {
		super(facadeFactory, translator);
	}
	
	public QueryTranslator getTarget() {
		return (QueryTranslator)super.getTarget();
	}

}
