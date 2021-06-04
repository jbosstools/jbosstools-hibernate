package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.Collections;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.runtime.common.AbstractPrimaryKeyFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IPrimaryKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PrimaryKeyFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IPrimaryKey primaryKeyFacade = null; 
	private PrimaryKey primaryKeyTarget = null;
	
	@BeforeEach
	public void beforeEach() {
		primaryKeyTarget = new PrimaryKey(new Table());
		primaryKeyFacade = new AbstractPrimaryKeyFacade(FACADE_FACTORY, primaryKeyTarget) {};
	}
	
	@Test
	public void testAddColumn() throws Exception {
		Field field = AbstractPrimaryKeyFacade.class.getDeclaredField("columns");
		field.setAccessible(true);
		assertNull(field.get(primaryKeyFacade));
		field.set(primaryKeyFacade, Collections.emptyList());
		Column columnTarget = new Column();
		IColumn columnFacade = FACADE_FACTORY.createColumn(columnTarget);
		assertTrue(primaryKeyTarget.getColumns().isEmpty());
		primaryKeyFacade.addColumn(columnFacade);
		assertEquals(1, primaryKeyTarget.getColumns().size());
		assertSame(columnTarget, primaryKeyTarget.getColumns().get(0));
		assertNull(field.get(primaryKeyFacade));
	}

}
