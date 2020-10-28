package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import java.util.List;

import javax.persistence.TemporalType;

import org.hibernate.metamodel.model.domain.AllowableParameterType;
import org.hibernate.query.Query;
import org.jboss.tools.hibernate.runtime.common.AbstractQueryFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;

public class QueryFacadeImpl extends AbstractQueryFacade {

	public QueryFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}
	
	@Override
	public void setParameter(int position, Object value, IType type) {
		Object typeTarget = ((IFacade)type).getTarget();
		if (typeTarget instanceof AllowableParameterType<?>) {
			((Query<?>)getTarget()).setParameter(position, value, (AllowableParameterType<?>)typeTarget);
		} else if (typeTarget instanceof TemporalType) {
			((Query<?>)getTarget()).setParameter(position, value, (TemporalType)typeTarget); 
		} else {
			((Query<?>)getTarget()).setParameter(position, value);
		}
	}

	@Override
	public void setParameter(String name, Object value, IType type) {
		Object typeTarget = ((IFacade)type).getTarget();
		if (typeTarget instanceof AllowableParameterType<?>) {
			((Query<?>)getTarget()).setParameter(name, value, (AllowableParameterType<?>)typeTarget);
		} else if (typeTarget instanceof TemporalType) {
			((Query<?>)getTarget()).setParameter(name, value, (TemporalType)typeTarget); 
		} else {
			((Query<?>)getTarget()).setParameter(name, value);
		}
	}
	
	@Override
	public void setParameterList(String name, List<Object> list, IType type) {
		Object typeTarget = ((IFacade)type).getTarget();
		if (typeTarget instanceof AllowableParameterType<?>) {
			((Query<?>)getTarget()).setParameter(name, list, (AllowableParameterType<?>)typeTarget);
		} else {
			((Query<?>)getTarget()).setParameter(name, list);
		}
	}
	
	// TODO JBIDE-27532: Review the Query Page Viewer as the used APIs have completely changed
	@Override
	@Deprecated
	public String[] getReturnAliases() {
		return new String[0];
	}

}
