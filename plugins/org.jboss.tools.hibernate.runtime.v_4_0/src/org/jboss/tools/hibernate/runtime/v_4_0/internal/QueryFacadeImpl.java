package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import java.util.ArrayList;

import org.hibernate.Query;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.runtime.common.AbstractQueryFacade;
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
