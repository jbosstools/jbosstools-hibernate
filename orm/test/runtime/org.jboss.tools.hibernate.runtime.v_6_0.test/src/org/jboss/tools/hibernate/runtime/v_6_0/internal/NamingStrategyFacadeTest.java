package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertEquals;

import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.NamingStrategy;
import org.jboss.tools.hibernate.runtime.common.AbstractNamingStrategyFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.junit.Before;
import org.junit.Test;

public class NamingStrategyFacadeTest {

	private final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private NamingStrategy namingStrategyTarget = null;
	private INamingStrategy namingStrategyFacade = null;
	
	@Before
	public void before() {
		namingStrategyTarget = new TestNamingStrategy();
		namingStrategyFacade = new AbstractNamingStrategyFacade(FACADE_FACTORY, namingStrategyTarget) {};
	}
	
	@Test
	public void testCollectionTableName() {
		String tableName = namingStrategyFacade.collectionTableName(
				"FooEntity", 
				"FooTable", 
				"BarEntity", 
				"BarTable", 
				"FooBarProperty");
		assertEquals("FooBarCollectionTableName", tableName);
	}
	
	@Test
	public void testColumnName() {
		assertEquals("FooBarColumnName", namingStrategyFacade.columnName("foo"));
	}
	
	private class TestNamingStrategy extends DefaultNamingStrategy {
		private static final long serialVersionUID = 1L;
		@Override
		public String collectionTableName(
				String ownerEntity, 
				String ownerEntityTable, 
				String associatedEntity, 
				String associatedEntityTable,
				String propertyName) {
			return "FooBarCollectionTableName";
		}
		@Override
		public String columnName(String columnName) {
			return "FooBarColumnName";
		}
	}

}
