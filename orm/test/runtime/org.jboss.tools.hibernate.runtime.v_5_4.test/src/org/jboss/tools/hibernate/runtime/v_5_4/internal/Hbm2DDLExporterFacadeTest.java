package org.jboss.tools.hibernate.runtime.v_5_4.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.Properties;

import org.hibernate.tool.hbm2x.AbstractExporter;
import org.hibernate.tool.hbm2x.Hbm2DDLExporter;
import org.jboss.tools.hibernate.runtime.common.AbstractHbm2DDLExporterFacade;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class Hbm2DDLExporterFacadeTest {

	private static final FacadeFactoryImpl FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IHbm2DDLExporter ddlExporterFacade = null;
	private Hbm2DDLExporter ddlExporterTarget = null;
	
	
	@BeforeEach
	public void before() {
		ddlExporterTarget = new Hbm2DDLExporter();
		ddlExporterFacade = new AbstractHbm2DDLExporterFacade(FACADE_FACTORY, ddlExporterTarget) {};
	}
	
	@Test 
	public void testGetProperties() throws Exception {
		Field propertiesField = AbstractExporter.class.getDeclaredField("properties");
		propertiesField.setAccessible(true);
		Properties properties = new Properties();
		assertNotSame(properties, ddlExporterFacade.getProperties());
		propertiesField.set(ddlExporterTarget, properties);
		assertSame(properties, ddlExporterFacade.getProperties());
	}
	
	@Test
	public void testSetExport() throws Exception {
		Field exportToDatabaseField = Hbm2DDLExporter.class.getDeclaredField("exportToDatabase");
		exportToDatabaseField.setAccessible(true);
		assertTrue((Boolean)exportToDatabaseField.get(ddlExporterTarget));
		ddlExporterFacade.setExport(false);
		assertFalse((Boolean)exportToDatabaseField.get(ddlExporterTarget));
	}

}
