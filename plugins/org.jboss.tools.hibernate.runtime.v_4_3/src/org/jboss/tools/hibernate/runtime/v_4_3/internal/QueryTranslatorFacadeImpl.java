package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import java.util.List;

import org.hibernate.hql.spi.QueryTranslator;
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

	@Override
	public List<String> collectSqlStrings() {
		return getTarget().collectSqlStrings();
	}

}
