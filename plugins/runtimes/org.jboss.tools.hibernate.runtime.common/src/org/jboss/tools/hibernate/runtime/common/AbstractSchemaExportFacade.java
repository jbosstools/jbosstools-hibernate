package org.jboss.tools.hibernate.runtime.common;

import java.util.List;

import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;

public abstract class AbstractSchemaExportFacade 
extends AbstractFacade 
implements ISchemaExport {

	public AbstractSchemaExportFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public void create() {
		Util.invokeMethod(
				getTarget(), 
				"create", 
				new Class[] { boolean.class, boolean.class }, 
				new Object[] { Boolean.FALSE, Boolean.TRUE });
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Throwable> getExceptions() {
		return (List<Throwable>)Util.invokeMethod(
				getTarget(), 
				"getExceptions", 
				new Class[] {}, 
				new Object[] {});
	}

}
