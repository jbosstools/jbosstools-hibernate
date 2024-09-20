package org.jboss.tools.hibernate.orm.runtime.v_6_5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.tool.internal.reveng.strategy.TableFilter;
import org.hibernate.tool.orm.jbt.api.factory.WrapperFactory;
import org.hibernate.tool.orm.jbt.api.wrp.Wrapper;
import org.jboss.tools.hibernate.orm.runtime.common.GenericFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ITableFilterTest {
	
	private ITableFilter tableFilterFacade = null;
	private TableFilter tableFilterTarget = null;
	
	@BeforeEach
	public void beforeEach() {
		Wrapper wrapper = (Wrapper)WrapperFactory.createTableFilterWrapper();
		tableFilterTarget = (TableFilter)wrapper.getWrappedObject();
		tableFilterFacade = (ITableFilter)GenericFacadeFactory.createFacade(
				ITableFilter.class, 
				wrapper);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(tableFilterTarget);
		assertNotNull(tableFilterFacade);
	}

	@Test
	public void testSetExclude() {
		assertNull(tableFilterTarget.getExclude());
		tableFilterFacade.setExclude(true);
		assertTrue(tableFilterTarget.getExclude());
	}
	
	@Test
	public void testSetMatchCatalog() {
		assertNotEquals("foo", tableFilterTarget.getMatchCatalog());
		tableFilterFacade.setMatchCatalog("foo");
		assertEquals("foo", tableFilterTarget.getMatchCatalog());
	}
		
	@Test
	public void testSetMatchSchema() {
		assertNotEquals("foo", tableFilterTarget.getMatchSchema());
		tableFilterFacade.setMatchSchema("foo");
		assertEquals("foo", tableFilterTarget.getMatchSchema());
	}
		
	@Test
	public void testSetMatchName() {
		assertNotEquals("foo", tableFilterTarget.getMatchName());
		tableFilterFacade.setMatchName("foo");
		assertEquals("foo", tableFilterTarget.getMatchName());
	}
		
	@Test
	public void testGetExclude() {
		assertNull(tableFilterFacade.getExclude());
		tableFilterTarget.setExclude(true);
		assertTrue(tableFilterFacade.getExclude());
	}
		
	@Test
	public void testGetMatchCatalog() {
		assertNotEquals("foo", tableFilterFacade.getMatchCatalog());
		tableFilterTarget.setMatchCatalog("foo");
		assertEquals("foo", tableFilterFacade.getMatchCatalog());
	}
		
	@Test
	public void testGetMatchSchema() {
		assertNotEquals("foo", tableFilterFacade.getMatchSchema());
		tableFilterTarget.setMatchSchema("foo");
		assertEquals("foo", tableFilterFacade.getMatchSchema());
	}
		
	@Test
	public void testGetMatchName() {
		assertNotEquals("foo", tableFilterFacade.getMatchName());
		tableFilterTarget.setMatchName("foo");
		assertEquals("foo", tableFilterFacade.getMatchName());
	}
		
}
