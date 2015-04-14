package org.jboss.tools.hibernate.proxy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.hql.spi.QueryTranslator;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IQueryTranslator;
import org.jboss.tools.hibernate.runtime.spi.IType;

public class QueryTranslatorProxy implements IQueryTranslator {
	
	private QueryTranslator target = null;
	private IType[] returnTypes = null;

	public QueryTranslatorProxy(
			IFacadeFactory facadeFactory,
			QueryTranslator translator) {
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
	public IType[] getReturnTypes() {
		if (returnTypes == null) {
			initializeReturnTypes();
		}
		return returnTypes;
	}
	
	private void initializeReturnTypes() {
		Type[] origin = target.getReturnTypes();
		ArrayList<IType> returnTypes = new ArrayList<IType>(origin.length);
		for (Type type : origin) {
			returnTypes.add(new TypeProxy(type));
		}
		this.returnTypes = returnTypes.toArray(new IType[origin.length]);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> collectSqlStrings() {
		return target.collectSqlStrings();
	}

}
