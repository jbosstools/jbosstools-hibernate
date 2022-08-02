package org.jboss.tools.hibernate.runtime.v_6_1.internal;

import java.util.List;

import org.hibernate.query.Query;
import org.jboss.tools.hibernate.runtime.common.AbstractQueryFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;

public class QueryFacadeImpl extends AbstractQueryFacade {

	public QueryFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}
	
	@Override
	public void setParameter(int position, Object value, IType type) {
		((Query<?>)getTarget()).setParameter(position, value);
	}

	@Override
	public void setParameter(String name, Object value, IType type) {
		((Query<?>)getTarget()).setParameter(name, value);
	}
	
	@Override
	public void setParameterList(String name, List<Object> list, IType type) {
		((Query<?>)getTarget()).setParameter(name, list);
	}
	
	// TODO JBIDE-27532: Review the Query Page Viewer as the used APIs have completely changed
	@Override
	@Deprecated
	public String[] getReturnAliases() {
		return new String[0];
	}

	// TODO JBIDE-27532: Review the Query Page Viewer as the used APIs have completely changed
	@Override
	@Deprecated
	public IType[] getReturnTypes() {
		return new IType[0];
	}

}
