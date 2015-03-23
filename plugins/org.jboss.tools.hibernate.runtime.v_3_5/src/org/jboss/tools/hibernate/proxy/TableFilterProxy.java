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

	public TableFilter getTarget() {
		return target;
	}

}
