package org.jboss.tools.hibernate.proxy;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IQuery;
import org.jboss.tools.hibernate.runtime.spi.IType;

public class QueryProxy implements IQuery {
	
	private Query target = null;
	private IType[] returnTypes = null;

	public QueryProxy(
			IFacadeFactory facadeFactory, 
			Query query) {
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
	public void setParameter(int pos, Object value, IType type) {
		if (type instanceof TypeProxy) {
			target.setParameter(pos, value, ((TypeProxy)type).getTarget());
		}
	}

	@Override
	public void setParameterList(String name, List<Object> list, IType type) {
		if (type instanceof TypeProxy) {
			target.setParameterList(name, list, ((TypeProxy)type).getTarget());
		}
	}

	@Override
	public void setParameter(String name, Object value, IType type) {
		if (type instanceof TypeProxy) {
			target.setParameter(name, value, ((TypeProxy)type).getTarget());
		}
	}

	@Override
	public String[] getReturnAliases() {
		return target.getReturnAliases();
	}

	@Override
	public IType[] getReturnTypes() {
		if (returnTypes == null) {
			initializeReturnTypes();
		}
		return returnTypes;
	}
	
	private void initializeReturnTypes() {
		Type[] origin = target.getReturnTypes();
		ArrayList<IType> destination = new ArrayList<IType>(origin.length);
		for (Type type : origin) {
			destination.add(new TypeProxy(type));
		}
		this.returnTypes = destination.toArray(new IType[destination.size()]);
	}

}
