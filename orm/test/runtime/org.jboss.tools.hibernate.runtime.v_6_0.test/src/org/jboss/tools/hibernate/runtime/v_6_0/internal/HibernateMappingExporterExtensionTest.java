package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.lang.reflect.Field;
import java.util.Map;

import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IExportPOJODelegate;
import org.jboss.tools.hibernate.runtime.spi.IPOJOClass;
import org.junit.Before;
import org.junit.Test;

public class HibernateMappingExporterExtensionTest {

	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private HibernateMappingExporterExtension hibernateMappingExporterExtension;

	@Before
	public void setUp() throws Exception {
		hibernateMappingExporterExtension = new HibernateMappingExporterExtension(FACADE_FACTORY, null, null);
	}
	
	@Test
	public void testSetDelegate() throws Exception {
		Field delegateField = HibernateMappingExporterExtension.class.getDeclaredField("delegateExporter");
		delegateField.setAccessible(true);
		IExportPOJODelegate exportPojoDelegate = new IExportPOJODelegate() {			
			@Override
			public void exportPOJO(Map<Object, Object> map, IPOJOClass pojoClass) { }
		};
		assertNull(delegateField.get(hibernateMappingExporterExtension));
		hibernateMappingExporterExtension.setDelegate(exportPojoDelegate);
		assertSame(exportPojoDelegate, delegateField.get(hibernateMappingExporterExtension));
	}
	
}	
