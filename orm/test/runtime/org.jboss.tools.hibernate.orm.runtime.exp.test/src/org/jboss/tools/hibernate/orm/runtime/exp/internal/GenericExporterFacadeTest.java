package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.internal.export.common.GenericExporter;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IGenericExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GenericExporterFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IGenericExporter genericExporterFacade = null; 
	private GenericExporter genericExporter = null;
	
	@BeforeEach
	public void beforeEach() throws Exception {
		genericExporter = new GenericExporter();
		genericExporterFacade = new GenericExporterFacadeImpl(FACADE_FACTORY, genericExporter) {};
	}
	
	@Test
	public void testSetFilePattern() {
		assertNull(genericExporter.getProperties().get(ExporterConstants.FILE_PATTERN));
		genericExporterFacade.setFilePattern("foobar");
		assertEquals("foobar", genericExporter.getProperties().get(ExporterConstants.FILE_PATTERN));
	}
	
	@Test
	public void testSetTemplate() {
		assertNull(genericExporter.getProperties().get(ExporterConstants.TEMPLATE_NAME));
		genericExporterFacade.setTemplateName("barfoo");
		assertEquals("barfoo", genericExporter.getProperties().get(ExporterConstants.TEMPLATE_NAME));
	}
	
	@Test
	public void testSetForEach() {
		assertNull(genericExporter.getProperties().get(ExporterConstants.FOR_EACH));
		genericExporterFacade.setForEach("foobar");
		assertEquals("foobar", genericExporter.getProperties().get(ExporterConstants.FOR_EACH));
	}
	
	@Test
	public void testGetFilePattern() {
		assertNull(genericExporterFacade.getFilePattern());
		genericExporter.getProperties().put(ExporterConstants.FILE_PATTERN, "foobar");
		assertEquals("foobar", genericExporterFacade.getFilePattern());
	}
	
	@Test
	public void testGetTemplateName() {
		assertNull(genericExporterFacade.getTemplateName());
		genericExporter.getProperties().put(ExporterConstants.TEMPLATE_NAME, "foobar");
		assertEquals("foobar", genericExporterFacade.getTemplateName());
	}
	
}
