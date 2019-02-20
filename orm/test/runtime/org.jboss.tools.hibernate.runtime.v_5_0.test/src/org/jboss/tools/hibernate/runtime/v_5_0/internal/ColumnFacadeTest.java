package org.jboss.tools.hibernate.runtime.v_5_0.internal;

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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class ColumnFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IColumn columnFacade = null; 
	private Column column = null;
	
	@Before
	public void setUp() {
		column = new Column();
		columnFacade = FACADE_FACTORY.createColumn(column);
	}
	
	@Test
	public void testGetMappedClass() {
		Assert.assertNull(columnFacade.getName());
		column.setName("foobar");
		Assert.assertEquals("foobar", columnFacade.getName());
	}
	
	@Test
	public void testGetSqlTypeCode() {
		Assert.assertNull(columnFacade.getSqlTypeCode());
		column.setSqlTypeCode(Integer.MAX_VALUE);
		Assert.assertEquals(Integer.MAX_VALUE, columnFacade.getSqlTypeCode().intValue());
	}
	
	@Test
	public void testGetSqlType() {
		Assert.assertNull(columnFacade.getSqlType());
		column.setSqlType("foobar");
		Assert.assertEquals("foobar", columnFacade.getSqlType());
		Configuration configuration = new Configuration();
		configuration.setProperty(Environment.DIALECT, "org.hibernate.dialect.H2Dialect");
		MetadataImplementor metadata = 
				(MetadataImplementor)MetadataHelper.getMetadata(configuration);
		SimpleValue value = new SimpleValue(metadata);
		value.setTypeName("int");
		column.setValue(value);
		IConfiguration configurationFacade = FACADE_FACTORY.createConfiguration(configuration);
		column.setSqlType(null);
		Assert.assertEquals("integer", columnFacade.getSqlType(configurationFacade));
	}
	
	@Test
	public void testGetLength() {
		Assert.assertEquals(Column.DEFAULT_LENGTH, columnFacade.getLength());
		column.setLength(999);
		Assert.assertEquals(999, columnFacade.getLength());
	}
	
	@Test
	public void testGetDefaultLength() {
		Assert.assertEquals(Column.DEFAULT_LENGTH, columnFacade.getDefaultLength());
	}
	
	@Test
	public void testGetPrecision() {
		Assert.assertEquals(Column.DEFAULT_PRECISION, columnFacade.getPrecision());
		column.setPrecision(999);
		Assert.assertEquals(999, columnFacade.getPrecision());
	}
	
	@Test
	public void testGetDefaultPrecision() {
		Assert.assertEquals(Column.DEFAULT_PRECISION, columnFacade.getDefaultPrecision());
	}
	
	@Test
	public void testGetScale() {
		Assert.assertEquals(Column.DEFAULT_SCALE, columnFacade.getScale());
		column.setScale(999);
		Assert.assertEquals(999, columnFacade.getScale());
	}
	
	@Test
	public void testGetDefaultScale() {
		Assert.assertEquals(Column.DEFAULT_SCALE, columnFacade.getDefaultScale());
	}
	
	@Test
	public void testIsNullable() {
		column.setNullable(true);
		Assert.assertTrue(columnFacade.isNullable());
		column.setNullable(false);
		Assert.assertFalse(columnFacade.isNullable());
	}
	
	public void testGetValue() throws Exception {
		Value targetValue = null;
		column.setValue(targetValue);
		Assert.assertNull(columnFacade.getValue());
		targetValue = new SimpleValue(
				(MetadataImplementor)MetadataHelper.getMetadata(new Configuration()));
		column.setValue(targetValue);
		IValue value = columnFacade.getValue();
		Assert.assertNotNull(value);
		Assert.assertEquals(targetValue, ((IFacade)value).getTarget());
	}
	
	public void testIsUnique() {
		column.setUnique(true);
		Assert.assertFalse(columnFacade.isUnique());
		column.setUnique(true);
		Assert.assertTrue(columnFacade.isUnique());
	}
	
	public void testSetSqlType() {
		Assert.assertNull(column.getSqlType());
		columnFacade.setSqlType("blah");
		Assert.assertEquals("blah", column.getSqlType());
	}
	
}
