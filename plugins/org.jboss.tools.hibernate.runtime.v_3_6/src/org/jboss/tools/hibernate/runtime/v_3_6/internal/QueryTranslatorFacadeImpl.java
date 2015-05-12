package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.hql.QueryTranslator;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.runtime.common.AbstractQueryTranslatorFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IType;

public class QueryTranslatorFacadeImpl extends AbstractQueryTranslatorFacade {
	
	private IType[] returnTypes = null;

	public QueryTranslatorFacadeImpl(
			IFacadeFactory facadeFactory,
			QueryTranslator translator) {
		super(facadeFactory, translator);
	}

	public QueryTranslator getTarget() {
		return (QueryTranslator)super.getTarget();
	}

	@Override
	public boolean isManipulationStatement() {
		return getTarget().isManipulationStatement();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<Serializable> getQuerySpaces() {
		return getTarget().getQuerySpaces();
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
		ArrayList<IType> returnTypes = new ArrayList<IType>(origin.length);
		for (Type type : origin) {
			returnTypes.add(getFacadeFactory().createType(type));
		}
		this.returnTypes = returnTypes.toArray(new IType[origin.length]);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> collectSqlStrings() {
		return getTarget().collectSqlStrings();
	}

}
