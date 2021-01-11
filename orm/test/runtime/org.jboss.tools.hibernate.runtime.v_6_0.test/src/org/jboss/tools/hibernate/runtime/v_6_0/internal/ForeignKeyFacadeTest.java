package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertSame;

import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.runtime.common.AbstractForeignKeyFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IForeignKey;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.junit.Before;
import org.junit.Test;

public class ForeignKeyFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IForeignKey foreignKeyFacade = null; 
	private ForeignKey foreignKey = null;
	
	@Before
	public void before() {
		foreignKey = new ForeignKey();
		foreignKeyFacade = new AbstractForeignKeyFacade(FACADE_FACTORY, foreignKey) {};
	}
	
	@Test
	public void testGetReferencedTable() {
		Table tableTarget = new Table();
		foreignKey.setReferencedTable(tableTarget);
		ITable table = foreignKeyFacade.getReferencedTable();
		assertSame(tableTarget, ((IFacade)table).getTarget());
	}
	
}
