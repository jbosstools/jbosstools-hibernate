package org.jboss.tools.hibernate.proxy;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.spi.IQuery;

public class QueryProxy implements IQuery {
	
	private Query target = null;

	public QueryProxy(Query query) {
		target = query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> list() {
		return target.list();
	}

	@Override
	public void setMaxResults(int value) {
		target.setMaxResults(value);
	}

	@Override
	public void setParameter(int pos, Object value, Type type) {
		target.setParameter(pos, value, type);
	}

	@Override
	public void setParameterList(String name, List<Object> list, Type type) {
		target.setParameterList(name, list, type);
	}

	@Override
	public void setParameter(String name, Object value, Type type) {
		target.setParameter(name, value, type);
	}

	@Override
	public String[] getReturnAliases() {
		return target.getReturnAliases();
	}

	@Override
	public Type[] getReturnTypes() {
		return target.getReturnTypes();
	}

}
