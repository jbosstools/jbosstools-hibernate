package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IQuery;
import org.jboss.tools.hibernate.runtime.spi.ISession;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;

public abstract class AbstractSessionFacade 
extends AbstractFacade 
implements ISession {

	protected ISessionFactory targetFactory;

	public AbstractSessionFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public String getEntityName(Object o) {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getEntityName", 
				new Class[] { Object.class }, 
				new Object[] { o });
	}

	@Override
	public ISessionFactory getSessionFactory() {
		if (targetFactory == null) {
			Object targetSessionFactory = Util.invokeMethod(
					getTarget(), 
					"getSessionFactory", 
					new Class[] {}, 
					new Object[] {});
			if (targetSessionFactory != null) {
				targetFactory = getFacadeFactory().createSessionFactory(
						targetSessionFactory);
			}
		}
		return targetFactory;
	}

	@Override
	public IQuery createQuery(String queryString) {
		Object targetQuery = Util.invokeMethod(
				getTarget(), 
				"createQuery", 
				new Class[] { String.class }, 
				new Object[] { queryString });
		return getFacadeFactory().createQuery(targetQuery);
	}

	@Override
	public boolean isOpen() {
		return (boolean)Util.invokeMethod(
				getTarget(), 
				"isOpen", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public void close() {
		Util.invokeMethod(
				getTarget(), 
				"close", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public boolean contains(Object object) {
		return (boolean)Util.invokeMethod(
				getTarget(), 
				"contains", 
				new Class[] { Object.class }, 
				new Object[] { object });
	}

}
