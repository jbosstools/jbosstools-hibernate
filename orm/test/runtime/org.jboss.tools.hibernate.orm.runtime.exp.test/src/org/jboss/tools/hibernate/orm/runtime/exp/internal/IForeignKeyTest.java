package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.GenericFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IForeignKey;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IForeignKeyTest {

	private IForeignKey foreignKeyFacade = null; 
	private ForeignKey foreignKeyTarget = null;
	
	@BeforeEach
	public void beforeEach() {
		foreignKeyTarget = new ForeignKey();
		foreignKeyFacade = (IForeignKey)GenericFacadeFactory.createFacade(IForeignKey.class, foreignKeyTarget);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(foreignKeyFacade);
		assertNotNull(foreignKeyTarget);
	}

	@Test
	public void testGetReferencedTable() {
		Table tableTarget = new Table("");
		foreignKeyTarget.setReferencedTable(tableTarget);
		ITable table = foreignKeyFacade.getReferencedTable();
		assertSame(tableTarget, ((IFacade)table).getTarget());
	}
	
}
