package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.tool.internal.reveng.strategy.TableFilter;
import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.GenericFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ITableFilterTest {
	
	private ITableFilter tableFilterFacade = null;
	private TableFilter tableFilterTarget = null;
	
	@BeforeEach
	public void beforeEach() {
		tableFilterTarget = (TableFilter)WrapperFactory.createTableFilterWrapper();
		tableFilterFacade = (ITableFilter)GenericFacadeFactory.createFacade(
				ITableFilter.class, 
				tableFilterTarget);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(tableFilterTarget);
		assertNotNull(tableFilterFacade);
	}

}
