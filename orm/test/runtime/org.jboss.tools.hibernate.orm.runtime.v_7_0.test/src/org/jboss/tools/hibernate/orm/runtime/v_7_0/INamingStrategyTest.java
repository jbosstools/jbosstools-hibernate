package org.jboss.tools.hibernate.orm.runtime.v_7_0;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitBasicColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitCollectionTableNameSource;
import org.hibernate.boot.model.naming.ImplicitEntityNameSource;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.model.naming.ImplicitPrimaryKeyJoinColumnNameSource;
import org.hibernate.tool.orm.jbt.api.factory.WrapperFactory;
import org.jboss.tools.hibernate.orm.runtime.common.GenericFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class INamingStrategyTest {

	private INamingStrategy namingStrategyFacade = null;
	
	@BeforeEach
	public void beforeEach() {
		namingStrategyFacade = (INamingStrategy)GenericFacadeFactory.createFacade(
				INamingStrategy.class, 
				WrapperFactory.createNamingStrategyWrapper(TestNamingStrategy.class.getName()));
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
		assertEquals("FooBarColumnName", namingStrategyFacade.propertyToColumnName("bar"));
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
		assertEquals("BarFooTable", namingStrategyFacade.classToTableName("foobar"));
	}
	
	@Test
	public void testGetStrategyClassName() {
		assertEquals(TestNamingStrategy.class.getName(), namingStrategyFacade.getStrategyClassName());
	}
	
	@SuppressWarnings("serial")
	public static class TestNamingStrategy extends ImplicitNamingStrategyJpaCompliantImpl {
		@Override 
		public Identifier determineCollectionTableName(ImplicitCollectionTableNameSource source) {
			return Identifier.toIdentifier("FooBarCollectionTableName");
		}
		@Override
		public Identifier determineBasicColumnName(ImplicitBasicColumnNameSource source) {
			return Identifier.toIdentifier("FooBarColumnName");
		}
		@Override
		public Identifier determinePrimaryTableName(ImplicitEntityNameSource source) {
			return Identifier.toIdentifier("BarFooTable");
		}
		@Override
		public Identifier determinePrimaryKeyJoinColumnName(ImplicitPrimaryKeyJoinColumnNameSource source) {
			return Identifier.toIdentifier("FooBarJoinKeyColumnName");
		}
	}
	
}
