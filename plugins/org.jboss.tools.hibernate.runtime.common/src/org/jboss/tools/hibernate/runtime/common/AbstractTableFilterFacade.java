package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;

public abstract class AbstractTableFilterFacade 
extends AbstractFacade 
implements ITableFilter {

	public AbstractTableFilterFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public void setExclude(Boolean exclude) {
		Util.invokeMethod(
				getTarget(), 
				"setExclude", 
				new Class[] { Boolean.class }, 
				new Object[] { exclude });
	}

	@Override
	public void setMatchCatalog(String catalog) {
		Util.invokeMethod(
				getTarget(), 
				"setMatchCatalog", 
				new Class[] { String.class }, 
				new Object[] { catalog });
	}

	@Override
	public void setMatchSchema(String schema) {
		Util.invokeMethod(
				getTarget(), 
				"setMatchSchema", 
				new Class[] { String.class }, 
				new Object[] { schema });
	}

	@Override
	public void setMatchName(String name) {
		Util.invokeMethod(
				getTarget(), 
				"setMatchName", 
				new Class[] { String.class }, 
				new Object[] { name });
	}
	
	@Override
	public Boolean getExclude() {
		return (Boolean)Util.invokeMethod(
				getTarget(), 
				"getExclude", 
				new Class[] {}, 
				new Object[] {});
	}



}
