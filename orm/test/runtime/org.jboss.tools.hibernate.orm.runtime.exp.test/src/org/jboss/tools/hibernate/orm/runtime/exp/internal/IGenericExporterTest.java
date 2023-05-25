package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.common.GenericExporter;
import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.GenericFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IGenericExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IGenericExporterTest {
	
	private GenericExporter genericExporterTarget = null;
	private IGenericExporter genericExporterFacade = null;
	
	@BeforeEach
	public void beforeEach() {
		genericExporterFacade = (IGenericExporter)GenericFacadeFactory.createFacade(
				IGenericExporter.class, 
				WrapperFactory.createGenericExporterWrapper());
		genericExporterTarget = (GenericExporter)((IFacade)genericExporterFacade).getTarget();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(genericExporterTarget);
		assertNotNull(genericExporterFacade);
	}

	@Test
	public void testSetFilePattern() {
		assertNull(genericExporterTarget.getProperties().get(ExporterConstants.FILE_PATTERN));
		genericExporterFacade.setFilePattern("foobar");
		assertEquals("foobar", genericExporterTarget.getProperties().get(ExporterConstants.FILE_PATTERN));
	}
	
}