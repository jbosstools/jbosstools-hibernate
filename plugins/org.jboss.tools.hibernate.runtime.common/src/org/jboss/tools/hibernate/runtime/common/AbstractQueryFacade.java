package org.jboss.tools.hibernate.runtime.common;

import java.util.Collection;
import java.util.List;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IQuery;
import org.jboss.tools.hibernate.runtime.spi.IType;

public abstract class AbstractQueryFacade 
extends AbstractFacade 
implements IQuery {

	public AbstractQueryFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Object> list() {
		return (List<Object>)Util.invokeMethod(
				getTarget(), 
				"list", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public void setMaxResults(int value) {
		Util.invokeMethod(
				getTarget(), 
				"setMaxResults", 
				new Class[] { int.class }, 
				new Object[] {});
	}

	@Override
	public void setParameter(int pos, Object value, IType type) {
		if (type instanceof IFacade) {
			Object typeTarget = Util.invokeMethod(
					getTarget(), 
					"getTarget", 
					new Class[] {}, 
					new Object[] {});
			Util.invokeMethod(
					getTarget(), 
					"setParameter", 
					new Class[] { int.class,  Object.class, getTypeClass() }, 
					new Object[] { pos, value, typeTarget });
		}
	}
	
	@Override
	public void setParameterList(String name, List<Object> list, IType type) {
		if (type instanceof IFacade) {
			Object typeTarget = Util.invokeMethod(
					getTarget(), 
					"getTarget", 
					new Class[] {}, 
					new Object[] {});
			Util.invokeMethod(
					getTarget(), 
					"setParameterList", 
					new Class[] { String.class,  Collection.class, getTypeClass() }, 
					new Object[] { name, list, typeTarget });
		}
	}

	protected Class<?> getTypeClass() {
		return (Class<?>)Util.getClass(
				getTypeClassName(), 
				getFacadeFactoryClassLoader());
	}
	
	protected String getTypeClassName() {
		return "org.hibernate.type.Type";
	}

}
