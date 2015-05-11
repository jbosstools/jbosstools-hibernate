package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.runtime.common.AbstractQueryFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;

public class QueryFacadeImpl extends AbstractQueryFacade {
	
	private IType[] returnTypes = null;

	public QueryFacadeImpl(
			IFacadeFactory facadeFactory, 
			Query query) {
		super(facadeFactory, query);
	}

	public Query getTarget() {
		return (Query)super.getTarget();
	}

	@Override
	public void setParameter(int pos, Object value, IType type) {
		if (type instanceof IFacade) {
			getTarget().setParameter(pos, value, (Type)((IFacade)type).getTarget());
		}
	}

	@Override
	public void setParameterList(String name, List<Object> list, IType type) {
		if (type instanceof IFacade) {
			getTarget().setParameterList(name, list, (Type)((IFacade)type).getTarget());
		}
	}

	@Override
	public void setParameter(String name, Object value, IType type) {
		if (type instanceof IFacade) {
			getTarget().setParameter(name, value, (Type)((IFacade)type).getTarget());
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
