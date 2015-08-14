package org.jboss.tools.hibernate.runtime.v_3_6.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.hibernate.cfg.reveng.DatabaseCollector;
import org.hibernate.cfg.reveng.DefaultDatabaseCollector;
import org.hibernate.cfg.reveng.dialect.JDBCMetaDataDialect;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.runtime.common.AbstractDatabaseCollectorFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IDatabaseCollector;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class DatabaseCollectorFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IDatabaseCollector databaseCollectorFacade = null; 
	private DatabaseCollector databaseCollector = null;
	
	private String methodName = null;
	private Object[] arguments = null;
	
	@Before
	public void setUp() throws Exception {
		databaseCollector = new DefaultDatabaseCollector(new JDBCMetaDataDialect()) {
			@Override public Iterator<Entry<String, List<Table>>> getQualifierEntries() {
				methodName = "getQualifierEntries";
				HashMap<String, List<Table>> map = new HashMap<String, List<Table>>();
				ArrayList<Table> list = new ArrayList<Table>();
				list.add(new Table("foo"));
				list.add(new Table("bar"));
				map.put("one", list);
				list = new ArrayList<Table>();
				list.add(new Table("fubar"));
				map.put("two", list);
				return map.entrySet().iterator();
			}
		};
		databaseCollectorFacade = new AbstractDatabaseCollectorFacade(FACADE_FACTORY, databaseCollector) {};
	}
	
	@Test
	public void testGetQualifierEntries() {
		Iterator<Entry<String, List<ITable>>> iterator = databaseCollectorFacade.getQualifierEntries();
		Assert.assertEquals("getQualifierEntries", methodName);
		Assert.assertNull(arguments);
		while (iterator.hasNext()) {
			Entry<String, List<ITable>> entry = iterator.next();
			if ("one".equals(entry.getKey())) {
				List<ITable> list = entry.getValue();
				Assert.assertEquals(2, list.size());
			} else if ("two".equals(entry.getKey())) {
				List<ITable> list = entry.getValue();
				Assert.assertEquals(1, list.size());
			}
		}
		methodName = null;
		iterator = databaseCollectorFacade.getQualifierEntries();
		Assert.assertNull(methodName);
		Assert.assertNull(arguments);
		while (iterator.hasNext()) {
			Entry<String, List<ITable>> entry = iterator.next();
			if ("one".equals(entry.getKey())) {
				List<ITable> list = entry.getValue();
				Assert.assertEquals(2, list.size());
			} else if ("two".equals(entry.getKey())) {
				List<ITable> list = entry.getValue();
				Assert.assertEquals(1, list.size());
			}
		}		
	}
		
}
