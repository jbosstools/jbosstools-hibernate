package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.List;

import org.hibernate.mapping.Table;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.hibernate.tool.internal.reveng.strategy.DelegatingStrategy;
import org.hibernate.tool.internal.reveng.strategy.OverrideRepository;
import org.hibernate.tool.internal.reveng.strategy.TableFilter;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
	
	@BeforeEach
	public void setUp() {
		overrideRepository = new OverrideRepository();
		overrideRepositoryFacade = new OverrideRepositoryFacadeImpl(FACADE_FACTORY, overrideRepository);
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
		assertNotNull(table);
		assertEquals("FOO", table.getName());
	}
	
	@Test
	public void testGetReverseEngineeringStrategy() throws Exception {
		RevengStrategy res = new DefaultStrategy();
		IReverseEngineeringStrategy resFacade = FACADE_FACTORY.createReverseEngineeringStrategy(res);
		IReverseEngineeringStrategy result = overrideRepositoryFacade.getReverseEngineeringStrategy(resFacade);
		DelegatingStrategy resultTarget = 
				(DelegatingStrategy)((IFacade)result).getTarget();
		Field delegateField = DelegatingStrategy.class.getDeclaredField("delegate");
		delegateField.setAccessible(true);
		assertSame(res, delegateField.get(resultTarget));
	}
	
	@Test
	public void testAddTableFilter() throws Exception {
		TableFilter tableFilter = new TableFilter();
		ITableFilter tableFilterFacade = FACADE_FACTORY.createTableFilter(tableFilter);
		Field tableFiltersField = OverrideRepository.class.getDeclaredField("tableFilters");
		tableFiltersField.setAccessible(true);
		List<?> tableFilters = (List<?>)tableFiltersField.get(overrideRepository);
		assertTrue(tableFilters.isEmpty());
		overrideRepositoryFacade.addTableFilter(tableFilterFacade);
		tableFilters = (List<?>)tableFiltersField.get(overrideRepository);
		assertSame(tableFilter, tableFilters.get(0));		
	}
	
}
