package org.jboss.tools.hibernate.proxy;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.runtime.common.AbstractQueryFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.v_3_6.internal.TypeFacadeImpl;

public class QueryProxy extends AbstractQueryFacade {
	
	private IType[] returnTypes = null;

	public QueryProxy(
			IFacadeFactory facadeFactory, 
			Query query) {
		super(facadeFactory, query);
	}

	public Query getTarget() {
		return (Query)super.getTarget();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> list() {
		return getTarget().list();
	}

	@Override
	public void setMaxResults(int value) {
		getTarget().setMaxResults(value);
	}

	@Override
	public void setParameter(int pos, Object value, IType type) {
		if (type instanceof TypeFacadeImpl) {
			getTarget().setParameter(pos, value, (Type)((TypeFacadeImpl)type).getTarget());
		}
	}

	@Override
	public void setParameterList(String name, List<Object> list, IType type) {
		if (type instanceof TypeFacadeImpl) {
			getTarget().setParameterList(name, list, (Type)((TypeFacadeImpl)type).getTarget());
		}
	}

	@Override
	public void setParameter(String name, Object value, IType type) {
		if (type instanceof TypeFacadeImpl) {
			getTarget().setParameter(name, value, (Type)((TypeFacadeImpl)type).getTarget());
		}
	}

	@Override
	public String[] getReturnAliases() {
		return getTarget().getReturnAliases();
	}

	@Override
	public IType[] getReturnTypes() {
		if (returnTypes == null) {
			initializeReturnTypes();
		}
		return returnTypes;
	}
	
	private void initializeReturnTypes() {
		Type[] origin = getTarget().getReturnTypes();
		ArrayList<IType> destination = new ArrayList<IType>(origin.length);
		for (Type type : origin) {
			destination.add(getFacadeFactory().createType(type));
		}
		this.returnTypes = destination.toArray(new IType[destination.size()]);
	}

}
