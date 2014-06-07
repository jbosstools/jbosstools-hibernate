package org.jboss.tools.hibernate.proxy;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.hibernate.hql.QueryTranslator;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.spi.IQueryTranslator;

public class QueryTranslatorProxy implements IQueryTranslator {
	
	private QueryTranslator target = null;

	public QueryTranslatorProxy(QueryTranslator translator) {
		target = translator;
	}

	@Override
	public boolean isManipulationStatement() {
		return target.isManipulationStatement();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<Serializable> getQuerySpaces() {
		return target.getQuerySpaces();
	}

	@Override
	public Type[] getReturnTypes() {
		return target.getReturnTypes();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> collectSqlStrings() {
		return target.collectSqlStrings();
	}

}
