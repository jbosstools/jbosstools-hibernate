package org.jboss.tools.hibernate.runtime.common;

import java.io.Serializable;
import java.util.Set;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IQueryTranslator;
import org.jboss.tools.hibernate.runtime.spi.IType;

public abstract class AbstractQueryTranslatorFacade 
extends AbstractFacade 
implements IQueryTranslator {

	protected IType[] returnTypes = null;

	public AbstractQueryTranslatorFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public boolean isManipulationStatement() {
		return (boolean)Util.invokeMethod(
				getTarget(), 
				"isManipulationStatement", 
				new Class[] {}, 
				new Object[] {});
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<Serializable> getQuerySpaces() {
		return (Set<Serializable>)Util.invokeMethod(
				getTarget(), 
				"getQuerySpaces", 
				new Class[] {}, 
				new Object[] {});
	}

}
