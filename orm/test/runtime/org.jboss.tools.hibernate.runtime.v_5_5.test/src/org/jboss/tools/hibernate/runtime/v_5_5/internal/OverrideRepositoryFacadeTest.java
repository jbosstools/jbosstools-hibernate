package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.List;

import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.mapping.Table;
import org.jboss.tools.hibernate.runtime.common.AbstractOverrideRepositoryFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
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
		assertNotNull(table);
		assertEquals("FOO", table.getName());
	}
	
}
