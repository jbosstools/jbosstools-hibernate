package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.List;

import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.DelegatingReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableFilter;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.runtime.common.AbstractOverrideRepositoryFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class OverrideRepositoryFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private static final String HIBERNATE_REVERSE_ENGINEERING_XML =
			"<?xml version='1.0' encoding='UTF-8'?>                                 "+
			"<!DOCTYPE hibernate-reverse-engineering PUBLIC                         "+
			"      '-//Hibernate/Hibernate Reverse Engineering DTD 3.0//EN'         "+
			"      'http://hibernate.org/dtd/hibernate-reverse-engineering-3.0.dtd'>"+
			"<hibernate-reverse-engineering>                                        "+
			"    <table name='FOO'/>                                                "+
			"</hibernate-reverse-engineering>                                       ";

	private IOverrideRepository overrideRepositoryFacade = null; 
	private OverrideRepository overrideRepository;
	
	@Before
	public void setUp() {
		overrideRepository = new OverrideRepository();
		overrideRepositoryFacade = new AbstractOverrideRepositoryFacade(
				FACADE_FACTORY, 
				overrideRepository) {};
	}
	
	@Test
	public void testAddFile() throws Exception {
		File file = File.createTempFile("addFile", "tst");
		file.deleteOnExit();
		FileWriter fileWriter = new FileWriter(file);
		fileWriter.write(HIBERNATE_REVERSE_ENGINEERING_XML);
		fileWriter.close();
		overrideRepositoryFacade.addFile(file);
		Field tablesField = overrideRepository.getClass().getDeclaredField("tables");
		tablesField.setAccessible(true);
		Object object = tablesField.get(overrideRepository);
		List<?> tables = (List<?>)object;
		Table table = (Table)tables.get(0);
		Assert.assertNotNull(table);
		Assert.assertEquals("FOO", table.getName());
	}
	
	@Test
	public void testGetReverseEngineeringStrategy() throws Exception {
		ReverseEngineeringStrategy res = new DefaultReverseEngineeringStrategy();
		IReverseEngineeringStrategy resFacade = FACADE_FACTORY.createReverseEngineeringStrategy(res);
		IReverseEngineeringStrategy result = overrideRepositoryFacade.getReverseEngineeringStrategy(resFacade);
		DelegatingReverseEngineeringStrategy resultTarget = 
				(DelegatingReverseEngineeringStrategy)((IFacade)result).getTarget();
		Field delegateField = DelegatingReverseEngineeringStrategy.class.getDeclaredField("delegate");
		delegateField.setAccessible(true);
		Assert.assertSame(res, delegateField.get(resultTarget));
	}
	
	@Test
	public void testAddTableFilter() throws Exception {
		TableFilter tableFilter = new TableFilter();
		ITableFilter tableFilterFacade = FACADE_FACTORY.createTableFilter(tableFilter);
		Field tableFiltersField = OverrideRepository.class.getDeclaredField("tableFilters");
		tableFiltersField.setAccessible(true);
		List<?> tableFilters = (List<?>)tableFiltersField.get(overrideRepository);
		Assert.assertTrue(tableFilters.isEmpty());
		overrideRepositoryFacade.addTableFilter(tableFilterFacade);
		tableFilters = (List<?>)tableFiltersField.get(overrideRepository);
		Assert.assertSame(tableFilter, tableFilters.get(0));		
	}
	
}
