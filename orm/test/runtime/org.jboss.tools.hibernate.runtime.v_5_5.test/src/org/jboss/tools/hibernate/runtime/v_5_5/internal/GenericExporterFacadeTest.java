package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.tool.hbm2x.GenericExporter;
import org.jboss.tools.hibernate.runtime.common.AbstractGenericExporterFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IGenericExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GenericExporterFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IGenericExporter genericExporterFacade = null; 
	private GenericExporter genericExporter = null;
	
	@BeforeEach
	public void beforeEach() {
		genericExporter = new GenericExporter();
		genericExporterFacade = new AbstractGenericExporterFacade(FACADE_FACTORY, genericExporter) {};
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(genericExporterFacade);
	}
	
}
