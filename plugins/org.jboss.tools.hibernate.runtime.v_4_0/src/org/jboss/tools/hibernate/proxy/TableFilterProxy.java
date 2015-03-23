package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.reveng.TableFilter;
import org.jboss.tools.hibernate.runtime.common.AbstractTableFilterFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;

public class TableFilterProxy extends AbstractTableFilterFacade {
	
	private TableFilter target = null;

	public TableFilterProxy(
			IFacadeFactory facadeFactory, 
			TableFilter tableFilter) {
		super(facadeFactory, tableFilter);
		target = tableFilter;
	}

	@Override
	public String getMatchSchema() {
		return getTarget().getMatchSchema();
	}

	@Override
	public String getMatchName() {
		return getTarget().getMatchName();
	}

	public TableFilter getTarget() {
		return target;
	}

}
