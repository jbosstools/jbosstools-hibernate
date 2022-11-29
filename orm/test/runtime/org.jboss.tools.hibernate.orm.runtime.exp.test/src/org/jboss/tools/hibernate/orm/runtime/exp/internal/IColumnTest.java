package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.hibernate.mapping.Column;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IColumnTest {
	
	private static final NewFacadeFactory FACADE_FACTORY = NewFacadeFactory.INSTANCE;
	
	private IColumn columnFacade = null; 
	private Column columnTarget = null;
	
	@BeforeEach
	public void beforeEach() {
		columnFacade = FACADE_FACTORY.createColumn();
		columnTarget = (Column)((IFacade)columnFacade).getTarget();
	}
	
	@Test
	public void testInstance() {
		assertNotNull(columnFacade);
		assertNotNull(columnTarget);
	}
	
	@Test
	public void testGetName() {
		assertNull(columnFacade.getName());
		columnTarget.setName("foobar");
		assertEquals("foobar", columnFacade.getName());
	}
	
	@Test
	public void testGetSqlTypeCode() {
		assertNull(columnFacade.getSqlTypeCode());
		columnTarget.setSqlTypeCode(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, columnFacade.getSqlTypeCode().intValue());
	}

}
