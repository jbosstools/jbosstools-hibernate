package org.jboss.tools.hibernate.runtime.v_5_0.internal;

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
	
	@Test
	public void testSetMatchCatalog() {
		TableFilter tableFilter = new TableFilter();
		ITableFilter tableFilterFacade = FACADE_FACTORY.createTableFilter(tableFilter);
		Assert.assertNotEquals("foo", tableFilter.getMatchCatalog());
		tableFilterFacade.setMatchCatalog("foo");
		Assert.assertEquals("foo", tableFilter.getMatchCatalog());
	}
		
	@Test
	public void testSetMatchSchema() {
		TableFilter tableFilter = new TableFilter();
		ITableFilter tableFilterFacade = FACADE_FACTORY.createTableFilter(tableFilter);
		Assert.assertNotEquals("foo", tableFilter.getMatchSchema());
		tableFilterFacade.setMatchSchema("foo");
		Assert.assertEquals("foo", tableFilter.getMatchSchema());
	}
		
	@Test
	public void testSetMatchName() {
		TableFilter tableFilter = new TableFilter();
		ITableFilter tableFilterFacade = FACADE_FACTORY.createTableFilter(tableFilter);
		Assert.assertNotEquals("foo", tableFilter.getMatchName());
		tableFilterFacade.setMatchName("foo");
		Assert.assertEquals("foo", tableFilter.getMatchName());
	}
		
	@Test
	public void testGetExclude() {
		TableFilter tableFilter = new TableFilter();
		ITableFilter tableFilterFacade = FACADE_FACTORY.createTableFilter(tableFilter);
		Assert.assertNull(tableFilterFacade.getExclude());
		tableFilter.setExclude(true);
		Assert.assertTrue(tableFilterFacade.getExclude());
	}
		
	@Test
	public void testGetMatchCatalog() {
		TableFilter tableFilter = new TableFilter();
		ITableFilter tableFilterFacade = FACADE_FACTORY.createTableFilter(tableFilter);
		Assert.assertNotEquals("foo", tableFilterFacade.getMatchCatalog());
		tableFilter.setMatchCatalog("foo");
		Assert.assertEquals("foo", tableFilterFacade.getMatchCatalog());
	}
		
	@Test
	public void testGetMatchSchema() {
		TableFilter tableFilter = new TableFilter();
		ITableFilter tableFilterFacade = FACADE_FACTORY.createTableFilter(tableFilter);
		Assert.assertNotEquals("foo", tableFilterFacade.getMatchSchema());
		tableFilter.setMatchSchema("foo");
		Assert.assertEquals("foo", tableFilterFacade.getMatchSchema());
	}
		
	@Test
	public void testGetMatchName() {
		TableFilter tableFilter = new TableFilter();
		ITableFilter tableFilterFacade = FACADE_FACTORY.createTableFilter(tableFilter);
		Assert.assertNotEquals("foo", tableFilterFacade.getMatchName());
		tableFilter.setMatchName("foo");
		Assert.assertEquals("foo", tableFilterFacade.getMatchName());
	}
		
}
