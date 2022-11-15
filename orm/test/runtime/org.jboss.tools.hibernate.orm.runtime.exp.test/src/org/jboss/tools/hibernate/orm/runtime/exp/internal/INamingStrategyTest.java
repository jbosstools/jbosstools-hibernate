package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.hibernate.cfg.DefaultNamingStrategy;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class INamingStrategyTest {

	private static final NewFacadeFactory FACADE_FACTORY = NewFacadeFactory.INSTANCE;

	private INamingStrategy namingStrategyFacade = null;
	
	@BeforeEach
	public void beforeEach() {
		namingStrategyFacade = FACADE_FACTORY.createNamingStrategy(TestNamingStrategy.class.getName());
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
	
	@Test
	public void testPropertyToColumnName() {
		assertEquals("BarFooPropertyColumn", namingStrategyFacade.propertyToColumnName("bar"));
	}
	
	@Test
	public void testTableName() {
		assertEquals("BarFooTable", namingStrategyFacade.tableName("foobar"));
	}
	
	@Test
	public void testJoinKeyColumnName() {
		assertEquals("FooBarJoinKeyColumnName", namingStrategyFacade.joinKeyColumnName("foo", "bar"));
	}
	
	@Test
	public void testClassToTableName() {
		assertEquals("FooBarClassTable", namingStrategyFacade.classToTableName("foobar"));
	}
	
	@Test
	public void testGetStrategyClassName() {
		assertEquals(TestNamingStrategy.class.getName(), namingStrategyFacade.getStrategyClassName());
	}
	
	public static class TestNamingStrategy extends DefaultNamingStrategy {
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
		@Override
		public String propertyToColumnName(String propertyName) {
			return "BarFooPropertyColumn";
		}
		@Override
		public String tableName(String tableName) {
			return "BarFooTable";
		}
		@Override
		public String joinKeyColumnName(String joinedColumn, String joinedTable) {
			return "FooBarJoinKeyColumnName";
		}
		@Override
		public String classToTableName(String className) {
			return "FooBarClassTable";
		}
	}

}
