package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import java.io.File;
import java.io.FileWriter;

import org.hibernate.cfg.reveng.OverrideRepository;
import org.jboss.tools.hibernate.runtime.common.AbstractOverrideRepositoryFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class OverrideRepositoryFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private static final String HIBERNATE_REVERSE_ENGINEERING_XML =
			"<?xml version='1.0' encoding='UTF-8'?>                                         "+
			"<!DOCTYPE hibernate-reverse-engineering PUBLIC                                 "+
			"      '-//Hibernate/Hibernate Reverse Engineering DTD 3.0//EN'                 "+
			"      'http://hibernate.sourceforge.net/hibernate-reverse-engineering-3.0.dtd'>"+
			"<hibernate-reverse-engineering/>                                               ";

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
		// addFile() should be processed without exceptions
		Assert.assertTrue(true);
	}


}
