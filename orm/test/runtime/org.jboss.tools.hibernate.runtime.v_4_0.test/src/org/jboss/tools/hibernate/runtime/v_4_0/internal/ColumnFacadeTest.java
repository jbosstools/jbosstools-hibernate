package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ColumnFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	public static class TestDialect extends Dialect {}
	
	private IColumn columnFacade = null; 
	private Column column = null;
	
	@BeforeEach
	public void beforeEach() {
		column = new Column();
		columnFacade = new ColumnFacadeImpl(FACADE_FACTORY, column);
	}
	
	@Test
	public void testGetName() {
		assertNull(columnFacade.getName());
		column.setName("foobar");
		assertEquals("foobar", columnFacade.getName());
	}
	
	@Test
	public void testGetSqlTypeCode() {
		assertNull(columnFacade.getSqlTypeCode());
		column.setSqlTypeCode(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, columnFacade.getSqlTypeCode().intValue());
	}

	@Test
	public void testGetSqlType() {
		assertNull(columnFacade.getSqlType());
		column.setSqlType("foobar");
		assertEquals("foobar", columnFacade.getSqlType());
		Configuration configuration = new Configuration();
		configuration.setProperty(AvailableSettings.DIALECT, TestDialect.class.getName());
		SimpleValue value = new SimpleValue(configuration.createMappings(), new Table());
		value.setTypeName("int");
		column.setValue(value);
		IConfiguration configurationFacade = FACADE_FACTORY.createConfiguration(configuration);
		column.setSqlType(null);
		assertEquals("integer", columnFacade.getSqlType(configurationFacade));
	}
	
	@Test
	public void testGetLength() {
		assertEquals(Column.DEFAULT_LENGTH, columnFacade.getLength());
		column.setLength(999);
		assertEquals(999, columnFacade.getLength());
	}
	
	@Test
	public void testGetDefaultLength() {
		assertEquals(Column.DEFAULT_LENGTH, columnFacade.getDefaultLength());
	}
	
	@Test
	public void testGetPrecision() {
		assertEquals(Column.DEFAULT_PRECISION, columnFacade.getPrecision());
		column.setPrecision(999);
		assertEquals(999, columnFacade.getPrecision());
	}
	
	@Test
	public void testGetDefaultPrecision() {
		assertEquals(Column.DEFAULT_PRECISION, columnFacade.getDefaultPrecision());
	}
	
	@Test
	public void testGetScale() {
		assertEquals(Column.DEFAULT_SCALE, columnFacade.getScale());
		column.setScale(999);
		assertEquals(999, columnFacade.getScale());
	}
	
	@Test
	public void testGetDefaultScale() {
		assertEquals(Column.DEFAULT_SCALE, columnFacade.getDefaultScale());
	}
	
	@Test
	public void testIsNullable() {
		column.setNullable(true);
		assertTrue(columnFacade.isNullable());
		column.setNullable(false);
		assertFalse(columnFacade.isNullable());
	}
	
	@Test
	public void testGetValue() throws Exception {
		Value targetValue = null;
		column.setValue(targetValue);
		assertNull(columnFacade.getValue());
		Configuration configuration = new Configuration();
		configuration.setProperty(AvailableSettings.DIALECT, TestDialect.class.getName());
		targetValue = new SimpleValue(null, new Table());
		column.setValue(targetValue);
		IValue value = columnFacade.getValue();
		assertNotNull(value);
		assertEquals(targetValue, ((IFacade)value).getTarget());
	}
	
	@Test
	public void testIsUnique() {
		column.setUnique(false);
		assertFalse(columnFacade.isUnique());
		column.setUnique(true);
		assertTrue(columnFacade.isUnique());
	}
	
	@Test
	public void testSetSqlType() {
		assertNull(column.getSqlType());
		columnFacade.setSqlType("blah");
		assertEquals("blah", column.getSqlType());
	}

}
