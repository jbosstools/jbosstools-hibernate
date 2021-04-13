package org.jboss.tools.hibernate.runtime.v_5_2.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Value;
import org.hibernate.tool.util.MetadataHelper;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ColumnFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IColumn columnFacade = null; 
	private Column column = null;
	
	@BeforeEach
	public void setUp() {
		column = new Column();
		columnFacade = FACADE_FACTORY.createColumn(column);
	}
	
	@Test
	public void testGetMappedClass() {
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
		configuration.setProperty(Environment.DIALECT, "org.hibernate.dialect.H2Dialect");
		MetadataImplementor metadata = 
				(MetadataImplementor)MetadataHelper.getMetadata(configuration);
		SimpleValue value = new SimpleValue(metadata);
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
	
	public void testGetValue() throws Exception {
		Value targetValue = null;
		column.setValue(targetValue);
		assertNull(columnFacade.getValue());
		targetValue = new SimpleValue(
				(MetadataImplementor)MetadataHelper.getMetadata(new Configuration()));
		column.setValue(targetValue);
		IValue value = columnFacade.getValue();
		assertNotNull(value);
		assertEquals(targetValue, ((IFacade)value).getTarget());
	}
	
	public void testIsUnique() {
		column.setUnique(true);
		assertFalse(columnFacade.isUnique());
		column.setUnique(true);
		assertTrue(columnFacade.isUnique());
	}
	
	public void testSetSqlType() {
		assertNull(column.getSqlType());
		columnFacade.setSqlType("blah");
		assertEquals("blah", column.getSqlType());
	}
	
}
