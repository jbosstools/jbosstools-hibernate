package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import org.hibernate.cfg.reveng.TableFilter;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;
import org.junit.Assert;
import org.junit.Test;

public class TableFilterFacadeTest {

	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	@Test
	public void testSetExclude() {
		TableFilter tableFilter = new TableFilter();
		ITableFilter tableFilterFacade = FACADE_FACTORY.createTableFilter(tableFilter);
		Assert.assertNull(tableFilter.getExclude());
		tableFilterFacade.setExclude(true);
		Assert.assertTrue(tableFilter.getExclude());
	}
	
}
