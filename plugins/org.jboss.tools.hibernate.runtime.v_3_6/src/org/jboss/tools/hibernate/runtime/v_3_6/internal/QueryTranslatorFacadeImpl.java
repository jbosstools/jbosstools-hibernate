package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import java.util.List;

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

	@SuppressWarnings("unchecked")
	@Override
	public List<String> collectSqlStrings() {
		return getTarget().collectSqlStrings();
	}

}
