package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.Field;
import java.util.Properties;

import org.hibernate.tool.hbm2x.AbstractExporter;
import org.hibernate.tool.hbm2x.Hbm2DDLExporter;
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
		ddlExporterFacade = new Hbm2DDLExporterFacadeImpl(FACADE_FACTORY, ddlExporterTarget);
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
	
}
