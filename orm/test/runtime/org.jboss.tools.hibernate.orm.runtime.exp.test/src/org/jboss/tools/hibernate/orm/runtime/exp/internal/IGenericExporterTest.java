package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.common.GenericExporter;
import org.hibernate.tool.orm.jbt.wrp.GenericExporterWrapperFactory;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;
import org.jboss.tools.hibernate.orm.runtime.common.GenericFacadeFactory;
import org.jboss.tools.hibernate.orm.runtime.common.IFacade;
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
				GenericExporterWrapperFactory.create(new GenericExporter()));
		Object genericExporterWrapper = ((IFacade)genericExporterFacade).getTarget();
		genericExporterTarget = (GenericExporter)((Wrapper)genericExporterWrapper).getWrappedObject();
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
	
	@Test
	public void testSetTemplate() {
		assertNull(genericExporterTarget.getProperties().get(ExporterConstants.TEMPLATE_NAME));
		genericExporterFacade.setTemplateName("barfoo");
		assertEquals("barfoo", genericExporterTarget.getProperties().get(ExporterConstants.TEMPLATE_NAME));
	}
	
	@Test
	public void testSetForEach() {
		assertNull(genericExporterTarget.getProperties().get(ExporterConstants.FOR_EACH));
		genericExporterFacade.setForEach("foobar");
		assertEquals("foobar", genericExporterTarget.getProperties().get(ExporterConstants.FOR_EACH));
	}
	
	@Test
	public void testGetFilePattern() {
		assertNull(genericExporterFacade.getFilePattern());
		genericExporterTarget.getProperties().put(ExporterConstants.FILE_PATTERN, "foobar");
		assertEquals("foobar", genericExporterFacade.getFilePattern());
	}
	
	@Test
	public void testGetTemplateName() {
		assertNull(genericExporterFacade.getTemplateName());
		genericExporterTarget.getProperties().put(ExporterConstants.TEMPLATE_NAME, "foobar");
		assertEquals("foobar", genericExporterFacade.getTemplateName());
	}
	
}
