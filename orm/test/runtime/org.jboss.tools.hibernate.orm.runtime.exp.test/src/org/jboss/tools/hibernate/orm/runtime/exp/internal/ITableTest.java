package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ITableTest {
	
	private ITable tableFacade = null;
	private Table tableTarget = null;
	
	@BeforeEach
	public void beforeEach() {
		tableFacade = NewFacadeFactory.INSTANCE.createTable("foo");
		tableTarget = (Table)((IFacade)tableFacade).getTarget();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(tableFacade);
		assertNotNull(tableTarget);
	}
	

}
